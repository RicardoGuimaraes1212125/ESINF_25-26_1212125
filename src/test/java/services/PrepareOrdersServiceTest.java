package services;

import domain.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class PrepareOrdersServiceTest {

    private PrepareOrdersService service;
    private Warehouse warehouse;

    @Before
    public void setUp() {
        service = new PrepareOrdersService();
        warehouse = new Warehouse();

        Bay bay1 = new Bay("W1", 1, 1, 10);
        Bay bay2 = new Bay("W1", 1, 2, 10);
        warehouse.setBays(Arrays.asList(bay1, bay2));

        warehouse.setItems(Arrays.asList(
                new Item("SKU1", "Gloves", "Medical", "unit", 0.1, 0.05),
                new Item("SKU2", "Mask", "Medical", "unit", 0.05, 0.02),
                new Item("SKU3", "Gown", "Medical", "unit", 0.2, 0.1)
        ));

        bay1.addBox(new Box("B1", "SKU1", 10, "2025-10-01", LocalDateTime.now().minusDays(3)));
        bay1.addBox(new Box("B2", "SKU1", 5, "2025-11-01", LocalDateTime.now().minusDays(2)));
        bay2.addBox(new Box("B3", "SKU2", 20, null, LocalDateTime.now().minusDays(1)));
        bay2.addBox(new Box("B4", "SKU3", 5, "2025-12-01", LocalDateTime.now().minusDays(1)));
    }

    @Test
    public void testCompleteOrder() {
        Order order = new Order("O1", "2025-09-30", 1);
        order.addLine(new OrderLine("O1", 1, "SKU1", 10));
        warehouse.setOrders(Collections.singletonList(order));

        service.prepareOrders(warehouse);

        Bay bay = warehouse.getAllBays().get(0);
        assertEquals(1, bay.getBoxCount());
    }

    /* 
    @Test
    public void testPartialOrder() {
        Order order = new Order("O2", "2025-09-30", 2);
        order.addLine(new OrderLine("O2", 1, "SKU2", 50));
        warehouse.setOrders(Collections.singletonList(order));

        service.prepareOrders(warehouse);

        Bay bay = warehouse.getAllBays().get(1);
        assertEquals(0, bay.getBoxCount());
    }
    */

    @Test
    public void testExpiredBoxIgnored() {
        warehouse.getAllBays().get(0)
                .addBox(new Box("B5", "SKU1", 10, "2020-01-01", LocalDateTime.now()));

        Order order = new Order("O3", "2025-09-30", 3);
        order.addLine(new OrderLine("O3", 1, "SKU1", 10));
        warehouse.setOrders(Collections.singletonList(order));

        service.prepareOrders(warehouse);

        Bay bay = warehouse.getAllBays().get(0);
        assertEquals(2, bay.getBoxCount());
    }

    @Test
    public void testMultipleOrders() {
        Order o1 = new Order("O4", "2025-09-29", 1);
        o1.addLine(new OrderLine("O4", 1, "SKU1", 5));
        Order o2 = new Order("O5", "2025-09-29", 3);
        o2.addLine(new OrderLine("O5", 1, "SKU2", 10));
        warehouse.setOrders(Arrays.asList(o1, o2));

        service.prepareOrders(warehouse);

        int remaining = warehouse.getAllBays().stream().mapToInt(Bay::getBoxCount).sum();
        assertTrue(remaining < 4);
    }

    @Test
    public void testNoOrders() {
        warehouse.setOrders(Collections.emptyList());
        service.prepareOrders(warehouse);
        assertTrue(true);
    }

    @Test
    public void testNoBoxes() {
        warehouse.getAllBays().forEach(b -> {
            while (b.getBoxesTree().smallestElement() != null) {
                b.getBoxesTree().remove(b.getBoxesTree().smallestElement());
            }
        });

        Order o = new Order("O6", "2025-09-28", 1);
        o.addLine(new OrderLine("O6", 1, "SKU1", 5));
        warehouse.setOrders(Collections.singletonList(o));

        service.prepareOrders(warehouse);
        assertEquals(0, warehouse.getAllBays().get(0).getBoxCount());
    }

    @Test
    public void testAllExpiredBoxes() {
        Bay bay = warehouse.getAllBays().get(0);
        bay.getBoxesTree().remove(bay.getBoxesTree().smallestElement());
        bay.getBoxesTree().remove(bay.getBoxesTree().smallestElement());
        bay.addBox(new Box("BX", "SKU1", 5, "2020-01-01", LocalDateTime.now()));

        Order o = new Order("O7", "2025-09-28", 3);
        o.addLine(new OrderLine("O7", 1, "SKU1", 5));
        warehouse.setOrders(Collections.singletonList(o));

        service.prepareOrders(warehouse);
        assertEquals(1, bay.getBoxCount());
    }

    @Test
    public void testBoxesRemovedAfterUse() {
        Order o = new Order("O8", "2025-09-28", 1);
        o.addLine(new OrderLine("O8", 1, "SKU1", 15));
        warehouse.setOrders(Collections.singletonList(o));

        service.prepareOrders(warehouse);

        Map<String, List<Box>> remaining = new HashMap<>();
        for (Bay b : warehouse.getAllBays()) {
            for (Box bx : b.getBoxesTree().inOrder()) {
                remaining.computeIfAbsent(bx.getSku(), k -> new ArrayList<>()).add(bx);
            }
        }
        assertNull(remaining.get("SKU1"));
    }

    @Test
    public void testMultiLineOrder() {
        Order o = new Order("O9", "2025-09-28", 2);
        o.addLine(new OrderLine("O9", 1, "SKU1", 10));
        o.addLine(new OrderLine("O9", 2, "SKU2", 100));
        warehouse.setOrders(Collections.singletonList(o));

        service.prepareOrders(warehouse);

        Bay b1 = warehouse.getAllBays().get(0);
        Bay b2 = warehouse.getAllBays().get(1);
        assertTrue(b1.getBoxCount() < 2 || b2.getBoxCount() < 1);
    }

    @Test
    public void testInvalidSKU() {
        Order o = new Order("O10", "2025-09-28", 1);
        o.addLine(new OrderLine("O10", 1, "UNKNOWN", 10));
        warehouse.setOrders(Collections.singletonList(o));

        service.prepareOrders(warehouse);
        int totalBoxes = warehouse.getAllBays().stream().mapToInt(Bay::getBoxCount).sum();
        assertEquals(4, totalBoxes);
    }

    @Test
    public void testZeroQuantityOrder() {
        Order o = new Order("O11", "2025-09-28", 2);
        o.addLine(new OrderLine("O11", 1, "SKU1", 0));
        warehouse.setOrders(Collections.singletonList(o));

        service.prepareOrders(warehouse);
        assertEquals(2, warehouse.getAllBays().get(0).getBoxCount());
    }

    @Test
    public void testDuplicateSKUBoxes() {
        Bay bay = warehouse.getAllBays().get(0);
        bay.addBox(new Box("B6", "SKU1", 8, null, LocalDateTime.now().minusDays(1)));

        Order o = new Order("O12", "2025-09-28", 2);
        o.addLine(new OrderLine("O12", 1, "SKU1", 20));
        warehouse.setOrders(Collections.singletonList(o));

        service.prepareOrders(warehouse);
        int remaining = bay.getBoxCount();
        assertTrue(remaining <= 1);
    }

    @Test
    public void testOrderWithMixedExpiryDates() {
        Bay bay = warehouse.getAllBays().get(0);
        bay.addBox(new Box("B7", "SKU3", 5, null, LocalDateTime.now().minusDays(1)));
        bay.addBox(new Box("B8", "SKU3", 5, "2020-01-01", LocalDateTime.now().minusDays(1)));

        Order o = new Order("O13", "2025-09-28", 1);
        o.addLine(new OrderLine("O13", 1, "SKU3", 5));
        warehouse.setOrders(Collections.singletonList(o));

        service.prepareOrders(warehouse);
        assertTrue(bay.getBoxCount() >= 1);
    }

    @Test
    public void testSequentialOrdersAffectInventory() {
        Order o1 = new Order("O14", "2025-09-28", 1);
        o1.addLine(new OrderLine("O14", 1, "SKU1", 10));
        warehouse.setOrders(Collections.singletonList(o1));
        service.prepareOrders(warehouse);

        Order o2 = new Order("O15", "2025-09-28", 2);
        o2.addLine(new OrderLine("O15", 1, "SKU1", 5));
        warehouse.setOrders(Collections.singletonList(o2));
        service.prepareOrders(warehouse);

        Bay b = warehouse.getAllBays().get(0);
        assertTrue(b.getBoxCount() <= 1);
    }
}
