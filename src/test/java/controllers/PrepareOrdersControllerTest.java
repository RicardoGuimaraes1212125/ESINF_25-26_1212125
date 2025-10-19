package controllers;

import domain.*;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

public class PrepareOrdersControllerTest {

    private Warehouse warehouse;
    private PrepareOrdersController controller;

    @Before
    public void setUp() {
        warehouse = new Warehouse();

        Bay bay = new Bay("W1", 1, 1, 10);
        bay.addBox(new Box("B1", "SKU1", 10, "2025-11-01", LocalDateTime.now().minusDays(1)));
        warehouse.setBays(Collections.singletonList(bay));

        warehouse.setItems(Collections.singletonList(
                new Item("SKU1", "Gloves", "Medical", "unit", 0.1, 0.05)
        ));

        Order order = new Order("O1", "2025-09-30", 1);
        order.addLine(new OrderLine("O1", 1, "SKU1", 5));
        warehouse.setOrders(Collections.singletonList(order));

        controller = new PrepareOrdersController(warehouse);
    }

    @Test
    public void testControllerRunsWithoutErrors() {
        try {
            controller.prepareOrders();
            assertTrue(true);
        } catch (Exception e) {
            assertTrue("Controller should not throw exceptions", false);
        }
    }

    @Test
    public void testControllerAffectsWarehouseInventory() {
        int before = warehouse.getAllBays().get(0).getBoxCount();
        controller.prepareOrders();
        int after = warehouse.getAllBays().get(0).getBoxCount();
        assertTrue("Inventory should change after preparing orders", after < before);
    }

    @Test
    public void testControllerWithEmptyOrders() {
        warehouse.setOrders(Collections.emptyList());
        try {
            controller.prepareOrders();
            assertTrue(true);
        } catch (Exception e) {
            assertTrue("Should handle empty orders gracefully", false);
        }
    }

    @Test
    public void testControllerWithMultipleOrders() {
        Bay bay = warehouse.getAllBays().get(0);
        bay.addBox(new Box("B2", "SKU1", 10, "2025-10-15", LocalDateTime.now().minusDays(2)));

        Order o1 = new Order("O2", "2025-09-30", 1);
        o1.addLine(new OrderLine("O2", 1, "SKU1", 8));
        Order o2 = new Order("O3", "2025-09-30", 2);
        o2.addLine(new OrderLine("O3", 1, "SKU1", 5));

        warehouse.setOrders(Arrays.asList(o1, o2));
        controller.prepareOrders();

        int total = warehouse.getAllBays().stream().mapToInt(Bay::getBoxCount).sum();
        assertTrue("Some boxes should be consumed after orders", total < 2);
    }
}
