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

    public Service() {
        this.client = new OkHttpClient();
    }


    public JsonArray getTicketMasterEvents(String city, String classification) throws IOException {
        String addedParams = "&city=[" +city +"]"+  "&classificationName=[" + classification+"]";
        Request request = new Request.Builder()
                .url(TICKEMASTER_API_URL + "/events.json?size=10" + addedParams+ "&apikey=" +TICKETMASTER_API_KEY )
                .build();

        Response response = client.newCall(request).execute();
        return processTicketMasterResponse(response);

    }
    public JsonArray getTicketMasterEvents(String city) throws IOException {
        String addedParams = "&city=[" +city + "]";
        Request request = new Request.Builder()
                .url(TICKEMASTER_API_URL + "/events.json?size=10" +addedParams + "&apikey=" +TICKETMASTER_API_KEY )
                .build();

        Response response = client.newCall(request).execute();
        return processTicketMasterResponse(response);
    }

    private JsonArray processTicketMasterResponse(Response response) throws IOException {
        String responseBody = parseResponse(response);
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonArray array = jsonObject.getAsJsonObject("_embedded").getAsJsonArray("events");

        JsonArray events = new JsonArray();
        for (JsonElement event: array){
            JsonObject o = event.getAsJsonObject();

            JsonObject eventProcessed = new JsonObject();
            eventProcessed.add("name", o.get("name"));
            eventProcessed.add("dates", o.get("dates"));
            eventProcessed.add("classifications", o.get("classifications"));
            eventProcessed.add("info", o.get("info"));
            // System.out.println(f(o.toString()));
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

}
