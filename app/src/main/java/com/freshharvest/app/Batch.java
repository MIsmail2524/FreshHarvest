package com.freshharvest.app;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Batch {
    // We map "Batch" to the "crops" table from the farmer
    private final SimpleIntegerProperty batchId; // This maps to crop_id
    private final SimpleStringProperty cropType; // This maps to crop_name
    private final SimpleStringProperty harvestDate; // This maps to harvest_date
    private final SimpleStringProperty status;
    private final SimpleStringProperty grade;

    public Batch(int batchId, String cropType, String harvestDate, String status, String grade) {
        this.batchId = new SimpleIntegerProperty(batchId);
        this.cropType = new SimpleStringProperty(cropType);
        this.harvestDate = new SimpleStringProperty(harvestDate);
        this.status = new SimpleStringProperty(status);
        this.grade = new SimpleStringProperty(grade == null ? "-" : grade);
    }

    public int getBatchId() { return batchId.get(); }
    public String getCropType() { return cropType.get(); }
    public String getHarvestDate() { return harvestDate.get(); }
    public String getStatus() { return status.get(); }
    public String getGrade() { return grade.get(); }
    
    // Property getters for TableView
    public SimpleIntegerProperty batchIdProperty() { return batchId; }
    public SimpleStringProperty cropTypeProperty() { return cropType; }
    public SimpleStringProperty harvestDateProperty() { return harvestDate; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty gradeProperty() { return grade; }
}