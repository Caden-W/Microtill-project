import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class POSSystem {
    private static final String DATA_FILE = "pos_data.json";
    private JSONObject data;
    
    public POSSystem() {
        loadData();
    }

    private void loadData() {
        try {
            JSONParser parser = new JSONParser();
            if (new File(DATA_FILE).exists()) {
                data = (JSONObject) parser.parse(new FileReader(DATA_FILE));
            } else {
                data = initializeNewData();
                saveData();
            }
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            data = initializeNewData();
        }
    }

    private JSONObject initializeNewData() {
        JSONObject newData = new JSONObject();
        newData.put("tables", new JSONObject());
        newData.put("sales_history", new JSONObject());
        newData.put("total_revenue", 0.0);
        return newData;
    }

    private void saveData() {
        try (FileWriter file = new FileWriter(DATA_FILE)) {
            file.write(data.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    public void addOrder(String tableNumber, List<OrderItem> items) {
        JSONObject tables = (JSONObject) data.get("tables");
        JSONObject tableOrder = new JSONObject();
        JSONArray orderItems = new JSONArray();
        
        double total = 0.0;
        for (OrderItem item : items) {
            JSONObject orderItem = new JSONObject();
            orderItem.put("name", item.getName());
            orderItem.put("price", item.getPrice());
            orderItem.put("quantity", item.getQuantity());
            orderItems.add(orderItem);
            total += item.getPrice() * item.getQuantity();
        }

        tableOrder.put("items", orderItems);
        tableOrder.put("total", total);
        tableOrder.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        tables.put(tableNumber, tableOrder);
        saveData();
    }

    public void clearTable(String tableNumber) {
        JSONObject tables = (JSONObject) data.get("tables");
        tables.remove(tableNumber);
        saveData();
    }

    public void completeSale(String tableNumber) {
        JSONObject tables = (JSONObject) data.get("tables");
        JSONObject tableOrder = (JSONObject) tables.get(tableNumber);
        
        if (tableOrder != null) {
            updateSalesHistory(tableOrder);
            clearTable(tableNumber);
        }
    }

    private void updateSalesHistory(JSONObject order) {
        JSONObject salesHistory = (JSONObject) data.get("sales_history");
        JSONArray items = (JSONArray) order.get("items");
        double totalRevenue = (double) data.get("total_revenue");

        for (Object item : items) {
            JSONObject orderItem = (JSONObject) item;
            String itemName = (String) orderItem.get("name");
            double price = (double) orderItem.get("price");
            long quantity = (long) orderItem.get("quantity");

            JSONObject itemHistory = (JSONObject) salesHistory.get(itemName);
            if (itemHistory == null) {
                itemHistory = new JSONObject();
                itemHistory.put("quantity", 0L);
                itemHistory.put("revenue", 0.0);
            }

            itemHistory.put("quantity", (long) itemHistory.get("quantity") + quantity);
            itemHistory.put("revenue", (double) itemHistory.get("revenue") + (price * quantity));
            salesHistory.put(itemName, itemHistory);
        }

        totalRevenue += (double) order.get("total");
        data.put("total_revenue", totalRevenue);
        saveData();
    }

    public JSONObject getSalesHistory() {
        return (JSONObject) data.get("sales_history");
    }

    public double getTotalRevenue() {
        return (double) data.get("total_revenue");
    }

    public JSONObject getTableOrders() {
        return (JSONObject) data.get("tables");
    }
}

class OrderItem {
    private String name;
    private double price;
    private int quantity;

    public OrderItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
} 