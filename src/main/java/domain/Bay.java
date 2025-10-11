package domain;

import java.util.*;

public class Bay {

    private String warehouseId;
    private int aisle;
    private int bayNumber;
    private int capacityBoxes;
    private final List<Box> boxes = new ArrayList<>();

    public Bay(String warehouseId, int aisle, int bayNumber, int capacityBoxes) {
        this.warehouseId = warehouseId;
        this.aisle = aisle;
        this.bayNumber = bayNumber;
        this.capacityBoxes = capacityBoxes;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public int getAisle() {
        return aisle;
    }

    public int getBayNumber() {
        return bayNumber;
    }

    public int getCapacityBoxes() {
        return capacityBoxes;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public boolean hasCapacity() {
        return boxes.size() < capacityBoxes;
    }

    public void addBox(Box box) {
        if (!hasCapacity())
            throw new IllegalStateException("Bay " + bayNumber + " está cheia.");
        boxes.add(box);
    }

    @Override
    public String toString() {
        return "Bay{" + warehouseId + ", aisle=" + aisle + ", bay=" + bayNumber + ", boxes=" + boxes.size() + '}';
    }
}

