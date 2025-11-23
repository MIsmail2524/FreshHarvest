package com.freshharvest.controller;

import com.freshharvest.app.Crop;
import com.freshharvest.app.CropService;
import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CropsController implements DataChangeListener {
    private NotificationService notificationService;
    @FXML private Label totalCropsLabel;
    @FXML private TableView<Crop> cropsTable;
    
    @FXML private TableColumn<Crop, Integer> colId;
    @FXML private TableColumn<Crop, String> colName;
    @FXML private TableColumn<Crop, String> colType;
    @FXML private TableColumn<Crop, String> colPlantingDate;
    @FXML private TableColumn<Crop, String> colHarvestDate;
    @FXML private TableColumn<Crop, String> colStatus;
    @FXML private TableColumn<Crop, Double> colQuantity;
    @FXML private TableColumn<Crop, String> colField;

    private CropService cropService;
    private ObservableList<Crop> cropsData;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
            notificationService = NotificationService.getInstance();
        notificationService.addListener(this);
        System.out.println("🚀 CropsController initializing...");
        
        cropService = new CropService();
        cropsData = FXCollections.observableArrayList();
        
        setupTableColumns();
        loadCropsData();
        
        System.out.println("✅ CropsController initialized successfully");
    }

     @Override
    public void onDataChanged(String dataType) {
        if ("CROP_ADDED".equals(dataType) || "CROP_UPDATED".equals(dataType)) {
            javafx.application.Platform.runLater(() -> {
                refreshData(); // Your existing refresh method
                System.out.println("Crops table automatically refreshed");
            });
        }
    }

    private void setupTableColumns() {
        try {
            System.out.println("🔄 Setting up table columns...");
            
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colType.setCellValueFactory(new PropertyValueFactory<>("type"));
            
            colPlantingDate.setCellValueFactory(cellData -> {
                LocalDate plantingDate = cellData.getValue().getPlantingDate();
                return new javafx.beans.property.SimpleStringProperty(
                    plantingDate != null ? plantingDate.format(dateFormatter) : "N/A"
                );
            });
            
            colHarvestDate.setCellValueFactory(cellData -> {
                LocalDate harvestDate = cellData.getValue().getHarvestDate();
                return new javafx.beans.property.SimpleStringProperty(
                    harvestDate != null ? harvestDate.format(dateFormatter) : "N/A"
                );
            });
            
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            colField.setCellValueFactory(new PropertyValueFactory<>("field"));

            cropsTable.setItems(cropsData);
            
            System.out.println("✅ Table columns setup completed");
            
        } catch (Exception e) {
            System.err.println("❌ Error setting up table columns: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCropsData() {
        try {
            System.out.println("🔄 Loading crops data...");
            
            cropsData.clear();
            
            var crops = cropService.getAllCrops();
            
            System.out.println("📊 Retrieved " + crops.size() + " crops from service");
            
            if (!crops.isEmpty()) {
                cropsData.setAll(crops);
                updateCropsCount();
                
                // Debug: Print first crop
                Crop firstCrop = crops.get(0);
                System.out.println("📝 First crop: " + firstCrop.getName() + " | ID: " + firstCrop.getId());
            } else {
                System.out.println("ℹ️ No crops found in database");
                totalCropsLabel.setText("0 Total");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error loading crops data: " + e.getMessage());
            e.printStackTrace();
            showAlert("Data Load Error", 
                     "Unable to load crops data. Please check your connection.\nError: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
            totalCropsLabel.setText("0 Total");
        }
    }

    private void updateCropsCount() {
        try {
            int total = cropsData.size();
            totalCropsLabel.setText(total + " Total");
            System.out.println("📈 Updated crops count: " + total);
        } catch (Exception e) {
            System.err.println("❌ Error updating crops count: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addCrop() {
        System.out.println("➕ Add Crop button clicked");
        MainController.getInstance().openAddCrop();
    }
    @FXML
private void updateCrop() {
    Crop selected = cropsTable.getSelectionModel().getSelectedItem();

    if (selected == null) {
        showAlert("Select Crop", "Please select a crop to update.", Alert.AlertType.WARNING);
        return;
    }

    UpdateCropController.showUpdateCropDialog(this, selected);
}



// Add this method to refresh after update from other controllers
public void refreshAfterUpdate() {
    loadCropsData();
}

    
   
    
    @FXML
    private void deleteCrop() {
        Crop selected = cropsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("🗑️ Deleting crop: " + selected.getName() + " (ID: " + selected.getId() + ")");
            
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Delete");
            confirmation.setHeaderText("Delete Crop");
            confirmation.setContentText("Are you sure you want to delete crop: " + selected.getName() + "?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    boolean success = cropService.deleteCrop(selected.getId());
                    if (success) {
                        showAlert("Success", "Crop deleted successfully!", Alert.AlertType.INFORMATION);
                        loadCropsData(); // Refresh the table
                    } else {
                        showAlert("Error", "Failed to delete crop.", Alert.AlertType.ERROR);
                    }
                }
            });
        } else {
            showAlert("Selection Required", "Please select a crop to delete.", Alert.AlertType.WARNING);
        }
    }
    
    @FXML
    private void refreshData() {
        System.out.println("🔄 Refreshing crops data...");
        loadCropsData();
        showAlert("Refreshed", "Crops data refreshed successfully!", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void closeApplication() {
        System.out.println("🚪 Closing application...");
        if (com.freshharvest.app.FreshHarvestApp.getPrimaryStage() != null) {
            com.freshharvest.app.FreshHarvestApp.getPrimaryStage().close();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Method to refresh data after adding a new crop
    public void refreshAfterAdd() {
        loadCropsData();
    }
}