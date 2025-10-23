package controllers;

import domain.*;
import dto.*;
import org.junit.Test;
import services.PrepareOrdersService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class PrepareOrdersControllerTest {

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
    public void testControllerReturnsValidResultObject() {
        Warehouse w = createWarehouse("SKU001", 10, "2025-12-31");
        Order o = new Order("ORD001", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD001", 1, "SKU001", 5));
        w.addOrder(o);

        PrepareOrdersController controller = new PrepareOrdersController(w);
        PrepareOrdersController.PrepareResultDTO result = controller.execute(AllocationMode.STRICT);

        assertNotNull(result);
        assertNotNull(result.getSummaries());
        assertNotNull(result.getAllocations());
    }

    @Test
    public void testControllerExecutesStrictModeSuccessfully() {
        Warehouse w = createWarehouse("SKU002", 10, "2025-12-31");
        Order o = new Order("ORD002", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD002", 1, "SKU002", 5));
        w.addOrder(o);

        PrepareOrdersController controller = new PrepareOrdersController(w);
        PrepareOrdersController.PrepareResultDTO result = controller.execute(AllocationMode.STRICT);

        assertEquals(1, result.getSummaries().size());
        assertEquals(1, result.getAllocations().size());
        PrepareOrdersDTO summary = result.getSummaries().get(0);
        assertTrue(summary.isAllEligible());
        assertEquals(LineStatus.ELIGIBLE, summary.getLineResults().get(0).getStatus());
    }

    @Test
    public void testControllerExecutesPartialModeSuccessfully() {
        Warehouse w = createWarehouse("SKU003", 3, "2025-12-31");
        Order o = new Order("ORD003", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD003", 1, "SKU003", 10));
        w.addOrder(o);

        PrepareOrdersController controller = new PrepareOrdersController(w);
        PrepareOrdersController.PrepareResultDTO result = controller.execute(AllocationMode.PARTIAL);

        assertEquals(1, result.getSummaries().size());
        PrepareOrdersDTO summary = result.getSummaries().get(0);
        assertFalse(summary.isAllEligible());
        assertEquals(LineStatus.PARTIAL, summary.getLineResults().get(0).getStatus());
    }

    @Test
    public void testControllerHandlesEmptyWarehouseGracefully() {
        Warehouse w = new Warehouse();
        PrepareOrdersController controller = new PrepareOrdersController(w);

        PrepareOrdersController.PrepareResultDTO result = controller.execute(AllocationMode.STRICT);
        assertNotNull(result);
        assertTrue(result.getSummaries().isEmpty());
        assertTrue(result.getAllocations().isEmpty());
    }

    @Test
    public void testControllerProducesSameResultsAsServiceDirectCall() {
        Warehouse w = createWarehouse("SKU004", 10, "2025-12-31");
        Order o = new Order("ORD004", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD004", 1, "SKU004", 5));
        w.addOrder(o);

        PrepareOrdersService service = new PrepareOrdersService();
        PrepareOrdersService.PrepareResult direct = service.prepareOrders(w, AllocationMode.STRICT);

        w = createWarehouse("SKU004", 10, "2025-12-31");
        o = new Order("ORD004", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD004", 1, "SKU004", 5));
        w.addOrder(o);

        PrepareOrdersController controller = new PrepareOrdersController(w);
        PrepareOrdersController.PrepareResultDTO viaController = controller.execute(AllocationMode.STRICT);

        assertEquals(direct.summaries.size(), viaController.getSummaries().size());
        assertEquals(direct.allocations.size(), viaController.getAllocations().size());
    }

    @Test
    public void testControllerResultDTOStoresDataCorrectly() {
        Warehouse w = createWarehouse("SKU005", 10, "2025-12-31");
        Order o = new Order("ORD005", "2025-12-30", 1);
        o.addLine(new OrderLine("ORD005", 1, "SKU005", 5));
        w.addOrder(o);

        PrepareOrdersController controller = new PrepareOrdersController(w);
        PrepareOrdersController.PrepareResultDTO result = controller.execute(AllocationMode.STRICT);

        assertEquals("ORD005", result.getSummaries().get(0).getOrderId());
        AllocationRowDTO row = result.getAllocations().get(0);
        assertEquals("BOX001", row.getBoxId());
        assertEquals("SKU005", row.getSku());
    }
}

