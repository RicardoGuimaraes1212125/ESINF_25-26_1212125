package controllers;

import domain.Warehouse;
import services.UnloadWagonsService;

import java.util.List;

public class UnloadWagonsController {

    private final Warehouse warehouse;
    private final UnloadWagonsService service;

    public UnloadWagonsController(Warehouse warehouse) {
        this.warehouse = warehouse;
        this.service = new UnloadWagonsService();
    }

    public List<String> unloadWagons() {
        return service.unload(warehouse);
    }
}


