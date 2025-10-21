package dto;

import java.util.List;

public class PrepareOrdersDTO {

    public static class LineResult {
        public String sku;
        public int requested;
        public int supplied;
        public boolean sufficient;

        public LineResult(String sku, int requested, int supplied, boolean sufficient) {
            this.sku = sku;
            this.requested = requested;
            this.supplied = supplied;
            this.sufficient = sufficient;
        }
    }

    public String orderId;
    public String dueDate;
    public int priority;
    public boolean fullyCompleted;
    public List<LineResult> results;

    public PrepareOrdersDTO(String orderId, String dueDate, int priority,
                            boolean fullyCompleted, List<LineResult> results) {
        this.orderId = orderId;
        this.dueDate = dueDate;
        this.priority = priority;
        this.fullyCompleted = fullyCompleted;
        this.results = results;
    }
}
