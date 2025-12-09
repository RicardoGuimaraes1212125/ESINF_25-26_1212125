package app;

import ui.EuropeStationsUI;
import ui.RailNetworkUI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n================ MAIN MENU ================");
            System.out.println("1 - Europe Stations Menu (US06 / US07)");
            System.out.println("2 - Warehouse Menu (US01 / US02)");
            System.out.println("3 - Railway Optimization Menu (US11 / US12)");
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
                    System.out.println("Warehouse Module is disabled in this version.");
                    break;

                case "3":
                    System.out.println("\n>>> Opening Railway Network Optimization Menu...");
                    RailNetworkUI railUI = new RailNetworkUI(sc);
                    railUI.run();
                    break;

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
