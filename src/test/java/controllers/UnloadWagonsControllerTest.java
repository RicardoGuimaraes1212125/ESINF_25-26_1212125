package controllers;

import domain.*;
import org.junit.Test;
import services.UnloadWagonsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class UnloadWagonsControllerTest {

    @Test
    public void testControllerInitialization() {
        Warehouse warehouse = new Warehouse();
        UnloadWagonsController controller = new UnloadWagonsController(warehouse);
        assertNotNull("Controller should be initialized with warehouse", controller);
    }

    @Test
    public void testUnloadWagonsDelegatesToService() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU001", "Item A", "Category", "unit", 1.0, 1.0));
        warehouse.addBay(new Bay("WH1", 1, 1, 5));
        warehouse.addWagon(new Wagon("WAG001", "BOX001", "SKU001", 10, "2025-12-31", LocalDateTime.now()));

        UnloadWagonsController controller = new UnloadWagonsController(warehouse);
        List<String> logs = controller.unloadWagons();

        assertNotNull("Logs should not be null", logs);
        assertTrue("Should contain 'Stored box' message", logs.stream().anyMatch(l -> l.contains("Stored box")));
    }

    @Test
    public void testControllerHandlesValidationErrors() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU002", "Item B", "Category", "unit", 1.0, 1.0));
        warehouse.addBay(new Bay("WH1", 1, 1, 5));
        warehouse.addWagon(new Wagon("WAG002", "BOX002", "SKU002", 0, "2025-12-31", LocalDateTime.now()));

        UnloadWagonsController controller = new UnloadWagonsController(warehouse);
        List<String> logs = controller.unloadWagons();

        assertTrue("Should report validation errors",
                logs.stream().anyMatch(l -> l.contains("Validation errors")));
    }

    @Test
    public void testControllerWithEmptyWarehouse() {
        Warehouse warehouse = new Warehouse();
        UnloadWagonsController controller = new UnloadWagonsController(warehouse);

        List<String> logs = controller.unloadWagons();

        assertEquals(1, logs.size());
        assertEquals("No wagons or bays available.", logs.get(0));
    }

    @Test
    public void testControllerUpdatesWarehouseInventory() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU003", "Item C", "Category", "unit", 1.0, 1.0));
        warehouse.addBay(new Bay("WH1", 1, 1, 5));
        warehouse.addWagon(new Wagon("WAG003", "BOX003", "SKU003", 5, "2025-12-31", LocalDateTime.now()));

        UnloadWagonsController controller = new UnloadWagonsController(warehouse);
        controller.unloadWagons();

        assertNotNull("Warehouse inventory should be indexed after unload",
                warehouse.getBoxesForSku("SKU003"));
        assertEquals(1, warehouse.getBoxesForSku("SKU003").size());
    }

    @Test
    public void testControllerProcessesMultipleWagons() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU004", "Item D", "Category", "unit", 1.0, 1.0));
        warehouse.addBay(new Bay("WH1", 1, 1, 5));

        warehouse.addWagon(new Wagon("WAG004", "BOX004", "SKU004", 5, "2025-11-30", LocalDateTime.now()));
        warehouse.addWagon(new Wagon("WAG005", "BOX005", "SKU004", 5, "2025-12-31", LocalDateTime.now()));

        UnloadWagonsController controller = new UnloadWagonsController(warehouse);
        List<String> logs = controller.unloadWagons();

        assertTrue("Should contain two stored box messages",
                logs.stream().filter(l -> l.contains("Stored box")).count() >= 2);
    }

    @Test
    public void testControllerHandlesMultipleWarehouses() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU006", "Item F", "Category", "unit", 1.0, 1.0));
        warehouse.addBay(new Bay("WH1", 1, 1, 1));
        warehouse.addBay(new Bay("WH2", 1, 1, 1));

        warehouse.addWagon(new Wagon("WAG008", "BOX008", "SKU006", 5, "2025-12-31", LocalDateTime.now()));
        warehouse.addWagon(new Wagon("WAG009", "BOX009", "SKU006", 5, "2025-12-31", LocalDateTime.now()));

        UnloadWagonsController controller = new UnloadWagonsController(warehouse);
        controller.unloadWagons();

        int totalBoxes = warehouse.getAllBays().stream().mapToInt(b -> b.getBoxes().size()).sum();
        assertEquals("Boxes should be stored in both warehouses", 2, totalBoxes);
    }

}

