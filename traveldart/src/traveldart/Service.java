package traveldart;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import okhttp3.*;
//
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Service {

    private static final String TICKETMASTER_API_KEY = "";
    private static final String TICKEMASTER_API_URL = "https://app.ticketmaster.com/discovery/v2";
    private static final String OPENAI_API_KEY = "";
    private static final String OPEN_AI_URL = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client;
    private final int requestAmount = 10;
    private final int openAIInputOutputMax = 4; //requests can only handle this many events we give them at once


    // These are the most popular categories I could find that were compatible with the API
    public enum Categories {
        COMEDY("Comedy"), CONCERTS("Concerts"), ROCK("Rock"), POP("Pop"),
        HIP_HOP_RAP("Hip-Hop/Rap"),  COUNTRY("Country"), CLASSICAL("Classical"),
        THEATRE("Theatre"), HOLIDAY_EVENTS("Holiday Events"), FAIRS_FESTIVALS("Fairs & Festivals"),
        FAMILY("Family"),  FOOTBALL("Football"), MOTORSPORTS_RACING("Motorsports/Racing"),
        BASKETBALL("Basketball"), SOCCER("Soccer"), BASEBALL("Baseball"),
        OPERA("Opera"), DANCE("Dance"),MUSIC("Music"), SPORTS("Sports") ;

        private final String genreName;

        Categories(String genreName) {
            this.genreName = genreName;
        }

        @Override
        public String toString() {
            return genreName;
        }
    }


    public Service() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS) // Connection timeout
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)    // Read timeout
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)   // Write timeout
                .build();
    }

    // Pass in a json object with the list of events and preferences, it can only handle 3 events per payload
    public JsonArray createRecommendations(JsonArray events, JsonArray prefernces) throws IOException {

        JsonArray recommendations = new JsonArray();
        if(events.size() <= openAIInputOutputMax){
            return events;
        }

        // OpenAI can only handle 3 recs, so I am breaking it down into 3 at a time
        for (int i =0; i < events.size(); i+= openAIInputOutputMax){
            JsonArray payload = new JsonArray();

            // Create the payload
            for (int j = i; j < i + openAIInputOutputMax; j ++){
                if(j < events.size()){
                    payload.add(events.get(j));
                }
            }

            System.out.println("the new payload was :\n" +payload);
            //Launch the payload
            // Convert JSON object to string
            String requestBody = formatOpenAiApiBody(payload, prefernces);

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

            // If something happens lets just pretend like it didnt

            try{
                Response response = client.newCall(request).execute();
                String parsedResponse = parseResponse(response);
                recommendations.add( formatOpenAiOutput(parsedResponse));
            } catch (Exception e){
                System.out.println("\n\n There was an error, call had a response of :");
                System.out.println("Error was " );
                e.printStackTrace();

            }
        }

        JsonArray finalRecs = new JsonArray();
        for(JsonElement rec: recommendations){
            try{
                finalRecs.add(rec.getAsJsonArray().get(0));
            }catch (Exception e){
                System.out.println("Not an array");
            }
        }

        return finalRecs;
    }

    // We will request
    public String formatOpenAiApiBody(JsonArray data, JsonArray preferences){
        String template = """
    From now on, respond only with the best recommendations from any input data that I give to you.
    
    Preferences are: """ + preferences.toString() + """
    
    The events are:
    """ + data.toString() + """
    
    Your response should be a single string formatted as a JSON array, containing individual JSON objects for each recommendation. Follow this format strictly:

    [
        {
            "name": " ",
            "dates": {
                "start": {
                    "localDate": " ",
                    "localTime": " ",
                    "dateTime": " ",
                    "dateTBD": " ",
                    "dateTBA": " ",
                    "timeTBA": " ",
                    "noSpecificTime": " "
                },
                "timezone": " ",
                "status": {
                    "code": " "
                },
                "spanMultipleDays": " "
            },
            "classifications": [
                {
                    "primary": " ",
                    "segment": {"id": " ", "name": " "},
                    "genre": {"id": " ", "name": " "},
                    "subGenre": {"id": " ", "name": " "},
                    "type": {"id": " ", "name": " "},
                    "subType": {"id": " ", "name": " "},
                    "family": " "
                }
            ],
            "url": " ",
            "location": {
                "address": " ",
                "city": " ",
                "state": " ",
                "postalCode": " ",
                "country": " "
            }
        }
    ]

    - Recommend at most """ + openAIInputOutputMax + """ 
    events. If fewer recommendations are available, include as many as possible. TRY YOUR HARDEST TO GET MORE THAN 1.
    - Do not include any special characters like \\t, \\n, or extra whitespace. The response should be a single-line string in valid JSON format.
    - Populate the fields in each JSON object directly from the recommendation data without adding any extra characters or explanations.
    - The JSON array should only contain the structured recommendations with no additional text.

    """;
        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("role", "user");
        messageObject.addProperty("content", template);

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(messageObject);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("model", "gpt-4o-mini"); // Change to "gpt-3.5-turbo" if needed
        jsonObject.add("messages", messagesArray);
        return jsonObject.toString();
    }

    public JsonArray formatOpenAiOutput(String response) throws IOException {
        JsonObject jsonArray = JsonParser.parseString(response).getAsJsonObject();
        JsonArray choices = jsonArray.get("choices").getAsJsonArray();
        JsonObject content = choices.get(0).getAsJsonObject().getAsJsonObject("message");
        String n = content.toString();

        // Formatting of the message from openai made me do this, please touch nothing
        n = n.replace("\\t", "").replace("\\n", "").replace("\\", "");
        n = n.substring(n.indexOf("["), n.lastIndexOf("\",")) ;
        // System.out.println("n is");
        // System.out.println(formatJson(n));
        return JsonParser.parseString(n).getAsJsonArray();
    }

    // dateTime needs to be in format YEAR-MONTH-DAYTHR:MIN:SECZ
    // dateTime example format->      2024-11-22T15:01:0Z
    // MUST BE HAVE THE SAME NUMBER OF DIGITS FOR EACH ONE, YEAR NEEDS 4 EVERYTHING ELSE NEEDS 2
    public JsonArray getTicketMasterEvents(String city, String startDateTime, String endDateTime, Categories classification) throws IOException {
        String addedParams = "&city=[" +city +"]"+  "&startDateTime=" + startDateTime + "&endDateTime=" + endDateTime +"&classification=" + classification.toString();
        Request request = new Request.Builder()
                .url(TICKEMASTER_API_URL + "/events.json?size="+requestAmount + addedParams+ "&apikey=" +TICKETMASTER_API_KEY )
                .build();

        Response response = client.newCall(request).execute();
        return processTicketMasterResponse(response);

    }
    public JsonArray getTicketMasterEvents(String city, String startDateTime, String endDateTime) throws IOException {
        String addedParams = "&city=[" +city +"]"+  "&startDateTime=" + startDateTime + "&endDateTime=" + endDateTime;
        Request request = new Request.Builder()
                .url(TICKEMASTER_API_URL + "/events.json?size="+requestAmount + addedParams+ "&apikey=" +TICKETMASTER_API_KEY )
                .build();

        Response response = client.newCall(request).execute();
        return processTicketMasterResponse(response);

    }

    public JsonArray getTicketMasterEvents(String city, Categories classification) throws IOException {
        String addedParams = "&city=[" +city +"]"+  "&classificationName=[" + classification.toString()+"]";
        Request request = new Request.Builder()
                .url(TICKEMASTER_API_URL + "/events.json?size="+requestAmount + addedParams+ "&apikey=" +TICKETMASTER_API_KEY )
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
