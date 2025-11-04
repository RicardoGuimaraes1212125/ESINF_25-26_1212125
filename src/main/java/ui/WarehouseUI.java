package ui;

import domain.Warehouse;
import java.util.Scanner;

import controllers.PrepareOrdersController;

public class WarehouseUI {

    private final Warehouse warehouse;
    private final Scanner scanner = new Scanner(System.in);

    public WarehouseUI(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            printMenu();
            System.out.print("Choose an option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    showWarehouseSummary();
                    break;
                case "2":
                    performUnloading();
                    break;
                case "3":
                    performOrderPreparation();
                    break;
                case "0":
                    exit = true;
                    System.out.println("Exit...");
                    break;
                default:
                    System.out.println(" Invalid option. Please try a valid option again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n========== Train Station Warehouse ==========");
        System.out.println("1️: Show warehouse summary");
        System.out.println("2️: USEI01 - Unload wagons (FEFO/FIFO)");
        System.out.println("3️: USEI02 - Order allocation");
        System.out.println("0: Exit");
        System.out.println("===============================================");
    }

    private void showWarehouseSummary() {
        System.out.println("\n Warehouse Summary:");
        System.out.println(" - Warehouses: " + warehouse.getWarehouses().size());
        System.out.println(" - Bays: " + warehouse.getAllBays().size());
        System.out.println(" - Items: " + warehouse.getItems().size());
        System.out.println(" - Wagons: " + warehouse.getWagons().size());
        System.out.println(" - Orders: " + warehouse.getOrders().size());
        System.out.println(" - Returns: " + warehouse.getReturns().size());
    }

    // --- USEI01 ---
    private void performUnloading() {
        System.out.println("\n[USEI01] Unloading Wagons..."); 
        UnloadWagonsUI ui = new UnloadWagonsUI(warehouse);
        ui.run();
    }

    // --- USEI02 ---
    private void performOrderPreparation() {
        System.out.println("\n[USEI02] Preparing Orders for Dispatch...");
        try {
        PrepareOrdersController controller = new PrepareOrdersController(warehouse);
        PrepareOrdersUI ui = new PrepareOrdersUI(controller);
        ui.run();
    } catch (Exception e) {
        System.err.println("Error preparing orders: " + e.getMessage());
        e.printStackTrace();
    }
    }


}

