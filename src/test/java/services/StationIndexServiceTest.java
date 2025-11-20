package services;

import domain.Station;
import domain.StationByLat;
import domain.StationByLon;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

    @Test
    public void testQueryByTimeZoneWindow() {

        Iterable<Station> list = service.getStationsByTZWindow("CET", "EET");

        int count = 0;
        for (Station s : list)
            count++;

        assertTrue("Espera-se muitas estações entre CET e EET", count > 9000);
    }

    @Test
    public void testQueryByLatitudeRangeLisbon() {

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

    @Test
    public void testQueryByLongitudeRangeLisbon() {

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

    private File smallCsv;
    private StationIndexService smallService;

    private void buildSmallCSV() throws IOException {

        smallCsv = File.createTempFile("stations_small", ".csv");

        FileWriter fw = new FileWriter(smallCsv);

        fw.write(
                "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport\n" +
                "FR,(\"Europe/Paris\"),CET,Chateau-Arnoux-St-Auban,44.08179,6.001625,True,False,False\n" +
                "FR,(\"Europe/Paris\"),CET,Chateau-Arnoux-St-Auban,44.0615651,5.9973734,False,True,False\n" +
                "FR,(\"Europe/Paris\"),CET,Chateau-Arnoux Mairie,44.063863,6.011248,False,False,False\n" +
                "FR,(\"Europe/Paris\"),CET,Digne-les-Bains,44.35,6.35,True,False,False\n" +
                "FR,(\"Europe/Paris\"),CET,Digne-les-Bains,44.08871013398,6.2229824066,False,True,False\n" +
                "FR,(\"Europe/Paris\"),CET,La Crau,43.1449352,6.0687663,False,False,False\n" +
                "FR,(\"Europe/Paris\"),CET,Aire-sur-l'Adour,43.703854,-0.258269,True,False,False\n" +
                "FR,(\"Europe/Paris\"),CET,Cagnes-sur-Mer,43.65813,7.14864,False,True,False\n" +
                "FR,(\"Europe/Paris\"),CET,Menton,43.776486,7.504349,True,False,False\n" +
                "FR,(\"Europe/Paris\"),CET,Menton,43.774444,7.4933,False,True,False\n" +
                "FR,(\"Europe/Paris\"),CET,Menton-Garavan,43.785202,7.517297,False,False,False\n" +
                "FR,(\"Europe/Paris\"),CET,Vievola,44.1125561,7.5627976,False,True,False\n" +
                "FR,(\"Europe/Paris\"),CET,Mandelieu-la-Napoule,43.523903,6.941313,False,False,False\n" +
                "GB,(\"Europe/London\"),WET/GMT,Heath High Level,51.516518,-3.181737,False,False,False\n"
        );

        fw.close();

        smallService = new StationIndexService();
        smallService.loadFromCSV(smallCsv.getAbsolutePath());
    }

    @Test
    public void testSmallCSV_CountValidStations() throws IOException {

        buildSmallCSV();

        assertEquals(11, smallService.getTzTree().size());
    }

    @Test
    public void testSmallCSV_TZGroupCET() throws IOException {

        buildSmallCSV();

        Iterable<Station> cet = smallService.getStationsByTZGroup("CET");

        int count = 0;
        for (Station s : cet)
            count++;

        assertEquals(10, count);
    }

    @Test
    public void testSmallCSV_TZGroupWET() throws IOException {

        buildSmallCSV();

        Iterable<Station> wet = smallService.getStationsByTZGroup("WET/GMT");

        int count = 0;
        boolean foundHeath = false;

        for (Station s : wet) {
            count++;
            if (s.getStationName().contains("Heath High Level"))
                foundHeath = true;
        }

        assertEquals(1, count);
        assertTrue(foundHeath);
    }

    @Test
    public void testSmallCSV_LatitudeRange() throws IOException {

        buildSmallCSV();

        Iterable<StationByLat> list =
                smallService.getStationsByLatitudeRange(44.00, 44.15);

        int count = 0;
        boolean found = false;

        for (StationByLat s : list) {
            count++;
            if (s.s.getStationName().equals("Chateau-Arnoux-St-Auban"))
                found = true;
        }

        assertTrue(found);
        assertTrue(count >= 2);
    }

    @Test
    public void testSmallCSV_LongitudeRange() throws IOException {

        buildSmallCSV();

        Iterable<StationByLon> list =
                smallService.getStationsByLongitudeRange(-0.50, -0.40);

        boolean found = false;

        for (StationByLon s : list) {
            if (s.s.getStationName().contains("Heathrow"))
                found = true;
        }

        assertTrue(found);
    }
}
