package controllers;

import domain.*;
import org.junit.Before;
import org.junit.Test;
import services.UnloadWagonsService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class UnloadWagonsControllerTest {

    private Warehouse warehouse;
    private UnloadWagonsController controller;

    @Before
    public void setUp() {
        warehouse = new Warehouse();
        controller = new UnloadWagonsController(warehouse);

        warehouse.setItems(Arrays.asList(
                new Item("SKU1", "Gauze", "Medical", "unit", 0.1, 0.05)
        ));
        warehouse.setBays(Arrays.asList(
                new Bay("W1", 1, 1, 2)
        ));
    }

    @Test
    public void testUnloadWagonsRunsWithoutException() {
        warehouse.setWagons(Collections.singletonList(
                new Wagon("W1", "B1", "SKU1", 10, null, LocalDateTime.now())
        ));

        try {
            controller.unloadWagons();
        } catch (Exception e) {
            fail("Controller should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testWarehouseIsUpdatedAfterUnload() {
        warehouse.setWagons(Collections.singletonList(
                new Wagon("W1", "B1", "SKU1", 10, null, LocalDateTime.now())
        ));

        controller.unloadWagons();

        //after unloading, there should be boxes inside the bays
        int totalBoxes = warehouse.getAllBays().stream()
                .mapToInt(Bay::getBoxCount)
                .sum();

        assertEquals("Expected boxes to be unloaded into bays", 1, totalBoxes);
    }

    @Test
    public void testHandlesEmptyWarehouseGracefully() {
        warehouse.setBays(Collections.emptyList());
        warehouse.setWagons(Collections.emptyList());

        try {
            controller.unloadWagons();
        } catch (Exception e) {
            fail("Empty warehouse should not cause exceptions");
        }
    }

    @Test
    public void testControllerUsesSameWarehouseInstance() {
        assertSame("Controller should hold reference to same warehouse instance",
                warehouse,
                getInternalWarehouse(controller));
    }

    private Warehouse getInternalWarehouse(UnloadWagonsController controller) {
        try {
            java.lang.reflect.Field field = UnloadWagonsController.class.getDeclaredField("warehouse");
            field.setAccessible(true);
            return (Warehouse) field.get(controller);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access warehouse field", e);
        }
    }
}

