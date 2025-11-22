package controllers;

import services.StationIndexService;
import domain.*;

public class StationIndexController {

    private final StationIndexService service;

    public StationIndexController(StationIndexService service) {
        this.service = service;
    }

    public void loadCSV(String path) {
        service.loadFromCSV(path);
    }

    public Iterable<Station> queryByTZ(String tz) {
        return service.getStationsByTZGroup(tz);
    }

    public Iterable<Station> queryByTZWindow(String low, String high) {
        return service.getStationsByTZWindow(low, high);
    }

    public Iterable<StationByLat> queryByLat(double min, double max) {
        return service.getStationsByLatitudeRange(min, max);
    }

    public Iterable<StationByLon> queryByLon(double min, double max) {
        return service.getStationsByLongitudeRange(min, max);
    }
    
    public Iterable<String> getTimeZoneGroups() {
        return service.getAllTimeZoneGroups();
    }

    public Iterable<Station> queryByLatLonWindow(double latMin, double latMax, double lonMin, double lonMax) {
        return service.getStationsByLatLonWindow(latMin, latMax, lonMin, lonMax);
    }

    public StationIndexService getService() {
        return service;
    }

}
