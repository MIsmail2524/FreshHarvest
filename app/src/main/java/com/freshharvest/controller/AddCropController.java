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
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.time.LocalDate;

public class AddCropController {

    @FXML private TextField txtCropName;
    @FXML private TextField txtCropType;
    @FXML private ComboBox<String> comboSeason;
    @FXML private TextField txtGrowthTime;

    private Stage dialogStage;
    private CropService cropService;
    private CropsController cropsController; // Reference to refresh the main table
        private NotificationService notificationService; 


    @FXML
    public void initialize() {
        cropService = new CropService();
                        notificationService = NotificationService.getInstance();

        comboSeason.getItems().addAll("Spring", "Summer", "Autumn", "Winter");
        System.out.println("🛠️ AddCropController initialized");
                    //    notificationService.addListener(this);


    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setCropsController(CropsController cropsController) {
        this.cropsController = cropsController;
    }

    @FXML
    private void onSaveCrop() {
        try {
            String cropName = txtCropName.getText().trim();
            String cropType = txtCropType.getText().trim();
            String season = comboSeason.getValue();
            String growthTime = txtGrowthTime.getText().trim();

            // Validate required fields
            if (cropName.isEmpty()) {
                showAlert("Validation Error", "Please enter crop name", Alert.AlertType.ERROR);
                txtCropName.requestFocus();
                return;
            }

            if (cropType.isEmpty()) {
                showAlert("Validation Error", "Please enter crop type", Alert.AlertType.ERROR);
                txtCropType.requestFocus();
                return;
            }

            // Create new crop object
            Crop newCrop = new Crop();
            newCrop.setName(cropName);
            newCrop.setType(cropType);
            newCrop.setPlantingDate(LocalDate.now()); // Default to today
            newCrop.setHarvestDate(LocalDate.now().plusDays(90)); // Default 90 days from now
            newCrop.setStatus("Planted");
            newCrop.setQuantity(0.0); // Default quantity
            newCrop.setField("Field A"); // Default field
            newCrop.setFarmerId(1); // Default farmer ID

            // Save to database
            boolean success = cropService.addCrop(newCrop);
            
            if (success) {
                System.out.println("✅ Crop saved successfully: " + newCrop.getName());
                showAlert("Success", "Crop '" + cropName + "' added successfully!", Alert.AlertType.INFORMATION);
                                notificationService.notifyDataChanged("CROP_ADDED");

                // Refresh the main crops table
                if (cropsController != null) {
                    cropsController.refreshAfterAdd();
                }
                
                onCancel();
            } else {
                showAlert("Error", "Failed to save crop to database.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            System.err.println("❌ Error saving crop: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to save crop: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onCancel() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Updated static method to open the Add Crop dialog
    public static void showAddCropDialog(CropsController cropsController) {
        try {
            FXMLLoader loader = new FXMLLoader(AddCropController.class.getResource("/fxml/add-crop.fxml"));
            Parent root = loader.load();

            AddCropController controller = loader.getController();
            controller.setCropsController(cropsController);

            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);

            dialogStage.setTitle("Add New Crop");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);

            Scene scene = new Scene(root, 450, 400);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();

        } catch (IOException e) {
            System.err.println("❌ Failed to load Add Crop dialog: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open Add Crop dialog");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
}