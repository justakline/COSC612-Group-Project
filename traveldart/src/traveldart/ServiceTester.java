package traveldart;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLOutput;

public class ServiceTester {

    public static void main(String[] args) {
        Service service = Service.getInstance();
        JsonArray request1;
        JsonArray request2;
        JsonArray request3;
        JsonArray request4;
        JsonArray request5;
        service.doSomething();


        try{
          // service.test();
          //   request1 = service.getTicketMasterEvents("Baltimore", Service.Categories.FAMILY);
          //   request2 = service.getTicketMasterEvents("Philadelphia", Service.Categories.MUSIC);
          //   request3 = service.getTicketMasterEvents("Baltimore", "2024-10-03T00:00:10Z", "2024-11-22T00:00:00Z");
//            request4 = service.getTicketMasterEvents("Baltimore", "2024-10-03T00:00:10Z", "2024-11-22T00:00:00Z", Service.Categories.BASKETBALL);
//            request5 = service.createRecommendationsOpenAi(request4, new JsonArray());
            // request5 = service.createRecommendations(request4, new JsonArray());
        }catch (Exception e){
            e.printStackTrace();
        //     System.err.println("Problem! you might not have the api key");
            request1 = new JsonArray();
            request2 = new JsonArray();
            request3 = new JsonArray();
            request4 = new JsonArray();
            request5 = new JsonArray();
        }
        //
        // System.out.println("Request 1: ");
        // System.out.println(Service.formatJson(request1.toString()));
        // System.out.println("Request 2: ");
        // System.out.println(Service.formatJson(request2.toString()));//
        // System.out.println("Request 3: ");
        // System.out.println(Service.formatJson(request3.toString()));
        // System.out.println("Request 4: ");
        // System.out.println(Service.formatJson(request4.toString()));
//        System.out.println("Request 5: ");
//        System.out.println(Service.formatJson(request5.toString()));
    }
}
