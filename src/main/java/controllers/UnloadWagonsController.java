package controllers;

import domain.Warehouse;
import services.UnloadWagonsService;

public class UnloadWagonsController {

    private final Warehouse warehouse;
    private final UnloadWagonsService service;

    public UnloadWagonsController(Warehouse warehouse) {
        this.warehouse = warehouse;
        this.service = new UnloadWagonsService();
    }

    public void unloadWagons() {
        service.unload(warehouse);
    }
}
