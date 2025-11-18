package com.freshharvest.app;

import java.time.LocalDate;

public class Customer {
    private int customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private int totalOrders;
    private double totalSpent;
    private LocalDate lastOrderDate;
    private LocalDate customerSince;

    // Constructors, getters, and setters
    public Customer() {}

    public Customer(int customerId, String firstName, String lastName, String email, 
                   String phone, int totalOrders, double totalSpent, 
                   LocalDate lastOrderDate, LocalDate customerSince) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
        this.lastOrderDate = lastOrderDate;
        this.customerSince = customerSince;
    }

    // Helper method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters and setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    
    public double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }
    
    public LocalDate getLastOrderDate() { return lastOrderDate; }
    public void setLastOrderDate(LocalDate lastOrderDate) { this.lastOrderDate = lastOrderDate; }
    
    public LocalDate getCustomerSince() { return customerSince; }
    public void setCustomerSince(LocalDate customerSince) { this.customerSince = customerSince; }
}