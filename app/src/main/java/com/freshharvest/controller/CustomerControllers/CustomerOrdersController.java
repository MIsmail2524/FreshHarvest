package com.freshharvest.controller.CustomerControllers;

import com.freshharvest.dao.CustomerDAO;
import com.freshharvest.app.Customers.CustomerOrder;
import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;
import com.freshharvest.util.SceneManager;
import com.freshharvest.service.SessionService;
import com.freshharvest.app.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerOrdersController implements Initializable, DataChangeListener { 
    
    private NotificationService notificationService; 

    @FXML private TableView<CustomerOrder> ordersTable;
    @FXML private TableColumn<CustomerOrder, Integer> colOrderId;
    @FXML private TableColumn<CustomerOrder, String> colStatus;
    @FXML private TableColumn<CustomerOrder, Double> colTotalAmount;
    @FXML private TableColumn<CustomerOrder, String> colPaymentMethod;
  //  @FXML private TableColumn<CustomerOrder, Integer> colItemCount;

    @FXML private Label lblTotalOrders;
    @FXML private Label lblPendingOrders;
    @FXML private Label lblDeliveredOrders;
    @FXML private Label lblTotalSpent;

    // Progress bar components
    @FXML private Label lblSelectedOrder;
    @FXML private Label lblSelectedAmount;
    @FXML private Label lblProgressPercentage;
    @FXML private HBox progressSegments;
    @FXML private StackPane currentPositionIndicator;
    @FXML private Circle positionCircle;
    @FXML private Label positionLabel;
    @FXML private HBox rejectedIndicator;

    private ObservableList<CustomerOrder> ordersList;
    private final CustomerDAO customerDAO = new CustomerDAO();
    private int currentCustomerId;
    private SessionService sessionService;

    // Define order status progression and colors
    private final String[] STATUS_FLOW = {"PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "RECEIVED"};
    private final String[] STATUS_LABELS = {"Pending", "Confirmed", "Processing", "Shipped", "Delivered", "Received"};
    private final String[] STATUS_COLORS = {"#f39c12", "#3498db", "#9b59b6", "#e67e22", "#27ae60", "#1abc9c"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

                 notificationService = NotificationService.getInstance(); 
            notificationService.addListener(this); 
            
            sessionService = SessionService.getInstance();
            initializeCurrentCustomer();
            initializeTableColumns();
            loadCustomerOrders();
            updateStatistics();
            setupTableSelectionListener();
            
        } catch (Exception e) {
            showAlert("Error", "Failed to initialize orders: " + e.getMessage());
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
                    System.out.println("Orders automatically refreshed");
                });
                break;
        }}
    private void initializeCurrentCustomer() {
        User currentUser = sessionService.getCurrentUser();
        if (currentUser != null) {
            currentCustomerId = customerDAO.getCustomerIdByUsername(currentUser.getUsername());
            if (currentCustomerId == -1) {
                showAlert("Error", "Customer profile not found. Please contact administrator.");
                // Fallback to a default customer ID for testing
                currentCustomerId = 3; // Assuming Saeed has ID 3
            }
        } else {
            showAlert("Error", "No user logged in. Please login again.");
            // Fallback for testing
            currentCustomerId = 3; // Default to Saeed
        }
    }

    private void initializeTableColumns() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
       // colItemCount.setCellValueFactory(new PropertyValueFactory<>("itemCount"));

        // Format total amount column
        colTotalAmount.setCellFactory(column -> new TableCell<CustomerOrder, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("Rs. %.2f", amount));
                }
            }
        });

    
        // Color code status column
        colStatus.setCellFactory(column -> new TableCell<CustomerOrder, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status.toUpperCase()) {
                        case "PENDING":
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                            break;
                        case "CONFIRMED":
                            setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                            break;
                        case "PROCESSING":
                            setStyle("-fx-text-fill: #9b59b6; -fx-font-weight: bold;");
                            break;
                        case "SHIPPED":
                            setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                            break;
                        case "DELIVERED":
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            break;
                        case "RECEIVED":
                            setStyle("-fx-text-fill: #1abc9c; -fx-font-weight: bold;");
                            break;
                        case "CANCELLED":
                        case "REJECTED":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #7f8c8d;");
                    }
                }
            }
        });
    }

    private void setupTableSelectionListener() {
        ordersTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    updateProgressBar(newValue);
                } else {
                    clearProgressBar();
                }
            }
        );
    }

    private void updateProgressBar(CustomerOrder order) {
        // Update selected order info
        lblSelectedOrder.setText("Order: " + order.getOrderNumber());
        lblSelectedAmount.setText("Amount: Rs. " + String.format("%.2f", order.getTotalAmount()));

        String currentStatus = order.getStatus().toUpperCase();
        
        // Handle cancelled/rejected orders
        if ("CANCELLED".equals(currentStatus) || "REJECTED".equals(currentStatus)) {
            showRejectedStatus();
            return;
        }

        // Hide rejected indicator for normal orders
        rejectedIndicator.setVisible(false);

        // Clear previous segments
        progressSegments.getChildren().clear();

        // Create progress segments
        double segmentWidth = 100.0 / STATUS_FLOW.length;
        int currentStatusIndex = -1;

        // Find current status index
        for (int i = 0; i < STATUS_FLOW.length; i++) {
            if (STATUS_FLOW[i].equals(currentStatus)) {
                currentStatusIndex = i;
                break;
            }
        }

        // If status not found in progression, show as completed
        if (currentStatusIndex == -1) {
            currentStatusIndex = STATUS_FLOW.length - 1;
        }

        // Create colored segments
        for (int i = 0; i < STATUS_FLOW.length; i++) {
            HBox segment = new HBox();
            segment.setPrefHeight(25);
            
            if (i <= currentStatusIndex) {
                // Completed or current segment
                segment.setStyle("-fx-background-color: " + STATUS_COLORS[i] + ";");
            } else {
                // Future segment
                segment.setStyle("-fx-background-color: #ecf0f1;");
            }
            
            // Set segment width
            segment.setPrefWidth(segmentWidth * (progressSegments.getWidth() / 100));
            progressSegments.getChildren().add(segment);
        }

        // Update progress percentage
        double progress = (currentStatusIndex + 1) / (double) STATUS_FLOW.length;
        lblProgressPercentage.setText(String.format("%.0f%%", progress * 100));

        // Position indicator
        if (currentStatusIndex >= 0) {
            double indicatorPosition = (currentStatusIndex + 0.5) * segmentWidth;
            currentPositionIndicator.setTranslateX((indicatorPosition / 100) * progressSegments.getWidth() - 10);
            
            // Update indicator color and text
            positionCircle.setFill(Color.web(STATUS_COLORS[currentStatusIndex]));
            positionLabel.setText(getStatusSymbol(currentStatus));
        }

        currentPositionIndicator.setVisible(true);
    }

    private String getStatusSymbol(String status) {
        switch (status.toUpperCase()) {
            case "PENDING": return "⏳";
            case "CONFIRMED": return "✓";
            case "PROCESSING": return "⚙";
            case "SHIPPED": return "🚚";
            case "DELIVERED": return "📦";
            case "RECEIVED": return "✅";
            default: return "•";
        }
    }

    private void showRejectedStatus() {
        // Clear progress segments
        progressSegments.getChildren().clear();
        
        // Create a single red segment for rejected orders
        HBox rejectedSegment = new HBox();
        rejectedSegment.setPrefHeight(25);
        rejectedSegment.setPrefWidth(progressSegments.getWidth());
        rejectedSegment.setStyle("-fx-background-color: #e74c3c;");
        progressSegments.getChildren().add(rejectedSegment);

        // Update labels
        lblProgressPercentage.setText("Cancelled");
        lblProgressPercentage.setStyle("-fx-text-fill: #e74c3c;");
        
        // Hide position indicator
        currentPositionIndicator.setVisible(false);
        
        // Show rejected indicator
        rejectedIndicator.setVisible(true);
    }

    private void clearProgressBar() {
        lblSelectedOrder.setText("No order selected");
        lblSelectedAmount.setText("");
        lblProgressPercentage.setText("0%");
        lblProgressPercentage.setStyle("-fx-text-fill: #3498db;");
        
        // Clear segments
        progressSegments.getChildren().clear();
        
        // Create empty gray bar
        HBox emptySegment = new HBox();
        emptySegment.setPrefHeight(25);
        emptySegment.setPrefWidth(progressSegments.getWidth());
        emptySegment.setStyle("-fx-background-color: #ecf0f1;");
        progressSegments.getChildren().add(emptySegment);
        
        // Hide indicators
        currentPositionIndicator.setVisible(false);
        rejectedIndicator.setVisible(false);
    }

    private void loadCustomerOrders() {
        try {
            // Use the safe method that handles dates properly
            List<CustomerOrder> orders = customerDAO.getCustomerOrdersSafe(currentCustomerId);
            ordersList = FXCollections.observableArrayList(orders);
            
            ordersTable.setItems(ordersList);
            System.out.println("Loaded " + ordersList.size() + " orders for customer ID: " + currentCustomerId);
            
            if (ordersList.isEmpty()) {
                showAlert("Information", "No orders found for your account.");
            }
            
        } catch (Exception e) {
            showAlert("Error", "Failed to load orders from database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatistics() {
        try {
            Object[] stats = customerDAO.getOrderStatistics(currentCustomerId);
            int totalOrders = (Integer) stats[0];
            int pendingOrders = (Integer) stats[1];
            int deliveredOrders = (Integer) stats[2];
            double totalSpent = (Double) stats[3];

            lblTotalOrders.setText(String.valueOf(totalOrders));
            lblPendingOrders.setText(String.valueOf(pendingOrders));
            lblDeliveredOrders.setText(String.valueOf(deliveredOrders));
            lblTotalSpent.setText(String.format("Rs. %.2f", totalSpent));

        } catch (Exception e) {
            System.err.println("Error loading statistics: " + e.getMessage());
            // Fallback to UI calculation
            calculateStatisticsFromUI();
        }
    }

    private void calculateStatisticsFromUI() {
        if (ordersList == null || ordersList.isEmpty()) {
            lblTotalOrders.setText("0");
            lblPendingOrders.setText("0");
            lblDeliveredOrders.setText("0");
            lblTotalSpent.setText("Rs. 0.00");
            return;
        }

        int total = ordersList.size();
        int pending = 0;
        int delivered = 0;
        double totalSpent = 0.0;

        for (CustomerOrder order : ordersList) {
            String status = order.getStatus().toUpperCase();
            if ("PENDING".equals(status) || "CONFIRMED".equals(status) || "PROCESSING".equals(status)) {
                pending++;
            }
            if ("DELIVERED".equals(status) || "RECEIVED".equals(status)) {
                delivered++;
            }
            if (!"CANCELLED".equals(status)) {
                totalSpent += order.getTotalAmount();
            }
        }

        lblTotalOrders.setText(String.valueOf(total));
        lblPendingOrders.setText(String.valueOf(pending));
        lblDeliveredOrders.setText(String.valueOf(delivered));
        lblTotalSpent.setText(String.format("Rs. %.2f", totalSpent));
    }

    @FXML
    private void onRefresh() {
        refreshAllData();
        showAlert("Success", "Orders refreshed successfully!");
    }

  
    private void refreshAllData() {
        loadCustomerOrders();
        updateStatistics();
        ordersTable.getSelectionModel().clearSelection();
        clearProgressBar();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}