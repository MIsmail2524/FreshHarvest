package com.freshharvest.dao;

import com.freshharvest.app.Order;
import com.freshharvest.util.DBHandle;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    
    // Get all orders for a farmer with complete details
 public List<Order> findByFarmerId(int farmerId) {
    List<Order> orders = new ArrayList<>();

String sql = """
    SELECT 
        o.*, 
        CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
        c.email AS customer_email,
        c.phone AS customer_phone
    FROM orders o
    LEFT JOIN customers c ON o.customer_id = c.customer_id
    WHERE o.farmer_id = ?
    ORDER BY o.order_date DESC
""";

    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, farmerId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            orders.add(mapResultSetToOrder(rs));
        }

    } catch (SQLException e) {
        System.err.println("DAO error finding orders: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Database error while fetching orders", e);
    }

    return orders;
}

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
    Order order = new Order();
    order.setOrderId(rs.getInt("order_id"));
    order.setOrderNumber(rs.getString("order_number"));
    order.setCustomerId(rs.getInt("customer_id"));
    order.setCustomerName(rs.getString("customer_name"));
    order.setCustomerEmail(rs.getString("customer_email"));   // <- fetched from customers table
    order.setCustomerPhone(rs.getString("customer_phone"));   // <- fetched from customers table
    order.setFarmerId(rs.getInt("farmer_id"));

    Timestamp orderDate = rs.getTimestamp("order_date");
    if (orderDate != null) {
        order.setOrderDate(orderDate.toLocalDateTime());
    }

    order.setTotalAmount(rs.getDouble("total_amount"));
    order.setStatus(rs.getString("status"));
    order.setDeliveryType(rs.getString("delivery_type"));
    order.setDeliveryAddress(rs.getString("delivery_address"));
    order.setNotes(rs.getString("notes"));
   // order.setItemCount(rs.getInt("item_count"));  // <- now uses correct column

    return order;
}



    // Get orders by farmer ID and status
    public List<Order> findByFarmerIdAndStatus(int farmerId, String status) {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT o.*, CONCAT(c.first_name, ' ', c.last_name) as customer_name 
            FROM orders o 
            LEFT JOIN customers c ON o.customer_id = c.customer_id 
            WHERE o.farmer_id = ? AND o.status = ? 
            ORDER BY o.order_date DESC
            """;
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            stmt.setString(2, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error finding orders by status: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching orders by status", e);
        }
        return orders;
    }
    
    // Find order by ID and farmer ID
    public Order findById(int orderId, int farmerId) {
        String sql = """
            SELECT o.*, CONCAT(c.first_name, ' ', c.last_name) as customer_name 
            FROM orders o 
            LEFT JOIN customers c ON o.customer_id = c.customer_id 
            WHERE o.order_id = ? AND o.farmer_id = ?
            """;
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            stmt.setInt(2, farmerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error finding order by ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching order", e);
        }
        return null;
    }
    
    // Create new order
    public boolean create(Order order) {
        String sql = """
            INSERT INTO orders (order_number, customer_id, farmer_id, order_date, 
            total_amount, status, delivery_type, delivery_address, customer_phone, 
            customer_email, notes, item_count) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, order.getOrderNumber());
            stmt.setInt(2, order.getCustomerId());
            stmt.setInt(3, order.getFarmerId());
            stmt.setTimestamp(4, Timestamp.valueOf(order.getOrderDate()));
            stmt.setDouble(5, order.getTotalAmount());
            stmt.setString(6, order.getStatus());
            stmt.setString(7, order.getDeliveryType());
            stmt.setString(8, order.getDeliveryAddress());
            stmt.setString(9, order.getCustomerPhone());
            stmt.setString(10, order.getCustomerEmail());
            stmt.setString(11, order.getNotes());
            stmt.setInt(12, order.getItemCount());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setOrderId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error creating order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Update order
    public boolean update(Order order) {
        String sql = """
            UPDATE orders SET order_number = ?, customer_id = ?, order_date = ?, 
            total_amount = ?, status = ?, delivery_type = ?, delivery_address = ?, 
            customer_phone = ?, customer_email = ?, notes = ?, item_count = ? 
            WHERE order_id = ? AND farmer_id = ?
            """;
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, order.getOrderNumber());
            stmt.setInt(2, order.getCustomerId());
            stmt.setTimestamp(3, Timestamp.valueOf(order.getOrderDate()));
            stmt.setDouble(4, order.getTotalAmount());
            stmt.setString(5, order.getStatus());
            stmt.setString(6, order.getDeliveryType());
            stmt.setString(7, order.getDeliveryAddress());
            stmt.setString(8, order.getCustomerPhone());
            stmt.setString(9, order.getCustomerEmail());
            stmt.setString(10, order.getNotes());
            stmt.setInt(11, order.getItemCount());
            stmt.setInt(12, order.getOrderId());
            stmt.setInt(13, order.getFarmerId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("DAO error updating order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete order
    public boolean delete(int orderId, int farmerId) {
        String sql = "DELETE FROM orders WHERE order_id = ? AND farmer_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            stmt.setInt(2, farmerId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("DAO error deleting order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Update order status
    public boolean updateStatus(int orderId, String newStatus, int farmerId) {
        String sql = """
            UPDATE orders SET status = ?, updated_at = CURRENT_TIMESTAMP 
            WHERE order_id = ? AND farmer_id = ?
            """;
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            stmt.setInt(3, farmerId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("DAO error updating order status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Get order statistics for dashboard
    public OrderStatistics getOrderStatistics(int farmerId) {
        OrderStatistics stats = new OrderStatistics();
        String sql = """
            SELECT 
            COUNT(*) as total_orders, 
            SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as pending_orders, 
            SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_orders, 
            SUM(CASE WHEN status IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'READY') THEN total_amount ELSE 0 END) as pending_amount, 
            SUM(CASE WHEN status = 'COMPLETED' THEN total_amount ELSE 0 END) as completed_amount 
            FROM orders WHERE farmer_id = ?
            """;
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                stats.setTotalOrders(rs.getInt("total_orders"));
                stats.setPendingOrders(rs.getInt("pending_orders"));
                stats.setCompletedOrders(rs.getInt("completed_orders"));
                stats.setPendingAmount(rs.getDouble("pending_amount"));
                stats.setCompletedAmount(rs.getDouble("completed_amount"));
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error getting order statistics: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }
    
    // Generate unique order number
    public String generateOrderNumber(int farmerId) {
        String sql = """
            SELECT CONCAT('ORD', LPAD(?, 6, '0'), '-', DATE_FORMAT(NOW(), '%y%m%d'), '-', 
            LPAD(COALESCE(MAX(SUBSTRING_INDEX(SUBSTRING_INDEX(order_number, '-', -1), '-', -1)), 0) + 1, 3, '0')) as new_order_number 
            FROM orders WHERE farmer_id = ? AND DATE(order_date) = CURDATE()
            """;
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            stmt.setInt(2, farmerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String orderNumber = rs.getString("new_order_number");
                if (orderNumber != null && !orderNumber.startsWith("ORD")) {
                    // Fallback if no orders today
                    return String.format("ORD%06d-%s-001", farmerId, 
                        LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd")));
                }
                return orderNumber;
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error generating order number: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Default fallback
        return String.format("ORD%06d-%s-001", farmerId, 
            LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd")));
    }
    
    // Inner class for order statistics
    public static class OrderStatistics {
        private int totalOrders;
        private int pendingOrders;
        private int completedOrders;
        private double pendingAmount;
        private double completedAmount;
        
        // Getters and setters
        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
        
        public int getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(int pendingOrders) { this.pendingOrders = pendingOrders; }
        
        public int getCompletedOrders() { return completedOrders; }
        public void setCompletedOrders(int completedOrders) { this.completedOrders = completedOrders; }
        
        public double getPendingAmount() { return pendingAmount; }
        public void setPendingAmount(double pendingAmount) { this.pendingAmount = pendingAmount; }
        
        public double getCompletedAmount() { return completedAmount; }
        public void setCompletedAmount(double completedAmount) { this.completedAmount = completedAmount; }
    }
}