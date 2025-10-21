package controllers;

import java.util.List;

import domain.Warehouse;
import dto.PrepareOrdersDTO;
import services.PrepareOrdersService;

public class PrepareOrdersController {

    private final Warehouse warehouse;
    private final PrepareOrdersService service;

    public PrepareOrdersController(Warehouse warehouse) {
        this.warehouse = warehouse;
        this.service = new PrepareOrdersService();
    }

    public List<PrepareOrdersDTO> prepareOrders() {
        return service.prepareOrders(warehouse);
    }
}

