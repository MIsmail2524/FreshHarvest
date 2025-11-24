package com.freshharvest.controller.Factory_Manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PackageAndLabelController implements Initializable {

    // Form Fields
    @FXML private ComboBox<String> batchComboBox;
    @FXML private ComboBox<String> productComboBox;
    @FXML private ComboBox<String> packageTypeComboBox;
    @FXML private TextField quantityField;
    @FXML private TextField weightField;
    @FXML private DatePicker packagingDatePicker;
    @FXML private TextField batchIdField;
    @FXML private TextArea labelingInstructionsArea;
    @FXML private CheckBox qualityCheckBox;
    @FXML private CheckBox labelingCheckBox;
    @FXML private CheckBox packagingCheckBox;

    // Table for batch history
    @FXML private TableView<PackagedBatch> batchesTable;
    @FXML private TableColumn<PackagedBatch, String> batchIdColumn;
    @FXML private TableColumn<PackagedBatch, String> productColumn;
    @FXML private TableColumn<PackagedBatch, String> packageTypeColumn;
    @FXML private TableColumn<PackagedBatch, Integer> quantityColumn;
    @FXML private TableColumn<PackagedBatch, String> statusColumn;
    @FXML private TableColumn<PackagedBatch, String> dateColumn;

    // Buttons
    @FXML private Button packageButton;
    @FXML private Button clearButton;
    @FXML private Button printLabelButton;
    @FXML private Button refreshButton;

    private ObservableList<PackagedBatch> packagedBatches = FXCollections.observableArrayList();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // Database connection
    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDatabase();
        setupComboBoxes();
        setupTableColumns();
        setupFormValidation();
        loadPackagedBatches();
        packagingDatePicker.setValue(LocalDate.now());
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/freshharvest", 
                "ismail", 
                "123"
            );
        } catch (SQLException e) {
            showAlert("Database Error", "Cannot connect to database: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void setupComboBoxes() {
        try {
            // Load batches from database
            ObservableList<String> batches = FXCollections.observableArrayList();
            String batchQuery = "SELECT batch_id, product_name FROM available_batches WHERE status = 'Processed'";
            PreparedStatement batchStmt = connection.prepareStatement(batchQuery);
            ResultSet batchRs = batchStmt.executeQuery();
            
            while (batchRs.next()) {
                batches.add(batchRs.getString("batch_id") + " - " + batchRs.getString("product_name"));
            }
            batchComboBox.setItems(batches);

            // Load package types from database
            ObservableList<String> packageTypes = FXCollections.observableArrayList();
            String packageQuery = "SELECT package_type FROM package_types WHERE is_active = TRUE";
            PreparedStatement packageStmt = connection.prepareStatement(packageQuery);
            ResultSet packageRs = packageStmt.executeQuery();
            
            while (packageRs.next()) {
                packageTypes.add(packageRs.getString("package_type"));
            }
            packageTypeComboBox.setItems(packageTypes);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupTableColumns() {
        batchIdColumn.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        packageTypeColumn.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("packagingDate"));

        batchesTable.setItems(packagedBatches);
    }

    private void setupFormValidation() {
        // Numeric validation for quantity and weight
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantityField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            calculateTotalWeight();
        });

        packageTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            calculateTotalWeight();
        });

        // Enable package button only when all checks are completed
        qualityCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> validateForm());
        labelingCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> validateForm());
        packagingCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    private void calculateTotalWeight() {
        try {
            if (packageTypeComboBox.getValue() != null && !quantityField.getText().isEmpty()) {
                String packageType = packageTypeComboBox.getValue();
                int quantity = Integer.parseInt(quantityField.getText());
                
                String query = "SELECT weight_capacity FROM package_types WHERE package_type = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, packageType);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    double unitWeight = rs.getDouble("weight_capacity");
                    double totalWeight = unitWeight * quantity;
                    weightField.setText(String.format("%.2f", totalWeight));
                }
            }
        } catch (SQLException e) {
            // Silently handle error - user can enter weight manually
        } catch (NumberFormatException e) {
            // Invalid quantity entered
        }
    }

    private void validateForm() {
        boolean isFormValid = 
            batchComboBox.getValue() != null &&
            productComboBox.getValue() != null &&
            packageTypeComboBox.getValue() != null &&
            !quantityField.getText().isEmpty() &&
            !weightField.getText().isEmpty() &&
            packagingDatePicker.getValue() != null &&
            qualityCheckBox.isSelected() &&
            labelingCheckBox.isSelected() &&
            packagingCheckBox.isSelected();

        packageButton.setDisable(!isFormValid);
        printLabelButton.setDisable(batchesTable.getSelectionModel().getSelectedItem() == null);
    }

    @FXML
    private void handlePackageBatch() {
        try {
            // Validate required fields
            if (!isFormValid()) {
                showAlert("Validation Error", "Please fill all required fields and complete all checks.", Alert.AlertType.ERROR);
                return;
            }

            // Extract original batch ID
            String selectedBatch = batchComboBox.getValue();
            String originalBatchId = selectedBatch.split(" - ")[0];

            // Create new packaged batch in database
            String insertSQL = "INSERT INTO packaged_batches (" +
                "batch_id, original_batch_id, product_name, package_type, quantity, " +
                "total_weight, packaging_date, labeling_instructions, " +
                "quality_checked, labeling_verified, packaging_verified, status" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = connection.prepareStatement(insertSQL);
            String newBatchId = generateBatchId();
            
            stmt.setString(1, newBatchId);
            stmt.setString(2, originalBatchId);
            stmt.setString(3, productComboBox.getValue());
            stmt.setString(4, packageTypeComboBox.getValue());
            stmt.setInt(5, Integer.parseInt(quantityField.getText()));
            stmt.setDouble(6, Double.parseDouble(weightField.getText()));
            stmt.setTimestamp(7, Timestamp.valueOf(packagingDatePicker.getValue().atStartOfDay()));
            stmt.setString(8, labelingInstructionsArea.getText());
            stmt.setBoolean(9, qualityCheckBox.isSelected());
            stmt.setBoolean(10, labelingCheckBox.isSelected());
            stmt.setBoolean(11, packagingCheckBox.isSelected());
            stmt.setString(12, "Packaged");
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Create new packaged batch object for table
                PackagedBatch newBatch = new PackagedBatch(
                    newBatchId,
                    productComboBox.getValue(),
                    packageTypeComboBox.getValue(),
                    Integer.parseInt(quantityField.getText()),
                    Double.parseDouble(weightField.getText()),
                    packagingDatePicker.getValue().atStartOfDay(),
                    "Packaged",
                    labelingInstructionsArea.getText()
                );

                // Add to table
                packagedBatches.add(0, newBatch);

                // Show success message
                showAlert("Success", "Batch packaged and labeled successfully!\nBatch ID: " + newBatchId, Alert.AlertType.INFORMATION);

                // Clear form
                clearForm();
                
                // Refresh table
                loadPackagedBatches();
            } else {
                showAlert("Database Error", "Failed to save batch to database.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to package batch: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "Failed to package batch: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClearForm() {
        clearForm();
    }

    @FXML
    private void handlePrintLabel() {
        PackagedBatch selectedBatch = batchesTable.getSelectionModel().getSelectedItem();
        if (selectedBatch == null) {
            showAlert("Selection Required", "Please select a batch from the table to print label.", Alert.AlertType.WARNING);
            return;
        }

        // Generate label content
        String labelContent = generateLabelContent(selectedBatch);
        
        // Show print preview
        TextArea printPreview = new TextArea(labelContent);
        printPreview.setEditable(false);
        printPreview.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        
        Alert printAlert = new Alert(Alert.AlertType.INFORMATION);
        printAlert.setTitle("Label Preview - " + selectedBatch.getBatchId());
        printAlert.setHeaderText("Label Preview (Ready for Printing)");
        printAlert.getDialogPane().setContent(printPreview);
        printAlert.getDialogPane().setPrefSize(400, 300);
        printAlert.showAndWait();
    }

    @FXML
    private void handleBatchSelection() {
        if (batchComboBox.getValue() != null) {
            String selectedValue = batchComboBox.getValue();
            String[] parts = selectedValue.split(" - ");
            if (parts.length >= 2) {
                String productName = parts[1];
                productComboBox.setValue(productName);
            }
            // Auto-generate batch ID
            batchIdField.setText("PKG-" + System.currentTimeMillis());
        }
    }

    @FXML
    private void handleRefresh() {
        loadPackagedBatches();
        setupComboBoxes();
        showAlert("Refreshed", "Data refreshed successfully.", Alert.AlertType.INFORMATION);
    }

    private void loadPackagedBatches() {
        try {
            packagedBatches.clear();
            
            String query = "SELECT batch_id, product_name, package_type, quantity, " +
                          "total_weight, packaging_date, status, labeling_instructions " +
                          "FROM packaged_batches ORDER BY packaging_date DESC LIMIT 50";
            
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                PackagedBatch batch = new PackagedBatch(
                    rs.getString("batch_id"),
                    rs.getString("product_name"),
                    rs.getString("package_type"),
                    rs.getInt("quantity"),
                    rs.getDouble("total_weight"),
                    rs.getTimestamp("packaging_date").toLocalDateTime(),
                    rs.getString("status"),
                    rs.getString("labeling_instructions")
                );
                packagedBatches.add(batch);
            }
            
            // Update total count
            updateTotalCount();
            
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load packaged batches: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateTotalCount() {
        try {
            String countQuery = "SELECT COUNT(*) as total FROM packaged_batches";
            PreparedStatement stmt = connection.prepareStatement(countQuery);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total");
                // You can display this in your UI if you add a label for it
                System.out.println("Total packaged batches: " + total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateLabelContent(PackagedBatch batch) {
        return String.format(
            "================================\n" +
            "       FRESHHARVEST FOODS       \n" +
            "================================\n" +
            "Product: %s\n" +
            "Batch ID: %s\n" +
            "Package: %s\n" +
            "Quantity: %d units\n" +
            "Weight: %.2f kg\n" +
            "Packaged: %s\n" +
            "Status: %s\n" +
            "--------------------------------\n" +
            "Instructions: %s\n" +
            "================================\n",
            batch.getProductName(),
            batch.getBatchId(),
            batch.getPackageType(),
            batch.getQuantity(),
            batch.getTotalWeight(),
            batch.getPackagingDate(),
            batch.getStatus(),
            batch.getLabelingInstructions() != null ? batch.getLabelingInstructions() : "None"
        );
    }

    private void clearForm() {
        batchComboBox.setValue(null);
        productComboBox.setValue(null);
        packageTypeComboBox.setValue(null);
        quantityField.clear();
        weightField.clear();
        packagingDatePicker.setValue(LocalDate.now());
        batchIdField.clear();
        labelingInstructionsArea.clear();
        qualityCheckBox.setSelected(false);
        labelingCheckBox.setSelected(false);
        packagingCheckBox.setSelected(false);
        packageButton.setDisable(true);
        printLabelButton.setDisable(true);
    }

    private boolean isFormValid() {
        return batchComboBox.getValue() != null &&
               productComboBox.getValue() != null &&
               packageTypeComboBox.getValue() != null &&
               !quantityField.getText().isEmpty() &&
               !weightField.getText().isEmpty() &&
               packagingDatePicker.getValue() != null &&
               qualityCheckBox.isSelected() &&
               labelingCheckBox.isSelected() &&
               packagingCheckBox.isSelected();
    }

    private String generateBatchId() {
        return "PKG-" + System.currentTimeMillis();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for packaged batch data
    public static class PackagedBatch {
        private final String batchId;
        private final String productName;
        private final String packageType;
        private final int quantity;
        private final double totalWeight;
        private final LocalDateTime packagingDate;
        private final String status;
        private final String labelingInstructions;

        public PackagedBatch(String batchId, String productName, String packageType, 
                           int quantity, double totalWeight, LocalDateTime packagingDate, 
                           String status, String labelingInstructions) {
            this.batchId = batchId;
            this.productName = productName;
            this.packageType = packageType;
            this.quantity = quantity;
            this.totalWeight = totalWeight;
            this.packagingDate = packagingDate;
            this.status = status;
            this.labelingInstructions = labelingInstructions;
        }

        // Getters
        public String getBatchId() { return batchId; }
        public String getProductName() { return productName; }
        public String getPackageType() { return packageType; }
        public int getQuantity() { return quantity; }
        public double getTotalWeight() { return totalWeight; }
        public String getPackagingDate() { 
            return packagingDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")); 
        }
        public String getStatus() { return status; }
        public String getLabelingInstructions() { return labelingInstructions; }
    }
}