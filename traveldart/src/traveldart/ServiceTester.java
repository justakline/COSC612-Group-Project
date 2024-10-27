package traveldart;

import com.google.gson.JsonArray;

public class ServiceTester {

    public static void main(String[] args) {
        Service service = new Service();
        JsonArray request1;
        JsonArray request2;
        try{
            request1 = service.getTicketMasterEvents("Baltimore");
            request2 = service.getTicketMasterEvents("Philadelphia", "Music");
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Problem! you might not have the api key");
            request1 = new JsonArray();
            request2 = new JsonArray();
        }

        System.out.println("Request 1: ");
        System.out.println(Service.formatJson(request1.toString()));
        System.out.println("Request 2: ");
        System.out.println(Service.formatJson(request2.toString()));
    }
}
