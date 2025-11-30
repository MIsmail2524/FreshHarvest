package com.freshharvest.dao;

import com.freshharvest.app.Factory_Manager.Batch;
import com.freshharvest.app.Factory_Manager.InventoryLot;
import com.freshharvest.app.Factory_Manager.Shipment;
import com.freshharvest.util.DBHandle;
import com.freshharvest.util.NotificationService;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FactoryDAOImpl implements FactoryDAO {
    private static final Logger LOGGER = Logger.getLogger(FactoryDAOImpl.class.getName());

    @Override
    public List<Map<String, Object>> getBatchProcessHistory(int cropId) {
        List<Map<String, Object>> processHistory = new ArrayList<>();
        String sql = "SELECT process_id, process_name, process_status, start_time, " +
                     "end_time, operator_name, notes, created_at " +
                     "FROM batch_processing " +
                     "WHERE crop_id = ? " +
                     "ORDER BY start_time DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cropId);
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    processHistory.add(row);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting batch process history for crop ID: " + cropId, e);
        }
        
        return processHistory;
    }

    @Override
    public List<Map<String, Object>> getActiveBatchProcesses() {
        List<Map<String, Object>> activeProcesses = new ArrayList<>();
        String sql = "SELECT process_id, crop_id, process_name, process_status, " +
                     "start_time, operator_name, notes, grade " +
                     "FROM batch_processing " +
                     "WHERE process_status IN ('Pending', 'In Progress') " +
                     "ORDER BY start_time ASC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                activeProcesses.add(row);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting active batch processes", e);
        }
        
        return activeProcesses;
    }

    

    @Override
    public boolean receiveBatchFromFarm(String farmName, String cropType, String productName, 
                                      Timestamp harvestDate, int quantity, String unit) {
        // First create crop entry
        String cropSql = "INSERT INTO crops (farm_name, crop_name, crop_type, harvest_date, " +
                         "received_date, status, grade, quantity, unit, created_date) " +
                         "VALUES (?, ?, ?, ?, ?, 'Received', 'Not Graded', ?, ?, ?)";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(cropSql, Statement.RETURN_GENERATED_KEYS)) {
            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            pstmt.setString(1, farmName);
            pstmt.setString(2, productName);
            pstmt.setString(3, cropType);
            pstmt.setTimestamp(4, harvestDate);
            pstmt.setTimestamp(5, now);
            pstmt.setInt(6, quantity);
            pstmt.setString(7, unit);
            pstmt.setTimestamp(8, now);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int cropId = generatedKeys.getInt(1);
                        // Start initial batch process
                        return startBatchProcess(cropId, "Initial Processing", "System");
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error receiving batch from farm", e);
            return false;
        }
        
        return false;
    }

    

    @Override
    public List<InventoryLot> getPackagedBatches() {
        return new ArrayList<>();
    }

    @Override
    public boolean storeBatchInInventory(int lotId, String location, String storageConditions) {
        return false;
    }

    @Override
    public List<InventoryLot> getAllInventory() {
        return new ArrayList<>();
    }

    @Override
    public boolean updateInventoryLocation(int lotId, String newLocation) {
        return false;
    }

    @Override
    public List<Map<String, Object>> getBatchStatusReport() {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT process_id, process_name, process_status, grade, " +
                     "start_time, end_time, operator_name, created_at " +
                     "FROM batch_processing ORDER BY process_status, created_at DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                report.add(row);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting batch status report", e);
        }
        
        return report;
    }

   
    @Override
    public List<Map<String, Object>> getShipmentReport() {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT shipment_id, customer_name, carrier, status, tracking_number, shipment_date " +
                     "FROM shipments ORDER BY shipment_date DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                report.add(row);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting shipment report", e);
        }
        
        return report;
    }

    @Override
    public List<Shipment> getPendingShipments() {
        List<Shipment> shipments = new ArrayList<>();
        String sql = "SELECT shipment_id, customer_name, carrier, status, tracking_number, shipment_date " +
                     "FROM shipments WHERE status = 'Pending' ORDER BY shipment_date";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Shipment shipment = new Shipment(
                    rs.getInt("shipment_id"),
                    rs.getString("customer_name"),
                    rs.getString("carrier"),
                    rs.getString("status"),
                    rs.getString("tracking_number"),
                    rs.getTimestamp("shipment_date")
                );
                shipments.add(shipment);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting pending shipments", e);
        }
        
        return shipments;
    }

    

    @Override
    public boolean confirmShipment(int shipmentId, String trackingNumber) {
        String sql = "UPDATE shipments SET status = 'Shipped', tracking_number = ? WHERE shipment_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, trackingNumber);
            pstmt.setInt(2, shipmentId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error confirming shipment: " + shipmentId, e);
            return false;
        }
    }

    @Override
public boolean createShipment(Shipment shipment) {
    String sql = "INSERT INTO shipments (customer_name, carrier, status, tracking_number, shipment_date, order_id) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, shipment.getCustomerName());
        pstmt.setString(2, shipment.getCarrier());
        pstmt.setString(3, shipment.getStatus());
        pstmt.setString(4, shipment.getTrackingNumber());
        pstmt.setTimestamp(5, shipment.getShipmentDate());
        
        // Handle order_id (can be null)
        if (shipment.getOrderId() != null) {
            pstmt.setInt(6, shipment.getOrderId());
        } else {
            pstmt.setNull(6, java.sql.Types.INTEGER);
        }
        
        return pstmt.executeUpdate() > 0;
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error creating shipment", e);
        return false;
    }
}

