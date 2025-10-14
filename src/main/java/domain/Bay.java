package domain;

import utils.BST;

public class Bay {

    private String warehouseId;
    private int aisle;
    private int bayNumber;
    private int capacityBoxes;
    private BST<Box> boxes = new BST<>();

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
        if (!hasCapacity())
            throw new IllegalStateException("Bay cheia!");
        boxes.insert(box);
    }

    public Box getNextBoxToDispatch() {
        return boxes.smallestElement(); //FEFO/FIFO
    }

    public BST<Box> getBoxesTree() {
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


