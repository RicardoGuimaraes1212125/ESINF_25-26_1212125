package dto;

import domain.LineStatus;
import java.util.List;

public class PrepareOrdersDTO {

    private final String orderId;
    private final String dueDate;
    private final int priority;
    private final boolean allEligible;
    private final List<LineResult> lineResults;

    public PrepareOrdersDTO(String orderId, String dueDate, int priority,
                            boolean allEligible, List<LineResult> lineResults) {
        this.orderId = orderId;
        this.dueDate = dueDate;
        this.priority = priority;
        this.allEligible = allEligible;
        this.lineResults = lineResults;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isAllEligible() {
        return allEligible;
    }

    public List<LineResult> getLineResults() {
        return lineResults;
    }

    public static class LineResult {
        private final int lineNo;
        private final String sku;
        private final int requestedQty;
        private final int allocatedQty;
        private final LineStatus status;

        public LineResult(int lineNo, String sku, int requestedQty, int allocatedQty, LineStatus status) {
            this.lineNo = lineNo;
            this.sku = sku;
            this.requestedQty = requestedQty;
            this.allocatedQty = allocatedQty;
            this.status = status;
        }

        public int getLineNo() {
            return lineNo;
        }

        public String getSku() {
            return sku;
        }

        public int getRequestedQty() {
            return requestedQty;
        }

        public int getAllocatedQty() {
            return allocatedQty;
        }

        public LineStatus getStatus() {
            return status;
        }
    }
}
