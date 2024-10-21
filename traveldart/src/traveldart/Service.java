package traveldart;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
//
import java.io.IOException;

// NOTHING IS WORKING BUT HERE IS AN OUTLINE

public class Service {

    private static final String YELP_API_KEY = "our key";
    private static final String EVENTBRITE_API_KEY = "our key";

    private static final String YELP_API_URL = "https://api.yelp.com/v3";
    private static final String EVENTBRITE_API_URL = "https://www.eventbriteapi.com/v3/";

    private OkHttpClient client;

    public Service() {
        this.client = new OkHttpClient();
    }

    // Fetch restaurants based on location and categories from Yelp API
    public String getYelpRestaurantsTest() throws IOException {
        Request request = new Request.Builder()
                .url("https://api.yelp.com/v3/businesses/search?sort_by=best_match&limit=20")
                .get()
                .addHeader("accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return parseResponse(response);
    }

    public String getYelpRestaurants(String location, String category) throws IOException {
        Request request = new Request.Builder()
                .url(YELP_API_URL + "params for the api request")
                .addHeader("Authorization", "Bearer " + YELP_API_KEY)
                .build();

        Response response = client.newCall(request).execute();
        return parseResponse(response);
    }

    // Fetch events from Eventbrite based on category and location
    public String getEventbriteEvents(String location, String category) throws IOException {
        Request request = new Request.Builder()
                .url(EVENTBRITE_API_URL + "params for the api request")
                .addHeader("Authorization", "Bearer " + EVENTBRITE_API_KEY)
                .build();

        Response response = client.newCall(request).execute();

        return parseResponse(response);
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
}
