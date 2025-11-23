package com.freshharvest.dao;

import com.freshharvest.util.DBHandle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public Object[] getOrderStatistics(int farmerId) {
        String sql = "SELECT " +
                "COUNT(*) as total_orders, " +
                "COUNT(CASE WHEN order_date >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 1 END) as weekly_orders " +
                "FROM orders " +
                "WHERE farmer_id = ? OR ? = 1"; // Adjust based on your farmer_id logic

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            stmt.setInt(2, farmerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("total_orders"),
                        rs.getInt("weekly_orders")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order statistics: " + e.getMessage());
        }
        return null;
    }

    public Object[] getCropStatistics(int farmerId) {
        String sql = "SELECT " +
                "COUNT(*) as total_crops, " +
                "COUNT(CASE WHEN quantity > 0 THEN 1 END) as active_crops " +
                "FROM crops " +
                "WHERE farmer_id = ? OR ? = 1";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            stmt.setInt(2, farmerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("total_crops"),
                        rs.getInt("active_crops")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting crop statistics: " + e.getMessage());
        }
        return null;
    }

    public Object[] getBatchStatistics(int farmerId) {
        String sql = "SELECT " +
                "COUNT(DISTINCT batch_number) as total_batches, " +
                "COUNT(DISTINCT CASE WHEN created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN batch_number END) as active_batches " +
                "FROM inventory " +
                "WHERE farmer_id = ? OR ? = 1";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            stmt.setInt(2, farmerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("total_batches"),
                        rs.getInt("active_batches")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting batch statistics: " + e.getMessage());
        }
        return null;
    }

    public Object[] getHarvestStatistics(int farmerId) {
        String sql = "SELECT " +
                "COALESCE(SUM(quantity), 0) as total_harvest, " +
                "COALESCE(SUM(CASE WHEN harvest_date >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN quantity END), 0) as weekly_harvest " +
                "FROM crops " +
                "WHERE farmer_id = ? OR ? = 1";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            stmt.setInt(2, farmerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getDouble("total_harvest"),
                        rs.getDouble("weekly_harvest")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting harvest statistics: " + e.getMessage());
        }
        return null;
    }

    public Object[] getFinancialStatistics(int farmerId) {
        String sql = "SELECT " +
                "COALESCE(SUM(total_amount), 0) as total_revenue, " +
                "COALESCE(SUM(amount), 0) as total_expenses " +
                "FROM ( " +
                "    SELECT total_amount, 0 as amount FROM orders WHERE farmer_id = ? OR ? = 1 " +
                "    UNION ALL " +
                "    SELECT 0 as total_amount, amount FROM expenses WHERE farmer_id = ? OR ? = 1 " +
                ") as finances";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            stmt.setInt(2, farmerId);
            stmt.setInt(3, farmerId);
            stmt.setInt(4, farmerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getDouble("total_revenue"),
                        rs.getDouble("total_expenses")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting financial statistics: " + e.getMessage());
        }
        return null;
    }

    public List<Object[]> getMonthlyRevenue(int farmerId) {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m') as month, " +
                     "COALESCE(SUM(total_amount), 0) as revenue " +
                     "FROM orders " +
                     "WHERE (farmer_id = ? OR ? = 1) AND order_date >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
                     "GROUP BY DATE_FORMAT(order_date, '%Y-%m') " +
                     "ORDER BY month";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            stmt.setInt(2, farmerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    data.add(new Object[]{
                        rs.getString("month"),
                        rs.getDouble("revenue")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting monthly revenue: " + e.getMessage());
        }
        return data;
    }

    public List<Object[]> getCropPerformance(int farmerId) {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT c.name as crop_name, " +
                     "COALESCE(SUM(oi.unit_price * oi.quantity), 0) as revenue " +
                     "FROM crops c " +
                     "LEFT JOIN order_items oi ON c.id = oi.crop_id " +
                     "LEFT JOIN orders o ON oi.order_id = o.id " +
                     "WHERE c.farmer_id = ? OR ? = 1 " +
                     "GROUP BY c.id, c.name " +
                     "ORDER BY revenue DESC " +
                     "LIMIT 10";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            stmt.setInt(2, farmerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    data.add(new Object[]{
                        rs.getString("crop_name"),
                        rs.getDouble("revenue")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting crop performance: " + e.getMessage());
        }
        return data;
    }

    public List<Object[]> getOrderStatusDistribution(int farmerId) {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT status, COUNT(*) as count " +
                     "FROM orders " +
                     "WHERE farmer_id = ? OR ? = 1 " +
                     "GROUP BY status " +
                     "ORDER BY count DESC";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            stmt.setInt(2, farmerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    data.add(new Object[]{
                        rs.getString("status"),
                        rs.getInt("count")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order status distribution: " + e.getMessage());
        }
        return data;
    }

    public List<Object[]> getExpenseBreakdown(int farmerId) {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT category, COALESCE(SUM(amount), 0) as total_amount " +
                     "FROM expenses " +
                     "WHERE farmer_id = ? OR ? = 1 " +
                     "GROUP BY category " +
                     "ORDER BY total_amount DESC";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            stmt.setInt(2, farmerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    data.add(new Object[]{
                        rs.getString("category"),
                        rs.getDouble("total_amount")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting expense breakdown: " + e.getMessage());
        }
        return data;
    }

    public List<Object[]> getMonthlySales(int farmerId) {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m') as month, " +
                     "COALESCE(SUM(total_amount), 0) as sales " +
                     "FROM orders " +
                     "WHERE (farmer_id = ? OR ? = 1) AND order_date >= DATE_SUB(NOW(), INTERVAL 12 MONTH) " +
                     "GROUP BY DATE_FORMAT(order_date, '%Y-%m') " +
                     "ORDER BY month";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            stmt.setInt(2, farmerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    data.add(new Object[]{
                        rs.getString("month"),
                        rs.getDouble("sales")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting monthly sales: " + e.getMessage());
        }
        return data;
    }
}