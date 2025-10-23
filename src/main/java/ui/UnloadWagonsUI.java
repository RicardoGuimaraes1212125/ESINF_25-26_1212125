package ui;

import controllers.UnloadWagonsController;
import domain.Warehouse;
import java.util.List;

public class UnloadWagonsUI {

    private final Warehouse warehouse;
    private final UnloadWagonsController controller;

    public UnloadWagonsUI(Warehouse warehouse) {
        this.warehouse = warehouse;
        this.controller = new UnloadWagonsController(warehouse);
    }

    public void run() {
        System.out.println("\n=== USEI01 - Unload Wagons ===");

        List<String> logs = controller.unloadWagons();

        System.out.println("\n---- RESULTS ----");

        if (logs.isEmpty()) {
            System.out.println("No wagons to unload or no space available.");
            return;
        }

        logs.forEach(System.out::println);

        int totalBoxes = warehouse.getAllBays().stream()
                .mapToInt(b -> b.getBoxes().size())
                .sum();
        System.out.println();
         System.out.println("Unload complete.");
        System.out.printf("Boxes stored in warehouse: %d%n", totalBoxes);
        System.out.printf("Total bays used: %d%n", warehouse.getAllBays().size());
        System.out.println("---------------------------");
        System.out.println("Returning to main menu...");
    }
}

