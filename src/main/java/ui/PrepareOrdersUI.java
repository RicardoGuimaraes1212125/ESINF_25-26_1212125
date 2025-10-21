package ui;

import java.util.List;

import controllers.PrepareOrdersController;
import domain.Warehouse;
import dto.PrepareOrdersDTO;

public class PrepareOrdersUI {

    private final PrepareOrdersController controller;

    public PrepareOrdersUI(Warehouse warehouse) {
        this.controller = new PrepareOrdersController(warehouse);
    }

    public void run() {
        System.out.println("\n=== USEI02 — Prepare Orders for Dispatch ===");

        List<PrepareOrdersDTO> dtos = controller.prepareOrders();

        if (dtos.isEmpty()) {
            System.out.println("No orders available.");
            return;
        }

        for (PrepareOrdersDTO dto : dtos) {
            System.out.println("\n--------------------------------------------");
            System.out.printf("Order: %s | Due: %s | Priority: %d\n",
                    dto.orderId, dto.dueDate, dto.priority);

            for (PrepareOrdersDTO.LineResult line : dto.results) {
                System.out.printf("  SKU: %-6s | Requested: %-3d | Supplied: %-3d | %s\n",
                        line.sku, line.requested, line.supplied,
                        line.sufficient ? "OK" : "INSUFFICIENT");
            }

            System.out.println("Status: " + (dto.fullyCompleted ? "COMPLETED" : "PARTIALLY FULFILLED"));
        }
    }
}
