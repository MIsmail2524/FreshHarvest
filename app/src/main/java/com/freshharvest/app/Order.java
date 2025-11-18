package com.freshharvest.app;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private String orderNumber;
    private int customerId;
    private String customerName;
    private int farmerId;
    private LocalDateTime orderDate;
    private double totalAmount;
    private String status; // PENDING, CONFIRMED, PROCESSING, READY, COMPLETED, CANCELLED
    private String deliveryType; // PICKUP, DELIVERY
    private String deliveryAddress;
    private String customerPhone;
    private String customerEmail;
    private String notes;
    private int itemCount; // Number of items in the order

    // Default constructor
    public Order() {}

    // Constructor for table display (without OrderItem dependency)
    public Order(int orderId, String orderNumber, String customerName, 
                 LocalDateTime orderDate, double totalAmount, String status, int itemCount) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.itemCount = itemCount;
    }

    // Full constructor
    public Order(int orderId, String orderNumber, int customerId, String customerName, 
                 int farmerId, LocalDateTime orderDate, double totalAmount, String status, 
                 String deliveryType, String deliveryAddress, String customerPhone, 
                 String customerEmail, String notes, int itemCount) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.customerName = customerName;
        this.farmerId = farmerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.deliveryType = deliveryType;
        this.deliveryAddress = deliveryAddress;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.notes = notes;
        this.itemCount = itemCount;
    }

    // Getters and Setters
    public int getOrderId() { 
        return orderId; 
    }
    
    public void setOrderId(int orderId) { 
        this.orderId = orderId; 
    }

    public String getOrderNumber() { 
        return orderNumber; 
    }
    
    public void setOrderNumber(String orderNumber) { 
        this.orderNumber = orderNumber; 
    }

    public int getCustomerId() { 
        return customerId; 
    }
    
    public void setCustomerId(int customerId) { 
        this.customerId = customerId; 
    }

    public String getCustomerName() { 
        return customerName; 
    }
    
    public void setCustomerName(String customerName) { 
        this.customerName = customerName; 
    }

    public int getFarmerId() { 
        return farmerId; 
    }
    
    public void setFarmerId(int farmerId) { 
        this.farmerId = farmerId; 
    }

    public LocalDateTime getOrderDate() { 
        return orderDate; 
    }
    
    public void setOrderDate(LocalDateTime orderDate) { 
        this.orderDate = orderDate; 
    }

    public double getTotalAmount() { 
        return totalAmount; 
    }
    
    public void setTotalAmount(double totalAmount) { 
        this.totalAmount = totalAmount; 
    }

    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }

    public String getDeliveryType() { 
        return deliveryType; 
    }
    
    public void setDeliveryType(String deliveryType) { 
        this.deliveryType = deliveryType; 
    }

    public String getDeliveryAddress() { 
        return deliveryAddress; 
    }
    
    public void setDeliveryAddress(String deliveryAddress) { 
        this.deliveryAddress = deliveryAddress; 
    }

    public String getCustomerPhone() { 
        return customerPhone; 
    }
    
    public void setCustomerPhone(String customerPhone) { 
        this.customerPhone = customerPhone; 
    }

    public String getCustomerEmail() { 
        return customerEmail; 
    }
    
    public void setCustomerEmail(String customerEmail) { 
        this.customerEmail = customerEmail; 
    }

    public String getNotes() { 
        return notes; 
    }
    
    public void setNotes(String notes) { 
        this.notes = notes; 
    }

    public int getItemCount() { 
        return itemCount; 
    }
    
    public void setItemCount(int itemCount) { 
        this.itemCount = itemCount; 
    }

    // Helper method to get items summary for table display (without OrderItem dependency)
    public String getItemsSummary() {
        if (itemCount <= 0) {
            return "No items";
        }
        
        if (itemCount == 1) {
            return "1 item";
        } else {
            return itemCount + " items";
        }
    }

    // Helper method to get formatted total amount
    public String getFormattedTotalAmount() {
        return String.format("$%.2f", totalAmount);
    }

    // Helper method to check if order is pending
    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }

    // Helper method to check if order is completed
    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }

    // Helper method to check if order can be cancelled
    public boolean canBeCancelled() {
        return "PENDING".equalsIgnoreCase(status) || "CONFIRMED".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", itemCount=" + itemCount +
                '}';
    }

    // Static helper methods for status
    public static class Status {
        public static final String PENDING = "PENDING";
        public static final String CONFIRMED = "CONFIRMED";
        public static final String PROCESSING = "PROCESSING";
        public static final String READY = "READY";
        public static final String COMPLETED = "COMPLETED";
        public static final String CANCELLED = "CANCELLED";
        
        public static String[] getAllStatuses() {
            return new String[]{PENDING, CONFIRMED, PROCESSING, READY, COMPLETED, CANCELLED};
        }
    }

    // Static helper methods for delivery type
    public static class DeliveryType {
        public static final String PICKUP = "PICKUP";
        public static final String DELIVERY = "DELIVERY";
        
        public static String[] getAllDeliveryTypes() {
            return new String[]{PICKUP, DELIVERY};
        }
    }
}