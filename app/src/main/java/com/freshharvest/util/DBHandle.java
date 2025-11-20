package com.freshharvest.util;

import com.freshharvest.model.Batch;
import com.freshharvest.model.InventoryLot;
import com.freshharvest.model.Shipment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DBHandle {

    private static DBHandle instance;
    private Connection connection;

    // UPDATE THESE WITH YOUR SETTINGS
    private static final String URL = "jdbc:mysql://localhost:3306/freshharvest_db";
    private static final String USER = "root";
    private static final String PASSWORD = "123"; // <--- CHECK THIS

    private DBHandle() {
        try {
            // Add the driver manually if needed
            try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (ClassNotFoundException e) {}
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to Integrated Database!");
        } catch (SQLException e) {
            System.err.println("Connection Failed: " + e.getMessage());
        }
    }

    public static synchronized DBHandle getInstance() {
        if (instance == null) instance = new DBHandle();
        return instance;
    }

    // --- DASHBOARD COUNTS ---
    public Map<String, Integer> getBatchStatusCounts() {
        Map<String, Integer> counts = new HashMap<>();
        // Query the FARMER'S 'crops' table
        String query = "SELECT status, COUNT(*) as count FROM crops GROUP BY status";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return counts;
    }

    public int getPendingShipmentCount() {
        // Query the FARMER'S 'orders' table for Confirmed orders (ready to ship)
        String query = "SELECT COUNT(*) AS count FROM orders WHERE status = 'Confirmed'";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt("count");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // --- BATCH MANAGEMENT (Uses 'crops' table) ---
    public ObservableList<Batch> getAllBatches() {
        ObservableList<Batch> list = FXCollections.observableArrayList();
        // Get crops that are ready for the factory (Harvested, Ready, Processing)
        String query = "SELECT crop_id, crop_name, harvest_date, status, grade FROM crops " +
                       "WHERE status IN ('Ready for Harvest', 'Harvested', 'Processing', 'Packaged')";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Batch(
                    rs.getInt("crop_id"),
                    rs.getString("crop_name"),
                    rs.getString("harvest_date"),
                    rs.getString("status"),
                    rs.getString("grade")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateBatchGradeAndStatus(int batchId, String grade, String status) {
        // Updates the FARMER'S 'crops' table
        String query = "UPDATE crops SET grade = ?, status = ? WHERE crop_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, grade);
            stmt.setString(2, status); // e.g. 'Processing'
            stmt.setInt(3, batchId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean createProcessedLot(int batchId, String packageType, int quantity) {
        // 1. Update 'crops' status to 'Packaged'
        // 2. Create 'ProcessedLot'
        String updateQ = "UPDATE crops SET status = 'Packaged' WHERE crop_id = ?";
        String insertQ = "INSERT INTO ProcessedLot (Crop_id, Date, Status, PackageType, TotalBoxes) VALUES (?, CURDATE(), 'Packaged', ?, ?)";
        
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement up = connection.prepareStatement(updateQ);
                 PreparedStatement in = connection.prepareStatement(insertQ)) {
                
                up.setInt(1, batchId);
                up.executeUpdate();

                in.setInt(1, batchId);
                in.setString(2, packageType);
                in.setInt(3, quantity);
                in.executeUpdate();
                
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // --- SHIPMENT MANAGEMENT (Uses 'orders' table) ---
    public ObservableList<Shipment> getPendingShipments() {
        ObservableList<Shipment> list = FXCollections.observableArrayList();
        // Only get orders that the Farmer has 'Confirmed'
        String query = "SELECT o.order_id, c.first_name, c.last_name, o.status, o.order_date " +
                       "FROM orders o " +
                       "JOIN customers c ON o.customer_id = c.customer_id " +
                       "WHERE o.status = 'Confirmed'";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                list.add(new Shipment(
                    rs.getInt("order_id"),
                    fullName,
                    rs.getString("status"),
                    rs.getString("order_date")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean confirmShipment(int orderId, String trackingNum) {
        // 1. Update FARMER'S order status to 'Shipped'
        // 2. Create entry in Factory's Shipment table
        String updateQ = "UPDATE orders SET status = 'Shipped' WHERE order_id = ?";
        String insertQ = "INSERT INTO Shipment (Order_id, TrackingNumber, ShipmentDate, Carrier) VALUES (?, ?, CURDATE(), 'Factory Fleet')";

        try {
            connection.setAutoCommit(false);
            try (PreparedStatement up = connection.prepareStatement(updateQ);
                 PreparedStatement in = connection.prepareStatement(insertQ)) {
                
                up.setInt(1, orderId);
                up.executeUpdate();

                in.setInt(1, orderId);
                in.setString(2, trackingNum);
                in.executeUpdate();
                
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // --- INVENTORY (Uses ProcessedLot) ---
    public ObservableList<InventoryLot> getPackagedBatches() {
        ObservableList<InventoryLot> list = FXCollections.observableArrayList();
        String query = "SELECT pl.P_id, pl.Crop_id, c.crop_name, c.grade, pl.PackageType, pl.TotalBoxes " +
                       "FROM ProcessedLot pl " +
                       "JOIN crops c ON pl.Crop_id = c.crop_id " +
                       "WHERE pl.WarehouseLocation IS NULL"; // Not yet stored
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new InventoryLot(
                    rs.getInt("P_id"),
                    rs.getInt("Crop_id"),
                    rs.getString("crop_name"),
                    rs.getString("grade"),
                    rs.getString("PackageType"),
                    rs.getInt("TotalBoxes")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public boolean storeBatchInInventory(int lotId, String location, String conditions) {
        String query = "UPDATE ProcessedLot SET WarehouseLocation = ?, StorageConditions = ? WHERE P_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, location);
            stmt.setString(2, conditions);
            stmt.setInt(3, lotId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // --- REPORTING ---
    public ResultSet getReportData(String reportType) {
        String query;
        if (reportType.equals("Inventory Stock")) {
             query = "SELECT WarehouseLocation, PackageType, SUM(TotalBoxes) as Total " +
                     "FROM ProcessedLot WHERE WarehouseLocation IS NOT NULL GROUP BY WarehouseLocation, PackageType";
        } else {
             query = "SELECT status, count(*) as count FROM crops GROUP BY status";
        }
        try {
            return connection.prepareStatement(query).executeQuery();
        } catch (SQLException e) { return null; }
    }
}