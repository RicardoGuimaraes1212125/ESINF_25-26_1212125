package domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Bay {

    private String warehouseId;
    private int aisle;
    private int bayNumber;
    private int capacityBoxes;
    private List<Box> boxes = new ArrayList<>();

    public Bay(String warehouseId, int aisle, int bayNumber, int capacityBoxes) {
        this.warehouseId = warehouseId;
        this.aisle = aisle;
        this.bayNumber = bayNumber;
        this.capacityBoxes = capacityBoxes;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public boolean hasCapacity() {
        return boxes.size() < capacityBoxes;
    }

    public void addBox(Box box) {
        if (!hasCapacity()) {
            throw new IllegalStateException("Bay cheia!");
        }
        boxes.add(box);
        boxes.sort(Comparator.naturalOrder());
    }

    public Box getNextBoxToDispatch() {
        return boxes.isEmpty() ? null : boxes.get(0);
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public int getBoxCount() {
        return boxes.size();
    }

    @Override
    public String toString() {
        return String.format("Bay %s/%d/%d: %d boxes", warehouseId, aisle, bayNumber, boxes.size());
    }

    public int getAisle() {
        return aisle;
    }

    public int getBayNumber() {
        return bayNumber;
    }
}
