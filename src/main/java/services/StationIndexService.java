package services;

import domain.*;
import utils.AVL;
import utils.StationCSVLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StationIndexService {

    private AVL<Station> tzTree;
    private AVL<StationByLat> latTree;
    private AVL<StationByLon> lonTree;

    public void loadFromCSV(String path) {

        List<Station> stations = StationCSVLoader.loadValidStationsList(path);


        tzTree = new AVL<>();
        latTree = new AVL<>();
        lonTree = new AVL<>();

        // a) index for timeZoneGroup/country/name 
        stations.sort(
                Comparator.comparing(Station::getTimeZoneGroup, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Station::getCountry, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Station::getStationName, String.CASE_INSENSITIVE_ORDER)
        );
        for (Station s : stations) {
            tzTree.insert(s);
        }

        // b) index for latitude:
        for (Station s : stations) {
            latTree.insert(new StationByLat(s));
        }

        // c) index for longitude
        for (Station s : stations) {
            lonTree.insert(new StationByLon(s));
        }

    }

    public Iterable<Station> getStationsByTZGroup(String tzGroup) {
        List<Station> result = new ArrayList<>();
        for (Station s : tzTree.inOrder()) {
            if (s.getTimeZoneGroup().equalsIgnoreCase(tzGroup))
                result.add(s);
        }
        return result;
    }

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
        List<StationByLat> result = new ArrayList<>();
        for (StationByLat s : latTree.inOrder()) {
            if (s.s.getLatitude() >= min && s.s.getLatitude() <= max)
                result.add(s);
        }
        return result;
    }

    public Iterable<StationByLon> getStationsByLongitudeRange(double min, double max) {
        List<StationByLon> result = new ArrayList<>();
        for (StationByLon s : lonTree.inOrder()) {
            if (s.s.getLongitude() >= min && s.s.getLongitude() <= max)
                result.add(s);
        }
        return result;
    }

    public AVL<Station> getTzTree() { return tzTree; }
    public AVL<StationByLat> getLatTree() { return latTree; }
    public AVL<StationByLon> getLonTree() { return lonTree; }
}
