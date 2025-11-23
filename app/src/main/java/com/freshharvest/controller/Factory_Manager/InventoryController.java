package com.freshharvest.controller.Factory_Manager;

import com.freshharvest.app.Factory_Manager.InventoryLot;
import com.freshharvest.dao.DAOFactory;
import com.freshharvest.dao.FactoryDAO;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;
public class InventoryController implements DataChangeListener {
    
    private NotificationService notificationService;
    @FXML private TableView<InventoryLot> packagedBatchTable;
    @FXML private TableColumn<InventoryLot, Integer> colLotId;
    @FXML private TableColumn<InventoryLot, String> colCropType;
    @FXML private TableColumn<InventoryLot, String> colGrade;
    @FXML private TableColumn<InventoryLot, String> colPackageType;
    @FXML private TableColumn<InventoryLot, Integer> colQuantity;

    @FXML private Label lblSelectedBatch;
    @FXML private TextField txtLocation;
    @FXML private TextField txtConditions;
    @FXML private Button btnStoreBatch;

    private InventoryLot selectedLot;
    private FactoryDAO factoryDAO;

    @FXML
    public void initialize() {
        factoryDAO = DAOFactory.getFactoryDAO();
        setupTableColumns();
        loadPackagedBatches();
        setupSelectionListener();
                NotificationService.getInstance().addListener(this);

    }

     @Override
    public void onDataChanged(String dataType) {
        switch (dataType) {
            case "BATCH_UPDATED":
            case "INVENTORY_CHANGED":
            case "ORDER_STATUS_CHANGED":
                // Refresh inventory data when changes occur
                javafx.application.Platform.runLater(() -> {
                    loadPackagedBatches();
                    System.out.println("Inventory data automatically refreshed");
                });
                break;
        }
    }

    
    private void setupTableColumns() {
        colLotId.setCellValueFactory(new PropertyValueFactory<>("lotId"));
        colCropType.setCellValueFactory(new PropertyValueFactory<>("cropType"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colPackageType.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    private void setupSelectionListener() {
        packagedBatchTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    this.selectedLot = newSelection;
                    lblSelectedBatch.setText("Selected: Lot #" + newSelection.getLotId() + " - " + newSelection.getCropType());
                    enableForm();
                } else {
                    this.selectedLot = null;
                    lblSelectedBatch.setText("No lot selected");
                    disableForm();
                }
            }
        );
    }

    private void enableForm() {
        txtLocation.setDisable(false);
        txtConditions.setDisable(false);
        btnStoreBatch.setDisable(false);
    }

    private void disableForm() {
        txtLocation.setDisable(true);
        txtConditions.setDisable(true);
        btnStoreBatch.setDisable(true);
    }

    private void loadPackagedBatches() {
        try {
            List<InventoryLot> lots = factoryDAO.getPackagedBatches();
            ObservableList<InventoryLot> observableLots = FXCollections.observableArrayList(lots);
            packagedBatchTable.setItems(observableLots);
            
            if (lots.isEmpty()) {
                lblSelectedBatch.setText("No packaged batches available");
            }
            
        } catch (Exception e) {
            showAlert("Error", "Failed to load packaged batches: " + e.getMessage());
        }
    }

    @FXML
    private void onStoreBatch() {
        if (this.selectedLot == null) {
            showAlert("No Batch Selected", "Please select a batch to store.");
            return;
        }

        String location = txtLocation.getText().trim();
        String conditions = txtConditions.getText().trim();

        if (location.isEmpty() || conditions.isEmpty()) {
            showAlert("Missing Information", "Location and storage conditions are required.");
            return;
        }

        System.out.println("Storing Batch #" + selectedLot.getLotId() + " in inventory...");
        
        try {
            boolean success = factoryDAO.storeBatchInInventory(selectedLot.getLotId(), location, conditions);

            if (success) {
                                notificationService.notifyInventoryChanged();

                showSuccessAlert("Success", "Batch #" + selectedLot.getLotId() + " stored in inventory successfully!");
                loadPackagedBatches(); // Refresh table
                clearForm();
                packagedBatchTable.getSelectionModel().clearSelection();
            } else {
                showAlert("Error", "Failed to store batch in inventory.");
            }
            
        } catch (Exception e) {
            System.err.println("Error storing batch: " + e.getMessage());
            showAlert("Database Error", "Could not store batch: " + e.getMessage());
        }
    }

    @FXML
    private void onRefresh() {
        loadPackagedBatches();
        clearForm();
        packagedBatchTable.getSelectionModel().clearSelection();
    }

    private void clearForm() {
        txtLocation.clear();
        txtConditions.clear();
        disableForm();
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