package ui;

import controllers.UnloadWagonsController;
import domain.Warehouse;
import java.util.List;
import java.util.Scanner;

public class UnloadWagonsUI {

    private final Warehouse warehouse;
    private final UnloadWagonsController controller;
    private final Scanner scanner = new Scanner(System.in);

    public UnloadWagonsUI(Warehouse warehouse) {
        this.warehouse = warehouse;
        this.controller = new UnloadWagonsController(warehouse);
    }

    public void run() {
        System.out.println("\n=== USEI01 - Unload Wagons ===");
        System.out.println("This process will unload all valid wagons using FEFO/FIFO and store in bays.");

        System.out.print("Do you want to continue? (Y/N): ");
        String opt = scanner.nextLine().trim().toUpperCase();

        if (!opt.equals("Y")) {
            System.out.println("Operation cancelled.");
            return;
        }

        List<String> logs = controller.unloadWagons();
        System.out.println("\n---- RESULTS ----");
        logs.forEach(System.out::println);

        System.out.println("---------------------------");
        System.out.println("Returning to main menu...");
    }
}
