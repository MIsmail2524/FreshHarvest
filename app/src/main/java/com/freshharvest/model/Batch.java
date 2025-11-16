package com.freshharvest.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Batch {
    private final SimpleIntegerProperty batchId;
    private final SimpleStringProperty cropType;
    private final SimpleStringProperty harvestDate;
    private final SimpleStringProperty status;
    private final SimpleStringProperty grade;

    public Batch(int batchId, String cropType, String harvestDate, String status, String grade) {
        this.batchId = new SimpleIntegerProperty(batchId);
        this.cropType = new SimpleStringProperty(cropType);
        this.harvestDate = new SimpleStringProperty(harvestDate);
        this.status = new SimpleStringProperty(status);
        this.grade = new SimpleStringProperty(grade);
    }

    // --- Getters ---
    public int getBatchId() { return batchId.get(); }
    public String getCropType() { return cropType.get(); }
    public String getHarvestDate() { return harvestDate.get(); }
    public String getStatus() { return status.get(); }
    public String getGrade() { return grade.get(); }

    // --- Property Getters ---
    public SimpleIntegerProperty batchIdProperty() { return batchId; }
    public SimpleStringProperty cropTypeProperty() { return cropType; }
    public SimpleStringProperty harvestDateProperty() { return harvestDate; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty gradeProperty() { return grade; }
}