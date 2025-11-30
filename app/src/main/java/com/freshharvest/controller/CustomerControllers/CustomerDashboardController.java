package com.freshharvest.controller.CustomerControllers;

import com.freshharvest.app.UserSession;
import com.freshharvest.dao.CustomerDAO;
import com.freshharvest.dao.ProductDAO;
import com.freshharvest.controller.CustomerControllers.CustomerDashboardFacade;
import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;
import com.freshharvest.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CustomerDashboardController implements Initializable, DataChangeListener { 
    
    private NotificationService notificationService; 

    @FXML private Label lblWelcome;
    @FXML private Label lblTotalOrders;
    @FXML private Label lblTotalSpent;
    @FXML private Label lblPendingOrders;
    @FXML private Label lblFavoriteCategory;
    @FXML private VBox chartContainer;
    @FXML private TableView<Order> tblRecentOrders;

    private CustomerDashboardFacade dashboardFacade;
    private int currentCustomerId;

    public static class Order {
        private final int orderId;
        private final String date;
        private final double amount;
        private final String status;
        private final String deliveryDate;

        public Order(int orderId, String date, double amount, String status, String deliveryDate) {
            this.orderId = orderId;
            this.date = date;
            this.amount = amount;
            this.status = status;
            this.deliveryDate = deliveryDate;
        }

        public int getOrderId() { return orderId; }
        public String getDate() { return date; }
        public double getAmount() { return amount; }
        public String getStatus() { return status; }
        public String getDeliveryDate() { return deliveryDate; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            ProductDAO productDAO = new ProductDAO();
            notificationService = NotificationService.getInstance();
            
            this.dashboardFacade = new CustomerDashboardFacade(customerDAO, productDAO, notificationService);
            
            currentCustomerId = getCurrentCustomerId();
            notificationService.addListener(this); 
            
            if (UserSession.getInstance() != null && UserSession.getInstance().getCurrentUserUsername() != null) {
                String username = UserSession.getInstance().getCurrentUserUsername();
                lblWelcome.setText("Welcome back, " + username + "!");
            } else {
                lblWelcome.setText("Welcome back, Customer!");
            }
            
            System.out.println("Initializing dashboard for customer ID: " + currentCustomerId);
            
            loadDashboardData();
            setupTableColumns();
            
        } catch (Exception e) {
            showAlert("Error", "Failed to initialize dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDataChanged(String dataType) {
        switch (dataType) {
            case "ORDER_CREATED":
            case "ORDER_STATUS_CHANGED":
            case "PAYMENT_PROCESSED":
                javafx.application.Platform.runLater(() -> {
                    loadDashboardData();
                    System.out.println("Dashboard automatically refreshed via facade");
                });
                break;
        }
    }

    private int getCurrentCustomerId() {
        try {
            if (UserSession.getInstance() != null && UserSession.getInstance().getCurrentUserUsername() != null) {
                String username = UserSession.getInstance().getCurrentUserUsername();
                int customerId = dashboardFacade.getCustomerIdByUsername(username);
                if (customerId != -1) {
                    return customerId;
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting customer ID from session: " + e.getMessage());
        }
        
        return 3; 
    }

    private void setupTableColumns() {
        try {
            TableColumn<Order, Integer> orderIdCol = (TableColumn<Order, Integer>) tblRecentOrders.getColumns().get(0);
            TableColumn<Order, String> dateCol = (TableColumn<Order, String>) tblRecentOrders.getColumns().get(1);
            TableColumn<Order, String> amountCol = (TableColumn<Order, String>) tblRecentOrders.getColumns().get(2);
            TableColumn<Order, String> statusCol = (TableColumn<Order, String>) tblRecentOrders.getColumns().get(3);
            TableColumn<Order, String> deliveryDateCol = (TableColumn<Order, String>) tblRecentOrders.getColumns().get(4);

            orderIdCol.setCellValueFactory(cellData -> 
                javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getOrderId()));
            dateCol.setCellValueFactory(cellData -> 
                javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().getDate()));
            amountCol.setCellValueFactory(cellData -> 
                javafx.beans.binding.Bindings.createStringBinding(() -> String.format("Rs. %.2f", cellData.getValue().getAmount())));
            statusCol.setCellValueFactory(cellData -> 
                javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().getStatus()));
            deliveryDateCol.setCellValueFactory(cellData -> 
                javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().getDeliveryDate()));
        } catch (Exception e) {
            System.err.println("Error setting up table columns: " + e.getMessage());
        }
    }

    private void loadDashboardData() {
        try {
            DashboardData data = dashboardFacade.getDashboardData(currentCustomerId);
            
            if (data.isSuccess()) {
                // Update statistics
                updateStatistics(data);
                
                // Update recent orders table
                updateRecentOrdersTable(data.getRecentOrders());
                
                System.out.println(" Dashboard loaded successfully via facade");
            } else {
                showAlert("Error", data.getMessage());
            }
            
        } catch (Exception e) {
            showAlert("Error", "Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatistics(DashboardData data) {
        lblTotalOrders.setText(String.valueOf(data.getTotalOrders()));
        lblTotalSpent.setText("Rs. " + String.format("%.2f", data.getTotalSpent()));
        lblPendingOrders.setText(String.valueOf(data.getPendingOrders()));
        lblFavoriteCategory.setText(data.getFavoriteCategory());
        
        System.out.println("Stats updated - Orders: " + data.getTotalOrders() + 
                         ", Spent: " + data.getTotalSpent() + ", Favorite: " + data.getFavoriteCategory());
    }

    private void updateRecentOrdersTable(List<com.freshharvest.app.Customers.CustomerOrder> customerOrders) {
        try {
            ObservableList<Order> orders = FXCollections.observableArrayList();
            
            if (customerOrders != null) {
                for (com.freshharvest.app.Customers.CustomerOrder customerOrder : customerOrders) {
                    orders.add(new Order(
                        customerOrder.getOrderId(),
                        customerOrder.getOrderDate() != null ? customerOrder.getOrderDate().toString() : "N/A",
                        customerOrder.getTotalAmount(),
                        customerOrder.getStatus(),
                        customerOrder.getDeliveryDate() != null ? customerOrder.getDeliveryDate().toString() : "Not set"
                    ));
                }
            }
            
            tblRecentOrders.setItems(orders);
            System.out.println("Updated recent orders table with " + orders.size() + " orders");
            
        } catch (Exception e) {
            System.err.println("Error updating orders table: " + e.getMessage());
        }
    }
@FXML
private void showOrderStatus() {
    try {
        System.out.println("Loading order status distribution via facade for customer: " + currentCustomerId);
        
        OrderStatusData statusData = dashboardFacade.getOrderStatusData(currentCustomerId);
        
        if (!statusData.isSuccess() || statusData.getOrdersData() == null || statusData.getOrdersData().isEmpty()) {
            showNoDataMessage("No order data available. Please place some orders first.");
            return;
        }
        
        List<Object[]> ordersData = statusData.getOrdersData();
        System.out.println("Retrieved " + ordersData.size() + " orders from facade");
        
        // Debug: Print the first row to see what data we have
        if (!ordersData.isEmpty()) {
            Object[] firstRow = ordersData.get(0);
            System.out.println("First row data structure:");
            for (int i = 0; i < firstRow.length; i++) {
                System.out.println("Index " + i + ": " + firstRow[i] + " (type: " + 
                    (firstRow[i] != null ? firstRow[i].getClass().getSimpleName() : "null") + ")");
            }
        }
        
        // Count unique statuses - try different indices to find the status
        Map<String, Integer> statusCounts = new HashMap<>();
        for (Object[] data : ordersData) {
            String status = findOrderStatus(data);
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
        }
        
        // Debug: Print all statuses found
        System.out.println("Found statuses: " + statusCounts.keySet());
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        // Add all unique statuses
        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        if (pieChartData.isEmpty()) {
            showNoDataMessage("No valid order status data available.");
            return;
        }

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Order Status Distribution");
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(false);
        pieChart.setPrefSize(500, 400);
        
        pieChart.setLabelLineLength(20);
        pieChart.setStartAngle(90);

        // Apply colors with meaning annotations
        applyOrderStatusColorsWithMeanings(pieChart);
        
        pieChart.setStyle(
            "-fx-legend-visible: true;" +
            "-fx-legend-side: right;" +
            "-fx-legend-label-font-size: 14px;" +
            "-fx-legend-label-fill: #2c3e50;" +
            "-fx-legend-item-symbol-width: 25px;" +
            "-fx-legend-item-symbol-height: 25px;" +
            "-fx-background-color: transparent;"
        );
        
        updateChartContainer(pieChart);
        
    } catch (Exception e) {
        System.err.println("Error in showOrderStatus: " + e.getMessage());
        e.printStackTrace();
        showAlert("Error", "Failed to load order status: " + e.getMessage());
    }
}


// Check if string is numeric
private boolean isNumeric(String str) {
    if (str == null || str.isEmpty()) return false;
    try {
        Double.parseDouble(str);
        return true;
    } catch (NumberFormatException e) {
        return false;
    }
}

// Check if string is a valid order status
private boolean isValidStatus(String status) {
    if (status == null) return false;
    
    String statusLower = status.toLowerCase();
    return statusLower.contains("pending") ||
           statusLower.contains("confirmed") ||
           statusLower.contains("processing") ||
           statusLower.contains("shipped") ||
           statusLower.contains("delivered") ||
           statusLower.contains("completed") ||
           statusLower.contains("cancelled") ||
           statusLower.contains("canceled") ||
           statusLower.contains("refund") ||
           statusLower.contains("hold") ||
           statusLower.contains("failed") ||
           statusLower.contains("dispatched");
}

// Updated method to get meaning for each order status
private String getOrderStatusMeaning(String status) {
    String normalizedStatus = normalizeStatus(status);
    
    switch (normalizedStatus) {
        case "Delivered": return "";
        case "Shipped": return "";
        case "Processing": return "";
        case "Confirmed": return "";
        case "Pending": return "";
        case "Cancelled": return "";
        case "Refunded": return "";
        case "On Hold": return "";
        default: return "";
    }
}
// Updated color application method for order status - FIXED
private void applyOrderStatusColorsWithMeanings(PieChart pieChart) {
    // Define colors with their meanings for order statuses
    Map<String, String> statusColorMap = new HashMap<>();
    statusColorMap.put("Delivered", "#4c59c0ff");      // Green - Success/Completed
    statusColorMap.put("Completed", "#95a5a6");      // Green - Success
    statusColorMap.put("Shipped", "#2ecc71");        // Blue - In Transit
    statusColorMap.put("Processing", "#f39c12");     // Orange - Being Processed
    statusColorMap.put("Pending", "#9b59b6");        // Yellow - Waiting
    statusColorMap.put("Confirmed", "#e74c3c");      // Yellow - Waiting
    statusColorMap.put("Cancelled", "#f1c40f");      // Red - Failed/Cancelled
    
    // Default colors for any unexpected statuses
    String[] defaultColors = {"#3498db", "#2ecc71", "#e74c3c", "#f39c12", "#9b59b6", "#1abc9c", "#e67e22", "#34495e"};
    
    int defaultColorIndex = 0;
    
    for (PieChart.Data data : pieChart.getData()) {
        String originalStatus = data.getName();
        String normalizedStatus = normalizeStatus(originalStatus);
        
        // Use normalized status for color lookup to ensure consistency
        String color = statusColorMap.getOrDefault(normalizedStatus, defaultColors[defaultColorIndex % defaultColors.length]);
        
        if (data.getNode() != null) {
            data.getNode().setStyle(
                "-fx-pie-color: " + color + ";" +
                "-fx-border-color: white;" +
                "-fx-border-width: 2px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"
            );
        }
        
        // Update the data name to include color meaning and count
        String meaning = getOrderStatusMeaning(originalStatus);
        int orderCount = (int) data.getPieValue();
        
        // Use normalized status for display name to ensure consistent grouping
        data.setName(normalizedStatus + " (" + orderCount + " orders) • " + meaning);
        
        defaultColorIndex++;
    }
    
    // Apply enhanced legend styling with meanings
    applyEnhancedLegendStyling(pieChart);
}

// Enhanced normalizeStatus method to handle more variations
private String normalizeStatus(String status) {
    if (status == null) return "Pending";
    
    String statusLower = status.toLowerCase().trim();
    
    if (statusLower.contains("delivered") || statusLower.contains("completed") || statusLower.equals("delivered") || statusLower.equals("completed")) {
        return "Delivered";
    } else if (statusLower.contains("shipped") || statusLower.contains("dispatched") || statusLower.equals("shipped") || statusLower.equals("dispatched")) {
        return "Shipped";
    } else if (statusLower.contains("processing") || statusLower.equals("processing") || statusLower.equals("process")) {
        return "Processing";
    } else if (statusLower.contains("confirmed") || statusLower.equals("confirmed") || statusLower.equals("confirm")) {
        return "Confirmed";
    } else if (statusLower.contains("cancelled") || statusLower.contains("canceled") || statusLower.contains("failed") || 
               statusLower.equals("cancelled") || statusLower.equals("canceled") || statusLower.equals("failed")) {
        return "Cancelled";
    } else if (statusLower.contains("refund") || statusLower.equals("refunded") || statusLower.equals("refund")) {
        return "Refunded";
    } else if (statusLower.contains("hold") || statusLower.contains("waiting") || statusLower.equals("hold") || statusLower.equals("on hold")) {
        return "On Hold";
    } else if (statusLower.contains("pending") || statusLower.equals("pending") || statusLower.equals("pend")) {
        return "Pending";
    } else {
        System.out.println("Unknown status normalized to Pending: '" + status + "'");
        return "Pending";
    }
}

// Alternative approach: Use the original data structure to find correct status index
// Let's add debug output to see what's actually in the data
private String findOrderStatus(Object[] data) {
    if (data == null) return "Pending";
    
    System.out.println("Searching for status in data row:");
    for (int i = 0; i < data.length; i++) {
        Object item = data[i];
        String itemStr = item != null ? item.toString().trim() : "null";
        System.out.println("  Index " + i + ": '" + itemStr + "'");
    }
    
    // Common indices for status in order data: 
    // Try index 4, 5, or look for specific string patterns
    int[] possibleStatusIndices = {4, 5, 3, 6, 2}; // Try these indices in order
    
    for (int index : possibleStatusIndices) {
        if (index < data.length && data[index] != null) {
            String candidate = data[index].toString().trim();
            if (!candidate.isEmpty() && !isNumeric(candidate) && isValidStatus(candidate)) {
                System.out.println("Found status at index " + index + ": '" + candidate + "'");
                return candidate;
            }
        }
    }
    
    // If no status found, check all indices for any string that might be a status
    for (int i = 0; i < data.length; i++) {
        if (data[i] != null) {
            String candidate = data[i].toString().trim();
            if (!candidate.isEmpty() && !isNumeric(candidate) && isValidStatus(candidate)) {
                System.out.println("Found status at index " + i + ": '" + candidate + "'");
                return candidate;
            }
        }
    }
    
    System.out.println("No valid status found, defaulting to 'Pending'");
    return "Pending";
}

// Method to convert numeric status to meaningful text
private String convertStatusToText(Object statusObj) {
    if (statusObj == null) {
        return "Pending";
    }
    
    try {
        // If it's already a string, return it
        if (statusObj instanceof String) {
            String statusStr = ((String) statusObj).trim();
            if (!statusStr.isEmpty() && !statusStr.matches("\\d+")) {
                return statusStr;
            }
        }
        
        // Convert to string and parse as integer
        String statusStr = statusObj.toString().trim();
        int statusCode;
        
        // Try to parse as integer
        try {
            statusCode = Integer.parseInt(statusStr);
        } catch (NumberFormatException e) {
            // If it's not a number, return the string as is
            System.out.println("Status is not a number: " + statusStr);
            return statusStr.isEmpty() ? "Pending" : statusStr;
        }
        
        // Map numeric status codes to meaningful names
        switch (statusCode) {
            case 0: return "Pending";
            case 1: return "Confirmed";
            case 2: return "Processing";
            case 3: return "Shipped";
            case 4: return "Delivered";
            case 5: return "Cancelled";
            case 6: return "Refunded";
            case 7: return "On Hold";
            case 8: return "Completed";
            case 9: return "Failed";
            default: 
                System.out.println("Unknown status code: " + statusCode);
                return "Pending";
        }
    } catch (Exception e) {
        System.err.println("Error converting status: " + statusObj + " - " + e.getMessage());
        return "Pending";
    }
}


    @FXML
    private void showCategoryAnalysis() {
        try {
            System.out.println("Loading category analysis via facade for customer: " + currentCustomerId);
            
            CategoryData categoryData = dashboardFacade.getCategoryData(currentCustomerId);
            
            BarChart<String, Number> barChart = createBarChart("Spending by Category");
            
            if (categoryData.isSuccess() && categoryData.getCategoryData() != null && !categoryData.getCategoryData().isEmpty()) {
                List<Object[]> data = categoryData.getCategoryData();
                System.out.println("Found " + data.size() + " categories via facade");
                
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Amount Spent");
                
                for (Object[] item : data) {
                    try {
                        String category = item[0] != null ? item[0].toString() : "Unknown";
                        double amount = item[1] != null ? Double.parseDouble(item[1].toString()) : 0.0;
                        series.getData().add(new XYChart.Data<>(category, amount));
                    } catch (Exception e) {
                        System.err.println("Error processing category data: " + e.getMessage());
                    }
                }
                
                barChart.getData().add(series);
            } else {
                showNoDataMessage("No category spending data available.");
                return;
            }
            
            if (barChart.getData().isEmpty() || barChart.getData().get(0).getData().isEmpty()) {
                showNoDataMessage("No category spending data available.");
                return;
            }
            
            updateChartContainer(barChart);
            
        } catch (Exception e) {
            System.err.println("Error in showCategoryAnalysis: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load category analysis: " + e.getMessage());
        }
    }

    @FXML
    private void showPaymentAnalysis() {
        try {
            System.out.println("Loading payment analysis via facade for customer: " + currentCustomerId);
            
            PaymentData paymentData = dashboardFacade.getPaymentData(currentCustomerId);
            
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Payment Methods");
            pieChart.setPrefSize(600, 400);
            pieChart.setLegendVisible(true);
            pieChart.setLabelsVisible(false);
            
            if (paymentData.isSuccess() && paymentData.getPaymentData() != null && !paymentData.getPaymentData().isEmpty()) {
                List<Object[]> data = paymentData.getPaymentData();
                System.out.println("Found " + data.size() + " payment methods via facade");
                
                for (Object[] item : data) {
                    try {
                        String paymentMethod = item[0] != null ? item[0].toString() : "Unknown";
                        double amount = item[1] != null ? Double.parseDouble(item[1].toString()) : 0.0;
                        pieChart.getData().add(new PieChart.Data(paymentMethod, amount));
                    } catch (Exception e) {
                        System.err.println("Error processing payment data: " + e.getMessage());
                    }
                }
            } else {
                showNoDataMessage("No payment method data available.");
                return;
            }
            
            if (pieChart.getData().isEmpty()) {
                showNoDataMessage("No payment method data available.");
                return;
            }
            
            // Apply payment method colors with meanings
            applyPaymentMethodColorsWithMeanings(pieChart);
            updateChartContainer(pieChart);
            
        } catch (Exception e) {
            System.err.println("Error in showPaymentAnalysis: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load payment analysis: " + e.getMessage());
        }
    }

    // New method specifically for payment method colors with meanings
    private void applyPaymentMethodColorsWithMeanings(PieChart pieChart) {
        // Define colors with their meanings for payment methods
        Map<String, String> paymentColorMap = new HashMap<>();
        paymentColorMap.put("Credit Card", "#3498db");      // Blue - Credit
        paymentColorMap.put("Debit Card", "#2ecc71");       // Green - Debit
        paymentColorMap.put("PayPal", "#003087");           // Dark Blue - PayPal
        paymentColorMap.put("Cash", "#27ae60");             // Green - Cash
        paymentColorMap.put("Bank Transfer", "#9b59b6");    // Purple - Bank
        paymentColorMap.put("Digital Wallet", "#e67e22");   // Orange - Digital
        paymentColorMap.put("COD", "#e74c3c");              // Red - Cash on Delivery
        
        String[] defaultColors = {"#3498db", "#2ecc71", "#e74c3c", "#f39c12", "#9b59b6", "#1abc9c", "#e67e22"};
        
        int defaultColorIndex = 0;
        
        for (PieChart.Data data : pieChart.getData()) {
            String paymentMethod = data.getName();
            String color = paymentColorMap.getOrDefault(paymentMethod, defaultColors[defaultColorIndex % defaultColors.length]);
            
            if (data.getNode() != null) {
                data.getNode().setStyle(
                    "-fx-pie-color: " + color + ";" +
                    "-fx-border-color: white;" +
                    "-fx-border-width: 2px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"
                );
            }
            
            String meaning = getPaymentMethodMeaning(paymentMethod);
            double amount = data.getPieValue();
            data.setName(paymentMethod + " (Rs. " + String.format("%.2f", amount) + ") • " + meaning);
            
            defaultColorIndex++;
        }
        
        applyEnhancedLegendStyling(pieChart);
    }

    private String getPaymentMethodMeaning(String paymentMethod) {
        if (paymentMethod == null) return "Unknown Payment Method";
        
        String methodLower = paymentMethod.toLowerCase();
        
        if (methodLower.contains("credit")) {
            return "";
        } else if (methodLower.contains("debit")) {
            return "Paid via debit card";
        } else if (methodLower.contains("paypal")) {
            return "Paid via PayPal";
        } else if (methodLower.contains("cash")) {
            return "";
        } else if (methodLower.contains("bank")) {
            return "Paid via bank transfer";
        } else if (methodLower.contains("digital") || methodLower.contains("wallet")) {
            return "Paid via digital wallet";
        } else if (methodLower.contains("cod")) {
            return "Cash on Delivery";
        } else {
            return "Payment method";
        }
    }

    // Enhanced legend styling for both pie charts
    private void applyEnhancedLegendStyling(PieChart pieChart) {
        pieChart.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // Wait for the layout to be complete
                javafx.application.Platform.runLater(() -> {
                    // Style the legend container
                    newScene.getRoot().lookupAll(".chart-legend").forEach(node -> {
                        node.setStyle(
                            "-fx-background-color: #f8f9fa;" +
                            "-fx-padding: 15px;" +
                            "-fx-border-color: #e9ecef;" +
                            "-fx-border-width: 1px;" +
                            "-fx-border-radius: 8px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
                        );
                    });
                    
                    // Style legend items to handle the formatted text
                    newScene.getRoot().lookupAll(".chart-legend-item").forEach(node -> {
                        node.setStyle(
                            "-fx-text-fill: #2c3e50;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 5px 0px;"
                        );
                        
                        // Add tooltips for more detailed information
                        Tooltip tooltip = new Tooltip();
                        String itemText = ((Labeled) node).getText();
                        if (itemText.contains("•")) {
                            String[] parts = itemText.split("•");
                            if (parts.length == 2) {
                                String statusInfo = parts[0].trim(); // e.g., "Delivered (5 orders)"
                                String meaning = parts[1].trim();    // e.g., "Order successfully delivered"
                                tooltip.setText(statusInfo + "\n" + meaning);
                                Tooltip.install(node, tooltip);
                            }
                        }
                    });
                    
                    // Style the color dots in legend to be smaller and cleaner
                    newScene.getRoot().lookupAll(".chart-legend-item-symbol").forEach(symbol -> {
                        symbol.setStyle(
                            "-fx-pref-width: 12px;" +
                            "-fx-pref-height: 12px;" +
                            "-fx-border-radius: 6px;" +
                            "-fx-background-radius: 6px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"
                        );
                    });
                });
            }
        });
    }

    @FXML
    private void showMonthlySpending() {
        try {
            System.out.println("Loading monthly spending via facade for customer: " + currentCustomerId);
            
            // Create a simple line chart for monthly spending trend
            LineChart<String, Number> lineChart = createLineChart("Monthly Spending Trend");
            
            // Sample data - you can replace this with actual data from your facade
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Monthly Spending");
            
            // Sample monthly data
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
            double[] amounts = {1200, 1800, 1500, 2200, 1900, 2100};
            
            for (int i = 0; i < months.length; i++) {
                series.getData().add(new XYChart.Data<>(months[i], amounts[i]));
            }
            
            lineChart.getData().add(series);
            updateChartContainer(lineChart);
            
        } catch (Exception e) {
            System.err.println("Error in showMonthlySpending: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load monthly spending: " + e.getMessage());
        }
    }

    @FXML
    private void refreshAllData() {
        try {
            System.out.println("Refreshing all data via facade for customer: " + currentCustomerId);
            loadDashboardData();
            showAlert("Success", "Dashboard data refreshed!");
        } catch (Exception e) {
            showAlert("Error", "Failed to refresh data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper methods for clean charts
    private BarChart<String, Number> createBarChart(String title) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Categories");
        yAxis.setLabel("Amount (Rs)");
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(title);
        barChart.setLegendVisible(false);
        barChart.setPrefSize(600, 400);
        
        return barChart;
    }

    private LineChart<String, Number> createLineChart(String title) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");
        yAxis.setLabel("Amount (Rs)");
        
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);
        lineChart.setCreateSymbols(true);
        lineChart.setPrefSize(600, 400);
        
        return lineChart;
    }

    private void updateChartContainer(javafx.scene.Node chart) {
        try {
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(chart);
        } catch (Exception e) {
            System.err.println("Error updating chart container: " + e.getMessage());
        }
    }

    private void showNoDataMessage(String message) {
        try {
            chartContainer.getChildren().clear();
            Label noDataLabel = new Label(message);
            noDataLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-padding: 20px;");
            chartContainer.getChildren().add(noDataLabel);
        } catch (Exception e) {
            System.err.println("Error showing no data message: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Remove old unused methods
    private void applyCleanPieChartColors(PieChart pieChart) {
        // This method is no longer used - replaced by enhanced methods
        String[] cleanColors = {"#3498db", "#2ecc71", "#e74c3c", "#f39c12", "#9b59b6", "#1abc9c"};
        
        int colorIndex = 0;
        for (PieChart.Data data : pieChart.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle(
                    "-fx-pie-color: " + cleanColors[colorIndex % cleanColors.length] + ";" +
                    "-fx-border-color: white;" +
                    "-fx-border-width: 2px;"
                );
                colorIndex++;
            }
        }
    }
}