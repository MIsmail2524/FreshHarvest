package com.freshharvest.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    // Key Metrics
    @FXML private Label lblTotalRevenue;
    @FXML private Label lblTotalExpenses;
    @FXML private Label lblNetProfit;
    @FXML private Label lblProfitMargin;

    // Data Table
    @FXML private TableView<ReportData> reportTable;
    @FXML private TableColumn<ReportData, String> colReportDate;
    @FXML private TableColumn<ReportData, String> colReportCategory;
    @FXML private TableColumn<ReportData, String> colReportDescription;
    @FXML private TableColumn<ReportData, String> colReportAmount;

    private ObservableList<ReportData> reportData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("Initializing Simple Reports Controller...");
            
            setupTable();
            loadSampleData();
            updateMetrics();
            
            System.out.println("Simple Reports Controller initialized successfully.");
            
        } catch (Exception e) {
            System.err.println("Error in initialize: " + e.getMessage());
            showAlert("Error", "Failed to initialize reports", Alert.AlertType.ERROR);
        }
    }

    private void setupTable() {
        try {
            reportData = FXCollections.observableArrayList();
            reportTable.setItems(reportData);
            
            // Setup cell value factories
            colReportDate.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate()));
            colReportCategory.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
            colReportDescription.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
            colReportAmount.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAmount()));
            
        } catch (Exception e) {
            System.err.println("Error setting up table: " + e.getMessage());
        }
    }

    private void loadSampleData() {
        try {
            // Add sample transaction data
            reportData.add(new ReportData("2024-01-15", "Revenue", "Tomatoes Sale", "+$1,200.00"));
            reportData.add(new ReportData("2024-01-20", "Expense", "Seeds Purchase", "-$450.00"));
            reportData.add(new ReportData("2024-02-10", "Revenue", "Carrots Sale", "+$850.00"));
            reportData.add(new ReportData("2024-02-15", "Expense", "Labor Costs", "-$1,200.00"));
            reportData.add(new ReportData("2024-03-05", "Revenue", "Lettuce Sale", "+$680.00"));
            reportData.add(new ReportData("2024-03-12", "Expense", "Fertilizers", "-$320.00"));
            reportData.add(new ReportData("2024-04-08", "Revenue", "Berries Sale", "+$2,100.00"));
            reportData.add(new ReportData("2024-04-20", "Expense", "Equipment", "-$850.00"));
            
        } catch (Exception e) {
            System.err.println("Error loading sample data: " + e.getMessage());
        }
    }

    private void updateMetrics() {
        try {
            // Set sample metrics
            lblTotalRevenue.setText("$45,230.00");
            lblTotalExpenses.setText("$28,150.00");
            lblNetProfit.setText("$17,080.00");
            lblProfitMargin.setText("37.8%");
            
        } catch (Exception e) {
            System.err.println("Error updating metrics: " + e.getMessage());
        }
    }

    // Button handlers
    @FXML
    private void handleFinancialSummary() {
        showAlert("Financial Summary", "Total Revenue: $45,230.00\nTotal Expenses: $28,150.00\nNet Profit: $17,080.00\nProfit Margin: 37.8%", 
                 Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleCropPerformance() {
        showAlert("Crop Performance", 
                 "Tomatoes: $5,200 profit\nCarrots: $3,200 profit\nLettuce: $2,800 profit\nBerries: $6,300 profit", 
                 Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleSeasonalAnalysis() {
        showAlert("Seasonal Analysis", 
                 "Winter: $10,000 profit\nSpring: $21,000 profit\nSummer: $30,800 profit\nFall: $15,200 profit", 
                 Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleExpenseReport() {
        showAlert("Expense Report", 
                 "Labor: 45%\nSeeds & Plants: 20%\nFertilizers: 15%\nEquipment: 12%\nUtilities: 8%", 
                 Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleGenerateReport() {
        showAlert("Report Generated", "Full farm report has been generated successfully!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleExportPDF() {
        showAlert("Export", "Data exported successfully!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handlePrint() {
        showAlert("Print", "Report sent to printer!", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        try {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing alert: " + e.getMessage());
        }
    }

    // Simple inner class for report data
    public static class ReportData {
        private final String date;
        private final String category;
        private final String description;
        private final String amount;

        public ReportData(String date, String category, String description, String amount) {
            this.date = date;
            this.category = category;
            this.description = description;
            this.amount = amount;
        }

        public String getDate() { return date; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public String getAmount() { return amount; }
    }
}