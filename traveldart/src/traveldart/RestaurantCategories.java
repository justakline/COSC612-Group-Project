package traveldart;
// The strings are directly useable with Yelp so we must use them
public enum RestaurantCategories {
    // Top Food Categories and Types
    TRADAMERICAN("tradamerican"),
    ITALIAN("italian"),
    MEXICAN("mexican"),
    ASIANFUSION("asianfusion"),
    JAPANESE("japanese"),
    CHINESE("chinese"),
    KOREAN("korean"),
    VIETNAMESE("vietnamese"),
    THAI("thai"),
    FRENCH("french"),
    GREEK("greek"),
    MEDITERRANEAN("mediterranean"),
    INDPACK("indpak"),
    VEGETARIAN("vegetarian"),
    VEGAN("vegan"),
    SEAFOOD("seafood"),
    BREAKFAST_BRUNCH("breakfast_brunch"),
    CAFES("cafes"),
    DESSERTS("desserts"),
    PIZZA("pizza"),
    COFFEE("coffee"),
    TEA("tea");

    private final String alias;

    // Constructor
    RestaurantCategories(String alias) {
        this.alias = alias;
    }

    // Override toString to return the original string value
    @Override
    public String toString() {
        return alias;
    }
}