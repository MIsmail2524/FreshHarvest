package com.freshharvest.dao;

import com.freshharvest.app.InventoryItem;
import com.freshharvest.app.InventoryService;
import com.freshharvest.util.DBHandle;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {
    
    public List<InventoryItem> findByFarmerId(int farmerId) {
        List<InventoryItem> inventory = new ArrayList<>();
        String sql = "SELECT product_id, product_name, category, current_stock, min_stock, unit, price, last_updated " +
                    "FROM products WHERE farmer_id = ? AND is_active = TRUE ORDER BY product_name";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                InventoryItem item = new InventoryItem(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getDouble("current_stock"),
                    rs.getDouble("min_stock"),
                    rs.getString("unit"),
                    rs.getDouble("price"),
                    rs.getTimestamp("last_updated") != null ? 
                        rs.getTimestamp("last_updated").toLocalDateTime() : null
                );
                inventory.add(item);
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error finding inventory: " + e.getMessage());
            throw new RuntimeException("Database error while fetching inventory", e);
        }
        return inventory;
    }
    
    public List<InventoryItem> findLowStockByFarmerId(int farmerId) {
        List<InventoryItem> inventory = new ArrayList<>();
        String sql = "SELECT product_id, product_name, category, current_stock, min_stock, unit, price, last_updated " +
                    "FROM products WHERE farmer_id = ? AND current_stock <= min_stock AND current_stock > 0 AND is_active = TRUE";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                InventoryItem item = new InventoryItem(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getDouble("current_stock"),
                    rs.getDouble("min_stock"),
                    rs.getString("unit"),
                    rs.getDouble("price"),
                    rs.getTimestamp("last_updated") != null ? 
                        rs.getTimestamp("last_updated").toLocalDateTime() : null
                );
                inventory.add(item);
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error finding low stock inventory: " + e.getMessage());
            throw new RuntimeException("Database error while fetching low stock inventory", e);
        }
        return inventory;
    }
    
    public List<InventoryItem> findOutOfStockByFarmerId(int farmerId) {
        List<InventoryItem> inventory = new ArrayList<>();
        String sql = "SELECT product_id, product_name, category, current_stock, min_stock, unit, price, last_updated " +
                    "FROM products WHERE farmer_id = ? AND current_stock <= 0 AND is_active = TRUE";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                InventoryItem item = new InventoryItem(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getDouble("current_stock"),
                    rs.getDouble("min_stock"),
                    rs.getString("unit"),
                    rs.getDouble("price"),
                    rs.getTimestamp("last_updated") != null ? 
                        rs.getTimestamp("last_updated").toLocalDateTime() : null
                );
                inventory.add(item);
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error finding out of stock inventory: " + e.getMessage());
            throw new RuntimeException("Database error while fetching out of stock inventory", e);
        }
        return inventory;
    }
    
    public boolean save(InventoryItem product, int farmerId) {
        String sql = "INSERT INTO products (product_name, category, current_stock, min_stock, unit, price, farmer_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getCategory());
            stmt.setDouble(3, product.getCurrentStock());
            stmt.setDouble(4, product.getMinStock());
            stmt.setString(5, product.getUnit());
            stmt.setDouble(6, product.getPrice());
            stmt.setInt(7, farmerId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("DAO error saving product: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateStock(int productId, double newStock, LocalDateTime lastUpdated, int farmerId) {
        String sql = "UPDATE products SET current_stock = ?, last_updated = ? WHERE product_id = ? AND farmer_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, newStock);
            stmt.setTimestamp(2, Timestamp.valueOf(lastUpdated));
            stmt.setInt(3, productId);
            stmt.setInt(4, farmerId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("DAO error updating stock: " + e.getMessage());
            return false;
        }
    }
    
    public boolean update(InventoryItem product, int farmerId) {
        String sql = "UPDATE products SET product_name = ?, category = ?, current_stock = ?, min_stock = ?, " +
                    "unit = ?, price = ?, last_updated = ? WHERE product_id = ? AND farmer_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getCategory());
            stmt.setDouble(3, product.getCurrentStock());
            stmt.setDouble(4, product.getMinStock());
            stmt.setString(5, product.getUnit());
            stmt.setDouble(6, product.getPrice());
            stmt.setTimestamp(7, Timestamp.valueOf(product.getLastUpdated()));
            stmt.setInt(8, product.getProductId());
            stmt.setInt(9, farmerId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("DAO error updating product: " + e.getMessage());
            return false;
        }
    }
    
    public boolean delete(int productId, int farmerId) {
        String sql = "UPDATE products SET is_active = FALSE WHERE product_id = ? AND farmer_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            stmt.setInt(2, farmerId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("DAO error deleting product: " + e.getMessage());
            return false;
        }
    }
    
    public InventoryService.InventoryStatistics getStatisticsByFarmerId(int farmerId) {
        String sql = "SELECT " +
                    "COUNT(*) as total_products, " +
                    "SUM(CASE WHEN current_stock <= min_stock AND current_stock > 0 THEN 1 ELSE 0 END) as low_stock, " +
                    "SUM(CASE WHEN current_stock <= 0 THEN 1 ELSE 0 END) as out_of_stock, " +
                    "SUM(current_stock * price) as total_value " +
                    "FROM products WHERE farmer_id = ? AND is_active = TRUE";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new InventoryService.InventoryStatistics(
                    rs.getInt("total_products"),
                    rs.getInt("low_stock"),
                    rs.getInt("out_of_stock"),
                    rs.getDouble("total_value")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error getting inventory statistics: " + e.getMessage());
        }
        
        return new InventoryService.InventoryStatistics(0, 0, 0, 0.0);
    }
    
    public List<InventoryItem> searchByTerm(String searchTerm, int farmerId) {
        List<InventoryItem> inventory = new ArrayList<>();
        String sql = "SELECT product_id, product_name, category, current_stock, min_stock, unit, price, last_updated " +
                    "FROM products WHERE farmer_id = ? AND is_active = TRUE AND " +
                    "(product_name LIKE ? OR category LIKE ?) ORDER BY product_name";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, farmerId);
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                InventoryItem item = new InventoryItem(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getDouble("current_stock"),
                    rs.getDouble("min_stock"),
                    rs.getString("unit"),
                    rs.getDouble("price"),
                    rs.getTimestamp("last_updated") != null ? 
                        rs.getTimestamp("last_updated").toLocalDateTime() : null
                );
                inventory.add(item);
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error searching inventory: " + e.getMessage());
            throw new RuntimeException("Database error while searching inventory", e);
        }
        return inventory;
    }
}