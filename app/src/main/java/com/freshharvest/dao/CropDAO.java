package com.freshharvest.dao;

import com.freshharvest.app.Crop;
import com.freshharvest.util.DBHandle;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CropDAO {
    
    public List<Crop> findAll() {
        List<Crop> crops = new ArrayList<>();
        String sql = "SELECT * FROM crops WHERE farmer_id = ?"; // Fixed: Added WHERE clause
        
        System.out.println("🔍 CropDAO: Fetching crops for farmer ID: " + getCurrentFarmerId());
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, getCurrentFarmerId());
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                Crop crop = mapResultSetToCrop(rs);
                crops.add(crop);
                System.out.println(" Found crop: " + crop.getName() + " | Type: " + crop.getType());
            }
            
            System.out.println(" Total crops retrieved: " + count);
            
        } catch (SQLException e) {
            System.err.println(" CropDAO error finding crops: " + e.getMessage());
            e.printStackTrace();
        }
        return crops;
    }
    
    public boolean save(Crop crop) {
        String sql = "INSERT INTO crops (crop_name, crop_type, planting_date, harvest_date, status, quantity, field_name, farmer_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println(" CropDAO: Saving new crop: " + crop.getName());
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, crop.getName());
            stmt.setString(2, crop.getType());
            stmt.setDate(3, Date.valueOf(crop.getPlantingDate()));
            stmt.setDate(4, Date.valueOf(crop.getHarvestDate()));
            stmt.setString(5, crop.getStatus());
            stmt.setDouble(6, crop.getQuantity());
            stmt.setString(7, crop.getField());
            stmt.setInt(8, crop.getFarmerId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        crop.setId(generatedKeys.getInt(1));
                        System.out.println(" Crop saved with ID: " + crop.getId());
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println(" CropDAO error saving crop: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean update(Crop crop) {
        String sql = "UPDATE crops SET crop_name=?, crop_type=?, planting_date=?, harvest_date=?, status=?, quantity=?, field_name=? WHERE crop_id=? AND farmer_id=?";
        
        System.out.println(" CropDAO: Updating crop ID: " + crop.getId());
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, crop.getName());
            stmt.setString(2, crop.getType());
            stmt.setDate(3, Date.valueOf(crop.getPlantingDate()));
            stmt.setDate(4, Date.valueOf(crop.getHarvestDate()));
            stmt.setString(5, crop.getStatus());
            stmt.setDouble(6, crop.getQuantity());
            stmt.setString(7, crop.getField());
            stmt.setInt(8, crop.getId());
            stmt.setInt(9, crop.getFarmerId());
            
            boolean success = stmt.executeUpdate() > 0;
            System.out.println(success ? " Crop updated successfully" : " Crop update failed");
            return success;
            
        } catch (SQLException e) {
            System.err.println(" CropDAO error updating crop: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean delete(int cropId) {
        String sql = "DELETE FROM crops WHERE crop_id=? AND farmer_id=?";
        
        System.out.println(" CropDAO: Deleting crop ID: " + cropId);
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cropId);
            stmt.setInt(2, getCurrentFarmerId());
            
            boolean success = stmt.executeUpdate() > 0;
            System.out.println(success ? " Crop deleted successfully" : " Crop delete failed");
            return success;
            
        } catch (SQLException e) {
            System.err.println(" CropDAO error deleting crop: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM crops WHERE farmer_id = ?"; 
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, getCurrentFarmerId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(" Total crops count: " + count);
                return count;
            }
        } catch (SQLException e) {
            System.err.println(" CropDAO error getting count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    private Crop mapResultSetToCrop(ResultSet rs) throws SQLException {
        Crop crop = new Crop();
        crop.setId(rs.getInt("crop_id"));
        crop.setName(rs.getString("crop_name"));
        crop.setType(rs.getString("crop_type"));
        
        Date plantingDate = rs.getDate("planting_date");
        if (plantingDate != null) {
            crop.setPlantingDate(plantingDate.toLocalDate());
        }
        
        Date harvestDate = rs.getDate("harvest_date");
        if (harvestDate != null) {
            crop.setHarvestDate(harvestDate.toLocalDate());
        }
        
        crop.setStatus(rs.getString("status"));
        crop.setQuantity(rs.getDouble("quantity"));
        crop.setField(rs.getString("field_name"));
        crop.setFarmerId(rs.getInt("farmer_id"));
        
        return crop;
    }

    private int getCurrentFarmerId() {
       
        return 1; 
    }

    public boolean saveFactoryRequest(Crop crop) {
        System.out.println(" saveFactoryRequest not implemented");
        return false;
    }

    public List<Crop> findByFactoryStatus() {
        System.out.println(" findByFactoryStatus not implemented");
        return new ArrayList<>();
    }

    public String getFactoryStatus(int cropId, int currentFarmerId) {
        System.out.println(" getFactoryStatus not implemented");
        return "NOT_IMPLEMENTED";
    }

    
}