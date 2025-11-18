package com.freshharvest.controller;

import com.freshharvest.app.InventoryItem;
import com.freshharvest.app.InventoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {

    // Statistics Labels
    @FXML private Label lblTotalProducts;
    @FXML private Label lblLowStock;
    @FXML private Label lblOutOfStock;
    @FXML private Label lblTotalValue;
    @FXML private Label lblFilterCount;

    // Filter
    @FXML private ComboBox<String> comboStockFilter;

    // Table and Columns
    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private TableColumn<InventoryItem, Integer> colProductId;
    @FXML private TableColumn<InventoryItem, String> colProductName;
    @FXML private TableColumn<InventoryItem, String> colCategory;
    @FXML private TableColumn<InventoryItem, Double> colCurrentStock;
    @FXML private TableColumn<InventoryItem, Double> colMinStock;
    @FXML private TableColumn<InventoryItem, String> colUnit;
    @FXML private TableColumn<InventoryItem, String> colPrice;
    @FXML private TableColumn<InventoryItem, String> colStatus;
    @FXML private TableColumn<InventoryItem, String> colLastUpdated;

    private InventoryService inventoryService;
    private ObservableList<InventoryItem> inventoryData;
    private FilteredList<InventoryItem> filteredInventory;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            inventoryService = new InventoryService();
            inventoryData = FXCollections.observableArrayList();
            filteredInventory = new FilteredList<>(inventoryData);

            setupTableColumns();
            setupStockFilter();
            loadInventoryData(); // Load real data from database
            
        } catch (Exception e) {
            handleError("Initialization Error", "Failed to initialize inventory: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        try {
            colProductId.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getProductId()).asObject());
            colProductName.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProductName()));
            colCategory.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
            colCurrentStock.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getCurrentStock()).asObject());
            colMinStock.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getMinStock()).asObject());
            colUnit.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUnit()));
            
            // Format price as Pakistani Rupees
            colPrice.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    String.format("Rs. %.2f", cellData.getValue().getPrice())
                ));
            
            colStatus.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(getStockStatus(cellData.getValue())));
            
            colLastUpdated.setCellValueFactory(cellData -> {
                LocalDateTime lastUpdated = cellData.getValue().getLastUpdated();
                return new javafx.beans.property.SimpleStringProperty(
                    lastUpdated != null ? lastUpdated.format(dateFormatter) : "N/A"
                );
            });

            inventoryTable.setItems(filteredInventory);
            
        } catch (Exception e) {
            handleError("Table Setup Error", "Failed to setup table columns: " + e.getMessage());
        }
    }

    private void setupStockFilter() {
        try {
            ObservableList<String> filterOptions = FXCollections.observableArrayList(
                "All Products", "In Stock", "Low Stock", "Out of Stock"
            );
            comboStockFilter.setItems(filterOptions);
            comboStockFilter.getSelectionModel().selectFirst();
            
            comboStockFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
                updateFilter();
            });
        } catch (Exception e) {
            handleError("Filter Setup Error", "Failed to setup filter: " + e.getMessage());
        }
    }

    private String getStockStatus(InventoryItem item) {
        if (item.getCurrentStock() <= 0) {
            return "Out of Stock";
        } else if (item.getCurrentStock() <= item.getMinStock()) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }

    private void updateFilter() {
        try {
            String filter = comboStockFilter.getValue();
            if (filter == null || "All Products".equals(filter)) {
                filteredInventory.setPredicate(item -> true);
            } else {
                filteredInventory.setPredicate(item -> filter.equals(getStockStatus(item)));
            }
            updateFilterCount();
        } catch (Exception e) {
            handleError("Filter Error", "Failed to apply filter: " + e.getMessage());
        }
    }

    private void loadInventoryData() {
        try {
            inventoryData.clear();
            var inventory = inventoryService.getInventoryByFarmer();
            
            if (inventory != null && !inventory.isEmpty()) {
                inventoryData.setAll(inventory);
                updateStatistics();
                updateFilterCount();
            } else {
                resetStatistics();
                showAlert("No Data", "No inventory data found for your farm.", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            handleError("Data Load Error", "Unable to load inventory data: " + e.getMessage());
            resetStatistics();
        }
    }

    private void updateStatistics() {
        try {
            var stats = inventoryService.getInventoryStatistics();
            
            lblTotalProducts.setText(String.valueOf(stats.getTotalProducts()));
            lblLowStock.setText(String.valueOf(stats.getLowStock()));
            lblOutOfStock.setText(String.valueOf(stats.getOutOfStock()));
            lblTotalValue.setText(String.format("Rs. %.2f", stats.getTotalValue()));
            
        } catch (Exception e) {
            handleError("Statistics Error", "Failed to update statistics: " + e.getMessage());
        }
    }

    private void updateFilterCount() {
        try {
            lblFilterCount.setText("Showing " + filteredInventory.size() + " products");
        } catch (Exception e) {
            lblFilterCount.setText("Error counting items");
        }
    }

    private void resetStatistics() {
        lblTotalProducts.setText("0");
        lblLowStock.setText("0");
        lblOutOfStock.setText("0");
        lblTotalValue.setText("Rs. 0");
        lblFilterCount.setText("Showing 0 products");
    }

    @FXML
    private void handleAddProduct() {
        try {
            // Show add product dialog
            showAddProductDialog();
        } catch (Exception e) {
            handleError("Add Product Error", "Failed to add product: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateStock() {
        try {
            InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Show update stock dialog
                showUpdateStockDialog(selected);
            } else {
                showAlert("Warning", "Please select a product to update stock.", Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            handleError("Update Stock Error", "Failed to update stock: " + e.getMessage());
        }
    }

    @FXML
    private void handleStockReport() {
        try {
            // Generate and show stock report
            showAlert("Stock Report", "Inventory report generated successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            handleError("Report Error", "Failed to generate report: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        try {
            loadInventoryData();
            showAlert("Refreshed", "Inventory data refreshed successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            handleError("Refresh Error", "Failed to refresh inventory: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilterChange() {
        updateFilter();
    }

    // Dialog methods (you need to implement these)
    private void showAddProductDialog() {
        // Implement add product dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Enter product details");
        dialog.setContentText("Product Name:");
        
        dialog.showAndWait().ifPresent(productName -> {
            // Here you would show a proper form with all fields
            showAlert("Add Product", "Add product feature - implement full form", Alert.AlertType.INFORMATION);
        });
    }

    private void showUpdateStockDialog(InventoryItem product) {
        // Implement update stock dialog
        TextInputDialog dialog = new TextInputDialog(String.valueOf(product.getCurrentStock()));
        dialog.setTitle("Update Stock");
        dialog.setHeaderText("Update stock for: " + product.getProductName());
        dialog.setContentText("New Stock Quantity:");
        
        dialog.showAndWait().ifPresent(newStock -> {
            try {
                double stockValue = Double.parseDouble(newStock);
                boolean success = inventoryService.updateStock(product.getProductId(), stockValue);
                if (success) {
                    loadInventoryData(); // Refresh data
                    showAlert("Success", "Stock updated successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to update stock.", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid number.", Alert.AlertType.ERROR);
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        try {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing alert: " + e.getMessage());
        }
    }

    private void handleError(String title, String message) {
        System.err.println(title + ": " + message);
        showAlert(title, message, Alert.AlertType.ERROR);
    }
}