package domain;

import java.util.*;

public class Warehouse {

    private final List<Bay> bays = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();
    private final List<Wagon> wagons = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<Return> returns = new ArrayList<>();

    public List<Bay> getAllBays() {
        return bays;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Wagon> getWagons() {
        return wagons;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Return> getReturns() {
        return returns;
    }

    public void addBay(Bay b) {
        bays.add(b);
    }

    public void addItem(Item i) {
        items.add(i);
    }

    public void addWagon(Wagon w) {
        wagons.add(w);
    }

    public void addOrder(Order o) {
        orders.add(o);
    }

    public void addReturn(Return r) {
        returns.add(r);
    }

    public Map<String, List<Bay>> getWarehouses() {
        Map<String, List<Bay>> map = new HashMap<>();
        for (Bay b : bays) {
            map.computeIfAbsent(b.getWarehouseId(), k -> new ArrayList<>()).add(b);
        }
        return map;
    }
}

