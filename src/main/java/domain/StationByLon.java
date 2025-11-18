package domain;

public class StationByLon implements Comparable<StationByLon> {
    public final Station s;

    public StationByLon(Station s) {
        this.s = s;
    }

    @Override
    public int compareTo(StationByLon o) {
        int c = Double.compare(s.getLongitude(), o.s.getLongitude());
        if (c == 0) c = Double.compare(s.getLatitude(), o.s.getLatitude());
        if (c == 0) c = s.getStationName().compareToIgnoreCase(o.s.getStationName());
        return c;
    }

    @Override
    public String toString() {
        return s.toString();
    }
}

