package traveldart;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// NOTHING IS WORKING BUT HERE IS AN OUTLINE

public class Service {

    private static final String TICKETMASTER_API_KEY = "our api key";
    private static final String TICKEMASTER_API_URL = "https://app.ticketmaster.com/discovery/v2";
    private OkHttpClient client;
    private final int requestAmount = 10;

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
        this.client = new OkHttpClient();
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
