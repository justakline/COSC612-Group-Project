package traveldart;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jdk.jfr.Event;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

public class ServiceTester {

    public static void main(String[] args) {
        Service service = Service.getInstance();
        JsonArray request1;
        JsonArray request2;
        JsonArray request3;
        JsonArray request4;
        JsonArray request5;
        JsonObject request6;
        List<RestaurantCategories> rCategories = List.of(RestaurantCategories.TRADAMERICAN, RestaurantCategories.SEAFOOD);
        List<EventCategories> eCategories = List.of(EventCategories.BASKETBALL,EventCategories.BASEBALL );
        String sLocation = "7800 York Road, Towson, MD, 21252";
        Location location = new Location(sLocation);
        String currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString() + "Z";
        String endTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(50).toString() +"Z";

        Preferences preferences = new Preferences(0, 0,rCategories, eCategories, 2,location , 10);

        try{
            // service.test();
            //   request1 = service.getTicketMasterEvents("Baltimore", Service.Categories.FAMILY);
            //   request2 = service.getTicketMasterEvents("Philadelphia", "Music");
            //   request3 = service.getTicketMasterEvents("Baltimore", "2024-10-03T00:00:10Z", "2024-11-22T00:00:00Z");
                request4 = service.getTicketMasterEvents(location.getCity(), currentTime.toString(), endTime.toString(), preferences.getEventPreferences());
            // service.testOpenAI();
            request5 = service.getYelpRestaurants(location.toString(), preferences.getFoodPreferences(),preferences.getBudget(), preferences.getRadiusMiles() );
            request6 = service.createRecommendation(request4, request5, preferences);

        }catch (Exception e){
            e.printStackTrace();
            //     System.err.println("Problem! you might not have the api key");
            request1 = new JsonArray();
            request2 = new JsonArray();
            request3 = new JsonArray();
            request4 = new JsonArray();
            request5 = new JsonArray();
            request6 = new JsonObject();
        }
        //
        // System.out.println("Request 1: ");
        // System.out.println(Service.formatJson(request1.toString()));
        // System.out.println("Request 2: ");
        // System.out.println(Service.formatJson(request2.toString()));//
        // System.out.println("Request 3: ");
        // System.out.println(Service.formatJson(request3.toString()));
        // System.out.println("Events: \n");
        // System.out.println("Request 4: ");
        // System.out.println(Service.formatJson(request4.toString()));
        // System.out.println("\n\n Restaurants: \n");
        // System.out.println("Request 5: ");
        // System.out.println(Service.formatJson(request5.toString()));
        System.out.println("\n\nRequest 6: ");
        System.out.println(Service.formatJson(request6.toString()));
    }
}
