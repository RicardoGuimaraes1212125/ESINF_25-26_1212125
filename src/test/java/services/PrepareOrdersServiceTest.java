package services;

import domain.*;
import dto.*;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class PrepareOrdersServiceTest {

    private final PrepareOrdersService service = new PrepareOrdersService();

    private Warehouse createWarehouse(String sku, int qty, String expiry) {
        Warehouse w = new Warehouse();
        Item item = new Item(sku, "Item A", "Category", "unit", 1.0, 1.0);
        w.addItem(item);

        Bay bay = new Bay("WH1", 1, 1, 10);
        Box box = new Box("BOX001", sku, qty, expiry, LocalDateTime.now());
        bay.addBox(box);

        w.addBay(bay);
        w.indexInventory();
        return w;
    }

    @Test
    public void testStrictMode_FullyEligible() {
        Warehouse w = createWarehouse("SKU001", 10, "2025-12-31");

        Order o = new Order("ORD001", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD001", 1, "SKU001", 5));
        w.addOrder(o);

        PrepareOrdersService.PrepareResult result = service.prepareOrders(w, AllocationMode.STRICT);

        assertEquals(1, result.summaries.size());
        PrepareOrdersDTO summary = result.summaries.get(0);
        assertTrue(summary.isAllEligible());
        assertEquals(LineStatus.ELIGIBLE, summary.getLineResults().get(0).getStatus());
        assertEquals(1, result.allocations.size());
        assertEquals(5, result.allocations.get(0).getQty());
    }

    @Test
    public void testPartialMode_PartiallyAllocates() {
        Warehouse w = createWarehouse("SKU002", 3, "2025-12-31");

        Order o = new Order("ORD002", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD002", 1, "SKU002", 10));
        w.addOrder(o);

        PrepareOrdersService.PrepareResult result = service.prepareOrders(w, AllocationMode.PARTIAL);

        assertEquals(LineStatus.PARTIAL, result.summaries.get(0).getLineResults().get(0).getStatus());
        assertEquals(3, result.summaries.get(0).getLineResults().get(0).getAllocatedQty());
    }

    @Test
    public void testStrictMode_RevertsPartialAllocations() {
        Warehouse w = createWarehouse("SKU003", 3, "2025-12-31");

        Order o = new Order("ORD003", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD003", 1, "SKU003", 10));
        w.addOrder(o);

        PrepareOrdersService.PrepareResult result = service.prepareOrders(w, AllocationMode.STRICT);

        PrepareOrdersDTO summary = result.summaries.get(0);
        assertEquals(LineStatus.UNDISPATCHABLE, summary.getLineResults().get(0).getStatus());
        //box quantity should remain unchanged
        Box box = w.getAllBays().get(0).getBoxes().get(0);
        assertEquals(3, box.getQuantity());
    }

    @Test
    public void testNoOrdersProducesEmptyResults() {
        Warehouse w = createWarehouse("SKU004", 5, "2025-12-31");
        PrepareOrdersService.PrepareResult result = service.prepareOrders(w, AllocationMode.STRICT);

        assertTrue(result.summaries.isEmpty());
        assertTrue(result.allocations.isEmpty());
    }

    @Test
    public void testOrdersSortedByPriorityAndDate() {
        Warehouse w = createWarehouse("SKU005", 50, "2025-12-31");
        Order low = new Order("ORD_LOW", "2025-12-31", 2);
        low.addLine(new OrderLine("ORD_LOW", 1, "SKU005", 10));
        Order high = new Order("ORD_HIGH", "2025-12-20", 1);
        high.addLine(new OrderLine("ORD_HIGH", 1, "SKU005", 10));
        w.addOrder(low);
        w.addOrder(high);

        PrepareOrdersService.PrepareResult result = service.prepareOrders(w, AllocationMode.STRICT);

        assertEquals("ORD_HIGH", result.summaries.get(0).getOrderId());
        assertEquals("ORD_LOW", result.summaries.get(1).getOrderId());
    }

    @Test
    public void testExpiredBoxesAreIgnored() {
        Warehouse w = createWarehouse("SKU006", 10, "2020-01-01");

        Order o = new Order("ORD_EXPIRED", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD_EXPIRED", 1, "SKU006", 5));
        w.addOrder(o);

        PrepareOrdersService.PrepareResult result = service.prepareOrders(w, AllocationMode.STRICT);
        assertEquals(0, result.allocations.size());
        assertEquals(LineStatus.UNDISPATCHABLE, result.summaries.get(0).getLineResults().get(0).getStatus());
    }

    @Test
    public void testInvalidExpiryDateIsAccepted() {
        Warehouse w = createWarehouse("SKU007", 10, "invalid-date");

        Order o = new Order("ORD_INVALID", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD_INVALID", 1, "SKU007", 5));
        w.addOrder(o);

        PrepareOrdersService.PrepareResult result = service.prepareOrders(w, AllocationMode.STRICT);
        assertEquals(1, result.allocations.size());
        assertEquals(LineStatus.ELIGIBLE, result.summaries.get(0).getLineResults().get(0).getStatus());
    }

    @Test
    public void testBoxRemovedWhenEmptied() {
        Warehouse w = createWarehouse("SKU008", 5, "2025-12-31");

        Order o = new Order("ORD_REMOVE", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD_REMOVE", 1, "SKU008", 5));
        w.addOrder(o);

        service.prepareOrders(w, AllocationMode.STRICT);

        assertEquals(0, w.getAllBays().get(0).getBoxes().size());
    }

    @Test
    public void testBoxesRemovedWhenFullyUsed() {
        Warehouse w = createWarehouse("SKU009", 8, "2025-12-31");

        Order o = new Order("ORD_PARTIAL", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD_PARTIAL", 1, "SKU009", 10));
        w.addOrder(o);

        service.prepareOrders(w, AllocationMode.PARTIAL);

        assertTrue(w.getAllBays().get(0).getBoxes().isEmpty());
    }


    @Test
    public void testAllocationRowDataIsCorrect() {
        Warehouse w = createWarehouse("SKU010", 10, "2025-12-31");

        Order o = new Order("ORD_ROW", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD_ROW", 1, "SKU010", 5));
        w.addOrder(o);

        PrepareOrdersService.PrepareResult result = service.prepareOrders(w, AllocationMode.STRICT);

        AllocationRowDTO row = result.allocations.get(0);
        assertEquals("ORD_ROW", row.getOrderId());
        assertEquals("SKU010", row.getSku());
        assertEquals(5, row.getQty());
        assertEquals("BOX001", row.getBoxId());
    }

    @Test
    public void testUnknownSkuBecomesUndispatchable() {
        Warehouse w = createWarehouse("SKU011", 10, "2025-12-31");

        Order o = new Order("ORD_UNKNOWN", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD_UNKNOWN", 1, "NONEXISTENT", 5));
        w.addOrder(o);

        PrepareOrdersService.PrepareResult result = service.prepareOrders(w, AllocationMode.STRICT);

        assertTrue(result.allocations.isEmpty());
        assertEquals(LineStatus.UNDISPATCHABLE, result.summaries.get(0).getLineResults().get(0).getStatus());
    }

    @Test
    public void testMultipleLinesInSameOrder() {
        Warehouse w = createWarehouse("SKU012", 10, "2025-12-31");
        w.addItem(new Item("SKU013", "Item B", "Cat", "unit", 1.0, 1.0));

        Bay bay2 = new Bay("WH1", 1, 2, 10);
        bay2.addBox(new Box("BOX002", "SKU013", 5, "2025-12-31", LocalDateTime.now()));
        w.addBay(bay2);
        w.indexInventory();

        Order o = new Order("ORD_MULTI", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD_MULTI", 1, "SKU012", 5));
        o.addLine(new OrderLine("ORD_MULTI", 2, "SKU013", 5));
        w.addOrder(o);

        PrepareOrdersService.PrepareResult result = service.prepareOrders(w, AllocationMode.STRICT);
        assertEquals(2, result.summaries.get(0).getLineResults().size());
        assertEquals(LineStatus.ELIGIBLE, result.summaries.get(0).getLineResults().get(0).getStatus());
        assertEquals(LineStatus.ELIGIBLE, result.summaries.get(0).getLineResults().get(1).getStatus());
    }

    @Test
    public void testMultipleOrdersCompetingForSameStock() {
        Warehouse w = createWarehouse("SKU014", 10, "2025-12-31");

        Order o1 = new Order("ORD_A", "2025-12-25", 1);
        o1.addLine(new OrderLine("ORD_A", 1, "SKU014", 7));

        Order o2 = new Order("ORD_B", "2025-12-26", 1);
        o2.addLine(new OrderLine("ORD_B", 1, "SKU014", 7));

        w.addOrder(o1);
        w.addOrder(o2);

        PrepareOrdersService.PrepareResult result = service.prepareOrders(w, AllocationMode.PARTIAL);

        PrepareOrdersDTO first = result.summaries.get(0);
        PrepareOrdersDTO second = result.summaries.get(1);

        assertEquals("ORD_A", first.getOrderId());
        assertEquals(LineStatus.ELIGIBLE, first.getLineResults().get(0).getStatus());
        assertEquals(LineStatus.PARTIAL, second.getLineResults().get(0).getStatus());
    }

    @Test
    public void testNoBaysAvailable() {
        Warehouse w = new Warehouse();
        w.addItem(new Item("SKU016", "Item", "Cat", "unit", 1, 1));
        w.indexInventory();

        Order o = new Order("ORD_NOBAY", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD_NOBAY", 1, "SKU016", 5));
        w.addOrder(o);

        PrepareOrdersService.PrepareResult result = service.prepareOrders(w, AllocationMode.STRICT);
        assertTrue(result.allocations.isEmpty());
    }

    @Test
    public void testWarehouseInventoryIndexUpdated() {
        Warehouse w = createWarehouse("SKU020", 5, "2025-12-31");

        Order o = new Order("ORD_INDEX", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD_INDEX", 1, "SKU020", 5));
        w.addOrder(o);

        service.prepareOrders(w, AllocationMode.STRICT);

        assertTrue(w.getBoxesForSku("SKU020") == null || w.getBoxesForSku("SKU020").isEmpty());
    }

}
