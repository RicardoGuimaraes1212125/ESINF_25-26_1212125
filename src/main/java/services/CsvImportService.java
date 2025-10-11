package services;

import domain.*;
import utils.CsvReader;
import java.io.File;
import java.net.URL;
import java.util.List;

public class CsvImportService {

    private final String basePath;

    public CsvImportService(String basePath) {
        this.basePath = basePath;
    }

    public Warehouse importAll() {
        Warehouse warehouse = new Warehouse();

        List<Bay> bays = CsvReader.readBays(getResourcePath("data/bays.csv"));
        List<Item> items = CsvReader.readItems(getResourcePath("data/items.csv"));
        List<Wagon> wagons = CsvReader.readWagons(getResourcePath("data/wagons.csv"));
        List<Order> orders = CsvReader.readOrders(
                getResourcePath("data/orders.csv"),
                getResourcePath("data/order_lines.csv"));
        List<Return> returns = CsvReader.readReturns(getResourcePath("data/returns.csv"));

        warehouse.getAllBays().addAll(bays);
        warehouse.getItems().addAll(items);
        warehouse.getWagons().addAll(wagons);
        warehouse.getOrders().addAll(orders);
        warehouse.getReturns().addAll(returns);

        return warehouse;
    }

    private String getResourcePath(String relativePath) {
        URL resource = getClass().getClassLoader().getResource(relativePath);
        if (resource == null) {
            throw new RuntimeException("Ficheiro não encontrado: " + relativePath);
        }
        return new File(resource.getFile()).getAbsolutePath();
    }
}




