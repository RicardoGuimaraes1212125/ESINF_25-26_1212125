package domain;

import java.time.LocalDateTime;

public class Box {

    private String boxId;
    private String sku;
    private int quantity;
    private LocalDateTime receivedAt;
    private String expiryDate; // null se não perecível

    public Box(String boxId, String sku, int quantity, String expiryDate, LocalDateTime receivedAt) {
        this.boxId = boxId;
        this.sku = sku;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.receivedAt = receivedAt;
    }

    public String getBoxId() {
        return boxId;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("Box{id='%s', sku='%s', qty=%d, exp=%s}", boxId, sku, quantity, expiryDate);
    }
}

