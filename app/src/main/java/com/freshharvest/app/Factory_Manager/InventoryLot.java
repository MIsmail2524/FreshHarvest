package com.freshharvest.app.Factory_Manager;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class InventoryLot {
    private final SimpleIntegerProperty lotId;
    private final SimpleStringProperty cropType;
    private final SimpleStringProperty grade;
    private final SimpleStringProperty packageType;
    private final SimpleIntegerProperty quantity;
    private final SimpleStringProperty location;
    private final SimpleStringProperty storageConditions;

    public InventoryLot(int lotId, String cropType, String grade, String packageType, 
                       int quantity, String location, String storageConditions) {
        this.lotId = new SimpleIntegerProperty(lotId);
        this.cropType = new SimpleStringProperty(cropType);
        this.grade = new SimpleStringProperty(grade);
        this.packageType = new SimpleStringProperty(packageType);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.location = new SimpleStringProperty(location);
        this.storageConditions = new SimpleStringProperty(storageConditions);
    }

    // Getters
    public int getLotId() { return lotId.get(); }
    public String getCropType() { return cropType.get(); }
    public String getGrade() { return grade.get(); }
    public String getPackageType() { return packageType.get(); }
    public int getQuantity() { return quantity.get(); }
    public String getLocation() { return location.get(); }
    public String getStorageConditions() { return storageConditions.get(); }

    // Setters
    public void setLotId(int lotId) { this.lotId.set(lotId); }
    public void setCropType(String cropType) { this.cropType.set(cropType); }
    public void setGrade(String grade) { this.grade.set(grade); }
    public void setPackageType(String packageType) { this.packageType.set(packageType); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public void setLocation(String location) { this.location.set(location); }
    public void setStorageConditions(String storageConditions) { this.storageConditions.set(storageConditions); }

    // Property getters
    public SimpleIntegerProperty lotIdProperty() { return lotId; }
    public SimpleStringProperty cropTypeProperty() { return cropType; }
    public SimpleStringProperty gradeProperty() { return grade; }
    public SimpleStringProperty packageTypeProperty() { return packageType; }
    public SimpleIntegerProperty quantityProperty() { return quantity; }
    public SimpleStringProperty locationProperty() { return location; }
    public SimpleStringProperty storageConditionsProperty() { return storageConditions; }
}