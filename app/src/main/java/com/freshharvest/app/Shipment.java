package com.freshharvest.app;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Shipment {
    // Maps to the 'orders' table
    private final SimpleIntegerProperty shipmentId; // This is actually Order ID
    private final SimpleStringProperty customerName;
    private final SimpleStringProperty status;
    private final SimpleStringProperty date;

    public Shipment(int shipmentId, String customerName, String status, String date) {
        this.shipmentId = new SimpleIntegerProperty(shipmentId);
        this.customerName = new SimpleStringProperty(customerName);
        this.status = new SimpleStringProperty(status);
        this.date = new SimpleStringProperty(date);
    }

    public int getShipmentId() { return shipmentId.get(); }
    public String getCustomerName() { return customerName.get(); }
    public String getStatus() { return status.get(); }
    public String getDate() { return date.get(); }
    
    public SimpleIntegerProperty shipmentIdProperty() { return shipmentId; }
    public SimpleStringProperty customerNameProperty() { return customerName; }
    public SimpleStringProperty statusProperty() { return status; }
}