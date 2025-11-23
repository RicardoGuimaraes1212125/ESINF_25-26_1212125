package services;

import domain.*;
import utils.AVL;
import utils.StationCSVLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class StationIndexService {

    private AVL<Station> tzTree;
    private AVL<StationByLat> latTree;
    private AVL<StationByLon> lonTree;

    public void loadFromCSV(String path) {

        List<Station> stations = StationCSVLoader.loadValidStationsList(path);

        tzTree = new AVL<>();
        latTree = new AVL<>();
        lonTree = new AVL<>();

        //index for timeZoneGroup/country/name 
        stations.sort(
                Comparator.comparing(Station::getTimeZoneGroup, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Station::getCountry, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Station::getStationName, String.CASE_INSENSITIVE_ORDER)
        );
        for (Station s : stations) {
            tzTree.insert(s);
        }

        //index for latitude:
        for (Station s : stations) {
            latTree.insert(new StationByLat(s));
        }

        //index for longitude
        for (Station s : stations) {
            lonTree.insert(new StationByLon(s));
        }

    }

    //returns stations whose timeZoneGroup is in the range
    public Iterable<Station> getStationsByTZGroup(String tzGroup) {
        List<Station> result = new ArrayList<>();
        for (Station s : tzTree.inOrder()) {
            if (s.getTimeZoneGroup().equalsIgnoreCase(tzGroup))
                result.add(s);
        }
        return result;
    }

    //returns stations whose timeZoneGroup is in the range
    public Iterable<Station> getStationsByTZWindow(String low, String high) {
        List<Station> result = new ArrayList<>();
        for (Station s : tzTree.inOrder()) {
            if (s.getTimeZoneGroup().compareToIgnoreCase(low) >= 0 &&
                s.getTimeZoneGroup().compareToIgnoreCase(high) <= 0)
                result.add(s);
        }
        return result;
    }

    public Iterable<StationByLat> getStationsByLatitudeRange(double min, double max) {
        //normalize range so callers can provide bounds in any order
        double lo = Math.min(min, max);
        double hi = Math.max(min, max);
        List<StationByLat> result = new ArrayList<>();
        for (StationByLat s : latTree.inOrder()) {
            double lat = s.s.getLatitude();
            if (lat >= lo && lat <= hi) result.add(s);
        }
        return result;
    }

    public Iterable<StationByLon> getStationsByLongitudeRange(double min, double max) {
        //normalize range so callers can provide bounds in any order
        double lo = Math.min(min, max);
        double hi = Math.max(min, max);
        List<StationByLon> result = new ArrayList<>();
        for (StationByLon s : lonTree.inOrder()) {
            double lon = s.s.getLongitude();
            if (lon >= lo && lon <= hi) result.add(s);
        }
        return result;
    }

    //returns stations whose latitude is in [latMin, latMax] AND longitude is in [lonMin, lonMax].
    public Iterable<Station> getStationsByLatLonWindow(double latMin, double latMax, double lonMin, double lonMax) {
        double latLo = Math.min(latMin, latMax);
        double latHi = Math.max(latMin, latMax);
        double lonLo = Math.min(lonMin, lonMax);
        double lonHi = Math.max(lonMin, lonMax);

        List<Station> result = new ArrayList<>();
        for (StationByLat s : latTree.inOrder()) {
            double lat = s.s.getLatitude();
            if (lat < latLo || lat > latHi) continue;
            double lon = s.s.getLongitude();
            if (lon >= lonLo && lon <= lonHi) result.add(s.s);
        }
        return result;
    }

    public AVL<Station> getTzTree() { return tzTree; }
    public AVL<StationByLat> getLatTree() { return latTree; }
    public AVL<StationByLon> getLonTree() { return lonTree; }

     // Returns the distinct time zone groups present in the index
    public Iterable<String> getAllTimeZoneGroups() {
        Set<String> groups = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (tzTree == null) return groups;
        for (Station s : tzTree.inOrder()) {
            if (s.getTimeZoneGroup() != null && !s.getTimeZoneGroup().isBlank())
                groups.add(s.getTimeZoneGroup());
        }
        return groups;
    }
}
