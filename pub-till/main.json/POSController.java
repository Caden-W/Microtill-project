import spark.Spark;
import com.google.gson.Gson;

public class POSController {
    private static POSSystem posSystem = new POSSystem();
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        Spark.port(4567);
        
        // Enable CORS
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type");
        });

        // API endpoints
        Spark.get("/tables", (req, res) -> {
            res.type("application/json");
            return gson.toJson(posSystem.getTableOrders());
        });

        Spark.post("/order/:table", (req, res) -> {
            String tableNumber = req.params(":table");
            OrderRequest orderRequest = gson.fromJson(req.body(), OrderRequest.class);
            posSystem.addOrder(tableNumber, orderRequest.getItems());
            return "Order added successfully";
        });

        Spark.delete("/table/:table", (req, res) -> {
            String tableNumber = req.params(":table");
            posSystem.clearTable(tableNumber);
            return "Table cleared successfully";
        });

        Spark.post("/complete/:table", (req, res) -> {
            String tableNumber = req.params(":table");
            posSystem.completeSale(tableNumber);
            return "Sale completed successfully";
        });

        Spark.get("/sales", (req, res) -> {
            res.type("application/json");
            JSONObject response = new JSONObject();
            response.put("history", posSystem.getSalesHistory());
            response.put("total_revenue", posSystem.getTotalRevenue());
            return response.toJSONString();
        });
    }
}

class OrderRequest {
    private List<OrderItem> items;
    public List<OrderItem> getItems() { return items; }
} 