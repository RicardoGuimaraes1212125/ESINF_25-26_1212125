package domain;

public class RailNode {

    private final String id;
    private final String name;
    private final double lat;
    private final double lon;
    private final double x;
    private final double y;

    public RailNode(String id, String name,
                    double lat, double lon,
                    double x, double y) {

        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.x = x;
        this.y = y;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public double getX() { return x; }
    public double getY() { return y; }

    public String vertexKey() {
        return id + " | " + name;
    }

    @Override
    public String toString() {
        return id + " | " + name;
    }
}
