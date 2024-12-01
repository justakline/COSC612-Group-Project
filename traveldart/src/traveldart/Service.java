package traveldart;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import okhttp3.*;
//
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// NOTHING IS WORKING BUT HERE IS AN OUTLINE

public class Service {

    private static final String YELP_API_KEY = "IcptW8UO0iwtUZWtzuL2kgUCK0tRxKLnuktfmi1IKpdahPZxG4P0m-hyM42zzuFxalBeTRAumTFRRDt8a736n5a1rnIk6DWUC0b3goGu30Dc3pNJ3eQFt99E4xcxZ3Yx";
    private static final String YELP_API_URL = "https://api.yelp.com/v3/businesses/search?";
    private static final String TICKETMASTER_API_KEY = "DS8GMTtiVvcMey0RZ5B0wcjPg1SIVcTn";
    private static final String TICKEMASTER_API_URL = "https://app.ticketmaster.com/discovery/v2";
    private static final String OPENAI_API_KEY = "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";
    private static final String OPEN_AI_URL = "https://api.openai.com/v1/chat/completions";
    private OkHttpClient client;
    private final int TICKET_MASTER_REQUEST_AMOUNT = 10;
    private final int YELP_REQUEST_AMOUNT = 20;




    public Service() {
        this.client = new OkHttpClient();
    }



    // userCategories should be things like "Thai, Mexican, Burgers... anything food related"
    // userBudget should be 1-4, 1 being cheapest, 4 being most expensive
    public JsonArray testYelp (String address, RestaurantCategories[] userCategories, int[] userBudget, double radiusMiles) throws IOException {
        // The replaces are needed to put it in a format tha yelp understands
        String location = address.replace(" ", "%20").replace(",", "%2C");
        String uCategories = "";
        for (RestaurantCategories item: userCategories){
            uCategories += item + "&categories=";
        }
        String categories = uCategories.substring(0, uCategories.lastIndexOf("&"));

        String budget = "";
        for (int item: userBudget){
            budget += item + "&price=";
        }
        budget = budget.substring(0, budget.lastIndexOf("&"));


        int radiusMeters = radiusMiles > 25? 40000: (int)(radiusMiles * 1609);
        String reqURL = YELP_API_URL +"""
            location="""+ location + """
            &radius=""" + radiusMeters + """
            &categories=""" + categories + """
            &price=""" + budget + """
            &open_now=true
            &sort_by=best_match
            &limit=""" + YELP_REQUEST_AMOUNT;
        System.out.println(reqURL);
        Request request = new Request.Builder()
                .url(reqURL)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer "+YELP_API_KEY)
                .build();
        // System.out.println(location);
        // System.out.println(categories);
        Response response = client.newCall(request).execute();
       return processYelpResponse(response);
    }


    public JsonArray processYelpResponse(Response response) throws IOException {
        String responseBody = parseResponse(response);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonArray newArray = new JsonArray();
        JsonArray array = jsonObject.getAsJsonArray("businesses");
        String[] jsonAttributes = {"name", "url", "image_url", "rating", "review_count"};


        for(JsonElement object : array){
            try{
                JsonObject responseItem = object.getAsJsonObject();
                JsonObject newArrayItem = new JsonObject();

                // The ones that are easy to get
                for (String attribute: jsonAttributes){
                    newArrayItem.addProperty(attribute, responseItem.get(attribute).getAsString());
                }

                // The more customized ones
                JsonArray address =  responseItem.getAsJsonObject("location").getAsJsonArray("display_address");
                String newAddress = "";
                for (JsonElement e: address){
                    newAddress += e.getAsString() +", ";
                }
                newAddress = newAddress.substring(0,newAddress.lastIndexOf(","));
                newArrayItem.addProperty("address", newAddress);
                newArrayItem.addProperty("price", responseItem.get("price").toString().split("\\$").length );

                JsonArray categories = responseItem.getAsJsonArray("categories");
                JsonArray newCategories = new JsonArray();
                for (JsonElement category : categories){
                    newCategories.add(category.getAsJsonObject().get("title"));
                }
                newArrayItem.add("categories", newCategories);

                JsonArray hours = responseItem.getAsJsonArray("business_hours").get(0).getAsJsonObject().getAsJsonArray("open");
                JsonArray newHours = new JsonArray();
                for (JsonElement day : hours){
                    newHours.add(String.valueOf(LocalTime.parse(day.getAsJsonObject().get("start").getAsString(), timeFormatter)) +":00");
                    newHours.add(String.valueOf(LocalTime.parse(day.getAsJsonObject().get("end").getAsString(), timeFormatter))+":00");
                    newHours.add(day.getAsJsonObject().get("day"));
                }
                newArrayItem.add("hours", newHours);
                newArray.add(newArrayItem);
            }catch(Exception e){
                System.out.println("Something wrong with this item");
                // e.printStackTrace();
            }

        }

        return newArray;
    }

