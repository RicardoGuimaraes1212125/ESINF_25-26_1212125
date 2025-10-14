package services;

import domain.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class UnloadWagonsService {

    /**
     * Execute USEI01:
     * Unload wagons (FEFO/FIFO) and store boxes in bays (first fit).
     */

    public void unload(Warehouse warehouse) {
        List<Wagon> wagons = warehouse.getWagons();
        List<Item> items = warehouse.getItems();
        List<Bay> bays = warehouse.getAllBays();

        if (wagons.isEmpty() || bays.isEmpty()) {
            System.err.println("No wagons or bays available to perform unloading.");
            return;
        }

        //validation
        Set<String> validSkus = items.stream()
                .map(Item::getSku)
                .collect(Collectors.toSet());
        List<String> errors = new ArrayList<>();

        for (Wagon w : wagons) {
            if (!validSkus.contains(w.getSku())) {
                errors.add("unknow SKU '" + w.getSku() + "' in wagon " + w.getWagonId());
            }
            if (w.getQuantity() <= 0) {
                errors.add("Invalid Quantity (" + w.getQuantity() + ") in wagon " + w.getWagonId());
            }
            if (w.getReceivedAt() == null) {
                errors.add("Miss reception data " + w.getWagonId());
            }
            if (w.getExpiryDate() != null && !w.getExpiryDate().isBlank()) {
                try {
                    LocalDate.parse(w.getExpiryDate());
                } catch (Exception e) {
                    errors.add("Invalid expiration date " + w.getExpiryDate() + "' in wagon " + w.getWagonId());
                }
            }
        }

        if (!errors.isEmpty()) {
            System.err.println("\n Validation errors:");
            errors.forEach(e -> System.err.println("   - " + e));
            return;
        }

        //box creation from wagons
        List<Box> allBoxes = wagons.stream()
                .map(w -> new Box(
                        w.getBoxId(),
                        w.getSku(),
                        w.getQuantity(),
                        w.getExpiryDate(),
                        w.getReceivedAt()
                ))
                .collect(Collectors.toList());

        //FEFO/FIFO
        allBoxes.sort(Box::compareTo);
        System.out.println(" Total boxes to be unloaded: " + allBoxes.size());

        //boxing in bays - first fit
        int nextBayIndex = 0;
        for (Box box : allBoxes) {
            boolean placed = false;

            while (!placed && nextBayIndex < bays.size()) {
                Bay bay = bays.get(nextBayIndex);

                if (bay.hasCapacity()) {
                    bay.addBox(box);
                    placed = true;
                } else {
                    nextBayIndex++;
                }
            }

            if (!placed) {
                System.err.println(" No space available for box " + box.getBoxId() +
                        " (SKU: " + box.getSku() + ")");
            }
        }

        //index inventory after unloading
        warehouse.indexInventory();

        //summary
        printSummary(bays);
    }

    private void printSummary(List<Bay> bays) {
        System.out.println("\n Unload Complete.");
        System.out.println("📊 Summary:");
        for (Bay bay : bays) {
            System.out.printf(" - %s/%d/%d → %d boxes\n",
                    bay.getWarehouseId(),
                    bay.getAisle(),
                    bay.getBayNumber(),
                    bay.getBoxCount());
        }
    }
}

