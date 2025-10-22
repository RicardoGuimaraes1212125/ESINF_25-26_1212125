package services;

import domain.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class UnloadWagonsService {

    public List<String> unload(Warehouse warehouse) {
        List<String> logs = new ArrayList<>();

        List<Wagon> wagons = warehouse.getWagons();
        List<Item> items = warehouse.getItems();
        List<Bay> bays = warehouse.getAllBays();

        if (wagons.isEmpty() || bays.isEmpty()) {
            logs.add("No wagons or bays available.");
            return logs;
        }

        Set<String> validSkus = items.stream()
                .map(Item::getSku)
                .collect(Collectors.toSet());
        List<String> errors = new ArrayList<>();

        for (Wagon w : wagons) {
            if (!validSkus.contains(w.getSku()))
                errors.add("Unknown SKU: " + w.getSku());
            if (w.getQuantity() <= 0)
                errors.add("Invalid quantity in wagon " + w.getWagonId());
            if (w.getReceivedAt() == null)
                errors.add("Missing received date in wagon " + w.getWagonId());
            if (w.getExpiryDate() != null && !w.getExpiryDate().isBlank()) {
                try {
                    LocalDate.parse(w.getExpiryDate());
                } catch (Exception e) {
                    errors.add("Invalid expiry date in wagon " + w.getWagonId());
                }
            }
        }

        if (!errors.isEmpty()) {
            logs.add("Validation errors:");
            logs.addAll(errors);
            return logs;
        }

        //convert wagons to boxes
        List<Box> allBoxes = wagons.stream()
                .map(w -> new Box(
                        w.getBoxId(),
                        w.getSku(),
                        w.getQuantity(),
                        w.getExpiryDate(),
                        w.getReceivedAt()))
                .collect(Collectors.toList());

        //FEFO/FIFO ordering
        allBoxes.sort(Box::compareTo);
        logs.add("Total boxes to unload: " + allBoxes.size());

        //store boxes in bays
        int totalStored = 0;
        int bayIndex = 0;

        for (Box box : allBoxes) {
            boolean placed = false;
            while (!placed && bayIndex < bays.size()) {
                Bay bay = bays.get(bayIndex);

                if (bay.hasCapacity()) {
                    bay.addBox(box);
                    logs.add(String.format(
                            "Stored box %s (SKU: %s) in Bay %s/%d/%d",
                            box.getBoxId(),
                            box.getSku(),
                            bay.getWarehouseId(),
                            bay.getAisle(),
                            bay.getBayNumber()));
                    placed = true;
                    totalStored++;
                } else {
                    bayIndex++;
                }
            }

            if (!placed)
                logs.add(String.format("No space available for box %s (SKU: %s)",
                        box.getBoxId(), box.getSku()));
        }

        //save updated bays back to warehouse
        warehouse.setBays(bays);
        warehouse.indexInventory();
        
        return logs;
    }
}
