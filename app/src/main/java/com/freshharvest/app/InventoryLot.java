package com.freshharvest.app;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class InventoryLot {
    private final SimpleIntegerProperty lotId;
    private final SimpleIntegerProperty originalBatchId;
    private final SimpleStringProperty cropType;
    private final SimpleStringProperty grade;
    private final SimpleStringProperty packageType;
    private final SimpleIntegerProperty quantity; // Total boxes

    public InventoryLot(int lotId, int originalBatchId, String cropType, String grade, String packageType, int quantity) {
        this.lotId = new SimpleIntegerProperty(lotId);
        this.originalBatchId = new SimpleIntegerProperty(originalBatchId);
        this.cropType = new SimpleStringProperty(cropType);
        this.grade = new SimpleStringProperty(grade);
        this.packageType = new SimpleStringProperty(packageType);
        this.quantity = new SimpleIntegerProperty(quantity);
    }

    // --- Getters ---
    public int getLotId() { return lotId.get(); }
    public int getOriginalBatchId() { return originalBatchId.get(); }
    public String getCropType() { return cropType.get(); }
    public String getGrade() { return grade.get(); }
    public String getPackageType() { return packageType.get(); }
    public int getQuantity() { return quantity.get(); }

    // --- Property Getters ---
    public SimpleIntegerProperty lotIdProperty() { return lotId; }
    public SimpleIntegerProperty originalBatchIdProperty() { return originalBatchId; }
    public SimpleStringProperty cropTypeProperty() { return cropType; }
    public SimpleStringProperty gradeProperty() { return grade; }
    public SimpleStringProperty packageTypeProperty() { return packageType; }
    public SimpleIntegerProperty quantityProperty() { return quantity; }
}