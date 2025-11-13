package com.freshharvest.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
// import com.freshharvest.model.Batch;
// import com.freshharvest.util.DBHandler;

public class FactoryDashboardController {

    // FXML fields for stats
    @FXML
    private Label lblPendingBatches;
    @FXML
    private Label lblInProgressBatches;
    @FXML
    private Label lblPackagedBatches;

    // FXML fields for table
    @FXML
    private TableView<Object> pendingActionsTable; // Replace 'Object' with Batch model
    // ... (TableColumn fields) ...
    
    // private DBHandler dbHandler;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // dbHandler = DBHandler.getInstance();
        loadDashboardStats();
        loadPendingActions();
    }

    /**
     * Fetches data from the database to populate the top-level stats.
     */
    private void loadDashboardStats() {
        // --- Database Logic ---
        // int pending = dbHandler.getBatchCountByStatus("Pending");
        // int inProgress = dbHandler.getBatchCountByStatus("Processing");
        // int packaged = dbHandler.getBatchCountByStatus("Packaged");
        
        // Placeholder data:
        int pending = 12;
        int inProgress = 5;
        int packaged = 42;

        lblPendingBatches.setText(String.valueOf(pending));
        lblInProgressBatches.setText(String.valueOf(inProgress));
        lblPackagedBatches.setText(String.valueOf(packaged));
    }

    /**
     * Fetches data for the table of items needing immediate attention.
     */
    private void loadPendingActions() {
        // --- Database Logic ---
        // ObservableList<Batch> pendingBatches = dbHandler.getPendingBatches();
        // pendingActionsTable.setItems(pendingBatches);
        
        // (You would also set cell value factories here)
    }
}