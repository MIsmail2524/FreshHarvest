package com.freshharvest.app.Customers;

import java.time.LocalDate;

public class CustomerOrder {
    private int orderId;
    private String orderNumber;
    private LocalDate orderDate;
    private String status;
    private double totalAmount;
    private String paymentMethod;
    private LocalDate deliveryDate;
    private String shippingAddress;
    private String paymentStatus;
    private String customerName;
    private int itemCount;

    public CustomerOrder(int orderId, String orderNumber, LocalDate orderDate, String status, 
                        double totalAmount, String paymentMethod) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
    }

    public CustomerOrder(int orderId, String orderNumber, LocalDate orderDate, String status,
                        double totalAmount, String paymentMethod, LocalDate deliveryDate,
                        String shippingAddress, String paymentStatus, String customerName, int itemCount) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.deliveryDate = deliveryDate;
        this.shippingAddress = shippingAddress;
        this.paymentStatus = paymentStatus;
        this.customerName = customerName;
        this.itemCount = itemCount;
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
}