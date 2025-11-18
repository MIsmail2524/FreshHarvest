package com.freshharvest.controller;

import com.freshharvest.app.Order;
import com.freshharvest.app.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class OrderController implements Initializable {

    @FXML
    private Label orderTitle;
    
    // Statistics Labels
    @FXML private Label lblTotalOrders;
    @FXML private Label lblPendingOrders;
    @FXML private Label lblCompletedOrders;
    
    // Table and Columns
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, String> colOrderId;
    @FXML private TableColumn<Order, String> colCustomer;
    @FXML private TableColumn<Order, String> colOrderDate;
    @FXML private TableColumn<Order, String> colTotalAmount;
    @FXML private TableColumn<Order, String> colStatus;
    @FXML private TableColumn<Order, String> colItems;
    @FXML private TableColumn<Order, String> colDeliveryType;

    private OrderService orderService;
    private ObservableList<Order> ordersData;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🚀 OrderController initializing...");
        
        try {
            orderService = new OrderService();
            ordersData = FXCollections.observableArrayList();
            setupTableColumns();
            loadOrdersData();
            
            System.out.println("✅ OrderController initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Error initializing OrderController: " + e.getMessage());
            e.printStackTrace();
            showAlert("Initialization Error", "Failed to initialize orders: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
// Call this when user clicks a row or menu item

@FXML
private void showOrderDetails(ActionEvent event) {
    Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
    if (selectedOrder != null) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OrderDetails.fxml"));
            Scene scene = new Scene(loader.load());
            Stage popupStage = new Stage();
            popupStage.setTitle("Order Details");
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL); // Block main window
            OrderDetailsController controller = loader.getController();
            controller.setOrderData(selectedOrder);
            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } else {
        showAlert("No Selection", "Please select an order from the table first.", Alert.AlertType.WARNING);
    }
}

    private void setupTableColumns() {
        try {
            System.out.println("🔄 Setting up table columns...");
            
            // Set up cell value factories
            colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
            colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            
            colOrderDate.setCellValueFactory(cellData -> {
                LocalDateTime orderDate = cellData.getValue().getOrderDate();
                return new javafx.beans.property.SimpleStringProperty(
                    orderDate != null ? orderDate.format(dateFormatter) : "N/A"
                );
            });
            
            colTotalAmount.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    String.format("$%.2f", cellData.getValue().getTotalAmount())
                )
            );
            
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colDeliveryType.setCellValueFactory(new PropertyValueFactory<>("deliveryType"));
            
            colItems.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getItemsSummary())
            );

            // Set table data
            ordersTable.setItems(ordersData);
            
            System.out.println("✅ Table columns setup completed");
            
        } catch (Exception e) {
            System.err.println("❌ Error setting up table columns: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadOrdersData() {
        try {
            System.out.println("🔄 Loading orders data...");
            
            // Clear existing data
            ordersData.clear();
            
            // Load data from service - using default farmer ID 1 for testing
            var orders = orderService.getOrdersByFarmer(1);
            
            System.out.println("📊 Retrieved " + (orders != null ? orders.size() : 0) + " orders from service");
            
            if (orders != null && !orders.isEmpty()) {
                ordersData.setAll(orders);
                updateStatistics();
                
                // DEBUG: Print first order to console
                if (!orders.isEmpty()) {
                    Order firstOrder = orders.get(0);
                    System.out.println("📝 First order: " + firstOrder.toString());
                }
            } else {
                // No data found
                System.out.println("ℹ️ No orders found in database");
                lblTotalOrders.setText("0");
                lblPendingOrders.setText("0");
                lblCompletedOrders.setText("0");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error loading orders data: " + e.getMessage());
            e.printStackTrace();
            showAlert("Data Load Error", 
                     "Unable to load orders data. Please check your connection and try again.\n\nError: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
            // Reset to safe state
            lblTotalOrders.setText("0");
            lblPendingOrders.setText("0");
            lblCompletedOrders.setText("0");
        }
    }

    @FXML
    private void pendingOrders() {
        try {
            System.out.println("⏳ Filtering pending orders...");
            filterOrdersByStatus("PENDING");
            showAlert("Filter Applied", "Showing pending orders", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            System.err.println("❌ Error filtering pending orders: " + e.getMessage());
            showAlert("Error", "Failed to filter pending orders: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void completedOrders() {
        try {
            System.out.println("✅ Filtering completed orders...");
            filterOrdersByStatus("COMPLETED");
            showAlert("Filter Applied", "Showing completed orders", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            System.err.println("❌ Error filtering completed orders: " + e.getMessage());
            showAlert("Error", "Failed to filter completed orders: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void allOrders() {
        try {
            System.out.println("📦 Showing all orders...");
            ordersTable.setItems(ordersData);
            updateStatistics();
            showAlert("Filter Applied", "Showing all orders", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            System.err.println("❌ Error showing all orders: " + e.getMessage());
            showAlert("Error", "Failed to show all orders: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void notifyFactory() {
        try {
            Order selected = ordersTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                System.out.println("🏭 Notifying factory for order: " + selected.getOrderNumber());
                showAlert("Factory Notification", 
                         "Factory has been notified about order: " + selected.getOrderNumber(), 
                         Alert.AlertType.INFORMATION);
            } else {
                showAlert("Warning", "Please select an order to notify factory.", Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            System.err.println("❌ Error notifying factory: " + e.getMessage());
            showAlert("Error", "Failed to notify factory: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
 
    @FXML
    private void deleteOrder() {
        try {
            Order selected = ordersTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                System.out.println("🗑️ Deleting order: " + selected.getOrderNumber());
                
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Delete");
                confirmation.setHeaderText("Delete Order");
                confirmation.setContentText("Are you sure you want to delete order: " + selected.getOrderNumber() + "?");

                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        boolean success = orderService.deleteOrder(selected.getOrderId());
                        if (success) {
                            showAlert("Success", "Order deleted successfully!", Alert.AlertType.INFORMATION);
                            loadOrdersData();
                        } else {
                            showAlert("Error", "Failed to delete order.", Alert.AlertType.ERROR);
                        }
                    }
                });
            } else {
                showAlert("Warning", "Please select an order to delete.", Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            System.err.println("❌ Error deleting order: " + e.getMessage());
            showAlert("Error", "Failed to delete order: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void refreshData() {
        try {
            System.out.println("🔄 Refreshing orders data...");
            loadOrdersData();
            showAlert("Refreshed", "Orders data refreshed successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            System.err.println("❌ Error refreshing orders: " + e.getMessage());
            showAlert("Error", "Failed to refresh orders: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Close the current window
    @FXML
    private void closeApplication() {
        try {
            System.out.println("🚪 Closing orders window...");
            Stage stage = (Stage) orderTitle.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.err.println("❌ Error closing window: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        try {
            int total = ordersData.size();
            int pending = (int) ordersData.stream()
                .filter(o -> o.getStatus() != null && "PENDING".equalsIgnoreCase(o.getStatus()))
                .count();
            int completed = (int) ordersData.stream()
                .filter(o -> o.getStatus() != null && "COMPLETED".equalsIgnoreCase(o.getStatus()))
                .count();

            lblTotalOrders.setText(String.valueOf(total));
            lblPendingOrders.setText(String.valueOf(pending));
            lblCompletedOrders.setText(String.valueOf(completed));
            
            System.out.println("📈 Statistics updated - Total: " + total + ", Pending: " + pending + ", Completed: " + completed);
            
        } catch (Exception e) {
            System.err.println("❌ Error updating statistics: " + e.getMessage());
        }
    }

    private void filterOrdersByStatus(String status) {
        try {
            ObservableList<Order> filtered = FXCollections.observableArrayList();
            for (Order order : ordersData) {
                if (order.getStatus() != null && status.equalsIgnoreCase(order.getStatus())) {
                    filtered.add(order);
                }
            }
            ordersTable.setItems(filtered);
            System.out.println("🔍 Filtered " + filtered.size() + " orders with status: " + status);
            
        } catch (Exception e) {
            System.err.println("❌ Error filtering orders: " + e.getMessage());
            throw e;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        try {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("❌ Error showing alert: " + e.getMessage());
        }
    }

    // Static method to open the Order window from any other controller
    public static void openOrderWindow() {
        try {
            System.out.println("🚀 Opening Orders Management Window...");
            
            FXMLLoader loader = new FXMLLoader(OrderController.class.getResource("/fxml/order.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Orders Management - FreshHarvest");
            stage.setScene(new Scene(root, 1200, 800));
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            
            stage.show();
            
            System.out.println("✅ Orders window opened successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to load order window: " + e.getMessage());
            e.printStackTrace();
            
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open Orders window");
            alert.setContentText("Please check if the FXML file exists and is configured correctly.\n\nError: " + e.getMessage());
            alert.showAndWait();
        }
    }
}