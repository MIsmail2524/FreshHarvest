package com.freshharvest.controller.Factory_Manager;

import com.freshharvest.app.Factory_Manager.Batch;
import com.freshharvest.dao.DAOFactory;
import com.freshharvest.dao.FactoryDAO;
import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class BatchManagementController implements DataChangeListener {
    
    private NotificationService notificationService;
    private FactoryDAO factoryDAO;

    @FXML private TableView<Batch> batchTable;
    @FXML private TableColumn<Batch, Integer> colBatchId;
    @FXML private TableColumn<Batch, Integer> colCropId;
    @FXML private TableColumn<Batch, String> colCropName;
    @FXML private TableColumn<Batch, String> colProcessName;
    @FXML private TableColumn<Batch, String> colStatus;
    @FXML private TableColumn<Batch, String> colGrade;
    @FXML private TableColumn<Batch, String> colOperator;
    @FXML private TableColumn<Batch, Timestamp> colStartTime;
    @FXML private TableColumn<Batch, Timestamp> colProcessedDate;
    
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilter;
    @FXML private ComboBox<String> cmbGrade;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private ComboBox<String> cmbCrop;
    @FXML private TextArea txtNotes;
    @FXML private Label lblSelectedBatch;
    @FXML private Label lblBatchDetails;
    @FXML private Label lblTotalCount;
    @FXML private Button btnSaveProcessing;
    @FXML private Button btnCreateBatch;
    
    private ObservableList<Batch> masterData;
    private ObservableList<String> availableCrops;
    private Batch selectedBatch;

    @FXML
    public void initialize() {
        factoryDAO = DAOFactory.getFactoryDAO();
        notificationService = NotificationService.getInstance();
        notificationService.addListener(this);
        
        setupTableColumns();
        setupComboBoxes();
        setupTableRowFactory();
        loadCropData();
        loadData();
        setupSelectionListener();
        setupSearchAndFilter();
    }

    @Override
    public void onDataChanged(String dataType) {
        switch (dataType) {
            case "BATCH_UPDATED":
            case "ORDER_STATUS_CHANGED":
            case "INVENTORY_CHANGED":
                javafx.application.Platform.runLater(() -> {
                    loadData();
                    loadCropData();
                    showNotification("Data updated automatically");
                });
                break;
        }
    }

    private void showNotification(String message) {
        System.out.println("Notification: " + message);
    }

    private void setupTableColumns() {
        colBatchId.setCellValueFactory(new PropertyValueFactory<>("processId"));
        colCropId.setCellValueFactory(new PropertyValueFactory<>("cropId"));
        colCropName.setCellValueFactory(new PropertyValueFactory<>("cropName"));
        colProcessName.setCellValueFactory(new PropertyValueFactory<>("processName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("processStatus"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colOperator.setCellValueFactory(new PropertyValueFactory<>("operatorName"));
        colStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colProcessedDate.setCellValueFactory(new PropertyValueFactory<>("processedDate"));
        
        colStartTime.setCellFactory(column -> new TableCell<Batch, Timestamp>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(item.toLocalDateTime().toLocalDate().toString());
                }
            }
        });
        
        colProcessedDate.setCellFactory(column -> new TableCell<Batch, Timestamp>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(item.toLocalDateTime().toLocalDate().toString());
                }
            }
        });
    }

    private void setupComboBoxes() {
        // Grade combo box
        cmbGrade.setItems(FXCollections.observableArrayList(
            "Not Graded", "Premium", "Grade A", "Grade B", "Grade C", "Rejected"
        ));
        
        // Status combo box
        cmbStatus.setItems(FXCollections.observableArrayList(
            "Pending", "In Progress", "Completed", "Failed"
        ));
        
        // Filter combo box
        cmbFilter.setItems(FXCollections.observableArrayList(
            "All Batches", "Pending", "In Progress", "Completed", "Failed"
        ));
        cmbFilter.setValue("All Batches");
    }

  private void loadCropData() {
    try {
        // Get available crops from database - this should return crops, not batches
        List<Map<String, Object>> crops = factoryDAO.getAvailableCropsForProcessing();
        availableCrops = FXCollections.observableArrayList();
        
        for (Map<String, Object> crop : crops) {
            String cropInfo = String.format("%s (ID: %d)", 
                crop.get("crop_name"), 
                crop.get("crop_id"));
            availableCrops.add(cropInfo);
        }
        
        cmbCrop.setItems(availableCrops);
        
    } catch (Exception e) {
        System.err.println("Error loading crop data: " + e.getMessage());
        // Fallback to sample data
        availableCrops = FXCollections.observableArrayList(
            "Tomatoes (ID: 1)", "Potatoes (ID: 2)", "Carrots (ID: 3)"
        );
        cmbCrop.setItems(availableCrops);
    }
}
    private void setupTableRowFactory() {
        batchTable.setRowFactory(tv -> new TableRow<Batch>() {
            @Override
            protected void updateItem(Batch batch, boolean empty) {
                super.updateItem(batch, empty);
                
                if (empty || batch == null) {
                    setStyle("");
                } else {
                    switch (batch.getProcessStatus().toLowerCase()) {
                        case "pending":
                            setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ffe0b2;");
                            break;
                        case "in progress":
                            setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #bbdefb;");
                            break;
                        case "completed":
                            setStyle("-fx-background-color: #e8f5e8; -fx-border-color: #c8e6c9;");
                            break;
                        case "failed":
                            setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

    private void setupSelectionListener() {
        batchTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                selectedBatch = newValue;
                if (newValue != null) {
                    updateFormWithSelectedBatch(newValue);
                    btnSaveProcessing.setDisable(false);
                } else {
                    clearForm();
                    btnSaveProcessing.setDisable(true);
                }
            });
    }

    private void setupSearchAndFilter() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTable();
        });
        
        cmbFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterTable();
        });
    }

    private void updateFormWithSelectedBatch(Batch batch) {
    cmbGrade.setValue(batch.getGrade());
    cmbStatus.setValue(batch.getProcessStatus());
    txtNotes.setText(batch.getNotes() != null ? batch.getNotes() : "");
    
    lblSelectedBatch.setText("Processing: Batch #" + batch.getProcessId() + " - " + batch.getProcessName());
    
    lblBatchDetails.setText(String.format(
        "Crop: %s | Operator: %s | Started: %s | Status: %s | Grade: %s",
        batch.getCropName() != null ? batch.getCropName() : "No Crop", 
        batch.getOperatorName() != null ? batch.getOperatorName() : "Not Assigned",
        batch.getStartTime() != null ? batch.getStartTime().toLocalDateTime().toLocalDate().toString() : "Not Started",
        batch.getProcessStatus(),
        batch.getGrade() != null ? batch.getGrade() : "Not Graded"
    ));
}

    private void clearForm() {
        cmbGrade.setValue(null);
        cmbStatus.setValue(null);
        txtNotes.clear();
        lblSelectedBatch.setText("No batch selected");
        lblBatchDetails.setText("Select a batch to process");
    }

    private void loadData() {
        try {
            List<Batch> batches = factoryDAO.getAllBatches();
            masterData = FXCollections.observableArrayList(batches);
            batchTable.setItems(masterData);
            updateTotalCount();
            
        } catch (Exception e) {
            showAlert("Error", "Failed to load batches: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTotalCount() {
        if (masterData != null) {
            lblTotalCount.setText(String.valueOf(masterData.size()));
        }
    }

    private void filterTable() {
        if (masterData == null) return;
        
        String searchText = txtSearch.getText().toLowerCase();
        String filterStatus = cmbFilter.getValue();
        
        ObservableList<Batch> filteredData = FXCollections.observableArrayList();
        
        for (Batch batch : masterData) {
            boolean matchesSearch = searchText.isEmpty() ||
                String.valueOf(batch.getProcessId()).contains(searchText) ||
                batch.getProcessName().toLowerCase().contains(searchText) ||
                (batch.getOperatorName() != null && batch.getOperatorName().toLowerCase().contains(searchText));            
            boolean matchesFilter = "All Batches".equals(filterStatus) ||
                batch.getProcessStatus().equals(filterStatus);
            
            if (matchesSearch && matchesFilter) {
                filteredData.add(batch);
            }
        }
        
        batchTable.setItems(filteredData);
        lblTotalCount.setText(String.valueOf(filteredData.size()));
    }

    @FXML
    private void onSaveProcessing() {
        if (selectedBatch == null) {
            showAlert("No Batch Selected", "Please select a batch to process.");
            return;
        }

        String newGrade = cmbGrade.getValue();
        String newStatus = cmbStatus.getValue();
        String notes = txtNotes.getText();

        if (newGrade == null || newGrade.isEmpty()) {
            showAlert("Invalid Grade", "Please select a quality grade.");
            return;
        }
        
        if (newStatus == null || newStatus.isEmpty()) {
            showAlert("Invalid Status", "Please select a processing status.");
            return;
        }

        try {
            boolean success = factoryDAO.updateBatchProcessing(
                selectedBatch.getProcessId(), newGrade, newStatus, notes);
            
            if (success) {
                showSuccessAlert("Success", 
                    "Batch processing updated:\n" +
                    "Batch: #" + selectedBatch.getProcessId() + "\n" +
                    "Status: " + newStatus + "\n" +
                    "Grade: " + newGrade);
                
                loadData();
                clearSelectionAfterDelay();
                notificationService.notifyDataChanged("BATCH_UPDATED");
                
            } else {
                showAlert("Error", "Failed to update batch processing in database.");
            }
            
        } catch (Exception e) {
            showAlert("Database Error", "Could not connect to database: " + e.getMessage());
        }
    }

    @FXML
    private void onCreateBatch() {
        Dialog<Batch> dialog = new Dialog<>();
        dialog.setTitle("Create New Batch Process");
        dialog.setHeaderText("Enter batch process details");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        ComboBox<String> cropCombo = new ComboBox<>();
        cropCombo.setItems(availableCrops);
        cropCombo.setPromptText("Select Crop");
        
        TextField processNameField = new TextField();
        processNameField.setPromptText("Process Name");
        
        TextField operatorField = new TextField();
        operatorField.setPromptText("Operator Name");
        
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Pending", "In Progress");
        statusCombo.setValue("Pending");
        
        ComboBox<String> gradeCombo = new ComboBox<>();
        gradeCombo.getItems().addAll("Not Graded", "Premium", "Grade A", "Grade B", "Grade C");
        gradeCombo.setValue("Not Graded");

        grid.add(new Label("Crop *:"), 0, 0);
        grid.add(cropCombo, 1, 0);
        grid.add(new Label("Process Name *:"), 0, 1);
        grid.add(processNameField, 1, 1);
        grid.add(new Label("Operator:"), 0, 2);
        grid.add(operatorField, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusCombo, 1, 3);
        grid.add(new Label("Grade:"), 0, 4);
        grid.add(gradeCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Enable/disable create button based on input
        javafx.scene.Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);

        // Add listeners to enable button when required fields are filled
        cropCombo.valueProperty().addListener((observable, oldValue, newValue) -> 
            validateCreateForm(cropCombo, processNameField, createButton));
        processNameField.textProperty().addListener((observable, oldValue, newValue) -> 
            validateCreateForm(cropCombo, processNameField, createButton));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String selectedCrop = cropCombo.getValue();
                    String processName = processNameField.getText().trim();
                    String operator = operatorField.getText().trim();
                    
                    if (selectedCrop == null || processName.isEmpty()) {
                        showAlert("Missing Information", "Please select a crop and enter a process name.");
                        return null;
                    }
                    
                    // Extract crop ID from selection
                    int cropId = extractCropIdFromSelection(selectedCrop);
                    
                    Batch newBatch = new Batch();
                    newBatch.setCropId(cropId);
                    newBatch.setProcessName(processName);
                    newBatch.setProcessStatus(statusCombo.getValue());
                    newBatch.setStartTime(new Timestamp(System.currentTimeMillis()));
                    newBatch.setOperatorName(operator.isEmpty() ? null : operator);
                    newBatch.setNotes("");
                    newBatch.setGrade(gradeCombo.getValue());
                    
                    boolean success = factoryDAO.createBatch(newBatch);
                    
                    if (success) {
                        showSuccessAlert("Success", "New batch process created successfully!");
                        notificationService.notifyDataChanged("BATCH_UPDATED");
                        return newBatch;
                    } else {
                        showAlert("Error", "Failed to create batch process.");
                    }
                } catch (Exception e) {
                    showAlert("Error", "Failed to create batch: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result != null) {
                loadData();
            }
        });
    }

    private int extractCropIdFromSelection(String cropSelection) {
        try {
            // Extract ID from format: "Crop Name (ID: 123)"
            String idPart = cropSelection.substring(cropSelection.indexOf("ID: ") + 4);
            idPart = idPart.replace(")", "").trim();
            return Integer.parseInt(idPart);
        } catch (Exception e) {
            showAlert("Error", "Invalid crop selection format.");
            return -1;
        }
    }

    private void validateCreateForm(ComboBox<String> cropCombo, TextField processNameField, javafx.scene.Node createButton) {
        boolean isValid = cropCombo.getValue() != null && 
                         !processNameField.getText().trim().isEmpty();
        createButton.setDisable(!isValid);
    }

    @FXML
    private void onRefresh() {
        loadData();
        loadCropData();
        clearForm();
        batchTable.getSelectionModel().clearSelection();
        txtSearch.clear();
        cmbFilter.setValue("All Batches");
    }

    private void clearSelectionAfterDelay() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(() -> {
                    batchTable.getSelectionModel().clearSelection();
                    clearForm();
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
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