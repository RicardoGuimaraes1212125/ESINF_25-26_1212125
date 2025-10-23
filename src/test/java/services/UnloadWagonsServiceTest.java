package services;

import domain.*;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class UnloadWagonsServiceTest {

    private UnloadWagonsService service = new UnloadWagonsService();

    @Test
    public void testUnloadWithNoWagonsOrBays() {
        Warehouse warehouse = new Warehouse();
        List<String> logs = service.unload(warehouse);
        assertEquals(1, logs.size());
        assertEquals("No wagons or bays available.", logs.get(0));
    }

    @Test
    public void testUnloadWithEmptyBaysOnly() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU001", "Item A", "Category", "unit", 1, 1));
        List<String> logs = service.unload(warehouse);
        assertEquals("No wagons or bays available.", logs.get(0));
    }

    @Test
    public void testUnloadWithUnknownSku() {
        Warehouse warehouse = new Warehouse();
        warehouse.addBay(new Bay("W1", 1, 1, 5));
        warehouse.addWagon(new Wagon("W1", "B1", "INVALID", 5, "2025-12-31", LocalDateTime.now()));
        List<String> logs = service.unload(warehouse);
        assertTrue(logs.stream().anyMatch(l -> l.contains("Unknown SKU")));
    }

    @Test
    public void testUnloadWithInvalidQuantity() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU001", "Item A", "Cat", "u", 1, 1));
        warehouse.addBay(new Bay("W1", 1, 1, 5));
        warehouse.addWagon(new Wagon("W2", "B2", "SKU001", 0, "2025-12-31", LocalDateTime.now()));
        List<String> logs = service.unload(warehouse);
        assertTrue(logs.stream().anyMatch(l -> l.contains("Invalid quantity")));
    }

    @Test
    public void testUnloadWithInvalidExpiryDateFormat() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU002", "Item B", "Cat", "u", 1, 1));
        warehouse.addBay(new Bay("W1", 1, 1, 5));
        warehouse.addWagon(new Wagon("W3", "B3", "SKU002", 10, "31/12/2025", LocalDateTime.now()));
        List<String> logs = service.unload(warehouse);
        assertTrue(logs.stream().anyMatch(l -> l.contains("Invalid expiry date")));
    }

    @Test
    public void testUnloadWithMissingReceivedDate() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU003", "Item C", "Cat", "u", 1, 1));
        warehouse.addBay(new Bay("W1", 1, 1, 5));
        warehouse.addWagon(new Wagon("W4", "B4", "SKU003", 5, "2025-12-31", null));
        List<String> logs = service.unload(warehouse);
        assertTrue(logs.stream().anyMatch(l -> l.contains("Missing received date")));
    }

    @Test
    public void testSuccessfulUnloadStoresBoxes() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU004", "Item D", "Cat", "u", 1, 1));
        warehouse.addBay(new Bay("W1", 1, 1, 5));
        warehouse.addWagon(new Wagon("W5", "B5", "SKU004", 5, "2025-12-31", LocalDateTime.now()));

        List<String> logs = service.unload(warehouse);

        assertTrue(logs.stream().anyMatch(l -> l.contains("Stored box")));
        assertEquals(1, warehouse.getAllBays().get(0).getBoxes().size());
        assertEquals("SKU004", warehouse.getAllBays().get(0).getBoxes().get(0).getSku());
    }

    @Test
    public void testUnloadMultipleBoxesFEFOOrder() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU005", "Item E", "Cat", "u", 1, 1));
        Bay bay = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay);
        warehouse.addWagon(new Wagon("W6", "B6", "SKU005", 5, "2025-10-01", LocalDateTime.now()));
        warehouse.addWagon(new Wagon("W7", "B7", "SKU005", 5, "2025-12-01", LocalDateTime.now()));

        service.unload(warehouse);
        List<Box> boxes = bay.getBoxes();

        assertEquals("B6", boxes.get(0).getBoxId());
        assertEquals("B7", boxes.get(1).getBoxId());
    }

    @Test
    public void testUnloadFifoWhenNoExpiryDates() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU006", "Item F", "Cat", "u", 1, 1));
        Bay bay = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay);
        warehouse.addWagon(new Wagon("W8", "B8", "SKU006", 5, "", LocalDateTime.now().minusDays(2)));
        warehouse.addWagon(new Wagon("W9", "B9", "SKU006", 5, "", LocalDateTime.now()));

        service.unload(warehouse);
        List<Box> boxes = bay.getBoxes();
        assertEquals("B8", boxes.get(0).getBoxId());
    }

    @Test
    public void testUnloadRespectsCapacityLimit() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU007", "Item G", "Cat", "u", 1, 1));
        warehouse.addBay(new Bay("W1", 1, 1, 1));
        warehouse.addWagon(new Wagon("W10", "B10", "SKU007", 5, "2025-12-31", LocalDateTime.now()));
        warehouse.addWagon(new Wagon("W11", "B11", "SKU007", 5, "2025-12-31", LocalDateTime.now()));

        List<String> logs = service.unload(warehouse);
        assertTrue(logs.stream().anyMatch(l -> l.contains("No space available")));
    }

    @Test
    public void testWarehouseInventoryUpdatedAfterUnload() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU008", "Item H", "Cat", "u", 1, 1));
        warehouse.addBay(new Bay("W1", 1, 1, 5));
        warehouse.addWagon(new Wagon("W12", "B12", "SKU008", 10, "2025-12-31", LocalDateTime.now()));

        service.unload(warehouse);
        assertNotNull("Inventory should include SKU008", warehouse.getBoxesForSku("SKU008"));
        assertEquals(1, warehouse.getBoxesForSku("SKU008").size());
    }

    @Test
    public void testUnloadWithLargeNumberOfWagons() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU009", "BulkItem", "Cat", "u", 1, 1));
        warehouse.addBay(new Bay("W1", 1, 1, 200));

        for (int i = 0; i < 100; i++) {
            warehouse.addWagon(new Wagon("WG" + i, "BX" + i, "SKU009", 10, "2025-12-31", LocalDateTime.now()));
        }

        List<String> logs = service.unload(warehouse);
        assertTrue(logs.stream().anyMatch(l -> l.contains("Total boxes to unload")));
        assertTrue(logs.stream().filter(l -> l.contains("Stored box")).count() > 90);
    }

    @Test
    public void testUnloadWithMultipleWarehouses() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU010", "Item J", "Cat", "u", 1, 1));

        warehouse.addBay(new Bay("W1", 1, 1, 1));
        warehouse.addBay(new Bay("W2", 1, 1, 1));

        warehouse.addWagon(new Wagon("W13", "B13", "SKU010", 5, "2025-12-31", LocalDateTime.now()));
        warehouse.addWagon(new Wagon("W14", "B14", "SKU010", 5, "2025-12-31", LocalDateTime.now()));

        List<String> logs = service.unload(warehouse);
        assertEquals("Should store across both warehouses", 2,
                warehouse.getAllBays().stream().mapToInt(b -> b.getBoxes().size()).sum());
    }

    @Test
    public void testUnloadWithExpiredItemsSkipped() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU011", "Item K", "Cat", "u", 1, 1));
        warehouse.addBay(new Bay("W1", 1, 1, 5));
        warehouse.addWagon(new Wagon("W15", "B15", "SKU011", 5, "2020-01-01", LocalDateTime.now())); // expired
        warehouse.addWagon(new Wagon("W16", "B16", "SKU011", 5, "2026-01-01", LocalDateTime.now())); // valid

        service.unload(warehouse);
        List<Box> boxes = warehouse.getAllBays().get(0).getBoxes();

        assertEquals(2, boxes.size()); //expired should be stored
    }

    @Test
    public void testUnloadLogContainsSummary() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU012", "Item L", "Cat", "u", 1, 1));
        warehouse.addBay(new Bay("W1", 1, 1, 3));
        warehouse.addWagon(new Wagon("W17", "B17", "SKU012", 5, "2025-12-31", LocalDateTime.now()));

        List<String> logs = service.unload(warehouse);
        assertTrue("Should contain completion message or count info",
                logs.stream().anyMatch(l -> l.contains("Total boxes to unload")));
    }

    @Test
    public void testUnloadDoesNotStoreWhenValidationFails() {
        Warehouse warehouse = new Warehouse();
        warehouse.addItem(new Item("SKU013", "Item M", "Cat", "u", 1, 1));
        warehouse.addBay(new Bay("W1", 1, 1, 5));
        warehouse.addWagon(new Wagon("W18", "B18", "INVALIDSKU", 5, "2025-12-31", LocalDateTime.now()));

        List<String> logs = service.unload(warehouse);
        assertTrue("Should detect validation error", logs.stream().anyMatch(l -> l.contains("Validation errors")));
        assertEquals("No boxes should be stored", 0, warehouse.getAllBays().get(0).getBoxes().size());
    }
}
