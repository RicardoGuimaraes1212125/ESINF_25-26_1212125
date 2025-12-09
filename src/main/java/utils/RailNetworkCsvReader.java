package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import domain.StationConnection;

public class RailNetworkCsvReader {

    public List<StationConnection> readCsv(String filePath) throws IOException {
        List<StationConnection> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // primeira linha = header
            if (line == null) return list;

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";", -1); // preserva colunas vazias

                if (cols.length < 7) continue; // segurança

                String geoShape   = cols[0];
                String fromId     = cols[1];
                String fromName   = cols[2];
                String toId       = cols[3];
                String toName     = cols[4];
                double lengthKm   = parseDoubleSafe(cols[5]);
                String geoPoint   = cols[6];

                StationConnection c = new StationConnection(
                        geoShape, fromId, fromName, toId, toName, lengthKm, geoPoint
                );

                list.add(c);
            }
        }

        return list;
    }

    private double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.replace(",", "."));
        } catch (Exception e) {
            return 0;
        }
    }
}

