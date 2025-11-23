package controllers;

import domain.Station;
import domain.StationByLat;
import domain.StationByLon;
import org.junit.Before;
import org.junit.Test;
import services.KDTreeService;
import services.StationIndexService;

import static org.junit.Assert.*;

public class KDTreeControllerTest {

    private StationIndexService indexService;
    private KDTreeController controller;

    @Before
    public void setup() {
        indexService = new StationIndexService();

        Station s = new Station("A", "PT", "tz", "WET/GMT", 10, 10, false, false, false);
    
        indexService.loadFromCSV("src/test/resources/empty.csv"); 

        indexService.getLatTree().insert(new StationByLat(s));
        indexService.getLonTree().insert(new StationByLon(s));
        indexService.getTzTree().insert(s);

        controller = new KDTreeController(indexService);
        controller.buildKDTree();
    }

    @Test
    public void testKDTreeBuildsSuccessfully() {
        assertNotNull(controller.getTree());
    }

    @Test
    public void testRangeSearchReturnsIterable() {
        Iterable<Station> result = controller.rangeSearch(0, 20, 0, 20);
        assertNotNull(result);
    }

    @Test
    public void testNearestReturnsAStationOrNull() {
        Station nearest = controller.nearest(10, 10);
        assertNotNull(nearest);
    }

    @Test
    public void testControllerReturnsSize() {
        assertTrue(controller.getSize() >= 1);
    }
}