    public void testOpenAI() throws IOException {
        String question = "Where is the White house located?";
        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("role", "user");
        messageObject.addProperty("content", question);

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(messageObject);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("model", "gpt-4o-mini"); // Change to "gpt-3.5-turbo" if needed
        jsonObject.add("messages", messagesArray);

        // Convert JSON object to string
        String requestBody = jsonObject.toString();

        // Create the request body
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),   requestBody
        );

        // Build the request
        Request request = new Request.Builder()
                .url(OPEN_AI_URL) // Replace with the correct URL
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(formatJson(response.body().string()));
    }


    // dateTime needs to be in format YEAR-MONTH-DAYTHR:MIN:SECZ
    // dateTime example format->      2024-11-22T15:01:0Z
    // MUST BE HAVE THE SAME NUMBER OF DIGITS FOR EACH ONE, YEAR NEEDS 4 EVERYTHING ELSE NEEDS 2
    public JsonArray getTicketMasterEvents(String city, String startDateTime, String endDateTime, EventCategories classification) throws IOException {
        String addedParams = "&city=[" +city +"]"+  "&startDateTime=" + startDateTime + "&endDateTime=" + endDateTime +"&classification=" + classification.toString();
        Request request = new Request.Builder()
                .url(TICKEMASTER_API_URL + "/events.json?size="+TICKET_MASTER_REQUEST_AMOUNT + addedParams+ "&apikey=" +TICKETMASTER_API_KEY )
                .build();

        Response response = client.newCall(request).execute();
        return processTicketMasterResponse(response);

    }
    public JsonArray getTicketMasterEvents(String city, String startDateTime, String endDateTime) throws IOException {
        String addedParams = "&city=[" +city +"]"+  "&startDateTime=" + startDateTime + "&endDateTime=" + endDateTime;
        Request request = new Request.Builder()
                .url(TICKEMASTER_API_URL + "/events.json?size="+TICKET_MASTER_REQUEST_AMOUNT + addedParams+ "&apikey=" +TICKETMASTER_API_KEY )
                .build();

        Response response = client.newCall(request).execute();
        return processTicketMasterResponse(response);

    }

    public JsonArray getTicketMasterEvents(String city, EventCategories classification) throws IOException {
        String addedParams = "&city=[" +city +"]"+  "&classificationName=[" + classification.toString()+"]";
        Request request = new Request.Builder()
                .url(TICKEMASTER_API_URL + "/events.json?size="+TICKET_MASTER_REQUEST_AMOUNT + addedParams+ "&apikey=" +TICKETMASTER_API_KEY )
                .build();

        Response response = client.newCall(request).execute();
        return processTicketMasterResponse(response);

    }
    public JsonArray getTicketMasterEvents(String city) throws IOException {
        String addedParams = "&city=[" +city + "]";
        Request request = new Request.Builder()
                .url(TICKEMASTER_API_URL + "/events.json?size=1" +addedParams + "&apikey=" +TICKETMASTER_API_KEY )
                .build();

        Response response = client.newCall(request).execute();
        return processTicketMasterResponse(response);
    }

    private JsonArray processTicketMasterResponse(Response response) throws IOException {
        String responseBody = parseResponse(response);
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonArray array = jsonObject.getAsJsonObject("_embedded").getAsJsonArray("events");

        JsonArray events = new JsonArray();
        String[] buildJson = {"name", "dates", "classifications", "info", "url", "_embedded"};

        for (JsonElement event: array){
            JsonObject o = event.getAsJsonObject();

            JsonObject eventProcessed = new JsonObject();
            for(String item : buildJson){
                if(o.has(item)){
                    if(item.equals("_embedded")){
                        JsonObject location = new JsonObject();
                        location = getLocationData(o.get(item).getAsJsonObject());
                        eventProcessed.add("location", location);
                    }else{
                        eventProcessed.add(item, o.get(item));

                    }
                }
            }
            events.add(eventProcessed);
        }

        return events;
    }


    // Parse the API response into JSON format
    private String parseResponse(Response response) throws IOException {
        if (response.isSuccessful()) {
            String responseData = response.body().string();
            return responseData;
            // JSONObject jsonObject = new JSONObject(responseData);
            // return jsonObject.toString(4);  // Pretty print JSON
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
    public static String formatJson(String json) {

        String indentString = "";
        String finalString = "";
        int tabs = 0;

        for (char charFromJson : json.toCharArray()) {
            finalString += charFromJson;
            if("{".indexOf(charFromJson) != -1) {
                finalString += "\n";
                tabs +=1;
                finalString += "\t".repeat(tabs);
            }else if("}".indexOf(charFromJson) != -1) {
                finalString += "\n";
                tabs -=1;
                finalString += "\t".repeat(tabs);
            }


        }

        return finalString;
    }
    private JsonObject getLocationData(JsonObject o){
        JsonObject location = new JsonObject();
        JsonObject locationData = o.getAsJsonArray("venues").get(0).getAsJsonObject();
        if (locationData.has("address") && locationData.getAsJsonObject("address").has("line1")) {
            location.add("address", locationData.getAsJsonObject("address").get("line1"));
        }

        if (locationData.has("city") && locationData.getAsJsonObject("city").has("name")) {
            location.add("city", locationData.getAsJsonObject("city").get("name"));
        }

        if (locationData.has("state") && locationData.getAsJsonObject("state").has("name")) {
            location.add("state", locationData.getAsJsonObject("state").get("name"));
        }

        if (locationData.has("postalCode")) {
            location.add("postalCode", locationData.get("postalCode"));
        }

        if (locationData.has("country") && locationData.getAsJsonObject("country").has("name")) {
            location.add("country", locationData.getAsJsonObject("country").get("name"));
        }

        return location;
    }

}