// Update getAllShipments to include order_id
@Override
public List<Shipment> getAllShipments() {
    List<Shipment> shipments = new ArrayList<>();
    String sql = "SELECT shipment_id, customer_name, carrier, status, tracking_number, " +
                 "shipment_date, order_id, created_at " +
                 "FROM shipments ORDER BY shipment_date DESC";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            Shipment shipment = new Shipment(
                rs.getInt("shipment_id"),
                rs.getString("customer_name"),
                rs.getString("carrier"),
                rs.getString("status"),
                rs.getString("tracking_number"),
                rs.getTimestamp("shipment_date")
            );
            // Set order_id if exists
            int orderId = rs.getInt("order_id");
            if (!rs.wasNull()) {
                shipment.setOrderId(orderId);
            }
            shipments.add(shipment);
        }
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error getting all shipments", e);
    }
    
    return shipments;
}

    @Override
    public boolean updateShipmentStatus(int shipmentId, String status) {
        String sql = "UPDATE shipments SET status = ? WHERE shipment_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, shipmentId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating shipment status: " + shipmentId, e);
            return false;
        }
    }

    @Override
    public boolean moveToInventory(int batchId, String location, String storageConditions) {
        // Not applicable for batch processing only
        return false;
    }

    @Override
    public boolean receiveBatchFromFarm(String farmName, String cropType, String productName,
            com.google.protobuf.Timestamp harvestDate, int quantity, String unit) {
        throw new UnsupportedOperationException("Unimplemented method 'receiveBatchFromFarm'");
    }









   
@Override
public List<Batch> getReceivedBatches() {
    List<Batch> batches = new ArrayList<>();
    String sql = "SELECT bp.*, c.crop_name " +
                 "FROM batch_processing bp " +
                 "LEFT JOIN crops c ON bp.crop_id = c.crop_id " +
                 "WHERE bp.process_status IN ('Pending', 'In Progress') " +
                 "ORDER BY bp.created_at DESC";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            batches.add(mapResultSetToBatch(rs));
        }
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error getting received batches", e);
    }
    
    return batches;
}


   @Override
public List<Batch> getBatchesForProcessing() {
    List<Batch> batches = new ArrayList<>();
    String sql = "SELECT bp.*, c.crop_name " +
                 "FROM batch_processing bp " +
                 "LEFT JOIN crops c ON bp.crop_id = c.crop_id " +
                 "WHERE bp.process_status IN ('Pending', 'In Progress') " +
                 "ORDER BY bp.start_time ASC";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            batches.add(mapResultSetToBatch(rs));
        }
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error getting batches for processing", e);
    }
    
    return batches;
}
    @Override
    public boolean startBatchProcess(int cropId, String processName, String operatorName) {
        String sql = "INSERT INTO batch_processing (crop_id, process_name, process_status, start_time, operator_name) " +
                     "VALUES (?, ?, 'In Progress', ?, ?)";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cropId);
            pstmt.setString(2, processName);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, operatorName);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error starting batch process for crop ID: " + cropId, e);
            return false;
        }
    }

   @Override
public boolean updateBatchProcessing(int processId, String grade, String status, String qualityNotes) {
    Connection conn = null;
    try {
        conn = DBHandle.getInstance().getConnection();
        conn.setAutoCommit(false); // Start transaction
        
        // First get batch details including crop_id
        String getBatchSql = "SELECT crop_id, process_name FROM batch_processing WHERE process_id = ?";
        int cropId = -1;
        String processName = "";
        
        try (PreparedStatement getStmt = conn.prepareStatement(getBatchSql)) {
            getStmt.setInt(1, processId);
            try (ResultSet rs = getStmt.executeQuery()) {
                if (rs.next()) {
                    cropId = rs.getInt("crop_id");
                    processName = rs.getString("process_name");
                }
            }
        }
        
        // Update batch processing
        String updateBatchSql = "UPDATE batch_processing SET process_status = ?, notes = ?, grade = ? WHERE process_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateBatchSql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, qualityNotes);
            pstmt.setString(3, grade);
            pstmt.setInt(4, processId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0 && "Completed".equals(status)) {
                // Set end_time and processed_date for completed batches
                String completeSql = "UPDATE batch_processing SET end_time = ?, processed_date = ? WHERE process_id = ?";
                try (PreparedStatement completeStmt = conn.prepareStatement(completeSql)) {
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    completeStmt.setTimestamp(1, now);
                    completeStmt.setTimestamp(2, now);
                    completeStmt.setInt(3, processId);
                    completeStmt.executeUpdate();
                }
                
                // Move to inventory if this is the final processing step
                if (cropId > 0 && isFinalProcessingStep(processName)) {
                    boolean inventoryMoved = moveCompletedBatchToInventory(conn, cropId, processId, grade, qualityNotes);
                    if (!inventoryMoved) {
                        conn.rollback();
                        return false;
                    }
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error updating batch processing for ID: " + processId, e);
        return false;
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing connection", e);
            }
        }
    }
}

