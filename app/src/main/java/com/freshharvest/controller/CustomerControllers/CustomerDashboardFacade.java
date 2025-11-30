package com.freshharvest.controller.CustomerControllers;

import com.freshharvest.app.Customers.CustomerOrder;
import com.freshharvest.dao.CustomerDAO;
import com.freshharvest.dao.ProductDAO;
import com.freshharvest.util.NotificationService;
import java.util.List;


public class CustomerDashboardFacade {
    
    private final CustomerDAO customerDAO;
    private final ProductDAO productDAO;
    private final NotificationService notificationService;
    
  
    public CustomerDashboardFacade(CustomerDAO customerDAO, ProductDAO productDAO, NotificationService notificationService) {
        this.customerDAO = customerDAO;
        this.productDAO = productDAO;
        this.notificationService = notificationService;
    }
    
   
  
    
  
    public DashboardData getDashboardData(int customerId) {
        DashboardData data = new DashboardData();
        
        try {
            Object[] stats = customerDAO.getOrderStatistics(customerId);
            if (stats != null) {
                data.setTotalOrders((Integer) stats[0]);
                data.setPendingOrders((Integer) stats[1]);
                data.setDeliveredOrders((Integer) stats[2]);
                data.setTotalSpent((Double) stats[3]);
            } else {
                calculateStatsFromOrders(data, customerId);
            }
            
            data.setFavoriteCategory(getFavoriteCategory(customerId));
            
            data.setRecentOrders(customerDAO.getCustomerOrdersSafe(customerId));
            
            data.setSuccess(true);
            data.setMessage("Dashboard data loaded successfully");
            
        } catch (Exception e) {
            data.setSuccess(false);
            data.setMessage("Failed to load dashboard: " + e.getMessage());
        }
        
        return data;
    }
    
  
    public OrderStatusData getOrderStatusData(int customerId) {
        OrderStatusData data = new OrderStatusData();
        
        try {
            List<Object[]> ordersData = customerDAO.getAllCustomerOrders(customerId);
            data.setOrdersData(ordersData);
            data.setSuccess(true);
        } catch (Exception e) {
            data.setSuccess(false);
            data.setMessage("Failed to load order status data: " + e.getMessage());
        }
        
        return data;
    }
    
   
    public CategoryData getCategoryData(int customerId) {
        CategoryData data = new CategoryData();
        
        try {
            List<Object[]> categoryData = customerDAO.getSpendingByCategory(customerId);
            data.setCategoryData(categoryData);
            data.setSuccess(true);
        } catch (Exception e) {
            data.setSuccess(false);
            data.setMessage("Failed to load category data: " + e.getMessage());
        }
        
        return data;
    }
    
    
    public PaymentData getPaymentData(int customerId) {
        PaymentData data = new PaymentData();
        
        try {
            List<Object[]> paymentData = customerDAO.getPaymentMethodDistribution(customerId);
            data.setPaymentData(paymentData);
            data.setSuccess(true);
        } catch (Exception e) {
            data.setSuccess(false);
            data.setMessage("Failed to load payment data: " + e.getMessage());
        }
        
        return data;
    }
    
 
    public int getCustomerIdByUsername(String username) {
        try {
            return customerDAO.getCustomerIdByUsername(username);
        } catch (Exception e) {
            System.err.println("Error getting customer ID: " + e.getMessage());
            return -1;
        }
    }
    
    private void calculateStatsFromOrders(DashboardData data, int customerId) {
        try {
            List<Object[]> ordersData = customerDAO.getAllCustomerOrders(customerId);
            
            int totalOrders = ordersData.size();
            double totalSpent = 0.0;
            int pendingOrders = 0;
            
            for (Object[] order : ordersData) {
                double amount = (Double) order[4];
                totalSpent += amount;
                
                String status = (String) order[3];
                if ("PENDING".equalsIgnoreCase(status) || "PROCESSING".equalsIgnoreCase(status)) {
                    pendingOrders++;
                }
            }
            
            data.setTotalOrders(totalOrders);
            data.setTotalSpent(totalSpent);
            data.setPendingOrders(pendingOrders);
            
        } catch (Exception e) {
            data.setTotalOrders(0);
            data.setTotalSpent(0.0);
            data.setPendingOrders(0);
        }
    }
    
    private String getFavoriteCategory(int customerId) {
        try {
            List<Object[]> categoryData = customerDAO.getSpendingByCategory(customerId);
            if (!categoryData.isEmpty()) {
                return categoryData.get(0)[0].toString();
            }
        } catch (Exception e) {
            System.err.println("Error getting favorite category: " + e.getMessage());
        }
        return "No data";
    }
}

class DashboardData {
    private boolean success;
    private String message;
    private int totalOrders;
    private int pendingOrders;
    private int deliveredOrders;
    private double totalSpent;
    private String favoriteCategory;
    private List<CustomerOrder> recentOrders;
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    
    public int getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(int pendingOrders) { this.pendingOrders = pendingOrders; }
    
    public int getDeliveredOrders() { return deliveredOrders; }
    public void setDeliveredOrders(int deliveredOrders) { this.deliveredOrders = deliveredOrders; }
    
    public double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }
    
    public String getFavoriteCategory() { return favoriteCategory; }
    public void setFavoriteCategory(String favoriteCategory) { this.favoriteCategory = favoriteCategory; }
    
    public List<CustomerOrder> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<CustomerOrder> recentOrders) { this.recentOrders = recentOrders; }
}

class OrderStatusData {
    private boolean success;
    private String message;
    private List<Object[]> ordersData;
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public List<Object[]> getOrdersData() { return ordersData; }
    public void setOrdersData(List<Object[]> ordersData) { this.ordersData = ordersData; }
}

class CategoryData {
    private boolean success;
    private String message;
    private List<Object[]> categoryData;
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public List<Object[]> getCategoryData() { return categoryData; }
    public void setCategoryData(List<Object[]> categoryData) { this.categoryData = categoryData; }
}

class PaymentData {
    private boolean success;
    private String message;
    private List<Object[]> paymentData;
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public List<Object[]> getPaymentData() { return paymentData; }
    public void setPaymentData(List<Object[]> paymentData) { this.paymentData = paymentData; }
}