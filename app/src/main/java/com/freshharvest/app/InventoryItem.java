package com.freshharvest.app;

import java.time.LocalDateTime;

public class InventoryItem {
    private int productId;
    private String productName;
    private String category;
    private double currentStock;
    private double minStock;
    private String unit;
    private double price;
    private String stockStatus;
    private LocalDateTime lastUpdated;

    // Constructors, getters, and setters
    public InventoryItem() {}

    public InventoryItem(int productId, String productName, String category, 
                        double currentStock, double minStock, String unit, 
                        double price, LocalDateTime lastUpdated) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.currentStock = currentStock;
        this.minStock = minStock;
        this.unit = unit;
        this.price = price;
        this.lastUpdated = lastUpdated;
        updateStockStatus();
    }

    private void updateStockStatus() {
        if (currentStock <= 0) {
            this.stockStatus = "Out of Stock";
        } else if (currentStock <= minStock) {
            this.stockStatus = "Low Stock";
        } else {
            this.stockStatus = "In Stock";
        }
    }

    // Getters and setters with status update
    public void setCurrentStock(double currentStock) {
        this.currentStock = currentStock;
        updateStockStatus();
    }

    public void setMinStock(double minStock) {
        this.minStock = minStock;
        updateStockStatus();
    }

    // Other getters and setters...
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public double getCurrentStock() { return currentStock; }
    
    public double getMinStock() { return minStock; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}