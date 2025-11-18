package com.freshharvest.app;

import com.freshharvest.dao.OrderDAO;
import java.util.List;

public class OrderService {
    private OrderDAO orderDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
    }

    // Get all orders for a farmer
    public List<Order> getOrdersByFarmer(int farmerId) {
        try {
            return orderDAO.findByFarmerId(farmerId);
        } catch (Exception e) {
            System.err.println("Service error getting orders by farmer: " + e.getMessage());
            throw new RuntimeException("Failed to get orders", e);
        }
    }

    // Get orders by status for a farmer
    public List<Order> getOrdersByFarmerAndStatus(int farmerId, String status) {
        try {
            return orderDAO.findByFarmerIdAndStatus(farmerId, status);
        } catch (Exception e) {
            System.err.println("Service error getting orders by status: " + e.getMessage());
            throw new RuntimeException("Failed to get orders by status", e);
        }
    }

    // Get order by ID
    public Order getOrderById(int orderId, int farmerId) {
        try {
            return orderDAO.findById(orderId, farmerId);
        } catch (Exception e) {
            System.err.println("Service error getting order by ID: " + e.getMessage());
            throw new RuntimeException("Failed to get order", e);
        }
    }

    // Create new order
    public boolean createOrder(Order order) {
        try {
            // Generate order number if not provided
            if (order.getOrderNumber() == null || order.getOrderNumber().isEmpty()) {
                order.setOrderNumber(orderDAO.generateOrderNumber(order.getFarmerId()));
            }
            return orderDAO.create(order);
        } catch (Exception e) {
            System.err.println("Service error creating order: " + e.getMessage());
            return false;
        }
    }

    // Update order
    public boolean updateOrder(Order order) {
        try {
            return orderDAO.update(order);
        } catch (Exception e) {
            System.err.println("Service error updating order: " + e.getMessage());
            return false;
        }
    }

    // Delete order
    public boolean deleteOrder(int orderId) {
        try {
            // Using default farmer ID 1 for testing - you might want to pass farmerId as parameter
            return orderDAO.delete(orderId, 1);
        } catch (Exception e) {
            System.err.println("Service error deleting order: " + e.getMessage());
            return false;
        }
    }

    // Update order status
    public boolean updateOrderStatus(int orderId, String newStatus) {
        try {
            // Using default farmer ID 1 for testing - you might want to pass farmerId as parameter
            return orderDAO.updateStatus(orderId, newStatus, 1);
        } catch (Exception e) {
            System.err.println("Service error updating order status: " + e.getMessage());
            return false;
        }
    }

    // Get order statistics
    public OrderDAO.OrderStatistics getOrderStatistics(int farmerId) {
        try {
            return orderDAO.getOrderStatistics(farmerId);
        } catch (Exception e) {
            System.err.println("Service error getting order statistics: " + e.getMessage());
            return new OrderDAO.OrderStatistics();
        }
    }
}