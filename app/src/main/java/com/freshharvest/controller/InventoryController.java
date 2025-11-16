package com.freshharvest.controller;

import com.freshharvest.model.InventoryLot;
import com.freshharvest.util.DBHandle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class InventoryController {

    // Table FXML fields
    @FXML
    private TableView<InventoryLot> packagedBatchTable;
    @FXML
    private TableColumn<InventoryLot, Integer> colLotId;
    @FXML
    private TableColumn<InventoryLot, String> colCropType;
    @FXML
    private TableColumn<InventoryLot, String> colGrade;
    @FXML
    private TableColumn<InventoryLot, String> colPackageType;
    @FXML
    private TableColumn<InventoryLot, Integer> colQuantity;

    // Form FXML fields
    @FXML
    private Label lblSelectedBatch;
    @FXML
    private TextField txtLocation;
    @FXML
    private TextField txtConditions;
    @FXML
    private Button btnStoreBatch;

    private InventoryLot selectedLot;
    private DBHandle dbHandler;

    @FXML
    public void initialize() {
        dbHandler = DBHandle.getInstance();

        // --- Set up TableView ---
        colLotId.setCellValueFactory(new PropertyValueFactory<>("lotId"));
        colCropType.setCellValueFactory(new PropertyValueFactory<>("cropType"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colPackageType.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadPackagedBatches();

        // --- Add listener to update form when a row is clicked ---
        packagedBatchTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    this.selectedLot = newSelection;
                    lblSelectedBatch.setText("Lot #" + newSelection.getLotId());
                } else {
                    this.selectedLot = null;
                    lblSelectedBatch.setText("None");
                }
            }
        );
    }

    private void loadPackagedBatches() {
        ObservableList<InventoryLot> lots = dbHandler.getPackagedBatches();
        packagedBatchTable.setItems(lots);
    }

    @FXML
    private void onStoreBatch() {
        if (this.selectedLot == null) {
            System.out.println("No batch selected.");
            return;
        }

        // Actor enters location, temperature
        String location = txtLocation.getText();
        String conditions = txtConditions.getText();

        if (location.isEmpty() || conditions.isEmpty()) {
            System.out.println("Location and conditions are required.");
            return;
        }

        System.out.println("--- Storing Batch (Use Case 7) ---");
        
        boolean success = dbHandler.storeBatchInInventory(selectedLot.getLotId(), location, conditions);

        if (success) {
            System.out.println("Batch stored in inventory!");
            loadPackagedBatches(); // Refresh the table (the item will disappear)
            clearForm();
        } else {
            System.out.println("Failed to store batch.");
        }
    }

    private void clearForm() {
        lblSelectedBatch.setText("None");
        txtLocation.clear();
        txtConditions.clear();
        selectedLot = null;
    }
}