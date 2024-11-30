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
import java.util.Comparator;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class Service {

    private static  Service service = null;
    private static final String YELP_API_KEY ="IcptW8UO0iwtUZWtzuL2kgUCK0tRxKLnuktfmi1IKpdahPZxG4P0m-hyM42zzuFxalBeTRAumTFRRDt8a736n5a1rnIk6DWUC0b3goGu30Dc3pNJ3eQFt99E4xcxZ3Yx";
    private static final String YELP_API_URL = "https://api.yelp.com/v3/businesses/search?";
    private static final String TICKETMASTER_API_KEY = "DS8GMTtiVvcMey0RZ5B0wcjPg1SIVcTn";
    private static final String TICKEMASTER_API_URL = "https://app.ticketmaster.com/discovery/v2";
    private static final String OPENAI_API_KEY = "";
    private static final String OPEN_AI_URL = "https://api.openai.com/v1/chat/completions";
    private OkHttpClient client;
    private final int TICKET_MASTER_REQUEST_AMOUNT = 30;
    private final int YELP_REQUEST_AMOUNT = 20;

    // These are the most popular categories I could find that were compatible with the API

    public static Service getInstance(){
        if (service == null){
            service = new Service();
        }
        return service;
    }
    private Service() {
        this.client = new OkHttpClient();
    }



    // userCategories should be things like "Thai, Mexican, Burgers... anything food related"
    // userBudget should be 1-4, 1 being cheapest, 4 being most expensive
    public JsonArray getYelpRestaurants (String address,List<RestaurantCategories> userCategories, int userBudget, double radiusMiles) throws IOException {
        // The replaces are needed to put it in a format tha yelp understands
        String location = address.replace(" ", "%20").replace(",", "%2C");
        String categories = "";
        if(!userCategories.isEmpty()){
            String uCategories = "";
            for (RestaurantCategories item: userCategories){
                uCategories += item + "&categories=";
            }
            categories = uCategories.substring(0, uCategories.lastIndexOf("&"));
        }




        int radiusMeters = radiusMiles > 25? 40000: (int)(radiusMiles * 1609);
        String reqURL = YELP_API_URL +"""
            location="""+ location + """
            &radius=""" + radiusMeters + """
            &categories=""" + categories + """
            &price=""" + userBudget + """
            &open_now=true
            &sort_by=best_match
            &limit=""" + YELP_REQUEST_AMOUNT;
        Request request = new Request.Builder()
                .url(reqURL)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer "+YELP_API_KEY)
                .build();

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
                newArrayItem.addProperty("distance", responseItem.get("distance").getAsString());
                newArrayItem.addProperty("price", responseItem.get("price").toString().split("\\$").length );

                JsonArray categories = responseItem.getAsJsonArray("categories");
                JsonArray newCategories = new JsonArray();
                for (JsonElement category : categories){
                    newCategories.add(category);
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
    }


    // dateTime needs to be in format YEAR-MONTH-DAYTHR:MIN:SECZ
    // dateTime example format->      2024-11-22T15:01:0Z
    // MUST BE HAVE THE SAME NUMBER OF DIGITS FOR EACH ONE, YEAR NEEDS 4 EVERYTHING ELSE NEEDS 2
    public JsonArray getTicketMasterEvents(String city, String startDateTime, String endDateTime, List<EventCategories> classifications ) throws IOException {

        String classificationParams = "";
        for (EventCategories category : classifications){
            classificationParams += category.toString() + ",";
        }
        classificationParams= classificationParams.substring(0, classificationParams.length());

        String addedParams = "&city=[" +city +"]"+  "&startDateTime=" + startDateTime + "&endDateTime=" + endDateTime +"&classification="+ classificationParams;
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


    public JsonObject createRecommendation(JsonArray events, JsonArray restaurants, Preferences preferences) {
        JsonObject recommendations = new JsonObject();

        // Process and rank
        JsonArray rankedEvents = rankEvents(events, preferences);
        JsonArray rankedRestaurants = rankRestaurants(restaurants, preferences);

        // Add the ranked lists to the recommendations object
        recommendations.add("events", rankedEvents);
        recommendations.add("restaurants", rankedRestaurants);

        return recommendations;
    }

    private JsonArray rankEvents(JsonArray events, Preferences preferences) {
        List<JsonObject> eventList = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            JsonObject event = events.get(i).getAsJsonObject();
            int relevanceScore = calculateEventScore(event, preferences);
            event.addProperty("score", relevanceScore); // Add score to event for sorting
            eventList.add(event);
        }

        // Sort events by relevance score in descending order
        eventList.sort(Comparator.comparingInt(e -> -e.get("score").getAsInt()));

        JsonArray rankedEvents = new JsonArray();
        for (JsonObject event : eventList) {
            // event.remove("score"); // Remove the score before returning
            rankedEvents.add(event);
        }

        return rankedEvents;
    }

    private int calculateEventScore(JsonObject event, Preferences preferences) {
        int score = 0;

        // Check if event category matches user preferences
        JsonArray classifications = event.getAsJsonArray("classifications");
        if (classifications != null) {
            for (int i = 0; i < classifications.size(); i++) {
                JsonObject classification = classifications.get(i).getAsJsonObject();
                String genre = classification.getAsJsonObject("genre").get("name").getAsString();
                String subgenre = classification.getAsJsonObject("subGenre").get("name").getAsString();
                for (EventCategories category : preferences.getEventPreferences()){
                    if (category.toString().equalsIgnoreCase(genre)) {
                        score += 10;
                    }
                    if (category.toString().equalsIgnoreCase(subgenre)) {
                        score += 20; //more specific would mean higher match it think
                    }
                }


            }
        }



        return score;
    }

    private JsonArray rankRestaurants(JsonArray restaurants, Preferences preferences) {
        List<JsonObject> restaurantList = new ArrayList<>();
        for (int i = 0; i < restaurants.size(); i++) {
            JsonObject restaurant = restaurants.get(i).getAsJsonObject();
            int relevanceScore = calculateRestaurantScore(restaurant, preferences);
            restaurant.addProperty("score", relevanceScore); // Add score to restaurant for sorting
            restaurantList.add(restaurant);
        }

        // Sort restaurants by relevance score in descending order
        restaurantList.sort(Comparator.comparingInt(r -> -r.get("score").getAsInt()));

        JsonArray rankedRestaurants = new JsonArray();
        for (JsonObject restaurant : restaurantList) {
            // restaurant.remove("score"); // Remove the score before returning
            rankedRestaurants.add(restaurant);
        }

        return rankedRestaurants;
    }

    private int calculateRestaurantScore(JsonObject restaurant, Preferences preferences) {
        int score = 0;

        // Check if food categories match user preferences
        JsonArray categories = restaurant.getAsJsonArray("categories");
        if (categories != null) {
            for (int i = 0; i < categories.size(); i++) {
                String restaurantCategory = categories.get(i).getAsJsonObject().get("alias").toString();
                for(RestaurantCategories userCategory : preferences.getFoodPreferences()){
                    if (userCategory.toString().equals((restaurantCategory.replace("\"", "")))) {
                        score += 15;
                    }

                }
            }
        }

        // Adjust score based on budget
        int price = restaurant.get("price").getAsInt();
        if (price < preferences.getBudget()) {
            score += 5; // Boost for affordability
        }

        float restaurantRating = restaurant.get("rating").getAsFloat();
        if(restaurantRating >= 4.5 ){
            score += 10;
        }else if (restaurantRating >= 4.0){
            score += 5;
        }

        int reviewCount = restaurant.get("review_count").getAsInt();
        if(reviewCount >= 200){
            score += 7;
        } else if (reviewCount >= 100) {
            score += 3;
        }

        float distance = restaurant.get("distance").getAsFloat();
        if(distance < 1500){
            score += 10;
        }else if(distance < 4000){
            score += 5;
        }
        else if(distance < 6000){
            score += 2;
        }else if(distance < 8000){
            score += 1;
        }



        return score;
    }
}
