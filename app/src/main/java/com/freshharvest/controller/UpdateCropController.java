package com.freshharvest.controller;

import com.freshharvest.app.Crop;
import com.freshharvest.app.CropService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UpdateCropController {

    @FXML private TextField txtCropName;
    @FXML private TextField txtCropType;
    @FXML private ComboBox<String> comboSeason;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtField;

    private Stage dialogStage;
    private Crop selectedCrop; // Crop being edited
    private CropsController cropsController; // Parent controller
    private CropService cropService;

    @FXML
    public void initialize() {
        cropService = new CropService();
        comboSeason.getItems().addAll("Spring", "Summer", "Autumn", "Winter");
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setCropsController(CropsController controller) {
        this.cropsController = controller;
    }

    public void setCropData(Crop crop) {
        this.selectedCrop = crop;

        txtCropName.setText(crop.getName());
        txtCropType.setText(crop.getType());
        comboSeason.setValue("Spring"); // You can fetch season if you have it
        txtQuantity.setText(String.valueOf(crop.getQuantity()));
        txtField.setText(crop.getField());
    }

    @FXML
    private void onUpdateCrop() {
        try {
            selectedCrop.setName(txtCropName.getText());
            selectedCrop.setType(txtCropType.getText());
            selectedCrop.setQuantity(Double.parseDouble(txtQuantity.getText()));
            selectedCrop.setField(txtField.getText());

            boolean success = cropService.updateCrop(selectedCrop);

            if (success) {
                showAlert("Success", "Crop updated successfully!", Alert.AlertType.INFORMATION);

                if (cropsController != null) {
                    cropsController.refreshAfterAdd(); // refresh table
                }

                dialogStage.close();
            } else {
                showAlert("Error", "Failed to update crop.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancel() {
        dialogStage.close();
    }
    public static void showUpdateCropDialog(CropsController parentController, Crop crop) {
    try {
        FXMLLoader loader = new FXMLLoader(UpdateCropController.class.getResource("/fxml/UpdateCrop.fxml"));
        Parent root = loader.load();

        UpdateCropController controller = loader.getController();
        controller.setCropsController(parentController);
        controller.setCropData(crop);

        Stage dialog = new Stage();
        controller.setDialogStage(dialog);

        dialog.setTitle("Update Crop");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setScene(new Scene(root, 600, 600));

        dialog.showAndWait();

    } catch (Exception e) {
        System.err.println("❌ Failed to open update crop dialog: " + e.getMessage());
        e.printStackTrace();
    }
}


    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
