package ui;

import domain.Warehouse;
import java.util.Scanner;

import controllers.UnloadWagonsController;

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
                    performOrderAllocation();
                    break;
                case "4":
                    performPickingPlan();
                    break;
                case "5":
                    performPickPathSequencing();
                    break;
                case "6":
                    processReturns();
                    break;
                case "0":
                    exit = true;
                    System.out.println("Exit...");
                    break;
                default:
                    System.out.println("❌ Invalid option. Please try a valid option again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n========== Train Station Warehouse ==========");
        System.out.println("1️: Show warehouse summary");
        System.out.println("2️: USEI01 - Unload wagons (FEFO/FIFO)");
        System.out.println("3️: USEI02 - Order allocation");
        System.out.println("4️: USEI03 - Picking plan generation (Not Developed)");
        System.out.println("5️: USEI04 - Pick path sequencing (Not Developed)");
        System.out.println("6️: USEI05 - Process returns (Not Developed)");
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
        UnloadWagonsController controller = new UnloadWagonsController(warehouse);
        controller.unloadWagons();  
    }

    // --- USEI02 ---
    private void performOrderAllocation() {
        System.out.println("\n[USEI02] Allocating Orders...");
    }

    // --- USEI03 ---
    private void performPickingPlan() {
        System.out.println("\n[USEI03] Generation of the picking plan...");
    }

    // --- USEI04 ---
    private void performPickPathSequencing() {
        System.out.println("\n[USEI04] Calculation of the picking sequence...");
    }

    // --- USEI05 ---
    private void processReturns() {
        System.out.println("\n[USEI05] Returns processing...");
    }
}

