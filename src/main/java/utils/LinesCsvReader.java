package utils;

import domain.RailLine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LinesCsvReader {

    public List<RailLine> readLines(String filePath) throws IOException {

        List<RailLine> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = br.readLine(); // header
            if (line == null) return lines;

            while ((line = br.readLine()) != null) {

                String[] cols = line.split(",", -1);
                if (cols.length < 5) continue;

                String fromId = cols[0].trim();
                String toId   = cols[1].trim();
                double dist   = parseDoubleSafe(cols[2]);
                int capacity  = parseIntSafe(cols[3]);
                double cost   = parseDoubleSafe(cols[4]);

                lines.add(new RailLine(fromId, toId, dist, capacity, cost));
            }
        }

        return lines;
    }

    private double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.replace(",", "."));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
