package domain;

import java.time.LocalDateTime;

public class Wagon {

    private String wagonId;
    private String boxId;
    private String sku;
    private int quantity;
    private String expiryDate; 
    private LocalDateTime receivedAt;

    public Wagon(String wagonId, String boxId, String sku, int quantity, String expiryDate, LocalDateTime receivedAt) {
        this.wagonId = wagonId;
        this.boxId = boxId;
        this.sku = sku;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.receivedAt = receivedAt;
    }

    public String getWagonId() { return wagonId; }
    public String getBoxId() { return boxId; }
    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public String getExpiryDate() { return expiryDate; }
    public LocalDateTime getReceivedAt() { return receivedAt; }

    @Override
    public String toString() {
        return String.format("Wagon{id=%s, box=%s, sku=%s, qty=%d, exp=%s, recv=%s}",
                wagonId, boxId, sku, quantity, expiryDate, receivedAt);
    }
}
