package ui;

import controllers.PrepareOrdersController;
import domain.Warehouse;

public class PrepareOrdersUI {

    private final PrepareOrdersController controller;

    public PrepareOrdersUI(Warehouse warehouse) {
        this.controller = new PrepareOrdersController(warehouse);
    }

    public void run() {
        System.out.println("\n=== USEI02 — Prepare Orders for Dispatch ===");
        controller.prepareOrders();
    }
}

