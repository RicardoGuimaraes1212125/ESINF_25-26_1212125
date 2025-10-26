package domain;

import java.time.LocalDateTime;

public class Return {

    private String returnId;
    private String sku;
    private int quantity;
    private String reason;
    private LocalDateTime timestamp;
    private String expiryDate; //opcional

    public Return(String returnId, String sku, int quantity, String reason, LocalDateTime timestamp, String expiryDate) {
        this.returnId = returnId;
        this.sku = sku;
        this.quantity = quantity;
        this.reason = reason;
        this.timestamp = timestamp;
        this.expiryDate = expiryDate;
    }

    public String getReturnId() {
        return returnId;
    }

    public String getSku() {
        return sku; 
    }

    public int getQuantity() { 
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getTimestamp() { 
        return timestamp;
    }
    
    public String getExpiryDate() { 
        return expiryDate; 
    }

    @Override
    public String toString() {
        return String.format("Return{%s, sku=%s, qty=%d, reason=%s}", returnId, sku, quantity, reason);
    }
}

