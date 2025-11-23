package services;

import domain.Station;
import org.junit.*;
import java.io.*;
import java.util.List;
import static org.junit.Assert.*;

public class KDTreeServiceTest {

    private final String SMALL_CSV = "src/test/resources/train_stations_small.csv";
    private final String REAL_CSV  = "src/main/resources/data/train_stations_europe.csv";

    private StationIndexService indexServiceSmall;
    private KDTreeService kdServiceSmall;

    private StationIndexService indexServiceReal;
    private KDTreeService kdServiceReal;

    @Before
    public void setupSmall() throws Exception {
        // create small CSV programmatically or ensure file exists in test resources
        indexServiceSmall = new StationIndexService();
        indexServiceSmall.loadFromCSV(SMALL_CSV); // uses loadValidStationsList internally
        kdServiceSmall = new KDTreeService();
        kdServiceSmall.buildFromIndex(indexServiceSmall);
    }

    @Before
    public void setupReal() {
        File f = new File(REAL_CSV);
        assertTrue("Real CSV must exist for integration tests", f.exists());
        indexServiceReal = new StationIndexService();
        indexServiceReal.loadFromCSV(REAL_CSV);
        kdServiceReal = new KDTreeService();
        kdServiceReal.buildFromIndex(indexServiceReal);
    }

    @Test
    public void testSmallKDTreeSizeMatchesIndex() {
        int tzSize = indexServiceSmall.getTzTree().size();
        assertEquals("KD tree total stations should equal index count",
                tzSize, kdServiceSmall.getSize());
    }

    @Test
    public void testRealKDTreeBasicStats() {
        int size = kdServiceReal.getSize();
        long height = kdServiceReal.getHeight();
        assertTrue(size > 60000);
        assertTrue(height >= 10 && height <= 20);
    }

    @Test
    public void testRangeSearchFindsKnownStation() {
        Iterable<Station> res = kdServiceReal.getKDTree().rangeSearch(38.6, 38.8, -9.2, -9.0);
        boolean found = false;
        for (Station s : res) {
            if ("Lisboa Oriente".equalsIgnoreCase(s.getStationName())) {
                found = true;
                break;
            }
        }
        assertTrue("Lisboa Oriente should be within lat/long box", found);
    }

    @Test
    public void testNearestReturnsReasonableResult() {
        Station near = kdServiceReal.getKDTree().nearest(38.713, -9.13);
        assertNotNull(near);
        //Lisbon stations expected
        String name = near.getStationName().toLowerCase();
        assertTrue(name.contains("lisbo") || name.contains("lisbon"));
    }
}

