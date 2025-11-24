package com.freshharvest.controller;

import com.freshharvest.app.Batch;
import com.freshharvest.util.DBHandle;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class BatchManagementController {

    // --- FXML Fields (Match the "S2" FXML) ---
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilter;

    @FXML private TableView<Batch> batchTable;
    @FXML private TableColumn<Batch, Integer> colBatchId;
    @FXML private TableColumn<Batch, String> colCropType;
    @FXML private TableColumn<Batch, String> colHarvestDate;
    @FXML private TableColumn<Batch, String> colStatus;
    @FXML private TableColumn<Batch, String> colGrade;

    @FXML private Label lblSelectedBatch;
    @FXML private ComboBox<String> cmbGrade;
    @FXML private ComboBox<String> cmbStatus;

    // --- Logic Fields ---
    private DBHandle dbHandler;
    private Batch selectedBatch;
    private ObservableList<Batch> masterData;
    private FilteredList<Batch> filteredData;

    @FXML
    public void initialize() {
        dbHandler = DBHandle.getInstance();

        // 1. Setup Columns
        colBatchId.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colCropType.setCellValueFactory(new PropertyValueFactory<>("cropType"));
        colHarvestDate.setCellValueFactory(new PropertyValueFactory<>("harvestDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // 2. Add "Status Pill" Styling (Colors based on status)
        colStatus.setCellFactory(new Callback<TableColumn<Batch, String>, TableCell<Batch, String>>() {
            @Override
            public TableCell<Batch, String> call(TableColumn<Batch, String> param) {
                return new TableCell<Batch, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                            getStyleClass().removeAll("status-pill", "status-success", "status-warning", "status-danger", "status-info");
                        } else {
                            setText(item);
                            getStyleClass().add("status-pill"); // Base pill shape
                            
                            // Color logic based on status text
                            if (item.equalsIgnoreCase("Accepted") || item.equalsIgnoreCase("Packaged")) {
                                getStyleClass().add("status-success"); // Green
                            } else if (item.equalsIgnoreCase("Pending") || item.equalsIgnoreCase("Processing")) {
                                getStyleClass().add("status-warning"); // Orange
                            } else if (item.equalsIgnoreCase("Rejected")) {
                                getStyleClass().add("status-danger");  // Red
                            } else {
                                getStyleClass().add("status-info");    // Blue
                            }
                        }
                    }
                };
            }
        });

        // 3. Populate Dropdowns
        cmbGrade.getItems().addAll("A", "B", "C", "-");
        cmbStatus.getItems().addAll("Processing", "Accepted", "Rejected", "Packaged");
        
        // Populate Filter Dropdown
        cmbFilter.getItems().add("All");
        cmbFilter.getItems().addAll("Processing", "Accepted", "Rejected", "Packaged");
        cmbFilter.setValue("All");

        // 4. Load Data & Setup Search/Filter
        loadData();

        // 5. Add Selection Listener
        batchTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedBatch = newVal;
                lblSelectedBatch.setText("Batch #" + newVal.getBatchId() + " (" + newVal.getCropType() + ")");
                cmbGrade.setValue(newVal.getGrade());
                cmbStatus.setValue(newVal.getStatus());
            } else {
                selectedBatch = null;
                lblSelectedBatch.setText("No Batch Selected");
            }
        });

        // 6. Bind Search & Filter Listeners
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());
        cmbFilter.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());
    }

    private void loadData() {
        // Fetch from DB
        masterData = dbHandler.getAllBatches();
        
        // Wrap in FilteredList
        filteredData = new FilteredList<>(masterData, p -> true);
        
        // Wrap in SortedList (so sorting columns works)
        SortedList<Batch> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(batchTable.comparatorProperty());
        
        batchTable.setItems(sortedData);
    }

    private void updateFilter() {
        String searchText = txtSearch.getText().toLowerCase();
        String statusFilter = cmbFilter.getValue();

        filteredData.setPredicate(batch -> {
            // 1. Check Status Filter
            boolean matchesStatus = true;
            if (statusFilter != null && !statusFilter.equals("All")) {
                matchesStatus = batch.getStatus().equalsIgnoreCase(statusFilter);
            }

            // 2. Check Search Text (ID or Crop Name)
            boolean matchesSearch = true;
            if (searchText != null && !searchText.isEmpty()) {
                matchesSearch = String.valueOf(batch.getBatchId()).contains(searchText) ||
                                batch.getCropType().toLowerCase().contains(searchText);
            }

            return matchesStatus && matchesSearch;
        });
    }

    @FXML
    private void onSaveGrading() {
        if (selectedBatch == null) return;

        String newGrade = cmbGrade.getValue();
        String newStatus = cmbStatus.getValue();

        System.out.println("Updating Batch #" + selectedBatch.getBatchId() + " -> " + newStatus);

        boolean success = dbHandler.updateBatchGradeAndStatus(selectedBatch.getBatchId(), newGrade, newStatus);
        
        if (success) {
            loadData(); // Refresh table to show new colors/status
            lblSelectedBatch.setText("Updated Successfully!");
        } else {
            lblSelectedBatch.setText("Update Failed.");
        }
    }
}