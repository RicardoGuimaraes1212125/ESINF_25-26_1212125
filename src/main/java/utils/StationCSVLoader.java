package utils;

import domain.Station;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class StationCSVLoader {

    public static AVL<Station> load(String path) {

        AVL<Station> tree = new AVL<>();
        List<Station> list = loadValidStationsList(path);  

        for (Station s : list)
            tree.insert(s);

        return tree;
    }

    public static List<Station> loadValidStationsList(String path) {

        List<Station> list = new ArrayList<>(64000); 

        int total = 0;
        int loaded = 0;
        int skippedMissingCoords = 0;
        int skippedInvalidRequired = 0;
        int skippedOutOfRange = 0;
        int skippedMalformed = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String header = br.readLine(); 
            String line;

            while ((line = br.readLine()) != null) {
                total++;

                try {
                    line = line.replace("\"", "").trim();

                    String[] parts = line.split(",(?![^()]*\\))");

                    if (parts.length < 9) {
                        skippedMalformed++;
                        continue;
                    }

                    String country = parts[0].trim();
                    String tzRaw = parts[1].trim();
                    String timeZone = cleanTimezone(tzRaw);
                    String timeZoneGroup = parts[2].trim();
                    String stationName = parts[3].trim();
                    String latStr = parts[4].trim();
                    String lonStr = parts[5].trim();

                    if (country.isEmpty() || stationName.isEmpty() || timeZoneGroup.isEmpty()) {
                        skippedInvalidRequired++;
                        continue;
                    }

                    if (latStr.isEmpty() || lonStr.isEmpty()) {
                        skippedMissingCoords++;
                        continue;
                    }

                    double latitude = Double.parseDouble(latStr);
                    double longitude = Double.parseDouble(lonStr);

                    if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
                        skippedOutOfRange++;
                        continue;
                    }

                    boolean isCity = Boolean.parseBoolean(parts[6].trim());
                    boolean isMain = Boolean.parseBoolean(parts[7].trim());
                    boolean isAirport = Boolean.parseBoolean(parts[8].trim());

                    Station s = new Station(
                            stationName,
                            country,
                            timeZone,
                            timeZoneGroup,
                            latitude,
                            longitude,
                            isCity,
                            isMain,
                            isAirport
                    );

                    list.add(s);
                    loaded++;

                } catch (NumberFormatException e) {
                    skippedMalformed++;
                } catch (Exception e) {
                    skippedMalformed++;
                }
            }

        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
        }

        System.out.println("—— Import Summary ——");
        System.out.println("Total lidas               : " + total);
        System.out.println("Carregadas                : " + loaded);
        System.out.println("Ignoradas (coords vazias) : " + skippedMissingCoords);
        System.out.println("Ignoradas (campos obrig.) : " + skippedInvalidRequired);
        System.out.println("Ignoradas (fora intervalo): " + skippedOutOfRange);
        System.out.println("Ignoradas (malformadas)   : " + skippedMalformed);
        System.out.println("------------------------");

        return list;
    }

    private static String cleanTimezone(String tz) {

        tz = tz.trim();

        if (tz.startsWith("(") && tz.endsWith(")")) {
            tz = tz.substring(1, tz.length() - 1).trim();
        }

        tz = tz.replace("'", "")
               .replace(",", "")
               .trim();

        return tz;
    }
}
