package com.freshharvest.app;

public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int productId;
    private String productName;
    private double quantity;
    private double unitPrice;
    private double totalPrice;

    // Constructors
    public OrderItem() {}

    public OrderItem(int productId, String productName, double quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }

    // Getters and Setters
    public int getOrderItemId() { 
        return orderItemId; 
    }
    
    public void setOrderItemId(int orderItemId) { 
        this.orderItemId = orderItemId; 
    }

    public int getOrderId() { 
        return orderId; 
    }
    
    public void setOrderId(int orderId) { 
        this.orderId = orderId; 
    }

    public int getProductId() { 
        return productId; 
    }
    
    public void setProductId(int productId) { 
        this.productId = productId; 
    }

    public String getProductName() { 
        return productName; 
    }
    
    public void setProductName(String productName) { 
        this.productName = productName; 
    }

    public double getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(double quantity) { 
        this.quantity = quantity;
        // Recalculate total price when quantity changes
        this.totalPrice = this.quantity * this.unitPrice;
    }

    public double getUnitPrice() { 
        return unitPrice; 
    }
    
    public void setUnitPrice(double unitPrice) { 
        this.unitPrice = unitPrice;
        // Recalculate total price when unit price changes
        this.totalPrice = this.quantity * this.unitPrice;
    }

    public double getTotalPrice() { 
        return totalPrice; 
    }
    
    public void setTotalPrice(double totalPrice) { 
        this.totalPrice = totalPrice; 
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}