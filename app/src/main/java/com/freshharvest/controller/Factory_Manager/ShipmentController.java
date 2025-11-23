package com.freshharvest.controller.Factory_Manager;

import com.freshharvest.app.Factory_Manager.Shipment;
import com.freshharvest.dao.DAOFactory;
import com.freshharvest.dao.FactoryDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.Timestamp;
import java.util.List;
import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;
public class ShipmentController implements DataChangeListener {
    private NotificationService notificationService;

    @FXML private TableView<Shipment> shipmentTable;
    @FXML private TableColumn<Shipment, Integer> colShipId;
    @FXML private TableColumn<Shipment, String> colCustomer;
    @FXML private TableColumn<Shipment, String> colCarrier;
    @FXML private TableColumn<Shipment, String> colStatus;
    @FXML private TableColumn<Shipment, String> colTracking;
    @FXML private TableColumn<Shipment, Timestamp> colShipDate;
  //  @FXML private TableColumn<Shipment, Timestamp> colCreatedAt;
    @FXML private TableColumn<Shipment, Integer> colOrderId;


    @FXML private Label lblSelectedShipment;
    @FXML private Label lblPendingCount;
    @FXML private Label lblShippedCount;
    @FXML private Label lblTotalCount;
    
    @FXML private TextField txtTracking;
    @FXML private TextField txtSearch;
    
    @FXML private ComboBox<String> cmbStatus;
    @FXML private ComboBox<String> cmbStatusFilter;
    
    @FXML private Button btnConfirm;
    @FXML private Button btnUpdate;

    private Shipment selectedShipment;
    private FactoryDAO factoryDAO;
    private ObservableList<Shipment> masterData;

    @FXML
    public void initialize() {
        notificationService = NotificationService.getInstance();
        notificationService.addListener(this);
        factoryDAO = DAOFactory.getFactoryDAO();
        setupTableColumns();
        setupComboBoxes();
        loadShipments(); // Make sure this method exists
        setupSelectionListener();
        setupTableRowFactory();
        setupSearchAndFilter();
        updateStatistics();
    }

    @Override
    public void onDataChanged(String dataType) {
        switch (dataType) {
            case "BATCH_UPDATED":
            case "ORDER_STATUS_CHANGED":
            case "INVENTORY_CHANGED":
            case "PAYMENT_PROCESSED": // Add this
                // Refresh data when relevant changes occur
                javafx.application.Platform.runLater(() -> {
                    loadShipments(); // Call your existing load method
                    updateStatistics();
                    System.out.println("Shipments automatically refreshed");
                });
                break;
        }
    }

