package services;

import domain.*;
import utils.BST;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class UnloadWagonsServiceTest {

    private UnloadWagonsService service;
    private Warehouse warehouse;

    @Before
    public void setUp() {
        service = new UnloadWagonsService();
        warehouse = new Warehouse();

        //bays capacity = 2
        Bay bay1 = new Bay("W1", 1, 1, 2);
        Bay bay2 = new Bay("W1", 1, 2, 2);
        warehouse.setBays(Arrays.asList(bay1, bay2));

        //items
        warehouse.setItems(Arrays.asList(
                new Item("SKU1", "Aspirin", "Pharma", "box", 0.2, 0.5),
                new Item("SKU2", "Gloves", "Medical", "unit", 0.1, 0.05)
        ));
    }

    @Test
    public void testSuccessfulUnload() {
        List<Wagon> wagons = Arrays.asList(
                new Wagon("W1", "B1", "SKU1", 10, "2025-12-31", LocalDateTime.now().minusDays(1)),
                new Wagon("W2", "B2", "SKU2", 20, null, LocalDateTime.now())
        );
        warehouse.setWagons(wagons);

        service.unload(warehouse);

        int totalBoxes = warehouse.getAllBays().stream()
                .mapToInt(Bay::getBoxCount)
                .sum();
        assertEquals(2, totalBoxes);
    }

    @Test
    public void testInvalidSkuInWagon() {
        List<Wagon> wagons = Collections.singletonList(
                new Wagon("W1", "B1", "UNKNOWN", 5, null, LocalDateTime.now())
        );
        warehouse.setWagons(wagons);

        service.unload(warehouse);

        assertTrue(warehouse.getAllBays().stream().allMatch(b -> b.getBoxCount() == 0));
    }

    @Test
    public void testInvalidQuantityInWagon() {
        List<Wagon> wagons = Collections.singletonList(
                new Wagon("W1", "B1", "SKU1", -10, null, LocalDateTime.now())
        );
        warehouse.setWagons(wagons);

        service.unload(warehouse);
        assertTrue(warehouse.getAllBays().stream().allMatch(b -> b.getBoxCount() == 0));
    }

    @Test
    public void testMissingReceivedAt() {
        List<Wagon> wagons = Collections.singletonList(
                new Wagon("W1", "B1", "SKU1", 5, null, null)
        );
        warehouse.setWagons(wagons);

        service.unload(warehouse);
        assertTrue(warehouse.getAllBays().stream().allMatch(b -> b.getBoxCount() == 0));
    }

    @Test
    public void testInvalidExpiryDateFormat() {
        List<Wagon> wagons = Collections.singletonList(
                new Wagon("W1", "B1", "SKU1", 10, "31-12-2025", LocalDateTime.now())
        );
        warehouse.setWagons(wagons);

        service.unload(warehouse);
        assertTrue(warehouse.getAllBays().stream().allMatch(b -> b.getBoxCount() == 0));
    }

    @Test
    public void testFEFOOrdering() {
        List<Wagon> wagons = Arrays.asList(
                new Wagon("W1", "B1", "SKU1", 10, "2025-11-01", LocalDateTime.now()),
                new Wagon("W2", "B2", "SKU1", 10, "2025-10-01", LocalDateTime.now().plusDays(1))
        );
        warehouse.setWagons(wagons);

        service.unload(warehouse);

        Bay bay = warehouse.getAllBays().get(0);
        List<Box> boxes = new ArrayList<>();
        for (Box b : bay.getBoxesTree().inOrder()) {
            boxes.add(b);
        }

        assertEquals("2025-10-01", boxes.get(0).getExpiryDate());
    }

    @Test
    public void testFIFOOrderingWhenNoExpiryDates() {
        List<Wagon> wagons = Arrays.asList(
                new Wagon("W1", "B1", "SKU2", 10, null, LocalDateTime.now().minusDays(2)),
                new Wagon("W2", "B2", "SKU2", 10, null, LocalDateTime.now())
        );
        warehouse.setWagons(wagons);

        service.unload(warehouse);

        Bay bay = warehouse.getAllBays().get(0);
        List<Box> boxes = new ArrayList<>();
        for (Box b : bay.getBoxesTree().inOrder()) {
            boxes.add(b);
        }

        assertTrue(boxes.get(0).getReceivedAt().isBefore(boxes.get(1).getReceivedAt()));
    }

    @Test
    public void testBayOverflowHandledGracefully() {
        List<Wagon> wagons = Arrays.asList(
                new Wagon("W1", "B1", "SKU1", 10, null, LocalDateTime.now()),
                new Wagon("W2", "B2", "SKU1", 10, null, LocalDateTime.now()),
                new Wagon("W3", "B3", "SKU1", 10, null, LocalDateTime.now()),
                new Wagon("W4", "B4", "SKU1", 10, null, LocalDateTime.now()),
                new Wagon("W5", "B5", "SKU1", 10, null, LocalDateTime.now())
        );
        warehouse.setWagons(wagons);

        service.unload(warehouse);

        int totalBoxes = warehouse.getAllBays().stream()
                .mapToInt(Bay::getBoxCount)
                .sum();
        assertEquals(4, totalBoxes);
    }

    @Test
    public void testEmptyWagonsOrBays() {
        warehouse.setWagons(Collections.emptyList());
        warehouse.setBays(Collections.emptyList());

        service.unload(warehouse); 
        assertTrue(warehouse.getAllBays().isEmpty());
    }

    @Test
    public void testNoExceptionThrownOnValidationErrors() {
        List<Wagon> wagons = Arrays.asList(
                new Wagon("W1", "B1", "UNKNOWN", 5, null, LocalDateTime.now()),
                new Wagon("W2", "B2", "SKU1", -10, null, LocalDateTime.now())
        );
        warehouse.setWagons(wagons);

        try {
            service.unload(warehouse);
        } catch (Exception e) {
            fail("Service should not throw exception on validation errors");
        }
    }

    @Test
    public void testInventoryIndexCreated() {
        List<Wagon> wagons = Arrays.asList(
                new Wagon("W1", "B1", "SKU1", 5, null, LocalDateTime.now()),
                new Wagon("W2", "B2", "SKU2", 5, null, LocalDateTime.now())
        );
        warehouse.setWagons(wagons);

        service.unload(warehouse);

        assertNotNull(warehouse.getBoxesForSku("SKU1"));
        assertNotNull(warehouse.getBoxesForSku("SKU2"));
    }

    @Test
    public void testBaysUsedSequentially() {
        //2 bays × capacity 2 = total 4 box
        List<Wagon> wagons = Arrays.asList(
                new Wagon("W1", "B1", "SKU1", 10, null, LocalDateTime.now()),
                new Wagon("W2", "B2", "SKU1", 10, null, LocalDateTime.now()),
                new Wagon("W3", "B3", "SKU1", 10, null, LocalDateTime.now()),
                new Wagon("W4", "B4", "SKU1", 10, null, LocalDateTime.now())
        );
        warehouse.setWagons(wagons);

        service.unload(warehouse);

        //1 should fill before 2
        Bay bay1 = warehouse.getAllBays().get(0);
        Bay bay2 = warehouse.getAllBays().get(1);

        assertEquals(2, bay1.getBoxCount());
        assertEquals(2, bay2.getBoxCount());
    }

    @Test
    public void testExpiredBoxesAreStillLoaded() {
        List<Wagon> wagons = Arrays.asList(
                new Wagon("W1", "B1", "SKU1", 5, "2023-01-01", LocalDateTime.now()),
                new Wagon("W2", "B2", "SKU1", 5, "2025-01-01", LocalDateTime.now())
        );
        warehouse.setWagons(wagons);

        service.unload(warehouse);

        BST<Box> tree = warehouse.getBoxesForSku("SKU1");
        assertNotNull(tree);
        assertEquals(2, tree.size());

        //expired must come first in FEFO order
        assertEquals("2023-01-01", tree.smallestElement().getExpiryDate());
    }

    @Test
    public void testMultipleWagonsSameSku() {
        List<Wagon> wagons = Arrays.asList(
                new Wagon("W1", "B1", "SKU1", 10, "2025-11-10", LocalDateTime.now()),
                new Wagon("W2", "B2", "SKU1", 15, "2025-12-10", LocalDateTime.now())
        );
        warehouse.setWagons(wagons);

        service.unload(warehouse);

        //all boxes of same SKU should go into bays
        BST<Box> tree = warehouse.getBoxesForSku("SKU1");
        assertNotNull(tree);
        assertEquals(2, tree.size());
    }






}
