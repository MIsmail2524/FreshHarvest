package com.freshharvest.controller;

import com.freshharvest.util.DBHandle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ReportController {

    @FXML
    private ComboBox<String> cmbReportType;
    @FXML
    private Button btnGenerateReport;
    @FXML
    private TextArea reportTextArea;

    private DBHandle dbHandler;

    @FXML
    public void initialize() {
        dbHandler = DBHandle.getInstance();

        // Use Case 12: Displays available report types
        cmbReportType.getItems().addAll("Batch Status", "Inventory Stock");
        cmbReportType.setValue("Batch Status"); // Set default
    }

    @FXML
    private void onGenerateReport() {
        String reportType = cmbReportType.getValue();
        if (reportType == null) {
            reportTextArea.setText("Please select a report type.");
            return;
        }

        System.out.println("--- Generating Report (Use Case 12) ---");
        reportTextArea.clear();

        try (ResultSet rs = dbHandler.getReportData(reportType)) {
            if (rs == null) {
                reportTextArea.setText("Error generating report.");
                return;
            }

            // --- Build a simple text-based report ---
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            // Build Header
            StringBuilder header = new StringBuilder();
            for (int i = 1; i <= colCount; i++) {
                header.append(String.format("%-25s", meta.getColumnName(i)));
            }
            reportTextArea.appendText(header.toString() + "\n");
            reportTextArea.appendText("-".repeat(header.length()) + "\n");

            // Build Rows
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= colCount; i++) {
                    row.append(String.format("%-25s", rs.getString(i)));
                }
                reportTextArea.appendText(row.toString() + "\n");
            }
            // --- End of report builder ---

        } catch (SQLException e) {
            e.printStackTrace();
            reportTextArea.setText("An SQL error occurred.");
        }
    }
}