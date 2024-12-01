package traveldart;

import com.google.gson.JsonArray;

public class ServiceTester {

    public static void main(String[] args) {
        Service service = new Service();
        JsonArray request1;
        JsonArray request2;
        JsonArray request3;
        JsonArray request4;
        JsonArray request5;

        try{
            // service.test();
            //   request1 = service.getTicketMasterEvents("Baltimore", Service.Categories.FAMILY);
            //   request2 = service.getTicketMasterEvents("Philadelphia", "Music");
            //   request3 = service.getTicketMasterEvents("Baltimore", "2024-10-03T00:00:10Z", "2024-11-22T00:00:00Z");
            //     request4 = service.getTicketMasterEvents("Baltimore", "2024-10-03T00:00:10Z", "2024-11-22T00:00:00Z", Service.Categories.BASKETBALL);
            // service.testOpenAI();
            request5 = service.testYelp("Towson, MD", new RestaurantCategories[]{RestaurantCategories.TRADAMERICAN, RestaurantCategories.JAPANESE},new int[]{2,3}, 2 );

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
        System.out.println("Request 5: ");
        System.out.println(Service.formatJson(request5.toString()));
    }
}
