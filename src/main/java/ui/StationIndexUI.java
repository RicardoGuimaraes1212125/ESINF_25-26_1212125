package ui;

import controllers.StationIndexController;
import domain.Station;
import domain.StationByLat;
import domain.StationByLon;

import java.util.Scanner;

public class StationIndexUI {

    private final StationIndexController controller;
    private final Scanner sc;

    public StationIndexUI(Scanner sc, StationIndexController controller) {
        this.sc = sc;
        this.controller = controller;  
    }

    public void run() {

        boolean exit = false;

        while (!exit) {
            System.out.println("\n==== US06 Queries ====");
            System.out.println("1. Search by Time Zone Group");
            System.out.println("2. Search by Window of Time Zone Groups");
            System.out.println("3. Search by Latitude Range");
            System.out.println("4. Search by Longitude Range");
            System.out.println("5. Search by Lat+Lon Window");
            System.out.println("0. Exit");
            System.out.print("Option: ");

            switch (sc.nextLine()) {
                case "1": queryByTZ(); break;
                case "2": queryByWindow(); break;
                case "3": queryByLat(); break;
                case "4": queryByLon(); break;
                case "5": queryByLatLonWindow(); break;
                case "0": exit = true; break;
                default: System.out.println("Invalid.");
            }
        }
    }

        private void queryByTZ() {
            // show available time-zone groups to help the user choose a valid value
            System.out.println("Available time-zone groups:");
            Iterable<String> tzs = controller.getTimeZoneGroups();
            boolean tzEmpty = true;
            for (String g : tzs) {
                System.out.println(" - " + g);
                tzEmpty = false;
            }
            if (tzEmpty) System.out.println(" (none indexed yet)");

            String tz = askNonEmpty("TZ Group: ");
            Iterable<Station> it = controller.queryByTZ(tz);
            boolean empty = true;
            for (Station s : it) {
                System.out.println(s);
                empty = false;
            }
            if (empty) System.out.println("No stations found for TZ group '" + tz + "'.");
    }

    private void queryByWindow() {
        // show available TZ groups first
        System.out.println("Available time-zone groups:");
        Iterable<String> tzs = controller.getTimeZoneGroups();
        boolean tzEmpty = true;
        for (String g : tzs) {
            System.out.println(" - " + g);
            tzEmpty = false;
        }
        if (tzEmpty) System.out.println(" (none indexed yet)");

        String low = askNonEmpty("TZ low: ");
        String high = askNonEmpty("TZ high: ");
        Iterable<Station> it = controller.queryByTZWindow(low, high);
        boolean empty = true;
        for (Station s : it) {
            System.out.println(s);
            empty = false;
        }
        if (empty) System.out.println("No stations found in TZ window ['" + low + "', '" + high + "']." );
    }

    private void queryByLat() {
        double min = askDouble("Latitude min: ");
        double max = askDouble("Latitude max: ");
        Iterable<StationByLat> it = controller.queryByLat(min, max);
        boolean empty = true;
        for (StationByLat s : it) {
            System.out.println(s);
            empty = false;
        }
        if (empty) System.out.println("No stations found in latitude range [" + min + ", " + max + "].");
    }

    private void queryByLon() {
        double min = askDouble("Longitude min: ");
        double max = askDouble("Longitude max: ");
        Iterable<StationByLon> it = controller.queryByLon(min, max);
        boolean empty = true;
        for (StationByLon s : it) {
            System.out.println(s);
            empty = false;
        }
        if (empty) System.out.println("No stations found in longitude range [" + min + ", " + max + "].");
    }

    private void queryByLatLonWindow() {
        double latMin = askDouble("Latitude min: ");
        double latMax = askDouble("Latitude max: ");
        double lonMin = askDouble("Longitude min: ");
        double lonMax = askDouble("Longitude max: ");

        Iterable<Station> it = controller.queryByLatLonWindow(latMin, latMax, lonMin, lonMax);
        System.out.println("\nResults (lat/lon window):");
        boolean empty = true;
        for (Station s : it) {
            System.out.println(s);
            empty = false;
        }
        if (empty) System.out.println("No stations found in lat/lon window.");
    }


    private double askDouble(String message) {
        while (true) {
            System.out.print(message);
            String line = sc.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println(" Invalid number. Please try again.");
            }
        }
    }

    private String askNonEmpty(String message) {
        while (true) {
            System.out.print(message);
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println(" Input cannot be empty. Please try again.");
        }
    }

}
