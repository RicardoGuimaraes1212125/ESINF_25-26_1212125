package domain;

public class OrderLine {

    private String orderId;
    private int lineNo;
    private String sku;
    private int quantity;

    public OrderLine(String orderId, int lineNo, String sku, int quantity) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.quantity = quantity;
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

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return String.format("OrderLine{%s-%d, sku=%s, qty=%d}", orderId, lineNo, sku, quantity);
    }
}

