package com.freshharvest.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
// You will need to create a simple 'Batch' model class for the TableView
// import com.freshharvest.model.Batch; 

public class BatchManagementController {

    // Table FXML fields
    @FXML
    private TableView<Object> batchTable; // Replace 'Object' with your Batch model
    @FXML
    private TableColumn<Object, Integer> colBatchId; // Replace 'Object'
    @FXML
    private TableColumn<Object, String> colCropType; // Replace 'Object'
    @FXML
    private TableColumn<Object, String> colHarvestDate; // Replace 'Object'
    @FXML
    private TableColumn<Object, String> colStatus; // Replace 'Object'
    @FXML
    private TableColumn<Object, String> colGrade; // Replace 'Object'

    // Form FXML fields
    @FXML
    private Label lblSelectedBatch;
    @FXML
    private ComboBox<String> cmbGrade;
    @FXML
    private ComboBox<String> cmbStatus;
    @FXML
    private Button btnSaveGrading;
    @FXML
    private ComboBox<String> cmbPackageType;
    @FXML
    private TextField txtPackageQty;
    @FXML
    private Button btnSavePackaging;

    // A placeholder for the currently selected batch
    private Object selectedBatch; // Replace 'Object' with your Batch model

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // --- Populate ComboBoxes based on Use Cases ---
        
        // From Use Case 5: Assigns a grade (A/B/C) 
        cmbGrade.getItems().addAll("A", "B", "C");
        
        // From Use Case 5: Status (Accepted, Rework, Rejected) 
        cmbStatus.getItems().addAll("Accepted", "Rework", "Rejected");

        // From Use Case 6: Prompts for packaging size and box type 
        cmbPackageType.getItems().addAll("Small Box (5kg)", "Medium Crate (15kg)", "Large Pallet");

        // --- Set up TableView ---
        // (You would set cell value factories here to link columns to your Batch model)
        // (You would call DBHandler.getBatches() to populate this table)

        // --- Add listener to update form when a row is clicked ---
        batchTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    this.selectedBatch = newSelection;
                    // Assuming your Batch model has a getBatchId() method
                    // lblSelectedBatch.setText(String.valueOf(newSelection.getBatchId()));
                    lblSelectedBatch.setText("Batch #1001"); // Placeholder
                }
            }
        );
    }

    /**
     * Handles saving the grading and status. (Use Case 5)
     */
    @FXML
    private void onSaveGrading() {
        if (this.selectedBatch == null) {
            System.out.println("No batch selected.");
            return;
        }
        
        String grade = cmbGrade.getValue();
        String status = cmbStatus.getValue();
        
        System.out.println("--- Saving Grade (Use Case 5) ---");
        System.out.println("Batch: " + lblSelectedBatch.getText());
        System.out.println("Grade: " + grade);
        System.out.println("Status: " + status);

        // TODO: Call DBHandler to update the batch record [cite: 20]
    }

    /**
     * Handles saving the packaging information. (Use Case 6)
     */
    @FXML
    private void onSavePackaging() {
        if (this.selectedBatch == null) {
            System.out.println("No batch selected.");
            return;
        }

        String packageType = cmbPackageType.getValue();
        String quantity = txtPackageQty.getText();
        
        System.out.println("--- Saving Packaging (Use Case 6) ---");
        System.out.println("Batch: " + lblSelectedBatch.getText());
        System.out.println("Package Type: " + packageType);
        System.out.println("Quantity: " + quantity);

        // TODO: Call DBHandler to update batch as "Packaged" 
        // This will also generate labels/QR codes 
    }
}