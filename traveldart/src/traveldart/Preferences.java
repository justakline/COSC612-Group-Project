package traveldart;
import java.util.List;

public class Preferences {

    private int preferenceId;
    private int userId;
    // Budget will be 1-4, corresponding with how yelp handles budget
    private int budget;
    private List<RestaurantCategories> foodPreferences;
    private List<EventCategories> eventPreferences;
    private Location location;
    private int radiusMiles;


    public Preferences(List<RestaurantCategories> foodPreferences, List<EventCategories> eventPreferences, int budget, Location location, int radius) {
      
        this.foodPreferences = foodPreferences;
        this.eventPreferences = eventPreferences;
        this.budget = budget;
        this.location = location;
        this.radiusMiles = radius;
    }
    
    public Preferences(int preferenceId, int userId, List<RestaurantCategories> foodPreferences,
                       List<EventCategories> eventPreferences, int budget, Location location, int radius) {
        this.preferenceId = preferenceId;
        this.userId = userId;
        this.foodPreferences = foodPreferences;
        this.eventPreferences = eventPreferences;
        this.budget = budget;
        this.location = location;
        this.radiusMiles = radius;
    }

    // Getter and Setter methods
    public int getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(int preferenceId) {
        this.preferenceId = preferenceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<RestaurantCategories> getFoodPreferences() {
        return foodPreferences;
    }

    public void setFoodPreferences(List<RestaurantCategories> foodPreferences) {
        this.foodPreferences = foodPreferences;
    }

    public List<EventCategories> getEventPreferences() {
        return eventPreferences;
    }

    public void setMusicPreferences(List<EventCategories> eventPreferences) {
        this.eventPreferences = eventPreferences;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void updatePreferences(int preferenceId, int userId, List<RestaurantCategories> foodPreferences,
                                  List<EventCategories> eventPreferences, int budget, Location location) {
        this.preferenceId = preferenceId;
        this.userId = userId;
        this.foodPreferences = foodPreferences;
        this.eventPreferences = eventPreferences;
        this.budget = budget;
        this.location = location;
    }

    public int getRadiusMiles() {
        return radiusMiles;
    }

    public void setRadiusMiles(int radiusMiles) {
        this.radiusMiles = radiusMiles;
    }
}