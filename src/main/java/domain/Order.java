package domain;

import java.util.*;

public class Order {

    private String orderId;
    private String dueDate;
    private int priority;
    private final List<OrderLine> lines = new ArrayList<>();

    public Order(String orderId, String dueDate, int priority) {
        this.orderId = orderId;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public void addLine(OrderLine line) {
        lines.add(line);
    }

    public String getOrderId() { return orderId; }
    public String getDueDate() { return dueDate; }
    public int getPriority() { return priority; }
    public List<OrderLine> getLines() { return lines; }

    @Override
    public String toString() {
        return "Order{" + orderId + ", lines=" + lines.size() + "}";
    }
}

