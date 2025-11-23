package com.freshharvest.controller;

import com.freshharvest.app.Crop;
import com.freshharvest.app.CropService;
import com.freshharvest.util.NotificationService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.time.LocalDate;

public class AddProductController {

    @FXML private TextField txtProductName;
    @FXML private ComboBox<String> comboProductType;
    @FXML private TextField txtQuantity;
    @FXML private ComboBox<String> comboUnit;
    @FXML private TextField txtExpectedYield;
    @FXML private ComboBox<String> comboProcessingType;
    @FXML private TextArea txtSpecialInstructions;
    @FXML private TextField txtDeliveryDate;
    private NotificationService notificationService;

    private Stage dialogStage;
    private CropService cropService;
    private boolean productSent = false;

    @FXML
    public void initialize() {
        cropService = new CropService();
                notificationService = NotificationService.getInstance(); 
        // Populate product type combo box
        comboProductType.getItems().addAll(
            "Organic Vegetables", "Fresh Fruits", "Herbs & Spices", 
            "Grains & Cereals", "Dairy Products", "Processed Foods",
            "Beverages", "Other"
        );
        
        // Populate unit combo box
        comboUnit.getItems().addAll("kg", "tons", "crates", "boxes", "bunches");
        
        // Populate processing type combo box
        comboProcessingType.getItems().addAll(
            "Washing & Cleaning",
            "Cutting & Slicing", 
            "Packaging",
            "Freezing",
            "Drying",
            "Mixing & Blending",
            "Quality Grading",
            "Custom Processing"
        );
        
        // Set default delivery date (3 days from now)
        txtDeliveryDate.setText(LocalDate.now().plusDays(3).toString());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isProductSent() {
        return productSent;
    }

    @FXML
    private void handleSendToFactory() {
        try {
            if (!validateInput()) {
                return;
            }

            // Create crop object for factory processing
            Crop crop = new Crop();
            crop.setName(txtProductName.getText().trim());
            crop.setType(comboProductType.getValue());
            crop.setQuantity(Double.parseDouble(txtQuantity.getText().trim()));
            crop.setField("Factory Processing");
            crop.setStatus("SENT_TO_FACTORY");
            crop.setFarmerId(getCurrentFarmerId());
            
            // Set factory processing details
            crop.setProcessingType(comboProcessingType.getValue());
            crop.setSpecialInstructions(txtSpecialInstructions.getText().trim());
            crop.setDeliveryDate(LocalDate.parse(txtDeliveryDate.getText().trim()));

            // Send to factory (this would create a factory processing request)
            boolean success = cropService.sendToFactory(crop);
            
            if (success) {
                productSent = true;
                showAlert("Success", 
                         "Crop sent to factory for processing!\n\n" +
                         "Product: " + crop.getName() + "\n" +
                         "Quantity: " + crop.getQuantity() + " " + comboUnit.getValue() + "\n" +
                         "Processing: " + crop.getProcessingType() + "\n" +
                         "Expected Delivery: " + crop.getDeliveryDate() + "\n\n" +
                         "Factory will process and add to inventory automatically.",
                         Alert.AlertType.INFORMATION);
                dialogStage.close();
                                notificationService.notifyDataChanged("FACTORY_ORDER_CREATED");

            } else {
                showAlert("Error", "Failed to send crop to factory. Please try again.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            showAlert("Error", "Failed to send crop to factory: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        productSent = false;
        dialogStage.close();
    }

    @FXML
    private void handleSetDefaultDate() {
        // Set delivery date to 3 days from now
        txtDeliveryDate.setText(LocalDate.now().plusDays(3).toString());
    }

    @FXML
    private void handleCalculateYield() {
        try {
            if (txtQuantity.getText() != null && !txtQuantity.getText().trim().isEmpty()) {
                double quantity = Double.parseDouble(txtQuantity.getText().trim());
                double expectedYield = quantity * 0.85; // 85% yield assumption
                txtExpectedYield.setText(String.format("%.2f", expectedYield));
            }
        } catch (NumberFormatException e) {
            // Ignore calculation if quantity is not valid
        }
    }

    private boolean validateInput() {
        // Validate product name
        if (txtProductName.getText() == null || txtProductName.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter product name", Alert.AlertType.ERROR);
            txtProductName.requestFocus();
            return false;
        }

        // Validate product type
        if (comboProductType.getValue() == null) {
            showAlert("Validation Error", "Please select product type", Alert.AlertType.ERROR);
            comboProductType.requestFocus();
            return false;
        }

        // Validate quantity
        if (txtQuantity.getText() == null || txtQuantity.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter quantity", Alert.AlertType.ERROR);
            txtQuantity.requestFocus();
            return false;
        }

        try {
            double quantity = Double.parseDouble(txtQuantity.getText().trim());
            if (quantity <= 0) {
                showAlert("Validation Error", "Quantity must be greater than 0", Alert.AlertType.ERROR);
                txtQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid number for quantity", Alert.AlertType.ERROR);
            txtQuantity.requestFocus();
            return false;
        }

        // Validate unit
        if (comboUnit.getValue() == null) {
            showAlert("Validation Error", "Please select unit", Alert.AlertType.ERROR);
            comboUnit.requestFocus();
            return false;
        }

        // Validate expected yield
        if (txtExpectedYield.getText() == null || txtExpectedYield.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter expected yield", Alert.AlertType.ERROR);
            txtExpectedYield.requestFocus();
            return false;
        }

        try {
            double yield = Double.parseDouble(txtExpectedYield.getText().trim());
            if (yield < 0) {
                showAlert("Validation Error", "Expected yield cannot be negative", Alert.AlertType.ERROR);
                txtExpectedYield.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid number for expected yield", Alert.AlertType.ERROR);
            txtExpectedYield.requestFocus();
            return false;
        }

        // Validate processing type
        if (comboProcessingType.getValue() == null) {
            showAlert("Validation Error", "Please select processing type", Alert.AlertType.ERROR);
            comboProcessingType.requestFocus();
            return false;
        }

        // Validate delivery date
        if (txtDeliveryDate.getText() == null || txtDeliveryDate.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter delivery date", Alert.AlertType.ERROR);
            txtDeliveryDate.requestFocus();
            return false;
        }

        try {
            LocalDate deliveryDate = LocalDate.parse(txtDeliveryDate.getText().trim());
            if (deliveryDate.isBefore(LocalDate.now())) {
                showAlert("Validation Error", "Delivery date cannot be in the past", Alert.AlertType.ERROR);
                txtDeliveryDate.requestFocus();
                return false;
            }
        } catch (Exception e) {
            showAlert("Validation Error", "Please enter a valid date (YYYY-MM-DD)", Alert.AlertType.ERROR);
            txtDeliveryDate.requestFocus();
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

    private int getCurrentFarmerId() {
        // Get current logged-in farmer ID (replace with actual session management)
        return 1; // Default for testing
    }

    // Static method to open add product dialog
    public static boolean showAddProductDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(AddProductController.class.getResource("/fxml/add-product.fxml"));
            Parent root = loader.load();

            AddProductController controller = loader.getController();

            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);

            dialogStage.setTitle("Send Crop to Factory");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);

            Scene scene = new Scene(root, 500, 600);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();

            return controller.isProductSent();

        } catch (IOException e) {
            System.err.println("Failed to load add product dialog: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open Send to Factory dialog");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
            
            return false;
        }
    }
}