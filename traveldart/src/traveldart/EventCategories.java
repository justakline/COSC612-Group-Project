package traveldart;

// The strings are directly useable with TicketMaster so we must use them
public enum EventCategories {
    COMEDY("Comedy"), CONCERTS("Concerts"), ROCK("Rock"), POP("Pop"),
    HIP_HOP_RAP("Hip-Hop/Rap"),  COUNTRY("Country"), CLASSICAL("Classical"),
    THEATRE("Theatre"), HOLIDAY_EVENTS("Holiday Events"), FAIRS_FESTIVALS("Fairs & Festivals"),
    FAMILY("Family"),  FOOTBALL("Football"), MOTORSPORTS_RACING("Motorsports/Racing"),
    BASKETBALL("Basketball"), SOCCER("Soccer"), BASEBALL("Baseball"),
    OPERA("Opera"), DANCE("Dance"),MUSIC("Music"), SPORTS("Sports") ;

    private final String genreName;

    EventCategories(String genreName) {
        this.genreName = genreName;
    }

    @Override
    public String toString() {
        return genreName;
    }
}
