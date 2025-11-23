package controllers;

import domain.Station;
import org.junit.Before;
import org.junit.Test;
import services.StationIndexService;

import static org.junit.Assert.*;

public class StationIndexControllerTest {

    private StationIndexService service;
    private StationIndexController controller;

    @Before
    public void setup() {
        service = new StationIndexService();
        controller = new StationIndexController(service);

        controller.loadCSV("src/test/resources/empty.csv");
    }

    @Test
    public void testQueryTZDoesNotCrash() {
        Iterable<Station> res = controller.queryByTZ("WET/GMT");
        assertNotNull(res);
    }

    @Test
    public void testQueryTZWindowDoesNotCrash() {
        Iterable<Station> res = controller.queryByTZWindow("A", "Z");
        assertNotNull(res);
    }

    @Test
    public void testQueryLatDoesNotCrash() {
        assertNotNull(controller.queryByLat(-10, 10));
    }

    @Test
    public void testQueryLonDoesNotCrash() {
        assertNotNull(controller.queryByLon(-10, 10));
    }

    @Test
    public void testGetTimeZoneGroupsDoesNotCrash() {
        assertNotNull(controller.getTimeZoneGroups());
    }
}
