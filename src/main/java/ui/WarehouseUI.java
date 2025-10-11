package ui;

import domain.Warehouse;
import java.util.Scanner;

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
            System.out.print("Escolha uma opção: ");
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
                    System.out.println("Saindo do sistema...");
                    break;
                default:
                    System.out.println("❌ Opção inválida. Tente novamente.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n========== Train Station Warehouse UI ==========");
        System.out.println("1️⃣  Mostrar resumo do armazém");
        System.out.println("2️⃣  USEI01 - Descarregar vagões (FEFO/FIFO)");
        System.out.println("3️⃣  USEI02 - Alocação de encomendas");
        System.out.println("4️⃣  USEI03 - Plano de picking");
        System.out.println("5️⃣  USEI04 - Sequenciação de percursos de picking");
        System.out.println("6️⃣  USEI05 - Processar devoluções (quarentena)");
        System.out.println("0️⃣  Sair");
        System.out.println("===============================================");
    }

    private void showWarehouseSummary() {
        System.out.println("\n📦 Resumo do armazém:");
        System.out.println(" - Warehouses: " + warehouse.getWarehouses().size());
        System.out.println(" - Bays: " + warehouse.getAllBays().size());
        System.out.println(" - Items: " + warehouse.getItems().size());
        System.out.println(" - Wagons: " + warehouse.getWagons().size());
        System.out.println(" - Orders: " + warehouse.getOrders().size());
        System.out.println(" - Returns: " + warehouse.getReturns().size());
    }

    // --- USEI01 ---
    private void performUnloading() {
        System.out.println("\n[USEI01] Descarregar vagões...");
        // Aqui será chamada InventoryService.processUnloading(warehouse);
        System.out.println("✅ (placeholder) Operação simulada com sucesso.");
    }

    // --- USEI02 ---
    private void performOrderAllocation() {
        System.out.println("\n[USEI02] Alocação de encomendas...");
        // OrderService.allocateOrders(warehouse);
        System.out.println("✅ (placeholder) Alocação concluída com sucesso.");
    }

    // --- USEI03 ---
    private void performPickingPlan() {
        System.out.println("\n[USEI03] Geração do plano de picking...");
        // PickingService.generatePlan(warehouse);
        System.out.println("✅ (placeholder) Plano de picking gerado.");
    }

    // --- USEI04 ---
    private void performPickPathSequencing() {
        System.out.println("\n[USEI04] Cálculo da sequência de picking...");
        // RoutingService.calculatePaths(warehouse);
        System.out.println("✅ (placeholder) Sequência de picking calculada.");
    }

    // --- USEI05 ---
    private void processReturns() {
        System.out.println("\n[USEI05] Processamento de devoluções...");
        // ReturnsService.process(warehouse);
        System.out.println("✅ (placeholder) Devoluções processadas e registadas no log.");
    }
}

