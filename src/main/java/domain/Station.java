package domain;

public class Station implements Comparable<Station> {
    private final String stationName;
    private final String country;
    private final String timeZone;
    private final String timeZoneGroup;
    private final double latitude;
    private final double longitude;
    private final boolean isCity;
    private final boolean isMainStation;
    private final boolean isAirport;

    public Station(String stationName, String country, String timeZone, String timeZoneGroup,
                   double latitude, double longitude, boolean isCity, boolean isMainStation, boolean isAirport) {
        if (stationName == null || stationName.isBlank()) throw new IllegalArgumentException("Invalid station name");
        if (country == null || country.isBlank()) throw new IllegalArgumentException("Invalid country");
        if (timeZoneGroup == null || timeZoneGroup.isBlank()) throw new IllegalArgumentException("Invalid time zone group");
        if (latitude < -90 || latitude > 90) throw new IllegalArgumentException("Invalid latitude");
        if (longitude < -180 || longitude > 180) throw new IllegalArgumentException("Invalid longitude");

        this.stationName = stationName;
        this.country = country;
        this.timeZone = timeZone;
        this.timeZoneGroup = timeZoneGroup;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isCity = isCity;
        this.isMainStation = isMainStation;
        this.isAirport = isAirport;
    }

    // Getters
    public String getStationName() {
        return stationName;
    }

    public String getCountry() {
        return country;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getTimeZoneGroup() {
        return timeZoneGroup;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isCity() {
        return isCity;
    }

    public boolean isMainStation() {
        return isMainStation;
    }

    public boolean isAirport() {
        return isAirport;
    }

    @Override
    public int compareTo(Station other) {
        int cmp = this.timeZoneGroup.compareToIgnoreCase(other.timeZoneGroup);
        if (cmp == 0) cmp = this.country.compareToIgnoreCase(other.country);
        if (cmp == 0) cmp = this.stationName.compareToIgnoreCase(other.stationName);
        return cmp;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) [%s, %s] - %.5f, %.5f",
                stationName, country, timeZoneGroup, timeZone, latitude, longitude);
    }
}

