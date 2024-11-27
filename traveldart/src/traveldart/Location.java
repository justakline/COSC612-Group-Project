package traveldart;

public class Location {

    private String street;
    private String city;
    private String state;
    private String zipcode;

    public Location (String fullAddress) {
        String[] parsedAddress = fullAddress.split(",");
        street = parsedAddress[0].trim();
        city = parsedAddress[1].trim();
        state = parsedAddress[2].trim();
        zipcode = parsedAddress[3].trim();

    }
    public Location (String street, String city, String state, String zipcode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s", street, city, state, zipcode);
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipcode() {
        return zipcode;
    }
}
