package domain;

import java.util.*;

import utils.BST;

public class Warehouse {

    private List<Bay> bays = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
    private List<Wagon> wagons = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private List<Return> returns = new ArrayList<>();

    private Map<String, BST<Box>> inventoryBySku = new HashMap<>();

    public void indexInventory() {
        inventoryBySku.clear();
        for (Bay bay : bays) {
            for (Box box : bay.getBoxesTree().inOrder()) {
                inventoryBySku.computeIfAbsent(box.getSku(), k -> new BST<>()).insert(box);
            }
        }
    }

    public BST<Box> getBoxesForSku(String sku) {
        return inventoryBySku.get(sku);
    }

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

     public void setBays(List<Bay> bays) {
        this.bays = bays != null ? bays : new ArrayList<>();
    }

    public void setItems(List<Item> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public void setWagons(List<Wagon> wagons) {
        this.wagons = wagons != null ? wagons : new ArrayList<>();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
    }

    public void setReturns(List<Return> returns) {
        this.returns = returns != null ? returns : new ArrayList<>();
    }
}

