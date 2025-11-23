package com.freshharvest.dao;

import com.freshharvest.app.ReportData;
import com.freshharvest.util.DBHandle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportsDAO {

    public List<ReportData> getAllTransactions() {
        List<ReportData> list = new ArrayList<>();
        String sql =
                "SELECT "+
    "o.order_date AS date, "+
    "'Revenue' AS category, "+
    "CONCAT('Order #', o.order_id, ' - ') AS description, "+
    "CONCAT('$', FORMAT(o.total_amount, 2)) AS amount "+
    "FROM orders o "+
    "LEFT JOIN crops c ON o.farmer_id = c.farmer_id "+
    "UNION ALL "+
    "SELECT "+
    "e.expense_date AS date, "+
    "'Expense' AS category, "+
    "CONCAT(e.category, ' - ', e.description) AS description, "+
    "CONCAT('$', FORMAT(e.amount, 2)) AS amount "+
    "FROM expenses e "+
    "ORDER BY date DESC "+
    "LIMIT 50;"
;

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new ReportData(
                    rs.getString("date"),
                    rs.getString("category"),
                    rs.getString("description"),
                    rs.getString("amount")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    public double getMonthlyExpenses(String month) {
        String sql =
                "SELECT COALESCE(SUM(amount), 0) AS expenses " +
                "FROM expenses " +
                "WHERE DATE_FORMAT(expense_date, '%Y-%m') = ?";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, month);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble("expenses") : 0.0;
            }

        } catch (SQLException e) {
            System.err.println("Error loading monthly expenses: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }

    public double getTotalRevenue() {
        return getSingleDouble("SELECT COALESCE(SUM(total_amount), 0) FROM orders");
    }

    public double getTotalExpenses() {
        return getSingleDouble("SELECT COALESCE(SUM(amount), 0) FROM expenses");
    }

    private double getSingleDouble(String sql) {
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            System.err.println("Error executing query: " + sql + " - " + e.getMessage());
            return 0.0;
        }
    }

    // Crop Performance Data for Charts
    public List<ReportData> getCropPerformance() {
        List<ReportData> list = new ArrayList<>();
        String sql =
                "SELECT "+
   " c.crop_name AS crop_name, "+
   " COALESCE(SUM(oi.unit_price * oi.quantity), 0) AS revenue, "+
   " COALESCE(SUM(e.amount), 0) AS expenses, "+
   " (COALESCE(SUM(oi.unit_price * oi.quantity), 0) - COALESCE(SUM(e.amount), 0)) AS profit "+
" FROM crops c "+
" LEFT JOIN order_items oi ON c.crop_id = oi.product_id "+
" LEFT JOIN expenses e ON c.crop_id = e.crop_id "+
" GROUP BY c.crop_id, c.crop_name "+
" ORDER BY profit DESC; ";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String cropName = rs.getString("crop_name");
                double profit = rs.getDouble("profit");
                double revenue = rs.getDouble("revenue");
                double expenses = rs.getDouble("expenses");
                
                list.add(new ReportData(
                    "",
                    "Crop Performance",
                    cropName,
                    String.format("$%.2f", profit)
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error loading crop performance: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Monthly Revenue Data
    public List<ReportData> getMonthlyRevenue() {
        List<ReportData> list = new ArrayList<>();
        String sql =
                "SELECT DATE_FORMAT(order_date, '%Y-%m') AS month, " +
                "SUM(total_amount) AS revenue " +
                "FROM orders " +
                "WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
                "GROUP BY DATE_FORMAT(order_date, '%Y-%m') " +
                "ORDER BY month";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new ReportData(
                    "",
                    "Monthly Revenue",
                    rs.getString("month"),
                    String.format("$%.2f", rs.getDouble("revenue"))
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error loading monthly revenue: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Expense by Category
    public List<ReportData> getExpenseByCategory() {
        List<ReportData> list = new ArrayList<>();
        String sql =
                "SELECT category, SUM(amount) AS total " +
                "FROM expenses " +
                "GROUP BY category " +
                "ORDER BY total DESC";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new ReportData(
                    "",
                    "Expense Category",
                    rs.getString("category"),
                    String.format("$%.2f", rs.getDouble("total"))
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error loading expense categories: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Crop Revenue Distribution
    public List<ReportData> getCropRevenueDistribution() {
        List<ReportData> list = new ArrayList<>();
        String sql =
                "SELECT  "+
   " c.crop_name AS crop_name, "+
   " SUM(oi.unit_price * oi.quantity) AS revenue "+
    " FROM crops c "+
" JOIN order_items oi ON c.crop_id = oi.product_id "+
" GROUP BY c.crop_id, c.crop_name "+
" ORDER BY revenue DESC";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new ReportData(
                    "",
                    "Revenue Distribution",
                    rs.getString("crop_name"),
                    String.format("$%.2f", rs.getDouble("revenue"))
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error loading crop revenue distribution: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }


   public List<ReportData> getSeasonalAnalysis() {
    List<ReportData> seasonalData = new ArrayList<>();
    
    String query = "SELECT season, SUM(revenue) as total_revenue, SUM(expenses) as total_expenses, SUM(revenue) - SUM(expenses) as net_profit FROM (SELECT CASE WHEN MONTH(o.order_date) IN (12, 1, 2) THEN 'Winter' WHEN MONTH(o.order_date) IN (3, 4, 5) THEN 'Spring' WHEN MONTH(o.order_date) IN (6, 7, 8) THEN 'Summer' WHEN MONTH(o.order_date) IN (9, 10, 11) THEN 'Fall' END as season, p.amount as revenue, 0 as expenses FROM payments p JOIN orders o ON p.order_id = o.order_id WHERE o.order_date IS NOT NULL AND p.payment_status = 'completed' UNION ALL SELECT CASE WHEN MONTH(e.expense_date) IN (12, 1, 2) THEN 'Winter' WHEN MONTH(e.expense_date) IN (3, 4, 5) THEN 'Spring' WHEN MONTH(e.expense_date) IN (6, 7, 8) THEN 'Summer' WHEN MONTH(e.expense_date) IN (9, 10, 11) THEN 'Fall' END as season, 0 as revenue, e.amount as expenses FROM expenses e WHERE e.expense_date IS NOT NULL) seasonal_data WHERE season IS NOT NULL GROUP BY season ORDER BY CASE season WHEN 'Winter' THEN 1 WHEN 'Spring' THEN 2 WHEN 'Summer' THEN 3 WHEN 'Fall' THEN 4 END";
    
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            String season = rs.getString("season");
            double netProfit = rs.getDouble("net_profit");
            seasonalData.add(new ReportData("", "Seasonal", season, 
                String.format("$%.2f", netProfit)));
        }
        
    } catch (SQLException e) {
        System.err.println("Error fetching seasonal analysis: " + e.getMessage());
        e.printStackTrace();
    }
    
    return seasonalData;
}

}