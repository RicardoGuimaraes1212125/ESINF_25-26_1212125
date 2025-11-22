package app;

import ui.EuropeStationsUI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n================ MAIN MENU ================");
            System.out.println("1 - Europe Stations Menu (US06 / US07)");
            System.out.println("2 - Warehouse Menu (US01 / US02)");
            System.out.println("0 - Exit");
            System.out.println("===========================================");
            System.out.print("Choose an option: ");

            String option = sc.nextLine().trim();

            switch (option) {
                case "1":
                    System.out.println("\n>>> Opening Europe Stations Menu...");
                    EuropeStationsUI europeUI = new EuropeStationsUI(sc);
                    europeUI.run();
                    break;

                case "2":
                /* 
                    System.out.println("\n>>> Opening Warehouse Menu...");
                    Warehouse warehouse = new Warehouse();
                    WarehouseUI warehouseUI = new WarehouseUI(warehouse, sc);
                    warehouseUI.start();
                    break;
                    */
                case "0":
                    exit = true;
                    System.out.println("Exiting system...");
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

    }
}
