package services;

import domain.*;
import utils.BST;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PrepareOrdersService {

    public void prepareOrders(Warehouse warehouse) {
        List<Order> orders = warehouse.getOrders();
        List<Bay> bays = warehouse.getAllBays();

        if (orders.isEmpty()) {
            System.out.println("No orders available for dispatch.");
            return;
        }

        System.out.println("Preparing " + orders.size() + " orders for dispatch...");
        LocalDate currentDate = LocalDate.of(2025, 9, 28);
        Map<String, List<Box>> inventory = collectInventory(bays);

        int totalBoxesUsed = 0;
        int completedOrders = 0;

        for (Order order : orders) {
            System.out.println("\n-------------------------------------------------");
            System.out.printf("Order ID: %s  |  Due Date: %s  |  Priority: %s\n",
                    order.getOrderId(), order.getDueDate(), order.getPriority());
            System.out.println("-------------------------------------------------");

            boolean complete = true;

            for (OrderLine line : order.getLines()) {
                String sku = line.getSku();
                int qtyRequired = line.getQuantity();

                List<Box> available = inventory.getOrDefault(sku, Collections.emptyList())
                        .stream()
                        .filter(b -> b.getExpiryDate() == null ||
                                LocalDate.parse(b.getExpiryDate()).isAfter(currentDate))
                        .sorted()
                        .collect(Collectors.toList());

                int qtyPicked = 0;
                List<Box> boxesUsed = new ArrayList<>();

                for (Box box : available) {
                    if (qtyPicked >= qtyRequired) break;
                    qtyPicked += box.getQuantity();
                    boxesUsed.add(box);
                }

                if (qtyPicked < qtyRequired) {
                    System.out.printf(" - SKU: %-10s | Requested: %-4d | Available: %-4d | Status: INSUFFICIENT\n",
                            sku, qtyRequired, qtyPicked);
                    complete = false;
                } else {
                    System.out.printf(" - SKU: %-10s | Requested: %-4d | Supplied: %-4d | Boxes Used: %-3d\n",
                            sku, qtyRequired, qtyPicked, boxesUsed.size());
                }

                //remove used boxes from bays and inventory
                for (Box used : boxesUsed) {
                    removeBoxFromBays(used, bays);
                    inventory.get(sku).remove(used);
                }

                totalBoxesUsed += boxesUsed.size();
            }

            if (complete) {
                System.out.println("Status: COMPLETED");
                completedOrders++;
            } else {
                System.out.println("Status: PARTIALLY FULFILLED");
            }
        }

        System.out.println("\n=================================================");
        System.out.println("Dispatch Summary:");
        System.out.printf(" - Total Orders Processed: %d\n", orders.size());
        System.out.printf(" - Orders Fully Completed: %d\n", completedOrders);
        System.out.printf(" - Boxes Used: %d\n", totalBoxesUsed);
        System.out.println("=================================================");
    }

    
    //get all boxes from bays organized by SKU 
    private Map<String, List<Box>> collectInventory(List<Bay> bays) {
        Map<String, List<Box>> map = new HashMap<>();

        for (Bay bay : bays) {
            for (Box box : bay.getBoxesTree().inOrder()) {
                map.computeIfAbsent(box.getSku(), k -> new ArrayList<>()).add(box);
            }
        }

        return map;
    }

    //remove box from all bays
    private void removeBoxFromBays(Box box, List<Bay> bays) {
        for (Bay bay : bays) {
            BST<Box> tree = bay.getBoxesTree();
            tree.remove(box);
        }
    }
}

