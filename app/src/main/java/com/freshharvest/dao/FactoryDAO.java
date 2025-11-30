package com.freshharvest.dao;

import com.freshharvest.app.Factory_Manager.Batch;
import com.freshharvest.app.Factory_Manager.InventoryLot;
import com.freshharvest.app.Factory_Manager.Shipment;
import com.google.protobuf.Timestamp;

import java.util.List;
import java.util.Map;

public interface FactoryDAO {
    // Batch operations
    List<Batch> getAllBatches();
    List<Batch> getBatchesByStatus(String status);
    Batch getBatchById(int batchId);
    boolean createBatch(Batch batch);
    boolean updateBatch(Batch batch);
    boolean updateBatchGradeAndStatus(int batchId, String grade, String status);
    boolean deleteBatch(int batchId);
    
    // Dashboard operations
    Map<String, Integer> getBatchStatusCounts();
    int getPendingShipmentCount();
    List<Batch> getRecentBatches(int limit);
    int getTotalInventoryQuantity();
    Map<String, Integer> getInventoryByProduct();
    
    // Inventory operations
    List<InventoryLot> getPackagedBatches();
    boolean storeBatchInInventory(int lotId, String location, String storageConditions);
    List<InventoryLot> getAllInventory();
    boolean updateInventoryLocation(int lotId, String newLocation);
    
    // Report operations
    List<Map<String, Object>> getBatchStatusReport();
    List<Map<String, Object>> getInventoryStockReport();
    List<Map<String, Object>> getShipmentReport();
    
    // Shipment operations
    List<Shipment> getPendingShipments();
    List<Shipment> getAllShipments();
    boolean confirmShipment(int shipmentId, String trackingNumber);
    boolean createShipment(Shipment shipment);
    boolean updateShipmentStatus(int shipmentId, String status);

    // Add to existing FactoryDAO interface
List<Batch> getReceivedBatches();
List<Batch> getBatchesForProcessing();
boolean receiveBatchFromFarm(String farmName, String cropType, String productName, 
                           Timestamp harvestDate, int quantity, String unit);
boolean updateBatchProcessing(int batchId, String grade, String status, String qualityNotes);
boolean moveToInventory(int batchId, String location, String storageConditions);
boolean receiveBatchFromFarm(String farmName, String cropType, String productName, java.sql.Timestamp harvestDate,
        int quantity, String unit);
        // Add these methods to your FactoryDAO interface
boolean startBatchProcess(int cropId, String processName, String operatorName);
boolean updateBatchProcessStatus(int processId, String status, String notes);
boolean completeBatchProcess(int processId, String notes);
List<Map<String, Object>> getBatchProcessHistory(int cropId);
List<Map<String, Object>> getActiveBatchProcesses();


List<Map<String, Object>> getBatchProductionReport(String timePeriod);
List<Map<String, Object>> getShipmentReport(String timePeriod);
List<Map<String, Object>> getProductionCostReport(String timePeriod);
List<Map<String, Object>> getQualityControlReport(String timePeriod);
List<Map<String, Object>> getBatchStatusSummary();
List<Map<String, Object>> getProcessEfficiencyReport(String timePeriod);
List<Map<String, Object>> getFarmSupplyReport(String timePeriod);

List<Map<String, Object>> getAvailableCropsForProcessing();
}