private boolean isFinalProcessingStep(String processName) {
    // Define which processes are final and should move to inventory
    return processName.toLowerCase().contains("packaging") || 
           processName.toLowerCase().contains("final") ||
           processName.toLowerCase().contains("quality") ||
           processName.toLowerCase().contains("complete");
}private boolean moveCompletedBatchToInventory(Connection conn, int cropId, int processId, String grade, String notes) throws SQLException {
    try {
        // First get crop details - make sure farmer_id is retrieved
        String getCropSql = "SELECT crop_name, quantity, unit, farmer_id FROM crops WHERE crop_id = ?";
        String cropName = "";
        double quantity = 0;
        String unit = "";
        int farmerId = 0;
        
        try (PreparedStatement getStmt = conn.prepareStatement(getCropSql)) {
            getStmt.setInt(1, cropId);
            try (ResultSet rs = getStmt.executeQuery()) {
                if (rs.next()) {
                    cropName = rs.getString("crop_name");
                    quantity = rs.getDouble("quantity");
                    unit = rs.getString("unit");
                    farmerId = rs.getInt("farmer_id");
                    
                    // Debug logging
                    System.out.println("Retrieved farmer_id: " + farmerId + " for crop: " + cropName);
                } else {
                    throw new SQLException("Crop not found with ID: " + cropId);
                }
            }
        }
        
        // If farmer_id is 0 or invalid, set to default value 1
        if (farmerId <= 0) {
            farmerId = 1;
            System.out.println("Using default farmer_id: 1 for crop: " + cropName);
        }
        
        // Create or get product ID
        int productId = getOrCreateProduct(conn, cropName, grade);
        
        double currentStock = getCurrentStock(conn, productId);
        
        // Create inventory movement - make sure farmer_id is included
        String movementSql = "INSERT INTO inventory_movements (product_id, farmer_id, movement_type, quantity, " +
                           "previous_stock, new_stock, reason, reference_id) " +
                           "VALUES (?, ?, 'IN', ?, ?, ?, ?, ?)";
        
        try (PreparedStatement movementStmt = conn.prepareStatement(movementSql)) {
            movementStmt.setInt(1, productId);
            movementStmt.setInt(2, farmerId); // This must be set to a valid value
            movementStmt.setDouble(3, quantity);
            movementStmt.setDouble(4, currentStock);
            movementStmt.setDouble(5, currentStock + quantity);
            movementStmt.setString(6, "Batch Processing Completed");
            movementStmt.setInt(7, processId);
            
            int rowsAffected = movementStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                String updateCropSql = "UPDATE crops SET status = 'Sold', processed_date = ? WHERE crop_id = ?";
                try (PreparedStatement cropStmt = conn.prepareStatement(updateCropSql)) {
                    cropStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    cropStmt.setInt(2, cropId);
                    cropStmt.executeUpdate();
                }
                
                LOGGER.info("Successfully moved crop ID " + cropId + " to inventory");
                return true;
            }
        }
        
        return false;
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error moving completed batch to inventory for crop ID: " + cropId, e);
        throw e;
    }
}
private int getOrCreateProduct(Connection conn, String cropName, String grade) throws SQLException {
    // Try to find existing product
    String findSql = "SELECT product_id FROM products WHERE product_name = ?";
    try (PreparedStatement findStmt = conn.prepareStatement(findSql)) {
        findStmt.setString(1, cropName);
        
        try (ResultSet rs = findStmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("product_id");
            }
        }
    }
    
    // FIXED: Removed description column
    String createSql = "INSERT INTO products (product_name, grade, price, last_updated) " +
                      "VALUES (?, ?, 0.0, ?)";
    try (PreparedStatement createStmt = conn.prepareStatement(createSql, Statement.RETURN_GENERATED_KEYS)) {
        createStmt.setString(1, cropName);
        createStmt.setString(2, grade);
        createStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        
        int rowsAffected = createStmt.executeUpdate();
        if (rowsAffected > 0) {
            try (ResultSet generatedKeys = createStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
    }
    
    throw new SQLException("Failed to create or find product");
}
private double getCurrentStock(Connection conn, int productId) throws SQLException {
    String sql = "SELECT new_stock FROM inventory_movements WHERE product_id = ? ORDER BY movement_date DESC LIMIT 1";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, productId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("new_stock");
            }
        }
    }
    return 0.0; 
}
    @Override
    public boolean updateBatchProcessStatus(int processId, String status, String notes) {
        String sql = "UPDATE batch_processing SET process_status = ?, notes = ? WHERE process_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, notes);
            pstmt.setInt(3, processId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating batch process status for process ID: " + processId, e);
            return false;
        }
    }

    @Override
    public boolean completeBatchProcess(int processId, String notes) {
        String sql = "UPDATE batch_processing SET process_status = 'Completed', end_time = ?, processed_date = ?, notes = ? WHERE process_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            pstmt.setTimestamp(1, now);
            pstmt.setTimestamp(2, now);
            pstmt.setString(3, notes);
            pstmt.setInt(4, processId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error completing batch process for process ID: " + processId, e);
            return false;
        }
    }


    @Override
    public Batch getBatchById(int batchId) {
        String sql = "SELECT process_id, process_name, process_status, " +
                     "start_time, end_time, operator_name, notes, " +
                     "grade, processed_date, created_at " +
                     "FROM batch_processing " +
                     "WHERE process_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, batchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBatch(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting batch by ID: " + batchId, e);
        }
        
        return null;
    }

    @Override
    public boolean createBatch(Batch batch) {
        String sql = "INSERT INTO batch_processing (process_name, process_status, start_time, operator_name, notes, grade) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, batch.getProcessName());
            pstmt.setString(2, batch.getProcessStatus());
            pstmt.setTimestamp(3, batch.getStartTime());
            pstmt.setString(4, batch.getOperatorName());
            pstmt.setString(5, batch.getNotes());
            pstmt.setString(6, batch.getGrade());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        batch.setProcessId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating batch", e);
        }
        
        return false;
    }

    @Override
    public boolean updateBatch(Batch batch) {
        String sql = "UPDATE batch_processing SET process_name = ?, process_status = ?, " +
                     "start_time = ?, end_time = ?, operator_name = ?, notes = ?, " +
                     "grade = ?, processed_date = ? WHERE process_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, batch.getProcessName());
            pstmt.setString(2, batch.getProcessStatus());
            pstmt.setTimestamp(3, batch.getStartTime());
            pstmt.setTimestamp(4, batch.getEndTime());
            pstmt.setString(5, batch.getOperatorName());
            pstmt.setString(6, batch.getNotes());
            pstmt.setString(7, batch.getGrade());
            pstmt.setTimestamp(8, batch.getProcessedDate());
            pstmt.setInt(9, batch.getProcessId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating batch: " + batch.getProcessId(), e);
            return false;
        }
    }

    @Override
    public boolean updateBatchGradeAndStatus(int batchId, String grade, String status) {
        return updateBatchProcessing(batchId, grade, status, "");
    }

    @Override
    public boolean deleteBatch(int batchId) {
        String sql = "DELETE FROM batch_processing WHERE process_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, batchId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting batch: " + batchId, e);
            return false;
        }
    }

    @Override
    public Map<String, Integer> getBatchStatusCounts() {
        Map<String, Integer> statusCounts = new HashMap<>();
        String sql = "SELECT process_status, COUNT(*) as count FROM batch_processing GROUP BY process_status";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                statusCounts.put(rs.getString("process_status"), rs.getInt("count"));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting batch status counts", e);
        }
        
        return statusCounts;
    }

   
