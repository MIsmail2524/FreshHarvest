package com.freshharvest.app;

import com.freshharvest.app.Customer;
import com.freshharvest.dao.CustomerDAO;
import java.util.List;

public class CustomerService {
    private CustomerDAO customerDAO;
    
    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }
    
    /**
     * Get all customers for the current farmer
     */
    public List<Customer> getCustomersByFarmer() {
        try {
            return customerDAO.findByFarmerId(getCurrentFarmerId());
        } catch (Exception e) {
            System.err.println("Service error getting customers: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }
    
    /**
     * Get customer by ID
     */
    public Customer getCustomerById(int customerId) {
        try {
            if (customerId <= 0) {
                throw new IllegalArgumentException("Invalid customer ID");
            }
            
            return customerDAO.findById(customerId, getCurrentFarmerId());
            
        } catch (Exception e) {
            System.err.println("Service error getting customer by ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Search customers by name, email, or phone
     */
    public List<Customer> searchCustomers(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return getCustomersByFarmer(); // Return all if search is empty
            }
            
            return customerDAO.searchByTerm(searchTerm, getCurrentFarmerId());
            
        } catch (Exception e) {
            System.err.println("Service error searching customers: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get top customers by total spent
     */
    public List<Customer> getTopCustomers(int limit) {
        try {
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit must be positive");
            }
            
            return customerDAO.findTopBySpending(limit, getCurrentFarmerId());
            
        } catch (Exception e) {
            System.err.println("Service error getting top customers: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get new customers (registered in the last 30 days)
     */
    public List<Customer> getNewCustomers() {
        try {
            return customerDAO.findNewCustomers(getCurrentFarmerId());
        } catch (Exception e) {
            System.err.println("Service error getting new customers: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get inactive customers (no orders in the last 90 days)
     */
    public List<Customer> getInactiveCustomers() {
        try {
            return customerDAO.findInactiveCustomers(getCurrentFarmerId());
        } catch (Exception e) {
            System.err.println("Service error getting inactive customers: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Update customer information
     */
    public boolean updateCustomer(Customer customer) {
        try {
            if (customer == null) {
                throw new IllegalArgumentException("Customer cannot be null");
            }
            
            if (customer.getCustomerId() <= 0) {
                throw new IllegalArgumentException("Invalid customer ID");
            }
            
            if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
                throw new IllegalArgumentException("First name is required");
            }
            
            if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            
            return customerDAO.update(customer, getCurrentFarmerId());
            
        } catch (Exception e) {
            System.err.println("Service error updating customer: " + e.getMessage());
            throw new RuntimeException("Failed to update customer: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get customer statistics
     */
    public CustomerStatistics getCustomerStatistics() {
        try {
            return customerDAO.getStatisticsByFarmerId(getCurrentFarmerId());
        } catch (Exception e) {
            System.err.println("Service error getting customer statistics: " + e.getMessage());
            return new CustomerStatistics(0, 0, 0, 0.0); // Return empty statistics
        }
    }
    
    /**
     * Get customer order history
     */
    public List<CustomerOrder> getCustomerOrderHistory(int customerId) {
        try {
            if (customerId <= 0) {
                throw new IllegalArgumentException("Invalid customer ID");
            }
            
            return customerDAO.getOrderHistory(customerId, getCurrentFarmerId());
            
        } catch (Exception e) {
            System.err.println("Service error getting customer order history: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Send notification to customer (placeholder for future implementation)
     */
    public boolean sendCustomerNotification(int customerId, String message) {
        try {
            if (customerId <= 0) {
                throw new IllegalArgumentException("Invalid customer ID");
            }
            
            if (message == null || message.trim().isEmpty()) {
                throw new IllegalArgumentException("Message cannot be empty");
            }
            
            // TODO: Implement actual notification system (email, SMS, etc.)
            System.out.println("Notification sent to customer " + customerId + ": " + message);
            return true;
            
        } catch (Exception e) {
            System.err.println("Service error sending notification: " + e.getMessage());
            return false;
        }
    }
    
    private int getCurrentFarmerId() {
        // Get current logged-in farmer ID (replace with actual session management)
        return 1; // Default for testing
    }
    
    /**
     * Inner class for customer statistics
     */
    public static class CustomerStatistics {
        private int totalCustomers;
        private int activeCustomers;
        private int newThisMonth;
        private double totalRevenue;
        
        public CustomerStatistics(int totalCustomers, int activeCustomers, int newThisMonth, double totalRevenue) {
            this.totalCustomers = totalCustomers;
            this.activeCustomers = activeCustomers;
            this.newThisMonth = newThisMonth;
            this.totalRevenue = totalRevenue;
        }
        
        // Getters
        public int getTotalCustomers() { return totalCustomers; }
        public int getActiveCustomers() { return activeCustomers; }
        public int getNewThisMonth() { return newThisMonth; }
        public double getTotalRevenue() { return totalRevenue; }
    }
    
    /**
     * Inner class for customer order history
     */
    public static class CustomerOrder {
        private String orderNumber;
        private String orderDate;
        private double totalAmount;
        private String status;
        private int itemCount;
        
        public CustomerOrder(String orderNumber, String orderDate, double totalAmount, String status, int itemCount) {
            this.orderNumber = orderNumber;
            this.orderDate = orderDate;
            this.totalAmount = totalAmount;
            this.status = status;
            this.itemCount = itemCount;
        }
        
        // Getters
        public String getOrderNumber() { return orderNumber; }
        public String getOrderDate() { return orderDate; }
        public double getTotalAmount() { return totalAmount; }
        public String getStatus() { return status; }
        public int getItemCount() { return itemCount; }
    }
}