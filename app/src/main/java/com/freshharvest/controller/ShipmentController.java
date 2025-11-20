package com.freshharvest.controller;

import com.freshharvest.model.Shipment;
import com.freshharvest.util.DBHandle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ShipmentController {

    @FXML private TableView<Shipment> shipmentTable;
    @FXML private TableColumn<Shipment, Integer> colShipId;
    @FXML private TableColumn<Shipment, String> colCustomer;
    @FXML private TableColumn<Shipment, String> colCarrier;
    @FXML private TableColumn<Shipment, String> colStatus;

    @FXML private Label lblSelectedShipment;
    @FXML private TextField txtTracking;

    private Shipment selectedShipment;
    private DBHandle dbHandler;

    @FXML
    public void initialize() {
        dbHandler = DBHandle.getInstance();

        colShipId.setCellValueFactory(new PropertyValueFactory<>("shipmentId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colCarrier.setCellValueFactory(new PropertyValueFactory<>("carrier"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadShipments();

        shipmentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                selectedShipment = newVal;
                lblSelectedShipment.setText("Shipment #" + newVal.getShipmentId());
            }
        });
    }

    private void loadShipments() {
        ObservableList<Shipment> list = dbHandler.getPendingShipments();
        shipmentTable.setItems(list);
    }

    @FXML
    private void onConfirmShipment() {
        if (selectedShipment == null || txtTracking.getText().isEmpty()) return;
        
        boolean success = dbHandler.confirmShipment(selectedShipment.getShipmentId(), txtTracking.getText());
        if (success) {
            System.out.println("Shipment dispatched!");
            loadShipments(); // Refresh table
            txtTracking.clear();
            lblSelectedShipment.setText("None");
        }
    }
}