@Override
public List<Batch> getRecentBatches(int limit) {
    List<Batch> batches = new ArrayList<>();
    String sql = "SELECT bp.*, c.crop_name " +
                 "FROM batch_processing bp " +
                 "LEFT JOIN crops c ON bp.crop_id = c.crop_id " +
                 "ORDER BY bp.created_at DESC LIMIT ?";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, limit);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Batch batch = mapResultSetToBatch(rs);
                batches.add(batch);
            }
        }
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error getting recent batches", e);
    }
    
    return batches;
}





private String buildBatchProductionQuery(String timePeriod) {
    String baseQuery = "SELECT " +
                      "bp.process_id, " +
                      "c.crop_name, " +
                      "bp.process_status, " +
                      "bp.grade, " +
                      "bp.process_name, " +
                      "bp.start_time, " +
                      "bp.end_time, " +
                      "bp.operator_name, " +
                      "bp.processed_date " +
                      "FROM batch_processing bp " +
                      "JOIN crops c ON bp.crop_id = c.crop_id ";
    
    return baseQuery + buildTimeWhereClause("bp.start_time", timePeriod) + " ORDER BY bp.start_time DESC";
}

private String buildShipmentQuery(String timePeriod) {
    String baseQuery = "SELECT " +
                      "s.shipment_id, " +
                      "s.customer_name, " +
                      "s.carrier, " +
                      "s.status, " +
                      "s.tracking_number, " +
                      "s.shipment_date, " +
                      "s.order_id " +
                      "FROM shipments s ";
    
    return baseQuery + buildTimeWhereClause("s.shipment_date", timePeriod) + " ORDER BY s.shipment_date DESC";
}

private String buildProductionCostQuery(String timePeriod) {
    String baseQuery = "SELECT " +
                      "bp.process_id, " +
                      "c.crop_name, " +
                      "bp.process_name, " +
                      "bp.operator_name, " +
                      "TIMESTAMPDIFF(HOUR, bp.start_time, bp.end_time) as process_duration_hours, " +
                      "TIMESTAMPDIFF(HOUR, bp.start_time, bp.end_time) * 25 as estimated_labor_cost, " +
                      "TIMESTAMPDIFF(HOUR, bp.start_time, bp.end_time) * 10 as estimated_material_cost, " +
                      "(TIMESTAMPDIFF(HOUR, bp.start_time, bp.end_time) * 25) + " +
                      "(TIMESTAMPDIFF(HOUR, bp.start_time, bp.end_time) * 10) as total_estimated_cost " +
                      "FROM batch_processing bp " +
                      "JOIN crops c ON bp.crop_id = c.crop_id " +
                      "WHERE bp.process_status = 'Completed' AND bp.start_time IS NOT NULL AND bp.end_time IS NOT NULL ";
    
    return baseQuery + buildTimeWhereClause("bp.start_time", timePeriod) + " ORDER BY total_estimated_cost DESC";
}

