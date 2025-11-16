package com.freshharvest.controller;

import com.freshharvest.model.Batch;
import com.freshharvest.util.DBHandle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class BatchManagementController {

    // Table FXML fields
    @FXML
    private TableView<Batch> batchTable;
    @FXML
    private TableColumn<Batch, Integer> colBatchId;
    @FXML
    private TableColumn<Batch, String> colCropType;
    @FXML
    private TableColumn<Batch, String> colHarvestDate;
    @FXML
    private TableColumn<Batch, String> colStatus;
    @FXML
    private TableColumn<Batch, String> colGrade;

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

    private Batch selectedBatch;
    private DBHandle dbHandler;

    @FXML
    public void initialize() {
        dbHandler = DBHandle.getInstance();

        // --- Populate ComboBoxes based on Use Cases ---
        
        // From Use Case 5: Assigns a grade (A/B/C)
        cmbGrade.getItems().addAll("A", "B", "C");
        
        // From Use Case 5: Status (Accepted, Rework, Rejected)
        cmbStatus.getItems().addAll("Accepted", "Rework", "Rejected");

        // From Use Case 6: Prompts for packaging size and box type
        cmbPackageType.getItems().addAll("Small Box (5kg)", "Medium Crate (15kg)", "Large Pallet");

        // --- Set up TableView ---
        colBatchId.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colCropType.setCellValueFactory(new PropertyValueFactory<>("cropType"));
        colHarvestDate.setCellValueFactory(new PropertyValueFactory<>("harvestDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        loadBatchTable();

        // --- Add listener to update form when a row is clicked ---
        batchTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    this.selectedBatch = newSelection;
                    lblSelectedBatch.setText("Batch #" + newSelection.getBatchId());
                    // Pre-fill form with existing data
                    cmbGrade.setValue(newSelection.getGrade());
                    cmbStatus.setValue(newSelection.getStatus());
                } else {
                    this.selectedBatch = null;
                    lblSelectedBatch.setText("None");
                }
            }
        );
    }

    private void loadBatchTable() {
        ObservableList<Batch> batches = dbHandler.getAllBatches();
        batchTable.setItems(batches);
    }

    @FXML
    private void onSaveGrading() {
        if (this.selectedBatch == null) {
            System.out.println("No batch selected."); // Later, show an Alert
            return;
        }
        
        String grade = cmbGrade.getValue();
        String status = cmbStatus.getValue();
        
        System.out.println("--- Saving Grade (Use Case 5) ---");
        
        boolean success = dbHandler.updateBatchGradeAndStatus(selectedBatch.getBatchId(), grade, status);

        if (success) {
            System.out.println("Grade/Status updated!");
            loadBatchTable(); // Refresh the table
        } else {
            System.out.println("Update failed.");
        }
    }

    @FXML
    private void onSavePackaging() {
        if (this.selectedBatch == null) {
            System.out.println("No batch selected.");
            return;
        }

        // Only allow packaging if batch is 'Accepted'
        if (!selectedBatch.getStatus().equals("Accepted")) {
            System.out.println("Batch must be 'Accepted' before packaging.");
            return;
        }

        String packageType = cmbPackageType.getValue();
        int quantity;
        try {
            quantity = Integer.parseInt(txtPackageQty.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }
        
        System.out.println("--- Saving Packaging (Use Case 6) ---");

        boolean success = dbHandler.createProcessedLot(selectedBatch.getBatchId(), packageType, quantity);

        if (success) {
            System.out.println("Batch packaged successfully!");
            loadBatchTable(); // Refresh table to show new 'Packaged' status
        } else {
            System.out.println("Packaging failed.");
        }
    }
}