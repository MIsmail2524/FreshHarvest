package com.freshharvest.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
// import com.freshharvest.model.Batch; 

public class InventoryController {

    // Table FXML fields
    @FXML
    private TableView<Object> packagedBatchTable; // Replace 'Object' with your Batch/Package model
    // ... (TableColumn fields would go here) ...

    // Form FXML fields
    @FXML
    private Label lblSelectedBatch;
    @FXML
    private TextField txtLocation;
    @FXML
    private TextField txtConditions;
    @FXML
    private Button btnStoreBatch;

    private Object selectedBatch; // Replace 'Object'

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // --- Set up TableView ---
        // (You would set cell value factories here)
        // (You would call DBHandler.getPackagedBatches() to populate this table) 

        // --- Add listener to update form when a row is clicked ---
        packagedBatchTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    this.selectedBatch = newSelection;
                    // lblSelectedBatch.setText(String.valueOf(newSelection.getBatchId()));
                    lblSelectedBatch.setText("Batch #1001"); // Placeholder
                }
            }
        );
    }

    /**
     * Handles storing the batch in inventory. (Use Case 7)
     */
    @FXML
    private void onStoreBatch() {
        if (this.selectedBatch == null) {
            System.out.println("No batch selected.");
            return;
        }

        // Actor enters location, temperature 
        String location = txtLocation.getText();
        String conditions = txtConditions.getText();

        System.out.println("--- Storing Batch (Use Case 7) ---");
        System.out.println("Batch: " + lblSelectedBatch.getText());
        System.out.println("Location: " + location);
        System.out.println("Conditions: " + conditions);

        // TODO: Call DBHandler to update inventory with storage details 
    }
}