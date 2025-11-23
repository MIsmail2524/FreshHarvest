package com.freshharvest.app.Customers;

import javafx.beans.property.*;

public class CartItem {
    private final IntegerProperty productId;
    private final StringProperty productName;
    private final DoubleProperty unitPrice;
    private final IntegerProperty quantity;
    private final DoubleProperty subtotal;

    public CartItem(int productId, String productName, double unitPrice, int quantity) {
        this.productId = new SimpleIntegerProperty(productId);
        this.productName = new SimpleStringProperty(productName);
        this.unitPrice = new SimpleDoubleProperty(unitPrice);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.subtotal = new SimpleDoubleProperty(unitPrice * quantity);
        
        // Update subtotal when quantity changes
        this.quantity.addListener((obs, oldVal, newVal) -> 
            this.subtotal.set(unitPrice * newVal.intValue())
        );
    }

    // Getters
    public int getProductId() { return productId.get(); }
    public String getProductName() { return productName.get(); }
    public double getUnitPrice() { return unitPrice.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getSubtotal() { return subtotal.get(); }

    // Setters
    public void setQuantity(int quantity) { this.quantity.set(quantity); }

    // Property getters for TableView
    public IntegerProperty productIdProperty() { return productId; }
    public StringProperty productNameProperty() { return productName; }
    public DoubleProperty unitPriceProperty() { return unitPrice; }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty subtotalProperty() { return subtotal; }
}