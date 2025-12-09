package ui;


import java.util.*;

import domain.StationConnection;
import utils.RailNetworkCsvReader;

public class RailNetworkUI {

    private final Scanner sc;

    public RailNetworkUI(Scanner sc) {
        this.sc = sc;
    }

    public void run() {

        boolean exit = false;

        while (!exit) {
            System.out.println("\n=========== RAILWAY NETWORK MENU ===========");
            System.out.println("1 - Directed Line Upgrade Plan (US11)");
            System.out.println("2 - Minimal Backbone Network (US12)");
            System.out.println("3 - Test CSV Import / Show Sample Data");
            System.out.println("0 - Back");
            System.out.println("=============================================");
            System.out.print("Choose an option: ");

            String option = sc.nextLine().trim();

            switch (option) {
                case "1":
                    // new US11DirectedUpgradeUI(sc).run();
                    break;

                case "2":
                    // new US12MinimalBackboneUI(sc).run();
                    break;

                case "3":
                    runCsvImportTest();
                    break;

                case "0":
                    exit = true;
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }



    private void runCsvImportTest() {

        System.out.println("\n=== CSV Import Test ===");

        System.out.print("Enter CSV path (default: station_to_station.csv): ");
        String path = sc.nextLine().trim();
        if (path.isEmpty()) path = "station_to_station.csv";

        try {
            RailNetworkCsvReader reader = new RailNetworkCsvReader();
            List<StationConnection> list = reader.readCsv(path);

            System.out.println("\nTotal connections loaded: " + list.size());

            // Show first 5 entries
            System.out.println("\n--- First 5 Connections ---");
            list.stream()
                .limit(5)
                .forEach(c -> System.out.println("• " + c));

            // Show unique station names
            Set<String> stations = new TreeSet<>();
            for (StationConnection c : list) {
                stations.add(c.getStationFromName());
                stations.add(c.getStationToName());
            }

            System.out.println("\nTotal unique stations: " + stations.size());

            System.out.println("\nSample of station names:");
            stations.stream()
                    .limit(10)
                    .forEach(s -> System.out.println("• " + s));

        } catch (Exception e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }

        System.out.println("\nPress ENTER to return.");
        sc.nextLine();
    }

}
