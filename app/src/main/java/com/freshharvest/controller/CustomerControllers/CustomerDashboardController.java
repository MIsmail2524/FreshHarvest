package com.freshharvest.controller.CustomerControllers;

import com.freshharvest.app.UserSession;
import com.freshharvest.dao.CustomerDAO;
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

    private final CustomerDAO customerDAO = new CustomerDAO();
    private int currentCustomerId;

    // Order class for table data
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
            // Get current customer ID from session
            currentCustomerId = getCurrentCustomerId();
             notificationService = NotificationService.getInstance(); 
            notificationService.addListener(this); 
            
            if (UserSession.getInstance() != null && UserSession.getInstance().getCurrentUserUsername() != null) {
                String username = UserSession.getInstance().getCurrentUserUsername();
                lblWelcome.setText("Welcome back, " + username + "!");
            } else {
                lblWelcome.setText("Welcome back, Customer!");
            }
            
            System.out.println("Initializing dashboard for customer ID: " + currentCustomerId);
            
            // Load real data from database
            refreshAllData();
            
            // Setup table columns
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
                    refreshAllData();
                    System.out.println("Dashboard automatically refreshed");
                });
                break;
        }}

    private int getCurrentCustomerId() {
        try {
            // Try to get customer ID from username
            if (UserSession.getInstance() != null && UserSession.getInstance().getCurrentUserUsername() != null) {
                String username = UserSession.getInstance().getCurrentUserUsername();
                int customerId = customerDAO.getCustomerIdByUsername(username);
                if (customerId != -1) {
                    return customerId;
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting customer ID from session: " + e.getMessage());
        }
        
        // Fallback: Use a default customer ID for testing
        return 3; // Default customer ID
    }

    private void setupTableColumns() {
        try {
            // Configure table columns to use Order properties
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
@FXML
private void showOrderStatus() {
    try {
        System.out.println("Loading order status distribution for customer: " + currentCustomerId);
        
        List<Object[]> ordersData = customerDAO.getAllCustomerOrders(currentCustomerId);
        System.out.println("Retrieved " + ordersData.size() + " orders from database");
        
        if (ordersData.isEmpty()) {
            showNoDataMessage("No order data available. Please place some orders first.");
            return;
        }
        
        // Count each unique status
        Map<String, Integer> statusCounts = new HashMap<>();
        for (Object[] data : ordersData) {
            String status = data[3] != null ? data[3].toString().trim() : "Pending";
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
        }
        
        System.out.println("=== EXACT STATUS COUNTS ===");
        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            System.out.println("'" + entry.getKey() + "': " + entry.getValue());
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        // Use the exact status names from database
        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            String status = entry.getKey();
            int count = entry.getValue();
            pieChartData.add(new PieChart.Data(status + " (" + count + ")", count));
        }

        if (pieChartData.isEmpty()) {
            showNoDataMessage("No valid order status data available.");
            return;
        }

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Order Status Distribution");
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);
        pieChart.setPrefSize(700, 350);

        // Add colors to chart slices based on status
        for (PieChart.Data data : pieChartData) {
            String label = data.getName();
            String statusName = label.substring(0, label.indexOf('(')).trim().toLowerCase();
            
            String color = getFlexibleStatusColor(statusName);
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            
            System.out.println("Setting color for " + statusName + ": " + color);
        }
        
        updateChartContainer(pieChart);
        
    } catch (Exception e) {
        System.err.println("Error in showOrderStatus: " + e.getMessage());
        e.printStackTrace();
        showAlert("Error", "Failed to load order status: " + e.getMessage());
    }
}

// CORRECTED: Fixed color mapping for Pending, Delivered, Completed
private String getFlexibleStatusColor(String status) {
    status = status.toLowerCase();
    
    if (status.contains("pending")) {
        return "#f39c12"; // Orange for Pending
    } else if (status.contains("delivered")) {
        return "#27ae60"; // Green for Delivered  
    } else if (status.contains("completed")) {
        return "#27ae60"; // Green for Completed (same as delivered)
    } else if (status.contains("accept") || status.contains("confirm")) {
        return "#3498db"; // Blue
    } else if (status.contains("process")) {
        return "#9b59b6"; // Purple
    } else if (status.contains("ship") || status.contains("dispatch")) {
        return "#e67e22"; // Dark Orange
    } else if (status.contains("cancel") || status.contains("reject")) {
        return "#e74c3c"; // Red
    } else {
        return "#95a5a6"; // Gray for unknown
    }
}
private String getStatusColor(String status) {
    switch (status) {
        case "pending": return "#f39c12"; // Orange
        case "accepted": return "#3498db"; // Blue
        case "processing": return "#9b59b6"; // Purple
        case "shipped": return "#e67e22"; // Dark Orange
        case "delivered": return "#27ae60"; // Green
        case "cancelled": return "#e74c3c"; // Red
        default: return "#95a5a6"; // Gray
    }
}


    @FXML
    private void showCategoryAnalysis() {
        try {
            System.out.println("Loading category analysis for customer: " + currentCustomerId);
            
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Spending by Category");
            pieChart.setPrefSize(700, 350);
            
            // Get real data from database
            List<Object[]> categoryData = customerDAO.getSpendingByCategory(currentCustomerId);
            System.out.println("Found " + categoryData.size() + " categories");
            
            if (categoryData.isEmpty()) {
                // Show sample data if no real data
                System.out.println("No category data found, using sample data");
                pieChart.getData().add(new PieChart.Data("Vegetables (Rs.85.50)", 85.50));
                pieChart.getData().add(new PieChart.Data("Fruits (Rs.42.30)", 42.30));
                pieChart.getData().add(new PieChart.Data("Dairy (Rs.15.75)", 15.75));
                pieChart.getData().add(new PieChart.Data("Grains (Rs.28.90)", 28.90));
            } else {
                for (Object[] data : categoryData) {
                    try {
                        String category = data[0] != null ? data[0].toString() : "Unknown";
                        double amount = data[1] != null ? Double.parseDouble(data[1].toString()) : 0.0;
                        pieChart.getData().add(new PieChart.Data(category + " (Rs." + String.format("%.2f", amount) + ")", amount));
                        System.out.println("Category: " + category + ", Amount: " + amount);
                    } catch (Exception e) {
                        System.err.println("Error processing category data: " + e.getMessage());
                    }
                }
            }
            
            if (pieChart.getData().isEmpty()) {
                showNoDataMessage("No category spending data available.");
                return;
            }
            
            updateChartContainer(pieChart);
            
        } catch (Exception e) {
            System.err.println("Error in showCategoryAnalysis: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load category analysis: " + e.getMessage());
        }
    }

    @FXML
    private void showPaymentAnalysis() {
        try {
            System.out.println("Loading payment analysis for customer: " + currentCustomerId);
            
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Cash vs Credit Payment Analysis");
            pieChart.setPrefSize(700, 350);
            
            // Get real data from database
            List<Object[]> paymentData = customerDAO.getPaymentMethodDistribution(currentCustomerId);
            System.out.println("Found " + paymentData.size() + " payment methods");
            
            if (paymentData.isEmpty()) {
                // Show sample data if no real data
                System.out.println("No payment data found, using sample data");
                pieChart.getData().add(new PieChart.Data("Cash (Rs.12,500)", 12500.0));
                pieChart.getData().add(new PieChart.Data("Credit (Rs.8,500)", 8500.0));
            } else {
                for (Object[] data : paymentData) {
                    try {
                        String paymentMethod = data[0] != null ? data[0].toString() : "Unknown";
                        double amount = data[1] != null ? Double.parseDouble(data[1].toString()) : 0.0;
                        pieChart.getData().add(new PieChart.Data(paymentMethod + " (Rs." + String.format("%.0f", amount) + ")", amount));
                        System.out.println("Payment Method: " + paymentMethod + ", Amount: " + amount);
                    } catch (Exception e) {
                        System.err.println("Error processing payment data: " + e.getMessage());
                    }
                }
            }
            
            if (pieChart.getData().isEmpty()) {
                showNoDataMessage("No payment method data available.");
                return;
            }
            
            updateChartContainer(pieChart);
            
        } catch (Exception e) {
            System.err.println("Error in showPaymentAnalysis: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load payment analysis: " + e.getMessage());
        }
    }

    @FXML
    private void refreshAllData() {
        try {
            System.out.println("Refreshing all data for customer: " + currentCustomerId);
            
            // Update stats with real data
            updateStats();
            
            // Refresh recent orders table with real data
            refreshRecentOrders();
            
            showAlert("Success", "Dashboard data refreshed!");
            
        } catch (Exception e) {
            showAlert("Error", "Failed to refresh data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStats() {
        try {
            // Get customer statistics from database using the working method
            Object[] stats = customerDAO.getOrderStatistics(currentCustomerId);
            
            if (stats != null) {
                int totalOrders = (Integer) stats[0];
                int pendingOrders = (Integer) stats[1];
                int deliveredOrders = (Integer) stats[2];
                double totalSpent = (Double) stats[3];
                
                lblTotalOrders.setText(String.valueOf(totalOrders));
                lblTotalSpent.setText("Rs. " + String.format("%.2f", totalSpent));
                lblPendingOrders.setText(String.valueOf(pendingOrders));
                
                // Get favorite category separately
                String favoriteCategory = getFavoriteCategory(currentCustomerId);
                lblFavoriteCategory.setText(favoriteCategory != null ? favoriteCategory : "No data");
                
                System.out.println("Stats updated - Orders: " + totalOrders + ", Spent: " + totalSpent + 
                                 ", Pending: " + pendingOrders + ", Favorite: " + favoriteCategory);
            } else {
                // Fallback: Calculate from orders data
                calculateStatsFromOrders();
            }
        } catch (Exception e) {
            System.err.println("Error updating stats: " + e.getMessage());
            // Fallback: Calculate from orders data
            calculateStatsFromOrders();
        }
    }

    private void calculateStatsFromOrders() {
        try {
            List<Object[]> ordersData = customerDAO.getAllCustomerOrders(currentCustomerId);
            
            int totalOrders = ordersData.size();
            double totalSpent = 0.0;
            int pendingOrders = 0;
            
            for (Object[] order : ordersData) {
                double amount = (Double) order[4]; // total_amount is at index 4
                totalSpent += amount;
                
                String status = (String) order[3]; // status is at index 3
                if ("PENDING".equalsIgnoreCase(status) || "PROCESSING".equalsIgnoreCase(status)) {
                    pendingOrders++;
                }
            }
            
            lblTotalOrders.setText(String.valueOf(totalOrders));
            lblTotalSpent.setText("Rs. " + String.format("%.2f", totalSpent));
            lblPendingOrders.setText(String.valueOf(pendingOrders));
            
            String favoriteCategory = getFavoriteCategory(currentCustomerId);
            lblFavoriteCategory.setText(favoriteCategory != null ? favoriteCategory : "No data");
            
            System.out.println("Calculated stats from orders - Total: " + totalOrders + 
                             ", Spent: " + totalSpent + ", Pending: " + pendingOrders);
            
        } catch (Exception e) {
            System.err.println("Error calculating stats from orders: " + e.getMessage());
            // Set default values
            lblTotalOrders.setText("0");
            lblTotalSpent.setText("Rs. 0.00");
            lblPendingOrders.setText("0");
            lblFavoriteCategory.setText("No data");
        }
    }

    private String getFavoriteCategory(int customerId) {
        try {
            List<Object[]> categoryData = customerDAO.getSpendingByCategory(customerId);
            if (!categoryData.isEmpty()) {
                // Return the category with highest spending (first one since it's ordered by DESC)
                return categoryData.get(0)[0].toString();
            }
        } catch (Exception e) {
            System.err.println("Error getting favorite category: " + e.getMessage());
        }
        return null;
    }

    private void refreshRecentOrders() {
        try {
            // Get recent orders from database
            List<Object[]> recentOrdersData = customerDAO.getRecentOrders(currentCustomerId, 5);
            ObservableList<Order> orders = FXCollections.observableArrayList();
            
            System.out.println("Found " + recentOrdersData.size() + " recent orders");
            
            for (Object[] data : recentOrdersData) {
                int orderId = (Integer) data[0];
                String date = data[1].toString();
                double amount = (Double) data[2];
                String status = (String) data[3];
                String deliveryDate = data[4] != null ? data[4].toString() : "Not set";
                
                orders.add(new Order(orderId, date, amount, status, deliveryDate));
                System.out.println("Order: " + orderId + ", Date: " + date + ", Amount: " + amount);
            }
            
            tblRecentOrders.setItems(orders);
            
        } catch (Exception e) {
            System.err.println("Error refreshing orders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateChartContainer(javafx.scene.Node chart) {
        try {
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(chart);
            System.out.println("Chart updated successfully");
        } catch (Exception e) {
            System.err.println("Error updating chart container: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showNoDataMessage(String message) {
        try {
            chartContainer.getChildren().clear();
            Label noDataLabel = new Label(message);
            noDataLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            chartContainer.getChildren().add(noDataLabel);
            System.out.println("Displayed no data message: " + message);
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
}