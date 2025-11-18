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
            System.out.println("0. Exit");
            System.out.print("Option: ");

            switch (sc.nextLine()) {
                case "1": queryByTZ(); break;
                case "2": queryByWindow(); break;
                case "3": queryByLat(); break;
                case "4": queryByLon(); break;
                case "0": exit = true; break;
                default: System.out.println("Invalid.");
            }
        }
    }

        private void queryByTZ() {
        String tz = askNonEmpty("TZ Group: ");
        for (Station s : controller.queryByTZ(tz)) {
            System.out.println(s);
        }
    }

    private void queryByWindow() {
        String low = askNonEmpty("TZ low: ");
        String high = askNonEmpty("TZ high: ");
        for (Station s : controller.queryByTZWindow(low, high)) {
            System.out.println(s);
        }
    }

    private void queryByLat() {
        double min = askDouble("Latitude min: ");
        double max = askDouble("Latitude max: ");
        for (StationByLat s : controller.queryByLat(min, max)) {
            System.out.println(s);
        }
    }

    private void queryByLon() {
        double min = askDouble("Longitude min: ");
        double max = askDouble("Longitude max: ");
        for (StationByLon s : controller.queryByLon(min, max)) {
            System.out.println(s);
        }
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
