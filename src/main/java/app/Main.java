package app;

import services.CsvImportService;
import domain.Warehouse;
import ui.WarehouseUI;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Train Station Warehouse Management (ESINF) ===");
        System.out.println("Starting data import...\n");

        String basePath = Paths.get("data").toAbsolutePath().toString();

        try {
            CsvImportService importService = new CsvImportService(basePath);
            Warehouse warehouse = importService.importAll();

            System.out.println("\n Import completed successfully!");
            WarehouseUI ui = new WarehouseUI(warehouse);
            ui.start(); // 🚀 Inicia a interface textual

        } catch (Exception e) {
            System.err.println("\n Error during import: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Import finished ===");
    }
}

