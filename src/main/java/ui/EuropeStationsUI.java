package ui;

import controllers.KDTreeController;
import controllers.StationIndexController;
import services.StationIndexService;

import java.io.File;
import java.util.Scanner;

public class EuropeStationsUI {

    private final Scanner sc;
    private String csvPath;                    
    private StationIndexController controller;  

    public EuropeStationsUI(Scanner sc) {
        this.sc = sc;
    }

    public void run() {
        System.out.println("=== ESINF Sprint 2 ===");
        csvPath = askCSVPath();                
        menuLoop();
    }

    private String askCSVPath() {
        while (true) {
            System.out.print(" Insert CSV Path: ");
            String path = sc.nextLine().trim();
            File f = new File(path);

            if (f.exists() && path.toLowerCase().endsWith(".csv")) {
                return path;
            }
            System.out.println(" Invalid path. Try again.\n");
        }
    }

    private void menuLoop() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== Sprint 2 – Europe Stations =====");
            System.out.println("1. US06 – Time-Zone Indexed Queries (AVL)");
            System.out.println("2. US07 – Spatial / KD-Tree Queries");
            System.out.println("0. Exit");
            System.out.print("Option: ");

            switch (sc.nextLine()) {
                case "1": openUS06(); break;
                case "2": openUS07(); break;
                case "0": exit = true; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private void openUS06() {

        System.out.println("\nBuilding AVL Indexes for US06...");

        controller = new StationIndexController(new StationIndexService());
        controller.loadCSV(csvPath);

        System.out.println("AVL indexes ready!");

        StationIndexUI ui = new StationIndexUI(sc, controller);
        ui.run();
    }

    private void openUS07() {

    System.out.println("\n Building KD-Tree for US07...");

    KDTreeController kd = new KDTreeController(controller.getService());
    kd.buildKDTree();

    KDTreeUI ui = new KDTreeUI(sc, kd);
    ui.run();
}

}
