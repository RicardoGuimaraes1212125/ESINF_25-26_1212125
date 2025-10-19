package controllers;

import domain.Warehouse;
import services.PrepareOrdersService;

public class PrepareOrdersController {

    private final Warehouse warehouse;
    private final PrepareOrdersService service;

    public PrepareOrdersController(Warehouse warehouse) {
        this.warehouse = warehouse;
        this.service = new PrepareOrdersService();
    }

    public void prepareOrders() {
        service.prepareOrders(warehouse);
    }
}
