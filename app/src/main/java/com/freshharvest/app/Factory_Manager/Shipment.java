package com.freshharvest.app.Factory_Manager;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.sql.Timestamp;

public class Shipment {
    private final SimpleIntegerProperty shipmentId;
    private final SimpleStringProperty customerName;
    private final SimpleStringProperty carrier;
    private final SimpleStringProperty status;
    private final SimpleStringProperty trackingNumber;
    private final SimpleObjectProperty<Timestamp> shipmentDate;
    private final SimpleIntegerProperty orderId; // Changed to property

    // Updated constructor with orderId
    public Shipment(int shipmentId, String customerName, String carrier, String status, 
                   String trackingNumber, Timestamp shipmentDate, Integer orderId) {
        this.shipmentId = new SimpleIntegerProperty(shipmentId);
        this.customerName = new SimpleStringProperty(customerName);
        this.carrier = new SimpleStringProperty(carrier);
        this.status = new SimpleStringProperty(status);
        this.trackingNumber = new SimpleStringProperty(trackingNumber);
        this.shipmentDate = new SimpleObjectProperty<>(shipmentDate);
        this.orderId = new SimpleIntegerProperty(orderId != null ? orderId : 0);
    }

    // Original constructor for backward compatibility
    public Shipment(int shipmentId, String customerName, String carrier, String status, 
                   String trackingNumber, Timestamp shipmentDate) {
        this(shipmentId, customerName, carrier, status, trackingNumber, shipmentDate, null);
    }

    // Getters
    public int getShipmentId() { return shipmentId.get(); }
    public String getCustomerName() { return customerName.get(); }
    public String getCarrier() { return carrier.get(); }
    public String getStatus() { return status.get(); }
    public String getTrackingNumber() { return trackingNumber.get(); }
    public Timestamp getShipmentDate() { return shipmentDate.get(); }
    public Integer getOrderId() { 
        int id = orderId.get();
        return id > 0 ? id : null; // Return null if orderId is 0 (not set)
    }

    // Setters
    public void setShipmentId(int shipmentId) { this.shipmentId.set(shipmentId); }
    public void setCustomerName(String customerName) { this.customerName.set(customerName); }
    public void setCarrier(String carrier) { this.carrier.set(carrier); }
    public void setStatus(String status) { this.status.set(status); }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber.set(trackingNumber); }
    public void setShipmentDate(Timestamp shipmentDate) { this.shipmentDate.set(shipmentDate); }
    public void setOrderId(Integer orderId) { 
        this.orderId.set(orderId != null ? orderId : 0); 
    }

    // Property getters
    public SimpleIntegerProperty shipmentIdProperty() { return shipmentId; }
    public SimpleStringProperty customerNameProperty() { return customerName; }
    public SimpleStringProperty carrierProperty() { return carrier; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty trackingNumberProperty() { return trackingNumber; }
    public SimpleObjectProperty<Timestamp> shipmentDateProperty() { return shipmentDate; }
    public SimpleIntegerProperty orderIdProperty() { return orderId; }
}