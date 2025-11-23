package com.freshharvest.dao;

import com.freshharvest.util.DBHandle;
import java.sql.*;
import java.time.LocalDateTime;

public class PaymentDAO {

    public Object[] getOrderForPayment(int orderId, int customerId) {
        String sql = "SELECT o.total_amount, " +
                    "COALESCE(SUM(p.amount), 0) as amount_paid, " +
                    "o.status, o.order_date, o.order_number " +
                    "FROM orders o " +
                    "LEFT JOIN payments p ON o.order_id = p.order_id " +
                    "WHERE o.order_id = ? AND o.customer_id = ? " +
                    "GROUP BY o.order_id, o.total_amount, o.status, o.order_date, o.order_number";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            stmt.setInt(2, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double totalAmount = rs.getDouble("total_amount");
                    double amountPaid = rs.getDouble("amount_paid");
                    String status = rs.getString("status");
                    
                    // Only allow payment for pending/confirmed orders that aren't fully paid
                    if (("PENDING".equalsIgnoreCase(status) || "CONFIRMED".equalsIgnoreCase(status)) && 
                        amountPaid < totalAmount) {
                        return new Object[]{
                            totalAmount,
                            amountPaid,
                            status,
                            rs.getTimestamp("order_date").toLocalDateTime(),
                            rs.getString("order_number")
                        };
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order for payment: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean processPayment(int orderId, int customerId, double amount, 
                                 String paymentMethod, String transactionId) {
        // TEMPORARY FIX: Remove payment_status from INSERT until column exists
        String insertPaymentSql = "INSERT INTO payments (order_id, customer_id, amount, payment_method, " +
                                 "transaction_id, payment_date) VALUES (?, ?, ?, ?, ?, NOW())";
        
        String updateOrderSql = "UPDATE orders SET " +
                               "amount_paid = COALESCE(amount_paid, 0) + ?, " +
                               "payment_status = CASE WHEN (COALESCE(amount_paid, 0) + ?) >= total_amount " +
                               "THEN 'PAID' ELSE 'PARTIAL' END, " +
                               "status = CASE WHEN (COALESCE(amount_paid, 0) + ?) >= total_amount " +
                               "THEN 'CONFIRMED' ELSE status END " +
                               "WHERE order_id = ? AND customer_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement paymentStmt = conn.prepareStatement(insertPaymentSql);
                 PreparedStatement orderStmt = conn.prepareStatement(updateOrderSql)) {
                
                // Insert payment record (without payment_status for now)
                paymentStmt.setInt(1, orderId);
                paymentStmt.setInt(2, customerId);
                paymentStmt.setDouble(3, amount);
                paymentStmt.setString(4, paymentMethod);
                paymentStmt.setString(5, transactionId);
                int paymentInserted = paymentStmt.executeUpdate();
                
                // Update order with payment
                orderStmt.setDouble(1, amount);
                orderStmt.setDouble(2, amount);
                orderStmt.setDouble(3, amount);
                orderStmt.setInt(4, orderId);
                orderStmt.setInt(5, customerId);
                int orderUpdated = orderStmt.executeUpdate();
                
                if (paymentInserted > 0 && orderUpdated > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error processing payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalPaidAmount(int orderId) {
        // TEMPORARY FIX: Remove payment_status filter until column exists
        String sql = "SELECT COALESCE(SUM(amount), 0) as total_paid FROM payments WHERE order_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_paid");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total paid amount: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
}