    private void setupTableColumns() {
        colShipId.setCellValueFactory(new PropertyValueFactory<>("shipmentId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colCarrier.setCellValueFactory(new PropertyValueFactory<>("carrier"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTracking.setCellValueFactory(new PropertyValueFactory<>("trackingNumber"));
        colShipDate.setCellValueFactory(new PropertyValueFactory<>("shipmentDate"));
       // colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
         colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
    colOrderId.setCellFactory(column -> new TableCell<Shipment, Integer>() {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null || item == 0) {
                setText("N/A");
            } else {
                setText("ORD-" + item);
            }
        }
    });
        // Format date columns
        colShipDate.setCellFactory(column -> new TableCell<Shipment, Timestamp>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toLocalDateTime().toLocalDate().toString());
                }
            }
        });
        
      
    }

    private void setupComboBoxes() {
        // Status combo box for actions
        cmbStatus.setItems(FXCollections.observableArrayList(
            "Pending", "Shipped", "Delivered", "Cancelled"
        ));
        
        // Filter combo box
        cmbStatusFilter.setItems(FXCollections.observableArrayList(
            "All Status", "Pending", "Shipped", "Delivered", "Cancelled"
        ));
        cmbStatusFilter.setValue("All Status");
    }

    private void setupSelectionListener() {
        shipmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedShipment = newVal;
            if (newVal != null) {
                updateFormWithSelectedShipment(newVal);
                btnConfirm.setDisable(false);
                btnUpdate.setDisable(false);
            } else {
                clearForm();
                btnConfirm.setDisable(true);
                btnUpdate.setDisable(true);
            }
        });
    }

    private void setupTableRowFactory() {
        shipmentTable.setRowFactory(tv -> new TableRow<Shipment>() {
            @Override
            protected void updateItem(Shipment shipment, boolean empty) {
                super.updateItem(shipment, empty);
                
                if (empty || shipment == null) {
                    setStyle("");
                } else {
                    switch (shipment.getStatus().toLowerCase()) {
                        case "pending":
                            setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffeaa7;");
                            break;
                        case "shipped":
                            setStyle("-fx-background-color: #d4edda; -fx-border-color: #c3e6cb;");
                            break;
                        case "delivered":
                            setStyle("-fx-background-color: #cce7ff; -fx-border-color: #b3d9ff;");
                            break;
                        case "cancelled":
                            setStyle("-fx-background-color: #f8d7da; -fx-border-color: #f5c6cb;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

    private void setupSearchAndFilter() {
        // Search functionality
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTable();
        });
        
        // Filter functionality
        cmbStatusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterTable();
        });
    }

    private void updateFormWithSelectedShipment(Shipment shipment) {
        cmbStatus.setValue(shipment.getStatus());
        
        if (shipment.getTrackingNumber() != null && !shipment.getTrackingNumber().isEmpty()) {
            txtTracking.setText(shipment.getTrackingNumber());
        } else {
            txtTracking.clear();
        }
        
        lblSelectedShipment.setText(String.format(
            "Selected: Shipment #%d - %s | Carrier: %s | Status: %s",
            shipment.getShipmentId(),
            shipment.getCustomerName(),
            shipment.getCarrier(),
            shipment.getStatus()
        ));
        
        // Enable/disable controls based on status
        boolean isPending = "Pending".equals(shipment.getStatus());
        btnConfirm.setDisable(!isPending);
    }

    private void clearForm() {
        cmbStatus.setValue(null);
        txtTracking.clear();
        lblSelectedShipment.setText("No shipment selected");
    }

    private void loadShipments() {
        try {
            List<Shipment> shipments = factoryDAO.getAllShipments();
            masterData = FXCollections.observableArrayList(shipments);
            shipmentTable.setItems(masterData);
            updateStatistics();
            
        } catch (Exception e) {
            showAlert("Error", "Failed to load shipments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatistics() {
        if (masterData != null) {
            long pendingCount = masterData.stream().filter(s -> "Pending".equals(s.getStatus())).count();
            long shippedCount = masterData.stream().filter(s -> "Shipped".equals(s.getStatus())).count();
            
            lblPendingCount.setText(String.valueOf(pendingCount));
            lblShippedCount.setText(String.valueOf(shippedCount));
            lblTotalCount.setText(String.valueOf(masterData.size()));
        }
    }

    private void filterTable() {
        if (masterData == null) return;
        
        String searchText = txtSearch.getText().toLowerCase();
        String filterStatus = cmbStatusFilter.getValue();
        
        ObservableList<Shipment> filteredData = FXCollections.observableArrayList();
        
        for (Shipment shipment : masterData) {
            boolean matchesSearch = searchText.isEmpty() ||
                String.valueOf(shipment.getShipmentId()).contains(searchText) ||
                shipment.getCustomerName().toLowerCase().contains(searchText) ||
                (shipment.getTrackingNumber() != null && 
                 shipment.getTrackingNumber().toLowerCase().contains(searchText)) ||
                shipment.getCarrier().toLowerCase().contains(searchText);
            
            boolean matchesFilter = "All Status".equals(filterStatus) ||
                shipment.getStatus().equals(filterStatus);
            
            if (matchesSearch && matchesFilter) {
                filteredData.add(shipment);
            }
        }
        
        shipmentTable.setItems(filteredData);
    }

    @FXML
    private void onConfirmShipment() {
        if (selectedShipment == null) {
            showAlert("No Shipment Selected", "Please select a shipment to confirm.");
            return;
        }

        String trackingNumber = txtTracking.getText().trim();
        if (trackingNumber.isEmpty()) {
            showAlert("Missing Information", "Please enter a tracking number.");
            return;
        }

        if (!"Pending".equals(selectedShipment.getStatus())) {
            showAlert("Invalid Operation", "Only pending shipments can be confirmed.");
            return;
        }

        System.out.println("Confirming Shipment #" + selectedShipment.getShipmentId() + " with tracking: " + trackingNumber);

        try {
            boolean success = factoryDAO.confirmShipment(selectedShipment.getShipmentId(), trackingNumber);
            
            if (success) {
                            notificationService.notifyOrderStatusChanged();

                showSuccessAlert("Success", 
                    "Shipment #" + selectedShipment.getShipmentId() + 
                    " confirmed and marked as shipped!\nTracking: " + trackingNumber);
                loadShipments();
                clearForm();
                shipmentTable.getSelectionModel().clearSelection();
            } else {
                showAlert("Error", "Failed to confirm shipment.");
            }
            
        } catch (Exception e) {
            System.err.println("Error confirming shipment: " + e.getMessage());
            showAlert("Database Error", "Could not confirm shipment: " + e.getMessage());
        }
    }

    @FXML
    private void onUpdateStatus() {
        if (selectedShipment == null) {
            showAlert("No Shipment Selected", "Please select a shipment to update.");
            return;
        }

        String newStatus = cmbStatus.getValue();
        if (newStatus == null || newStatus.isEmpty()) {
            showAlert("Invalid Status", "Please select a status.");
            return;
        }

        System.out.println("Updating Shipment #" + selectedShipment.getShipmentId() + " to status: " + newStatus);

        try {
            boolean success = factoryDAO.updateShipmentStatus(selectedShipment.getShipmentId(), newStatus);
            
            if (success) {
                showSuccessAlert("Success", 
                    "Shipment #" + selectedShipment.getShipmentId() + 
                    " status updated to: " + newStatus);
                loadShipments();
                clearForm();
                shipmentTable.getSelectionModel().clearSelection();
            } else {
                showAlert("Error", "Failed to update shipment status.");
            }
            
        } catch (Exception e) {
            System.err.println("Error updating shipment status: " + e.getMessage());
            showAlert("Database Error", "Could not update shipment status: " + e.getMessage());
        }
    }

    @FXML
    private void onCreateShipment() {
        Dialog<Shipment> dialog = new Dialog<>();
        dialog.setTitle("Create New Shipment");
        dialog.setHeaderText("Enter shipment details");

        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField customerField = new TextField();
        customerField.setPromptText("Customer Name");
        TextField carrierField = new TextField();
        carrierField.setPromptText("Carrier");
        TextField trackingField = new TextField();
        trackingField.setPromptText("Tracking Number (optional)");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Pending", "Shipped", "Delivered", "Cancelled");
        statusCombo.setValue("Pending");

        grid.add(new Label("Customer Name:"), 0, 0);
        grid.add(customerField, 1, 0);
        grid.add(new Label("Carrier:"), 0, 1);
        grid.add(carrierField, 1, 1);
        grid.add(new Label("Tracking Number:"), 0, 2);
        grid.add(trackingField, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a Shipment object when the create button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String customerName = customerField.getText().trim();
                    String carrier = carrierField.getText().trim();
                    
                    if (customerName.isEmpty() || carrier.isEmpty()) {
                        showAlert("Missing Information", "Please fill in all required fields.");
                        return null;
                    }
                    
                    Shipment newShipment = new Shipment(
                        0, // ID will be auto-generated
                        customerName,
                        carrier,
                        statusCombo.getValue(),
                        trackingField.getText().trim(),
                        new Timestamp(System.currentTimeMillis())
                    );
                    
                    boolean success = factoryDAO.createShipment(newShipment);
                    
                    if (success) {
                        showSuccessAlert("Success", "New shipment created successfully!");
                        return newShipment;
                    } else {
                        showAlert("Error", "Failed to create shipment.");
                    }
                } catch (Exception e) {
                    showAlert("Error", "Failed to create shipment: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result != null) {
                loadShipments(); // Refresh the table
            }
        });
    }

    @FXML
    private void onRefresh() {
        loadShipments();
        clearForm();
        shipmentTable.getSelectionModel().clearSelection();
        txtSearch.clear();
        cmbStatusFilter.setValue("All Status");
        showSuccessAlert("Refreshed", "Shipment data refreshed successfully!");
    }

    @FXML
    private void onViewAllShipments() {
        loadShipments();
        lblSelectedShipment.setText("Showing all shipments (" + masterData.size() + " total)");
    }

    @FXML
    private void onViewPendingOnly() {
        cmbStatusFilter.setValue("Pending");
        lblSelectedShipment.setText("Showing pending shipments only");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}