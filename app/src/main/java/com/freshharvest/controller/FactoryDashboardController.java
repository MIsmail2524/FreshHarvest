package com.freshharvest.controller;

import com.freshharvest.model.Batch;
import com.freshharvest.util.DBHandle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class FactoryDashboardController {

    // FXML fields for stats
    @FXML
    private Label lblPendingBatches;
    @FXML
    private Label lblInProgressBatches;
    @FXML
    private Label lblPackagedBatches;
    
    // NEW LABEL
    @FXML
    private Label lblPendingShipments;

    // FXML fields for table
    @FXML
    private TableView<Batch> pendingActionsTable;
    @FXML
    private TableColumn<Batch, Integer> colBatchId;
    @FXML
    private TableColumn<Batch, String> colCropType;
    @FXML
    private TableColumn<Batch, String> colStatus;
    @FXML
    private TableColumn<Batch, String> colReceived;
    
    private DBHandle dbHandler;

    @FXML
    public void initialize() {
        dbHandler = DBHandle.getInstance();
        
        // Setup table columns
        colBatchId.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colCropType.setCellValueFactory(new PropertyValueFactory<>("cropType"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colReceived.setCellValueFactory(new PropertyValueFactory<>("harvestDate"));

        loadDashboardStats();
        loadPendingActions();
    }

    private void loadDashboardStats() {
        // 1. Get Batch Counts
        Map<String, Integer> counts = dbHandler.getBatchStatusCounts();

        int pending = counts.getOrDefault("Pending", 0);
        int inProgress = counts.getOrDefault("Processing", 0);
        int packaged = counts.getOrDefault("Packaged", 0);

        lblPendingBatches.setText(String.valueOf(pending));
        lblInProgressBatches.setText(String.valueOf(inProgress));
        lblPackagedBatches.setText(String.valueOf(packaged));

        // 2. Get Shipment Counts (NEW)
        int pendingShipments = dbHandler.getPendingShipmentCount();
        lblPendingShipments.setText(String.valueOf(pendingShipments));
    }

    private void loadPendingActions() {
        ObservableList<Batch> pendingBatches = dbHandler.getAllBatches()
                .stream()
                .filter(b -> b.getStatus().equals("Pending"))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        
        pendingActionsTable.setItems(pendingBatches);
    }
}