private String buildQualityControlQuery(String timePeriod) {
    String baseQuery = "SELECT " +
                      "qc.qc_id, " +
                      "c.crop_name, " +
                      "qc.inspector_name, " +
                      "qc.inspection_date, " +
                      "qc.quality_score, " +
                      "qc.approved, " +
                      "qc.defects_found, " +
                      "qc.recommendations " +
                      "FROM quality_control qc " +
                      "JOIN crops c ON qc.crop_id = c.crop_id ";
    
    return baseQuery + buildTimeWhereClause("qc.inspection_date", timePeriod) + " ORDER BY qc.inspection_date DESC";
}

private String buildProcessEfficiencyQuery(String timePeriod) {
    return "SELECT " +
           "process_name, " +
           "COUNT(*) as total_processes, " +
           "SUM(CASE WHEN process_status = 'Completed' THEN 1 ELSE 0 END) as completed_processes, " +
           "AVG(TIMESTAMPDIFF(HOUR, start_time, end_time)) as avg_duration_hours, " +
           "(SUM(CASE WHEN process_status = 'Completed' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) as success_rate, " +
           "(SELECT operator_name FROM batch_processing bp2 " +
           " WHERE bp2.process_name = bp.process_name " +
           " GROUP BY operator_name ORDER BY COUNT(*) DESC LIMIT 1) as most_common_operator " +
           "FROM batch_processing bp " +
           buildTimeWhereClause("start_time", timePeriod) +
           "GROUP BY process_name " +
           "ORDER BY success_rate DESC";
}

private String buildFarmSupplyQuery(String timePeriod) {
    String baseQuery = "SELECT " +
                      "f.name as farmer_name, " +
                      "c.crop_name, " +
                      "COUNT(im.movement_id) as total_movements, " +
                      "SUM(im.quantity) as total_quantity, " +
                      "AVG(qc.quality_score) as avg_quality_score, " +
                      "MAX(im.movement_date) as last_delivery_date " +
                      "FROM inventory_movements im " +
                      "JOIN farmers f ON im.farmer_id = f.farmer_id " +
                      "JOIN products p ON im.product_id = p.product_id " +
                      "JOIN crops c ON p.crop_id = c.crop_id " +
                      "LEFT JOIN quality_control qc ON c.crop_id = qc.crop_id " +
                      "WHERE im.movement_type = 'IN' ";
    
    String fullQuery = baseQuery + buildTimeWhereClause("im.movement_date", timePeriod) + 
                      "GROUP BY f.farmer_id, f.name, c.crop_id, c.crop_name " +
                      "ORDER BY total_quantity DESC";
    
    return fullQuery;
}
@Override
public int getPendingShipmentCount() {
    String sql = "SELECT COUNT(*) as count FROM shipments WHERE status = 'Pending'";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        if (rs.next()) {
            return rs.getInt("count");
        }
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error getting pending shipment count", e);
    }
    
    return 0;
}

@Override
public int getTotalInventoryQuantity() {
    String sql = "SELECT COALESCE(SUM(current_quantity), 0) as total_quantity FROM inventory_locations";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        if (rs.next()) {
            return rs.getInt("total_quantity");
        }
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error getting total inventory quantity", e);
    }
    
    return 0;
}

@Override
public Map<String, Integer> getInventoryByProduct() {
    Map<String, Integer> inventoryByProduct = new HashMap<>();
    
    String sql = "SELECT location_name, current_quantity FROM inventory_locations WHERE current_quantity > 0";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            inventoryByProduct.put(rs.getString("location_name"), rs.getInt("current_quantity"));
        }
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error getting inventory by product", e);
    }
    
    return inventoryByProduct;
}






















    // Safe value getter methods
    private String getStringSafe(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return (value == null || "null".equalsIgnoreCase(value)) ? "" : value;
    }

    private int getIntSafe(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? 0 : value;
    }

    private double getDoubleSafe(ResultSet rs, String columnName) throws SQLException {
        double value = rs.getDouble(columnName);
        return rs.wasNull() ? 0.0 : value;
    }

    private boolean getBooleanSafe(ResultSet rs, String columnName) throws SQLException {
        boolean value = rs.getBoolean(columnName);
        return !rs.wasNull() && value;
    }

  
