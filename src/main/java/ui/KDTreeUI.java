package ui;

import controllers.KDTreeController;

import java.util.Scanner;

public class KDTreeUI {

    private final Scanner sc;
    private final KDTreeController controller;

    public KDTreeUI(Scanner sc, KDTreeController controller) {
        this.sc = sc;
        this.controller = controller;
    }

    public void run() {

        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== US07 – KD-Tree Spatial Index =====");
            System.out.println("1. Show Tree Size");
            System.out.println("2. Show Tree Height");
            System.out.println("3. Show Distinct Bucket Sizes");
            System.out.println("4. Range Search (lat/long window)");
            System.out.println("5. Nearest Neighbour Search");
            System.out.println("0. Exit");
            System.out.print("Option: ");

            String option = sc.nextLine().trim();

            switch (option) {
                case "1": showSize(); break;
                case "2": showHeight(); break;
                case "3": showBucketSizes(); break;
                /* 
                case "4": rangeSearch(); break;
                case "5": nearestNeighbour(); break;
                */
                case "0": exit = true; break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    //Option 1 — Tree Size
    private void showSize() {
        System.out.println("KD-Tree size: " + controller.getSize());
    }

    //Option 2 — Tree Height
    private void showHeight() {
        System.out.println("KD-Tree height: " + controller.getHeight());
    }

    //Option 3 — Distinct Bucket Sizes
    private void showBucketSizes() {
        System.out.println("Distinct bucket sizes:");

        Iterable<Integer> sizes = controller.getBucketSizes();

        boolean empty = true;
        for (int s : sizes) {
            System.out.println(" - " + s);
            empty = false;
        }

        if (empty)
            System.out.println("No buckets found.");
    }

    /* 
    //Option 4 — Range Search
    private void rangeSearch() {

        double latMin = askDouble("Latitude min: ");
        double latMax = askDouble("Latitude max: ");
        double lonMin = askDouble("Longitude min: ");
        double lonMax = askDouble("Longitude max: ");

        System.out.println("\nResults:");
        controller.rangeSearch(latMin, latMax, lonMin, lonMax)
                  .forEach(System.out::println);
    }

    //Option 5 — Nearest Neighbour
    private void nearestNeighbour() {

        double lat = askDouble("Latitude: ");
        double lon = askDouble("Longitude: ");

        System.out.println("\nNearest station:");
        System.out.println(controller.nearest(lat, lon));
    }

    //Helpers: validated input
    private double askDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
     */
}
