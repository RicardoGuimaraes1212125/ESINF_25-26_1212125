package utils;

import domain.RailNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StationsCsvReader {

    public Map<String, RailNode> readStations(String filePath) throws IOException {

        Map<String, RailNode> stations = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = br.readLine(); // header
            if (line == null) return stations;

            while ((line = br.readLine()) != null) {

                String[] cols = line.split(",", -1);
                if (cols.length < 6) continue;

                String id   = cols[0].trim();
                String name = cols[1].trim();

                double lat  = parseDouble(cols[2]);
                double lon  = parseDouble(cols[3]);
                double x    = parseDouble(cols[4]);
                double y    = parseDouble(cols[5]);

                RailNode node = new RailNode(id, name, lat, lon, x, y);
                stations.put(id, node);
            }
        }

        return stations;
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim().replace(",", "."));
        } catch (Exception e) {
            return 0.0;
        }
    }
}
