package com.freshharvest.app;

import java.time.LocalDate;


public class Crop {
    private int id;
    private String name;
    private String type;
    private LocalDate plantingDate;
    private LocalDate harvestDate;
    private String status;
    private double quantity;
    private String field;
    private int farmerId;
       private double expectedYield;
    private String processingType;
    private String specialInstructions;
    private LocalDate deliveryDate;
    private LocalDate sentToFactoryDate;
    private String factoryStatus; // PENDING, PROCESSING, COMPLETED


    // Default constructor
    public Crop() {}

    // Full constructor
    public Crop(int id, String name, String type, LocalDate plantingDate, 
                LocalDate harvestDate, String status, double quantity, String field, int farmerId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.plantingDate = plantingDate;
        this.harvestDate = harvestDate;
        this.status = status;
        this.quantity = quantity;
        this.field = field;
        this.farmerId = farmerId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getPlantingDate() { return plantingDate; }
    public void setPlantingDate(LocalDate plantingDate) { this.plantingDate = plantingDate; }

    public LocalDate getHarvestDate() { return harvestDate; }
    public void setHarvestDate(LocalDate harvestDate) { this.harvestDate = harvestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public int getFarmerId() { return farmerId; }
    public void setFarmerId(int farmerId) { this.farmerId = farmerId; }

    public String getProcessingType() { return processingType; }
    public void setProcessingType(String processingType) { this.processingType = processingType; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public LocalDate getSentToFactoryDate() { return sentToFactoryDate; }
    public void setSentToFactoryDate(LocalDate sentToFactoryDate) { this.sentToFactoryDate = sentToFactoryDate; }

    public String getFactoryStatus() { return factoryStatus; }
    public void setFactoryStatus(String factoryStatus) { this.factoryStatus = factoryStatus; }

    @Override
    public String toString() {
        return "Crop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", plantingDate=" + plantingDate +
                ", harvestDate=" + harvestDate +
                ", status='" + status + '\'' +
                ", quantity=" + quantity +
                ", field='" + field + '\'' +
                ", farmerId=" + farmerId +
                '}';
    }
}