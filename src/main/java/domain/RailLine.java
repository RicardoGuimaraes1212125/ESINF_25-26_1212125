package domain;

public class RailLine {

    private final String fromStationId;
    private final String toStationId;
    private final double distance;
    private final int capacity;
    private final double cost;

    public RailLine(String fromStationId,
                    String toStationId,
                    double distance,
                    int capacity,
                    double cost) {

        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        this.distance = distance;
        this.capacity = capacity;
        this.cost = cost;
    }

    public String getFromStationId() {
        return fromStationId;
    }

    public String getToStationId() {
        return toStationId;
    }

    public double getDistance() {
        return distance;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getCost() {
        return cost;
    }
}
