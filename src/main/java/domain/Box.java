package domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Box implements Comparable<Box> {

    private String boxId;
    private String sku;
    private int quantity;
    private String expiryDate;
    private LocalDateTime receivedAt;

    public Box(String boxId, String sku, int quantity, String expiryDate, LocalDateTime receivedAt) {
        this.boxId = boxId;
        this.sku = sku;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.receivedAt = receivedAt;
    }

    @Override
    public int compareTo(Box other) {

        if (this.boxId.equals(other.boxId)) return 0;

        LocalDate thisExp = parseDate(this.expiryDate);
        LocalDate otherExp = parseDate(other.expiryDate);

        // FEFO: menor expiração primeiro
        if (thisExp != null && otherExp != null) {
            int cmp = thisExp.compareTo(otherExp);
            if (cmp != 0) return cmp;
        } else if (thisExp != null) return -1;
        else if (otherExp != null) return 1;

        // FIFO: menor receivedAt primeiro
        int cmpRecv = this.receivedAt.compareTo(other.receivedAt);
        if (cmpRecv != 0) return cmpRecv;

        // fallback
        return this.boxId.compareTo(other.boxId);
    }

    private LocalDate parseDate(String str) {
        try {
            return (str == null || str.isBlank()) ? null : LocalDate.parse(str);
        } catch (Exception e) {
            return null;
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Box)) return false;
        Box box = (Box) o;
        return Objects.equals(boxId, box.boxId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boxId);
    }
}

