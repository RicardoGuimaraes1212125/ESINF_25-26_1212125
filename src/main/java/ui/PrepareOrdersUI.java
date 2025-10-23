package ui;

import controllers.PrepareOrdersController;
import domain.AllocationMode;
import dto.PrepareOrdersDTO;
import dto.AllocationRowDTO;

import java.util.List;
import java.util.Scanner;

public class PrepareOrdersUI {

    private final PrepareOrdersController controller;

    public PrepareOrdersUI(PrepareOrdersController controller) {
        this.controller = controller;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        AllocationMode mode = askMode(scanner);
        
        System.out.println("\n--- ORDER SUMMARIES ---\n");

        PrepareOrdersController.PrepareResultDTO result = controller.execute(mode);

        //summary
        for (PrepareOrdersDTO summary : result.getSummaries()) {
            String status = summary.isAllEligible() ? "COMPLETED" : "INCOMPLETE";
            System.out.printf("Order: %s | Due: %s | Priority: %d | %s%n",
                    summary.getOrderId(), summary.getDueDate(), summary.getPriority(), status);

            for (PrepareOrdersDTO.LineResult line : summary.getLineResults()) {
                System.out.printf("   Line %02d | SKU: %s | Req: %d | Alloc: %d | Status: %s%n",
                        line.getLineNo(), line.getSku(), line.getRequestedQty(),
                        line.getAllocatedQty(), line.getStatus());
            }
        }

        //show allocation rows
        System.out.println("\n--- ALLOCATION ROWS ---\n");
        List<AllocationRowDTO> allocations = result.getAllocations();
        if (allocations.isEmpty()) {
            System.out.println("No allocations performed.");
        } else {
            for (AllocationRowDTO a : allocations) {
                System.out.printf("Order %s | Line %02d | SKU %s | Qty %d | Box %s | Bay %s/%d/%d%n",
                        a.getOrderId(), a.getLineNo(), a.getSku(),
                        a.getQty(), a.getBoxId(), a.getWarehouseId(),
                        a.getAisle(), a.getBayNumber());
            }
        }

        System.out.println("\nOperation complete.\n");
    }

    private AllocationMode askMode(Scanner scanner) {
        System.out.print("Mode (S=Strict / P=Partial): ");
        String input = scanner.nextLine().trim().toLowerCase();
        if (input.startsWith("p")) return AllocationMode.PARTIAL;
        return AllocationMode.STRICT;
    }
}
