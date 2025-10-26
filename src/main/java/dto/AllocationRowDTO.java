package dto;

public class AllocationRowDTO {

    private final String orderId;
    private final int lineNo;
    private final String sku;
    private final int qty;
    private final String boxId;
    private final String warehouseId;
    private final int aisle;
    private final int bayNumber;

    public AllocationRowDTO(String orderId, int lineNo, String sku, int qty,
                            String boxId, String warehouseId, int aisle, int bayNumber) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.qty = qty;
        this.boxId = boxId;
        this.warehouseId = warehouseId;
        this.aisle = aisle;
        this.bayNumber = bayNumber;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String getSku() {
        return sku;
    }

    public int getQty() {
        return qty;
    }

    public String getBoxId() {
        return boxId;
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

    @Override
    public String toString() {
        return String.format("Order %s | Line %d | SKU %s | Qty %d | Box %s | Bay %s/%d/%d",
                orderId, lineNo, sku, qty, boxId, warehouseId, aisle, bayNumber);
    }
}
