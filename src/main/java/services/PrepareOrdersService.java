package services;

import domain.*;
import dto.PrepareOrdersDTO;
import utils.BST;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PrepareOrdersService {

    public List<PrepareOrdersDTO> prepareOrders(Warehouse warehouse) {

        List<Order> orders = warehouse.getOrders();
        List<Bay> bays = warehouse.getAllBays();
        List<PrepareOrdersDTO> results = new ArrayList<>();

        if (orders.isEmpty()) {
            return results;
        }

        LocalDate currentDate = LocalDate.of(2025, 9, 28);
        Map<String, List<Box>> inventory = collectInventory(bays);

        for (Order order : orders) {

            boolean complete = true;
            List<PrepareOrdersDTO.LineResult> lineResults = new ArrayList<>();

            for (OrderLine line : order.getLines()) {
                String sku = line.getSku();
                int qtyRequired = line.getQuantity();

                //Filter and sort boxes by FEFO/FIFO
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

                boolean sufficient = qtyPicked >= qtyRequired;
                if (!sufficient) complete = false;

                lineResults.add(new PrepareOrdersDTO.LineResult(
                        sku, qtyRequired, qtyPicked, sufficient
                ));

                for (Box used : boxesUsed) {
                    removeBoxFromBays(used, bays);
                    inventory.get(sku).remove(used);
                }
            }

            results.add(new PrepareOrdersDTO(
                    order.getOrderId(),
                    order.getDueDate(),
                    order.getPriority(),
                    complete,
                    lineResults
            ));
        }

        return results;
    }

    private Map<String, List<Box>> collectInventory(List<Bay> bays) {
        Map<String, List<Box>> map = new HashMap<>();
        for (Bay bay : bays) {
            for (Box box : bay.getBoxesTree().inOrder()) {
                map.computeIfAbsent(box.getSku(), k -> new ArrayList<>()).add(box);
            }
        }
        return map;
    }

    private void removeBoxFromBays(Box box, List<Bay> bays) {
        for (Bay bay : bays) {
            BST<Box> tree = bay.getBoxesTree();
            tree.remove(box);
        }
    }
}
