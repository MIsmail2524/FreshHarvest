package com.freshharvest.controller;

import com.freshharvest.app.InventoryItem;
import com.freshharvest.app.InventoryService;
import com.freshharvest.util.NotificationService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class UpdateStockController {
        private NotificationService notificationService; 


    @FXML private Label lblProductName;
    @FXML private Label lblCurrentStock;
    @FXML private Label lblMinStock;
    @FXML private Label lblUnit;
    @FXML private TextField txtNewStock;

    private Stage dialogStage;
    private InventoryItem inventoryItem;
    private InventoryService inventoryService;
    private boolean stockUpdated = false;

    @FXML
    public void initialize() {
            inventoryService = new InventoryService();
        notificationService = NotificationService.getInstance();
        inventoryService = new InventoryService();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setInventoryItem(InventoryItem item) {
        this.inventoryItem = item;
        populateStockData();
    }

    public boolean isStockUpdated() {
        return stockUpdated;
    }

    private void populateStockData() {
        if (inventoryItem != null) {
            lblProductName.setText(inventoryItem.getProductName());
            lblCurrentStock.setText(String.format("%.2f %s", 
                inventoryItem.getCurrentStock(), inventoryItem.getUnit()));
            lblMinStock.setText(String.format("%.2f %s", 
                inventoryItem.getMinStock(), inventoryItem.getUnit()));
            lblUnit.setText(inventoryItem.getUnit());
            
            // Set current stock as default value
            txtNewStock.setText(String.valueOf(inventoryItem.getCurrentStock()));
        }
    }

    @FXML
    private void handleUpdateStock() {
        try {
            if (!validateInput()) {
                return;
            }

            double newStock = Double.parseDouble(txtNewStock.getText().trim());
            
            // Update stock
            boolean success = inventoryService.updateStock(
                inventoryItem.getProductId(), newStock);
            
            if (success) {
                                notificationService.notifyInventoryChanged();

                stockUpdated = true;
                showAlert("Success", 
                         "Stock updated successfully!\n\n" +
                         "Product: " + inventoryItem.getProductName() + "\n" +
                         "New Stock: " + newStock + " " + inventoryItem.getUnit(),
                         Alert.AlertType.INFORMATION);
                dialogStage.close();
            } else {
                showAlert("Error", "Failed to update stock. Please try again.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid number for stock.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to update stock: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        stockUpdated = false;
        dialogStage.close();
    }

    @FXML
    private void handleSetToZero() {
        txtNewStock.setText("0");
    }

    @FXML
    private void handleSetToMin() {
        if (inventoryItem != null) {
            txtNewStock.setText(String.valueOf(inventoryItem.getMinStock()));
        }
    }

    private boolean validateInput() {
        // Validate new stock
        if (txtNewStock.getText() == null || txtNewStock.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter new stock quantity", Alert.AlertType.ERROR);
            txtNewStock.requestFocus();
            return false;
        }

        try {
            double newStock = Double.parseDouble(txtNewStock.getText().trim());
            if (newStock < 0) {
                showAlert("Validation Error", "Stock cannot be negative", Alert.AlertType.ERROR);
                txtNewStock.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid number for stock", Alert.AlertType.ERROR);
            txtNewStock.requestFocus();
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Static method to open update stock dialog
    public static boolean showUpdateStockDialog(InventoryItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(UpdateStockController.class.getResource("/fxml/update-stock.fxml"));
            Parent root = loader.load();

            UpdateStockController controller = loader.getController();
            controller.setInventoryItem(item);

            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);

            dialogStage.setTitle("Update Stock - " + item.getProductName());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);

            Scene scene = new Scene(root, 450, 400);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();

            return controller.isStockUpdated();

        } catch (IOException e) {
            System.err.println("Failed to load update stock dialog: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open Update Stock dialog");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
            
            return false;
        }
    }
}