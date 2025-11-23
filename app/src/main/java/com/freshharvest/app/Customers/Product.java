package com.freshharvest.app.Customers;

import javafx.beans.property.*;

public class Product {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final StringProperty category;
    private final DoubleProperty price;
    private final IntegerProperty stockQuantity;
    private final StringProperty unit;

    public Product() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.category = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
        this.stockQuantity = new SimpleIntegerProperty();
        this.unit = new SimpleStringProperty();
    }

    public Product(int id, String name, String description, String category, 
                   double price, int stockQuantity, String unit) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.category = new SimpleStringProperty(category);
        this.price = new SimpleDoubleProperty(price);
        this.stockQuantity = new SimpleIntegerProperty(stockQuantity);
        this.unit = new SimpleStringProperty(unit);
    }

    // Getters and Setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    
    public String getCategory() { return category.get(); }
    public void setCategory(String category) { this.category.set(category); }
    
    public double getPrice() { return price.get(); }
    public void setPrice(double price) { this.price.set(price); }
    
    public int getStockQuantity() { return stockQuantity.get(); }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity.set(stockQuantity); }
    
    public String getUnit() { return unit.get(); }
    public void setUnit(String unit) { this.unit.set(unit); }

    // Property getters for TableView
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty categoryProperty() { return category; }
    public DoubleProperty priceProperty() { return price; }
    public IntegerProperty stockQuantityProperty() { return stockQuantity; }
    public StringProperty unitProperty() { return unit; }
}