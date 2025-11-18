package com.freshharvest.controller;

import com.freshharvest.app.Crop;
import com.freshharvest.app.CropService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.time.LocalDate;

public class UpdateCropController {

    @FXML private TextField txtCropId;
    @FXML private TextField txtCropName;
    @FXML private ComboBox<String> comboCropType;
    @FXML private ComboBox<String> comboStatus;
    @FXML private DatePicker datePlanting;
    @FXML private DatePicker dateHarvest;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtFieldName;
    @FXML private TextArea txtNotes;

    private Stage dialogStage;
    private Crop currentCrop;
    private CropService cropService;
    private boolean updateClicked = false;

    @FXML
    public void initialize() {
        System.out.println("🚀 UpdateCropController initialize() called");
        
        // Debug FXML injection
        debugFXMLInjection();
        
        cropService = new CropService();
        
        // Populate combo boxes
        comboCropType.getItems().addAll("Vegetable", "Fruit", "Grain", "Herb", "Flower");
        comboStatus.getItems().addAll("Planted", "Growing", "Harvesting", "Harvested", "Dormant");
        
        System.out.println("✅ UpdateCropController initialized successfully");
    }

    private void debugFXMLInjection() {
        System.out.println("🔍 FXML Injection Debug:");
        System.out.println("   - txtCropId: " + (txtCropId != null ? "INJECTED" : "NULL"));
        System.out.println("   - txtCropName: " + (txtCropName != null ? "INJECTED" : "NULL"));
        System.out.println("   - comboCropType: " + (comboCropType != null ? "INJECTED" : "NULL"));
        System.out.println("   - comboStatus: " + (comboStatus != null ? "INJECTED" : "NULL"));
        System.out.println("   - datePlanting: " + (datePlanting != null ? "INJECTED" : "NULL"));
        System.out.println("   - dateHarvest: " + (dateHarvest != null ? "INJECTED" : "NULL"));
        System.out.println("   - txtQuantity: " + (txtQuantity != null ? "INJECTED" : "NULL"));
        System.out.println("   - txtFieldName: " + (txtFieldName != null ? "INJECTED" : "NULL"));
        System.out.println("   - txtNotes: " + (txtNotes != null ? "INJECTED" : "NULL"));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCropData(Crop crop) {
        System.out.println("🔄 Setting crop data in UpdateCropController...");
        System.out.println("📦 Received crop: " + (crop != null ? crop.getName() : "NULL"));
        
        this.currentCrop = crop;
        
        if (currentCrop != null) {
            System.out.println("✅ Crop data set successfully, populating form...");
            populateFormData();
        } else {
            System.err.println("❌ ERROR: Crop is null in setCropData!");
            showAlert("Error", "No crop data received for editing.", Alert.AlertType.ERROR);
        }
    }

    private void populateFormData() {
        System.out.println("📝 Starting form population...");
        
        if (currentCrop != null) {
            System.out.println("📦 Current crop details:");
            System.out.println("   - ID: " + currentCrop.getId());
            System.out.println("   - Name: " + currentCrop.getName());
            System.out.println("   - Type: " + currentCrop.getType());
            System.out.println("   - Status: " + currentCrop.getStatus());
            
            // Set crop ID with null check
            if (txtCropId != null) {
                txtCropId.setText(String.valueOf(currentCrop.getId()));
                System.out.println("   - Crop ID set: " + currentCrop.getId());
            } else {
                System.err.println("❌ txtCropId is null - FXML injection failed!");
                return;
            }
            
            // Set crop name
            if (txtCropName != null) {
                txtCropName.setText(currentCrop.getName());
            }
            
            // Set crop type
            if (comboCropType != null && currentCrop.getType() != null) {
                comboCropType.setValue(currentCrop.getType());
            }
            
            // Set status
            if (comboStatus != null && currentCrop.getStatus() != null) {
                comboStatus.setValue(currentCrop.getStatus());
            }
            
            // Set dates
            if (datePlanting != null && currentCrop.getPlantingDate() != null) {
                datePlanting.setValue(currentCrop.getPlantingDate());
            }
            
            if (dateHarvest != null && currentCrop.getHarvestDate() != null) {
                dateHarvest.setValue(currentCrop.getHarvestDate());
            }
            
            // Set quantity and field
            if (txtQuantity != null) {
                txtQuantity.setText(String.valueOf(currentCrop.getQuantity()));
            }
            
            if (txtFieldName != null && currentCrop.getField() != null) {
                txtFieldName.setText(currentCrop.getField());
            }
            
            System.out.println("✅ Form populated successfully");
        } else {
            System.err.println("❌ ERROR: Cannot populate form - currentCrop is null!");
            showAlert("Error", "Cannot load crop data for editing.", Alert.AlertType.ERROR);
        }
    }

    public boolean isUpdateClicked() {
        return updateClicked;
    }

    @FXML
    private void onUpdateCrop() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Update crop object with form data
            currentCrop.setName(txtCropName.getText().trim());
            currentCrop.setType(comboCropType.getValue());
            currentCrop.setStatus(comboStatus.getValue());
            currentCrop.setPlantingDate(datePlanting.getValue());
            currentCrop.setHarvestDate(dateHarvest.getValue());
            currentCrop.setQuantity(Double.parseDouble(txtQuantity.getText().trim()));
            currentCrop.setField(txtFieldName.getText().trim());
            // currentCrop.setNotes(txtNotes.getText().trim()); // Uncomment if you have notes field

            // Call service to update
            boolean success = cropService.updateCrop(currentCrop);
            
            if (success) {
                updateClicked = true;
                showAlert("Success", "Crop '" + currentCrop.getName() + "' updated successfully!", Alert.AlertType.INFORMATION);
                dialogStage.close();
            } else {
                showAlert("Error", "Failed to update crop. Please try again.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid number for quantity.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to update crop: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateInput() {
        if (txtCropName.getText() == null || txtCropName.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter crop name", Alert.AlertType.ERROR);
            txtCropName.requestFocus();
            return false;
        }

        if (comboCropType.getValue() == null) {
            showAlert("Validation Error", "Please select crop type", Alert.AlertType.ERROR);
            comboCropType.requestFocus();
            return false;
        }

        if (comboStatus.getValue() == null) {
            showAlert("Validation Error", "Please select status", Alert.AlertType.ERROR);
            comboStatus.requestFocus();
            return false;
        }

        if (txtQuantity.getText() == null || txtQuantity.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter quantity", Alert.AlertType.ERROR);
            txtQuantity.requestFocus();
            return false;
        }

        try {
            double quantity = Double.parseDouble(txtQuantity.getText().trim());
            if (quantity < 0) {
                showAlert("Validation Error", "Quantity cannot be negative", Alert.AlertType.ERROR);
                txtQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid number for quantity", Alert.AlertType.ERROR);
            txtQuantity.requestFocus();
            return false;
        }

        // Validate dates
        if (datePlanting.getValue() != null && dateHarvest.getValue() != null) {
            if (dateHarvest.getValue().isBefore(datePlanting.getValue())) {
                showAlert("Validation Error", "Harvest date cannot be before planting date", Alert.AlertType.ERROR);
                dateHarvest.requestFocus();
                return false;
            }
        }

        return true;
    }

    @FXML
    private void onCancel() {
        updateClicked = false;
        dialogStage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Static method to open the Update Crop dialog
    public static boolean showUpdateCropDialog(Crop crop) {
        try {
            System.out.println("🚀 Opening Update Crop Dialog...");
            System.out.println("📦 Crop passed to dialog: " + (crop != null ? crop.getName() : "NULL"));
            
            // Check if FXML file exists
            java.net.URL fxmlUrl = UpdateCropController.class.getResource("/fxml/UpdateCrop.fxml");
            if (fxmlUrl == null) {
                System.err.println("❌ FXML file not found: /fxml/UpdateCrop.fxml");
                showStaticAlert("Error", "FXML file not found: UpdateCrop.fxml", Alert.AlertType.ERROR);
                return false;
            }
            System.out.println("✅ FXML file found: " + fxmlUrl);
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            UpdateCropController controller = loader.getController();
            System.out.println("✅ Controller loaded: " + (controller != null ? "YES" : "NO"));

            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);
            controller.setCropData(crop);

            dialogStage.setTitle("Update Crop - " + (crop != null ? crop.getName() : "Unknown Crop"));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);

            Scene scene = new Scene(root, 600, 550);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();

            return controller.isUpdateClicked();

        } catch (IOException e) {
            System.err.println("❌ Failed to load Update Crop dialog: " + e.getMessage());
            e.printStackTrace();
            
            showStaticAlert("Error", "Failed to open Update Crop dialog: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    private static void showStaticAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}