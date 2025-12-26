package domain;

public class StationConnection {

    private String geoShape;
    private String stationFromId;
    private String stationFromName;
    private String stationToId;
    private String stationToName;
    private double lengthKm;
    private String geoPoint;

    public StationConnection(String geoShape,
                             String stationFromId,
                             String stationFromName,
                             String stationToId,
                             String stationToName,
                             double lengthKm,
                             String geoPoint) {

        this.geoShape = geoShape;
        this.stationFromId = stationFromId;
        this.stationFromName = stationFromName;
        this.stationToId = stationToId;
        this.stationToName = stationToName;
        this.lengthKm = lengthKm;
        this.geoPoint = geoPoint;
    }

    public String getStationFromName() {
        return stationFromName;
    }

    public String getStationToName() {
        return stationToName;
    }

    public double getLengthKm() {
        return lengthKm;
    }

    public String getStationFromId() {
        return stationFromId;
    }

    public String getStationToId() {
        return stationToId;
    }

    @Override
    public String toString() {
        return "From " + stationFromName +
               " → " + stationToName +
               " (" + lengthKm + " km)";
    }
}