private Batch mapResultSetToBatch(ResultSet rs) throws SQLException {
    Batch batch = new Batch();
    batch.setProcessId(rs.getInt("process_id"));
    
    int cropId = rs.getInt("crop_id");
    if (!rs.wasNull()) {
        batch.setCropId(cropId);
    } else {
        batch.setCropId(null);
    }
    
    try {
        String cropName = rs.getString("crop_name");
        batch.setCropName(cropName != null ? cropName : "No Crop");
    } catch (SQLException e) {
        batch.setCropName("Unknown");
    }
    
    batch.setProcessName(getStringSafe(rs, "process_name"));
    batch.setProcessStatus(getStringSafe(rs, "process_status"));
    batch.setStartTime(rs.getTimestamp("start_time"));
    batch.setEndTime(rs.getTimestamp("end_time"));
    batch.setOperatorName(getStringSafe(rs, "operator_name"));
    batch.setNotes(getStringSafe(rs, "notes"));
    batch.setGrade(getStringSafe(rs, "grade"));
    batch.setProcessedDate(rs.getTimestamp("processed_date"));
    batch.setCreatedAt(rs.getTimestamp("created_at"));
    
    return batch;
}
    @Override
    public List<Map<String, Object>> getBatchProductionReport(String timePeriod) {
        List<Map<String, Object>> reportData = new ArrayList<>();
        String query = "SELECT " +
                      "bp.process_id, " +
                      "c.crop_name, " +
                      "COALESCE(bp.process_status, 'Unknown') as process_status, " +
                      "COALESCE(bp.grade, 'Not Graded') as grade, " +
                      "COALESCE(bp.process_name, 'Unnamed Process') as process_name, " +
                      "bp.start_time, " +
                      "bp.end_time, " +
                      "COALESCE(bp.operator_name, 'Unknown') as operator_name, " +
                      "bp.processed_date, " +
                      "COALESCE(bp.estimated_cost, 0) as estimated_cost " +
                      "FROM batch_processing bp " +
                      "LEFT JOIN crops c ON bp.crop_id = c.crop_id " +
                      buildTimeWhereClause("bp.start_time", timePeriod) + 
                      " ORDER BY bp.start_time DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("process_id", getIntSafe(rs, "process_id"));
                row.put("crop_name", getStringSafe(rs, "crop_name"));
                row.put("process_status", getStringSafe(rs, "process_status"));
                row.put("grade", getStringSafe(rs, "grade"));
                row.put("process_name", getStringSafe(rs, "process_name"));
                row.put("start_time", rs.getTimestamp("start_time"));
                row.put("end_time", rs.getTimestamp("end_time"));
                row.put("operator_name", getStringSafe(rs, "operator_name"));
                row.put("processed_date", rs.getTimestamp("processed_date"));
                row.put("estimated_cost", getDoubleSafe(rs, "estimated_cost"));
                reportData.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error getting batch production report: " + e.getMessage());
            e.printStackTrace();
        }
        return reportData;
    }

    @Override
    public List<Map<String, Object>> getInventoryStockReport() {
        List<Map<String, Object>> reportData = new ArrayList<>();
        String query = "SELECT " +
                      "il.location_id, " +
                      "COALESCE(il.location_name, 'Unknown Location') as location_name, " +
                      "COALESCE(il.capacity, 0) as capacity, " +
                      "COALESCE(il.current_quantity, 0) as current_quantity, " +
                      "COALESCE(il.temperature_range, 'Not Set') as temperature_range, " +
                      "COALESCE(il.humidity_range, 'Not Set') as humidity_range, " +
                      "COALESCE(il.is_available, 1) as is_available " +
                      "FROM inventory_locations il " +
                      "ORDER BY il.current_quantity DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("location_id", getIntSafe(rs, "location_id"));
                row.put("location_name", getStringSafe(rs, "location_name"));
                row.put("capacity", getIntSafe(rs, "capacity"));
                row.put("current_quantity", getIntSafe(rs, "current_quantity"));
                row.put("temperature_range", getStringSafe(rs, "temperature_range"));
                row.put("humidity_range", getStringSafe(rs, "humidity_range"));
                row.put("is_available", getBooleanSafe(rs, "is_available"));
                
                int capacity = getIntSafe(rs, "capacity");
                int currentQuantity = getIntSafe(rs, "current_quantity");
                double utilization = (capacity > 0) ? (currentQuantity * 100.0) / capacity : 0;
                row.put("utilization_percentage", Math.round(utilization * 100.0) / 100.0);
                
                reportData.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error getting inventory stock report: " + e.getMessage());
            e.printStackTrace();
        }
        return reportData;
    }

    @Override
    public List<Map<String, Object>> getShipmentReport(String timePeriod) {
        List<Map<String, Object>> reportData = new ArrayList<>();
        String query = "SELECT " +
                      "s.shipment_id, " +
                      "COALESCE(s.customer_name, 'Unknown Customer') as customer_name, " +
                      "COALESCE(s.carrier, 'Unknown Carrier') as carrier, " +
                      "COALESCE(s.status, 'Pending') as status, " +
                      "COALESCE(s.tracking_number, 'No Tracking') as tracking_number, " +
                      "s.shipment_date, " +
                      "s.delivery_date, " +
                      "COALESCE(s.total_weight, 0) as total_weight, " +
                      "COALESCE(s.order_id, 0) as order_id " +
                      "FROM shipments s " +
                      buildTimeWhereClause("s.shipment_date", timePeriod) + 
                      " ORDER BY s.shipment_date DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("shipment_id", getIntSafe(rs, "shipment_id"));
                row.put("customer_name", getStringSafe(rs, "customer_name"));
                row.put("carrier", getStringSafe(rs, "carrier"));
                row.put("status", getStringSafe(rs, "status"));
                row.put("tracking_number", getStringSafe(rs, "tracking_number"));
                row.put("shipment_date", rs.getTimestamp("shipment_date"));
                row.put("delivery_date", rs.getTimestamp("delivery_date"));
                row.put("total_weight", getDoubleSafe(rs, "total_weight"));
                row.put("order_id", getIntSafe(rs, "order_id"));
                reportData.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error getting shipment report: " + e.getMessage());
            e.printStackTrace();
        }
        return reportData;
    }

    @Override
    public List<Map<String, Object>> getProductionCostReport(String timePeriod) {
        List<Map<String, Object>> reportData = new ArrayList<>();
        String query = "SELECT " +
                      "bp.process_id, " +
                      "COALESCE(c.crop_name, 'Unknown Crop') as crop_name, " +
                      "COALESCE(bp.process_name, 'Unnamed Process') as process_name, " +
                      "COALESCE(bp.operator_name, 'Unknown Operator') as operator_name, " +
                      "COALESCE(bp.estimated_cost, 0) as total_cost, " +
                      "COALESCE(bp.labor_hours * 25, 0) as labor_cost, " +
                      "COALESCE(bp.material_cost, 0) as material_cost, " +
                      "COALESCE(bp.estimated_cost * 1.3, 0) as revenue, " +
                      "COALESCE((bp.estimated_cost * 1.3) - bp.estimated_cost, 0) as profit " +
                      "FROM batch_processing bp " +
                      "LEFT JOIN crops c ON bp.crop_id = c.crop_id " +
                      "WHERE bp.process_status = 'Completed' " +
                      buildTimeWhereClause("bp.start_time", timePeriod, "AND") + 
                      " ORDER BY total_cost DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("process_id", getIntSafe(rs, "process_id"));
                row.put("crop_name", getStringSafe(rs, "crop_name"));
                row.put("process_name", getStringSafe(rs, "process_name"));
                row.put("operator_name", getStringSafe(rs, "operator_name"));
                row.put("total_cost", getDoubleSafe(rs, "total_cost"));
                row.put("labor_cost", getDoubleSafe(rs, "labor_cost"));
                row.put("material_cost", getDoubleSafe(rs, "material_cost"));
                row.put("revenue", getDoubleSafe(rs, "revenue"));
                row.put("profit", getDoubleSafe(rs, "profit"));
                reportData.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error getting production cost report: " + e.getMessage());
            e.printStackTrace();
        }
        return reportData;
    }

    @Override
    public List<Map<String, Object>> getQualityControlReport(String timePeriod) {
        List<Map<String, Object>> reportData = new ArrayList<>();
        String query = "SELECT " +
                      "qc.qc_id, " +
                      "COALESCE(c.crop_name, 'Unknown Crop') as crop_name, " +
                      "COALESCE(qc.inspector_name, 'Unknown Inspector') as inspector_name, " +
                      "qc.inspection_date, " +
                      "COALESCE(qc.quality_score, 0) as quality_score, " +
                      "COALESCE(qc.approved, 0) as approved, " +
                      "COALESCE(qc.defects_found, 'No defects found') as defects_found, " +
                      "COALESCE(qc.recommendations, 'No recommendations') as recommendations, " +
                      "COALESCE(qc.grade, 'Not Graded') as grade " +
                      "FROM quality_control qc " +
                      "LEFT JOIN crops c ON qc.crop_id = c.crop_id " +
                      buildTimeWhereClause("qc.inspection_date", timePeriod) + 
                      " ORDER BY qc.inspection_date DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("qc_id", getIntSafe(rs, "qc_id"));
                row.put("crop_name", getStringSafe(rs, "crop_name"));
                row.put("inspector_name", getStringSafe(rs, "inspector_name"));
                row.put("inspection_date", rs.getTimestamp("inspection_date"));
                row.put("quality_score", getIntSafe(rs, "quality_score"));
                row.put("approved", getBooleanSafe(rs, "approved"));
                row.put("defects_found", getStringSafe(rs, "defects_found"));
                row.put("recommendations", getStringSafe(rs, "recommendations"));
                row.put("grade", getStringSafe(rs, "grade"));
                reportData.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error getting quality control report: " + e.getMessage());
            e.printStackTrace();
        }
        return reportData;
    }

    @Override
    public List<Map<String, Object>> getBatchStatusSummary() {
        List<Map<String, Object>> reportData = new ArrayList<>();
        String query = "SELECT " +
                      "COALESCE(process_status, 'Unknown') as status, " +
                      "COUNT(*) as process_count " +
                      "FROM batch_processing " +
                      "GROUP BY process_status " +
                      "ORDER BY process_count DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("status", getStringSafe(rs, "status"));
                row.put("process_count", getIntSafe(rs, "process_count"));
                reportData.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error getting batch status summary: " + e.getMessage());
            e.printStackTrace();
        }
        return reportData;
    }

    @Override
    public List<Map<String, Object>> getProcessEfficiencyReport(String timePeriod) {
        List<Map<String, Object>> reportData = new ArrayList<>();
        String query = "SELECT " +
                      "COALESCE(process_name, 'Unknown Process') as process_name, " +
                      "COUNT(*) as total_processes, " +
                      "SUM(CASE WHEN process_status = 'Completed' THEN 1 ELSE 0 END) as completed_processes, " +
                      "COALESCE(AVG(TIMESTAMPDIFF(HOUR, start_time, end_time)), 0) as avg_duration_hours, " +
                      "COALESCE((SUM(CASE WHEN process_status = 'Completed' THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(*), 0)), 0) as success_rate " +
                      "FROM batch_processing " +
                      buildTimeWhereClause("start_time", timePeriod) +
                      "GROUP BY process_name " +
                      "ORDER BY success_rate DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("process_name", getStringSafe(rs, "process_name"));
                row.put("total_processes", getIntSafe(rs, "total_processes"));
                row.put("completed_processes", getIntSafe(rs, "completed_processes"));
                row.put("avg_duration_hours", getDoubleSafe(rs, "avg_duration_hours"));
                row.put("success_rate", getDoubleSafe(rs, "success_rate"));
                reportData.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error getting process efficiency report: " + e.getMessage());
            e.printStackTrace();
        }
        return reportData;
    }

    @Override
    public List<Map<String, Object>> getFarmSupplyReport(String timePeriod) {
        List<Map<String, Object>> reportData = new ArrayList<>();
        String query = "SELECT " +
                      "COALESCE(supplier_name, 'Unknown Supplier') as supplier_name, " +
                      "COALESCE(crop_name, 'Unknown Crop') as crop_name, " +
                      "COALESCE(total_quantity, 0) as total_quantity, " +
                      "COALESCE(delivery_count, 0) as delivery_count, " +
                      "last_delivery_date " +
                      "FROM farm_supply_view " +
                      buildTimeWhereClause("last_delivery_date", timePeriod) + 
                      " ORDER BY total_quantity DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("supplier_name", getStringSafe(rs, "supplier_name"));
                row.put("crop_name", getStringSafe(rs, "crop_name"));
                row.put("total_quantity", getDoubleSafe(rs, "total_quantity"));
                row.put("delivery_count", getIntSafe(rs, "delivery_count"));
                row.put("last_delivery_date", rs.getTimestamp("last_delivery_date"));
                reportData.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error getting farm supply report: " + e.getMessage());
            e.printStackTrace();
        }
        return reportData;
    }

    private String buildTimeWhereClause(String dateColumn, String timePeriod) {
        return buildTimeWhereClause(dateColumn, timePeriod, "WHERE");
    }

    private String buildTimeWhereClause(String dateColumn, String timePeriod, String clauseType) {
        if ("All Time".equals(timePeriod)) {
            return "";
        }
        
        String whereClause = " " + clauseType + " ";
        
        switch (timePeriod) {
            case "Today":
                return whereClause + "DATE(" + dateColumn + ") = CURDATE()";
            case "This Week":
                return whereClause + "YEARWEEK(" + dateColumn + ") = YEARWEEK(CURDATE())";
            case "This Month":
                return whereClause + "MONTH(" + dateColumn + ") = MONTH(CURDATE()) AND YEAR(" + dateColumn + ") = YEAR(CURDATE())";
            case "Last Month":
                return whereClause + "MONTH(" + dateColumn + ") = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) AND YEAR(" + dateColumn + ") = YEAR(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))";
            case "This Quarter":
                return whereClause + "QUARTER(" + dateColumn + ") = QUARTER(CURDATE()) AND YEAR(" + dateColumn + ") = YEAR(CURDATE())";
            case "This Year":
                return whereClause + "YEAR(" + dateColumn + ") = YEAR(CURDATE())";
            default:
                return "";
        }
    }

   @Override
