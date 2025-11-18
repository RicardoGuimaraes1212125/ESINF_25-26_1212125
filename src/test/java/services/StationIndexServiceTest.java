package services;

import domain.Station;
import domain.StationByLat;
import domain.StationByLon;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import static org.junit.Assert.*;

public class StationIndexServiceTest {

    private final String REAL_CSV = "src/main/resources/data/train_stations_europe.csv";
    private StationIndexService service;

    @Before
    public void setup() {

        File file = new File(REAL_CSV);
        assertTrue("CSV real não encontrado!", file.exists());

        service = new StationIndexService();
        service.loadFromCSV(REAL_CSV);
    }

    // --------------------------------------------------------------------

    /* 
    @Test
    public void testIndexTreesAreBuilt() {

        assertNotNull(service.getTzTree());
        assertNotNull(service.getLatTree());
        assertNotNull(service.getLonTree());

        int tzSize = service.getTzTree().size();
        int latSize = service.getLatTree().size();
        int lonSize = service.getLonTree().size();

        assertEquals(tzSize, latSize);
        assertEquals(tzSize, lonSize);

        assertTrue("Tamanho da AVL deveria ser > 0", tzSize > 0);
    }
    */

    // --------------------------------------------------------------------

    @Test
    public void testQueryByTimeZoneGroupWET() {

        Iterable<Station> wet = service.getStationsByTZGroup("WET/GMT");

        int count = 0;
        boolean foundLisboa = false;

        for (Station s : wet) {
            count++;
            if (s.getStationName().equalsIgnoreCase("Lisboa Oriente"))
                foundLisboa = true;
        }

        assertTrue("Devem existir várias estações WET/GMT", count > 1000);
        assertTrue("Lisboa Oriente deveria estar no grupo WET/GMT", foundLisboa);
    }

    // --------------------------------------------------------------------

    @Test
    public void testQueryByTimeZoneWindow() {

        Iterable<Station> list = service.getStationsByTZWindow("CET", "EET");

        int count = 0;

        for (Station s : list)
            count++;

        assertTrue("Espera-se muitas estações entre CET e EET", count > 9000);
    }

    // --------------------------------------------------------------------

    @Test
    public void testQueryByLatitudeRangeLisbon() {

        // Lisboa latitude ~ 38.713
        Iterable<StationByLat> list = service.getStationsByLatitudeRange(38.6, 38.8);

        boolean found = false;

        for (StationByLat s : list) {
            if (s.s.getStationName().equalsIgnoreCase("Lisboa Oriente")) {
                found = true;
                break;
            }
        }

        assertTrue("Lisboa Oriente não apareceu na query por latitude!", found);
    }

    // --------------------------------------------------------------------

    @Test
    public void testQueryByLongitudeRangeLisbon() {

        // Longitude Lisboa ~ -9.13
        Iterable<StationByLon> list =
                service.getStationsByLongitudeRange(-9.2, -9.0);

        boolean found = false;

        for (StationByLon s : list) {
            if (s.s.getStationName().equalsIgnoreCase("Lisboa Oriente")) {
                found = true;
                break;
            }
        }

        assertTrue("Lisboa Oriente deveria aparecer na query por longitude!", found);
    }
}
