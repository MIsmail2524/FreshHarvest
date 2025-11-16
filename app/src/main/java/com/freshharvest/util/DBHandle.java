package com.freshharvest.util;

import com.freshharvest.model.Batch;
import com.freshharvest.model.InventoryLot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBHandle {

    private static DBHandle instance;
    private Connection connection;

    // !! IMPORTANT !!
    // Update these details for your MySQL server
    private static final String URL = "jdbc:mysql://localhost:3306/freshharvest_db"; // Change 'freshharvest_db' if your DB has a different name
    private static final String USER = "root"; // Your MySQL username
    private static final String PASSWORD = "123"; // Your MySQL password

    private DBHandle() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }

    public static synchronized DBHandle getInstance() {
        if (instance == null) {
            instance = new DBHandle();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    // --- FactoryDashboardController Methods ---

    public Map<String, Integer> getBatchStatusCounts() {
        Map<String, Integer> counts = new HashMap<>();
        String query = "SELECT Status, COUNT(*) AS count FROM Batch GROUP BY Status";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                counts.put(rs.getString("Status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counts;
    }

    // --- BatchManagementController Methods ---

    public ObservableList<Batch> getAllBatches() {
        ObservableList<Batch> batchList = FXCollections.observableArrayList();
        // Query joins Batch and Crop tables to get CropName
        String query = "SELECT b.B_id, c.CropName, b.Date, b.Status, b.Grade " +
                       "FROM Batch b JOIN Crop c ON b.Crop_id = c.C_id";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                batchList.add(new Batch(
                        rs.getInt("B_id"),
                        rs.getString("CropName"),
                        rs.getString("Date"),
                        rs.getString("Status"),
                        rs.getString("Grade")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return batchList;
    }

    public boolean updateBatchGradeAndStatus(int batchId, String grade, String status) {
        // Implements Use Case 5: Assigns grades (A/B/C) and Status (Accepted, Rework, Rejected)
        String query = "UPDATE Batch SET Grade = ?, Status = ? WHERE B_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, grade);
            stmt.setString(2, status);
            stmt.setInt(3, batchId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createProcessedLot(int batchId, String packageType, int quantity) {
        // Implements Use Case 6: Package & Label Batch
        // 1. Update the original batch status to 'Packaged'
        String updateBatchQuery = "UPDATE Batch SET Status = 'Packaged' WHERE B_id = ?";
        
        // 2. Create the new ProcessedLot record
        String createLotQuery = "INSERT INTO ProcessedLot (Batch_id, Date, Status, PackageType, TotalBoxes, Label_QR_Code) " +
                                "VALUES (?, CURDATE(), 'Packaged', ?, ?, ?)";
        
        try (PreparedStatement updateStmt = connection.prepareStatement(updateBatchQuery);
             PreparedStatement createStmt = connection.prepareStatement(createLotQuery)) {
            
            // Transaction-like manual control
            connection.setAutoCommit(false);

            // Step 1
            updateStmt.setInt(1, batchId);
            updateStmt.executeUpdate();

            // Step 2
            String qrCode = "QR_CODE_FOR_BATCH_" + batchId; // Placeholder for QR generation
            createStmt.setInt(1, batchId);
            createStmt.setString(2, packageType);
            createStmt.setInt(3, quantity); // Assuming quantity is total boxes
            createStmt.setString(4, qrCode);
            createStmt.executeUpdate();

            connection.commit(); // Commit both changes
            connection.setAutoCommit(true); // Reset auto-commit
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }


    // --- InventoryController Methods ---

    public ObservableList<InventoryLot> getPackagedBatches() {
        ObservableList<InventoryLot> lotList = FXCollections.observableArrayList();
        String query = "SELECT pl.P_id, pl.Batch_id, c.CropName, b.Grade, pl.PackageType, pl.TotalBoxes " +
                       "FROM ProcessedLot pl " +
                       "JOIN Batch b ON pl.Batch_id = b.B_id " +
                       "JOIN Crop c ON b.Crop_id = c.C_id " +
                       "WHERE pl.Status = 'Packaged'"; // Only get lots not yet in inventory
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lotList.add(new InventoryLot(
                        rs.getInt("P_id"),
                        rs.getInt("Batch_id"),
                        rs.getString("CropName"),
                        rs.getString("Grade"),
                        rs.getString("PackageType"),
                        rs.getInt("TotalBoxes")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lotList;
    }

    public boolean storeBatchInInventory(int lotId, String location, String conditions) {
        // Implements Use Case 7: Update Inventory & Storage
        // 1. Create the Inventory record
        String createInventoryQuery = "INSERT INTO Inventory (Lot_id, StockAmount, DateStored, WarehouseLocation, StorageConditions) " +
                                     "SELECT ?, TotalBoxes, CURDATE(), ?, ? FROM ProcessedLot WHERE P_id = ?";
        
        // 2. Update the ProcessedLot status to 'Stored'
        String updateLotQuery = "UPDATE ProcessedLot SET Status = 'Stored' WHERE P_id = ?";

        try (PreparedStatement createStmt = connection.prepareStatement(createInventoryQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateLotQuery)) {
            
            connection.setAutoCommit(false);

            // Step 1
            createStmt.setInt(1, lotId);
            createStmt.setString(2, location);
            createStmt.setString(3, conditions);
            createStmt.setInt(4, lotId);
            createStmt.executeUpdate();

            // Step 2
            updateStmt.setInt(1, lotId);
            updateStmt.executeUpdate();
            
            connection.commit();
            connection.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    // --- ReportController Methods ---

    public ResultSet getReportData(String reportType) {
        // Implements Use Case 12: Generate Reports
        String query;
        if (reportType.equals("Inventory Stock")) {
            query = "SELECT i.WarehouseLocation, c.CropName, SUM(i.StockAmount) AS TotalStock " +
                    "FROM Inventory i " +
                    "JOIN ProcessedLot pl ON i.Lot_id = pl.P_id " +
                    "JOIN Batch b ON pl.Batch_id = b.B_id " +
                    "JOIN Crop c ON b.Crop_id = c.C_id " +
                    "GROUP BY i.WarehouseLocation, c.CropName";
        } else {
            // Default/Fallback query (e.g., Batch Status)
            query = "SELECT Status, COUNT(*) AS Count FROM Batch GROUP BY Status";
        }

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}