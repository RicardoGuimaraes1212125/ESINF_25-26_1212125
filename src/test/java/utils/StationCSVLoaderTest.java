package utils;

import domain.Station;
import org.junit.Test;
import java.io.File;
import java.util.List;
import static org.junit.Assert.*;

public class StationCSVLoaderTest {

    private final String REAL_CSV = "src/main/resources/data/train_stations_europe.csv";

    @Test
    public void testLoadValidStationsList() {

        File file = new File(REAL_CSV);
        assertTrue("CSV real não encontrado!", file.exists());

        List<Station> list = StationCSVLoader.loadValidStationsList(REAL_CSV);

        // valores reais do CSV original
        int expectedValid = 62142;

        assertEquals("Número de estações válidas diferente do esperado!",
                expectedValid, list.size());
    }


    @Test
    public void testNoInvalidStationsLoaded() {

        List<Station> list = StationCSVLoader.loadValidStationsList(REAL_CSV);

        boolean hasInvalid = false;

        for (Station s : list) {
            if (s.getStationName().trim().isEmpty()
                    || s.getCountry().trim().isEmpty()
                    || s.getTimeZoneGroup().trim().isEmpty()) {

                hasInvalid = true;
                break;
            }
        }

        assertFalse("Foram carregadas estações com campos obrigatórios vazios!", hasInvalid);
    }


    @Test
    public void testLatLonParsedCorrectly() {

        List<Station> list = StationCSVLoader.loadValidStationsList(REAL_CSV);

        // Procurar Lisboa Oriente (lat ~ 38.713, lon ~ -9.13)
        boolean found = false;

        for (Station s : list) {
            if (s.getStationName().equalsIgnoreCase("Lisboa Oriente")) {
                found = true;
                assertEquals(38.713, s.getLatitude(), 0.01);
                assertEquals(-9.13, s.getLongitude(), 0.05);
                break;
            }
        }

        assertTrue("Lisboa Oriente não encontrada no CSV!", found);
    }
}