public List<Batch> getAllBatches() {
    List<Batch> batches = new ArrayList<>();
    String query = "SELECT bp.*, c.crop_name " +
                   "FROM batch_processing bp " +
                   "LEFT JOIN crops c ON bp.crop_id = c.crop_id " +
                   "ORDER BY bp.created_at DESC";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            batches.add(mapResultSetToBatch(rs));
        }
    } catch (SQLException e) {
        System.err.println("Error getting all batches: " + e.getMessage());
        e.printStackTrace();
    }
    return batches;
}

   @Override
public List<Batch> getBatchesByStatus(String status) {
    List<Batch> batches = new ArrayList<>();
    String query = "SELECT bp.*, c.crop_name " +
                   "FROM batch_processing bp " +
                   "LEFT JOIN crops c ON bp.crop_id = c.crop_id " +
                   "WHERE bp.process_status = ? " +
                   "ORDER BY bp.created_at DESC";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, status);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            batches.add(mapResultSetToBatch(rs));
        }
    } catch (SQLException e) {
        System.err.println("Error getting batches by status: " + e.getMessage());
        e.printStackTrace();
    }
    return batches;
}


    @Override
public List<Map<String, Object>> getAvailableCropsForProcessing() {
    List<Map<String, Object>> crops = new ArrayList<>();
    // Removed crop_type from SELECT
    String sql = "SELECT crop_id, crop_name, quantity, unit, farm_name " +
                 "FROM crops WHERE status IN ('Harvested', 'Ready for Harvest') " +
                 "ORDER BY crop_name";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            Map<String, Object> crop = new HashMap<>();
            crop.put("crop_id", rs.getInt("crop_id"));
            crop.put("crop_name", rs.getString("crop_name"));
            crop.put("quantity", rs.getDouble("quantity"));
            crop.put("unit", rs.getString("unit"));
            crop.put("farm_name", rs.getString("farm_name"));
            crops.add(crop);
        }
        
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error getting available crops for processing", e);
        return new ArrayList<>();
    }
    
    return crops;
}

}
