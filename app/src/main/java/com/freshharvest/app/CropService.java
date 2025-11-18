package com.freshharvest.app;


import com.freshharvest.app.Crop;
import com.freshharvest.dao.CropDAO;

import java.time.LocalDate;
import java.util.List;

public class CropService {
    private CropDAO cropDAO;
    
    public CropService() {
        this.cropDAO = new CropDAO();
    }
    
    public List<Crop> getAllCrops() {
        try {
            System.out.println("🔍 CropService: Fetching all crops");
            List<Crop> crops = cropDAO.findAll();
            System.out.println("📦 CropService: Retrieved " + crops.size() + " crops");
            return crops;
        } catch (Exception e) {
            System.err.println("❌ CropService error getting crops: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    public boolean addCrop(Crop crop) {
        try {
            System.out.println("➕ CropService: Adding new crop: " + crop.getName());
            
            // Set default values if needed
            if (crop.getStatus() == null || crop.getStatus().isEmpty()) {
                crop.setStatus("Planted");
            }
            if (crop.getFarmerId() == 0) {
                crop.setFarmerId(1); // Default farmer ID
            }
            
            boolean success = cropDAO.save(crop);
            System.out.println(success ? "✅ Crop added successfully" : "❌ Failed to add crop");
            return success;
            
        } catch (Exception e) {
            System.err.println("❌ CropService error adding crop: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateCrop(Crop crop) {
        try {
            System.out.println("🔄 CropService: Updating crop ID: " + crop.getId());
             boolean success = cropDAO.update(crop);
           // boolean success=true;
            System.out.println(success ? "✅ Crop updated successfully" : "❌ Failed to update crop");
            return success;
        } catch (Exception e) {
            System.err.println("❌ CropService error updating crop: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteCrop(int cropId) {
        try {
            System.out.println("🗑️ CropService: Deleting crop ID: " + cropId);
            boolean success = cropDAO.delete(cropId);
            System.out.println(success ? "✅ Crop deleted successfully" : "❌ Failed to delete crop");
            return success;
        } catch (Exception e) {
            System.err.println("❌ CropService error deleting crop: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public int getCropsCount() {
        try {
            int count = cropDAO.getCount();
            System.out.println("📊 CropService: Total crops count: " + count);
            return count;
        } catch (Exception e) {
            System.err.println("❌ CropService error getting count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
 public boolean sendToFactory(Crop crop) {
        try {
            // Validate crop data
            if (crop == null) {
                throw new IllegalArgumentException("Crop cannot be null");
            }
            
            if (crop.getName() == null || crop.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Crop name is required");
            }
            
            if (crop.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
            
            // Set factory processing details
            crop.setSentToFactoryDate(LocalDate.now());
            crop.setFactoryStatus("PENDING");
            crop.setStatus("SENT_TO_FACTORY");
            
            // Save to database (this would create a factory processing request)
            return cropDAO.saveFactoryRequest(crop);
            
        } catch (Exception e) {
            System.err.println("Service error sending crop to factory: " + e.getMessage());
            throw new RuntimeException("Failed to send crop to factory: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get crops sent to factory
     */
    public List<Crop> getFactoryCrops() {
        try {
            return cropDAO.findByFactoryStatus();
        } catch (Exception e) {
            System.err.println("Service error getting factory crops: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get factory processing status
     */
    public String getFactoryStatus(int cropId) {
        /*
        try {
            return cropDAO.getFactoryStatus(cropId, getCurrentFarmerId());
        } catch (Exception e) {
            System.err.println("Service error getting factory status: " + e.getMessage());
            return "UNKNOWN";
        }
            */
            return " ";
    }
    
    private int getCurrentFarmerId() {
        // Get current logged-in farmer ID
        return 1; // Default for testing
    }
    
    public Crop getCropById(int cropId) {
        return cropDAO.findAll().stream()
                .filter(crop -> crop.getId() == cropId)
                .findFirst()
                .orElse(null);
    }
}