package services;

import domain.*;
import dto.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PrepareOrdersService {

    public static class PrepareResult {
        public final List<PrepareOrdersDTO> summaries;
        public final List<AllocationRowDTO> allocations;

        public PrepareResult(List<PrepareOrdersDTO> summaries, List<AllocationRowDTO> allocations) {
            this.summaries = summaries;
            this.allocations = allocations;
        }
    }

    public PrepareResult prepareOrders(Warehouse warehouse, AllocationMode mode) {

        List<Order> orders = new ArrayList<>(warehouse.getOrders());
        List<Bay> bays = warehouse.getAllBays();

        //sort orders by priority, due date, order ID
        orders.sort(Comparator
                .comparingInt(Order::getPriority)
                .thenComparing(Order::getDueDate)
                .thenComparing(Order::getOrderId));

        LocalDate today = LocalDate.now();

        List<PrepareOrdersDTO> orderSummaries = new ArrayList<>();
        List<AllocationRowDTO> allocationRows = new ArrayList<>();

        for (Order order : orders) {
            boolean allEligible = true;
            List<PrepareOrdersDTO.LineResult> lineResults = new ArrayList<>();

            List<OrderLine> lines = new ArrayList<>(order.getLines());
            lines.sort(Comparator.comparingInt(OrderLine::getLineNo));

            for (OrderLine line : lines) {
                String sku = line.getSku();
                int requested = line.getQuantity();
                int remaining = requested;
                int allocated = 0;

                //get available boxes for SKU in FEFO/FIFO order
                List<Box> availableBoxes = getValidBoxesForSku(warehouse, sku, today);

                //list of reserves
                List<Reserve> reserves = new ArrayList<>();

                for (Box box : availableBoxes) {
                    if (remaining <= 0) break;
                    int available = box.getQuantity();
                    if (available <= 0) continue;

                    int take = Math.min(remaining, available);
                    box.setQuantity(available - take);
                    allocated += take;
                    remaining -= take;

                    reserves.add(new Reserve(box, take));

                    //if box is emptied, remove it from warehouse
                    if (box.getQuantity() == 0) {
                        removeBoxFromWarehouse(warehouse, box.getBoxId());
                    }
                }

                //line status
                LineStatus status;
                if (allocated == 0) {
                    status = LineStatus.UNDISPATCHABLE;
                } else if (remaining > 0) {
                    status = LineStatus.PARTIAL;
                } else {
                    status = LineStatus.ELIGIBLE;
                }

                //if strict mode and line not eligible, revert reserves
                if (mode == AllocationMode.STRICT && status != LineStatus.ELIGIBLE) {
                    for (Reserve r : reserves) {
                        r.box.setQuantity(r.box.getQuantity() + r.qty);
                        restoreBoxToWarehouse(warehouse, r.box);
                    }
                    reserves.clear();
                    allocated = 0;
                    status = LineStatus.UNDISPATCHABLE;
                }

                //generate allocation rows
                for (Reserve r : reserves) {
                    Bay bay = findBayForBox(warehouse, r.box.getBoxId());
                    if (bay != null) {
                        allocationRows.add(new AllocationRowDTO(
                                order.getOrderId(),
                                line.getLineNo(),
                                sku,
                                r.qty,
                                r.box.getBoxId(),
                                bay.getWarehouseId(),
                                bay.getAisle(),
                                bay.getBayNumber()
                        ));
                    }
                }

                //add to summary
                lineResults.add(new PrepareOrdersDTO.LineResult(
                        line.getLineNo(),
                        sku,
                        requested,
                        allocated,
                        status
                ));

                if (status != LineStatus.ELIGIBLE) allEligible = false;
            }

            //summary for order
            orderSummaries.add(new PrepareOrdersDTO(
                    order.getOrderId(),
                    order.getDueDate(),
                    order.getPriority(),
                    allEligible,
                    lineResults
            ));
        }

        //update warehouse inventory index
        warehouse.indexInventory();

        return new PrepareResult(orderSummaries, allocationRows);
    }

    //filter boxes: quantity > 0 and not expired, FEFO/FIFO order
    private List<Box> getValidBoxesForSku(Warehouse warehouse, String sku, LocalDate today) {
        List<Box> all = warehouse.getBoxesForSku(sku);
        if (all == null) return Collections.emptyList();

        return all.stream()
                .filter(b -> b.getQuantity() > 0)
                .filter(b -> {
                    String exp = b.getExpiryDate();
                    if (exp == null || exp.isBlank()) return true;
                    try {
                        return LocalDate.parse(exp).isAfter(today);
                    } catch (Exception e) {
                        return true;
                    }
                })
                .sorted()
                .collect(Collectors.toList());
    }

    private void removeBoxFromWarehouse(Warehouse warehouse, String boxId) {
        for (Bay bay : warehouse.getAllBays()) {
            bay.getBoxes().removeIf(b -> b.getBoxId().equals(boxId));
        }
    }

    //reverse reservation in strict mode
    private void restoreBoxToWarehouse(Warehouse warehouse, Box box) {
        for (Bay bay : warehouse.getAllBays()) {
            if (bay.hasCapacity()) {
                bay.addBox(box);
                return;
            }
        }
    }

    //find bay containing the box
    private Bay findBayForBox(Warehouse warehouse, String boxId) {
        for (Bay bay : warehouse.getAllBays()) {
            for (Box b : bay.getBoxes()) {
                if (b.getBoxId().equals(boxId)) return bay;
            }
        }
        return null;
    }

    private static class Reserve {
        final Box box;
        final int qty;
        Reserve(Box box, int qty) {
            this.box = box;
            this.qty = qty;
        }
    }
}
