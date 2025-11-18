package com.freshharvest.app;

import com.freshharvest.app.InventoryItem;
import com.freshharvest.dao.InventoryDAO;
import java.time.LocalDateTime;
import java.util.List;

public class InventoryService {
    private InventoryDAO inventoryDAO;
    
    public InventoryService() {
        this.inventoryDAO = new InventoryDAO();
    }
public static class InventoryStatistics {
        private final int totalProducts;
        private final int lowStock;
        private final int outOfStock;
        private final double totalValue;
        
        public InventoryStatistics(int totalProducts, int lowStock, int outOfStock, double totalValue) {
            this.totalProducts = totalProducts;
            this.lowStock = lowStock;
            this.outOfStock = outOfStock;
            this.totalValue = totalValue;
        }
        
        // Getters
        public int getTotalProducts() { return totalProducts; }
        public int getLowStock() { return lowStock; }
        public int getOutOfStock() { return outOfStock; }
        public double getTotalValue() { return totalValue; }
    }

    
    /**
     * Get all inventory items for the current farmer
     */
    public List<InventoryItem> getInventoryByFarmer() {
        try {
            List<InventoryItem> inventory = inventoryDAO.findByFarmerId(getCurrentFarmerId());
            
            // Update stock status for each item
            for (InventoryItem item : inventory) {
                updateStockStatus(item);
            }
            
            return inventory;
        } catch (Exception e) {
            System.err.println("Service error getting inventory: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }
    
    /**
     * Get low stock items (stock <= min stock level)
     */
    public List<InventoryItem> getLowStockItems() {
        try {
            List<InventoryItem> inventory = inventoryDAO.findLowStockByFarmerId(getCurrentFarmerId());
            
            for (InventoryItem item : inventory) {
                updateStockStatus(item);
            }
            
            return inventory;
        } catch (Exception e) {
            System.err.println("Service error getting low stock items: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get out of stock items
     */
    public List<InventoryItem> getOutOfStockItems() {
        try {
            List<InventoryItem> inventory = inventoryDAO.findOutOfStockByFarmerId(getCurrentFarmerId());
            
            for (InventoryItem item : inventory) {
                updateStockStatus(item);
            }
            
            return inventory;
        } catch (Exception e) {
            System.err.println("Service error getting out of stock items: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Add new product to inventory
     */
    public boolean addProduct(InventoryItem product) {
        try {
            // Validate product data
            if (product == null) {
                throw new IllegalArgumentException("Product cannot be null");
            }
            
            if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
                throw new IllegalArgumentException("Product name is required");
            }
            
            if (product.getCurrentStock() < 0) {
                throw new IllegalArgumentException("Stock cannot be negative");
            }
            
            if (product.getPrice() < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            
            product.setLastUpdated(LocalDateTime.now());
            return inventoryDAO.save(product, getCurrentFarmerId());
            
        } catch (Exception e) {
            System.err.println("Service error adding product: " + e.getMessage());
            throw new RuntimeException("Failed to add product: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update product stock
     */
    public boolean updateStock(int productId, double newStock) {
        try {
            if (newStock < 0) {
                throw new IllegalArgumentException("Stock cannot be negative");
            }
            
            return inventoryDAO.updateStock(productId, newStock, LocalDateTime.now(), getCurrentFarmerId());
            
        } catch (Exception e) {
            System.err.println("Service error updating stock: " + e.getMessage());
            throw new RuntimeException("Failed to update stock: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update product information
     */
    public boolean updateProduct(InventoryItem product) {
        try {
            if (product == null) {
                throw new IllegalArgumentException("Product cannot be null");
            }
            
            if (product.getProductId() <= 0) {
                throw new IllegalArgumentException("Invalid product ID");
            }
            
            if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
                throw new IllegalArgumentException("Product name is required");
            }
            
            product.setLastUpdated(LocalDateTime.now());
            return inventoryDAO.update(product, getCurrentFarmerId());
            
        } catch (Exception e) {
            System.err.println("Service error updating product: " + e.getMessage());
            throw new RuntimeException("Failed to update product: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete product from inventory
     */
    public boolean deleteProduct(int productId) {
        try {
            if (productId <= 0) {
                throw new IllegalArgumentException("Invalid product ID");
            }
            
            return inventoryDAO.delete(productId, getCurrentFarmerId());
            
        } catch (Exception e) {
            System.err.println("Service error deleting product: " + e.getMessage());
            throw new RuntimeException("Failed to delete product: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get inventory statistics
     */
    public InventoryStatistics getInventoryStatistics() {
        try {
            return inventoryDAO.getStatisticsByFarmerId(getCurrentFarmerId());
        } catch (Exception e) {
            System.err.println("Service error getting inventory statistics: " + e.getMessage());
            return new InventoryStatistics(0, 0, 0, 0.0); // Return empty statistics
        }
    }
    
    /**
     * Update stock status based on current stock and min stock
     */
    private void updateStockStatus(InventoryItem item) {
        if (item.getCurrentStock() <= 0) {
            item.setStockStatus("Out of Stock");
        } else if (item.getCurrentStock() <= item.getMinStock()) {
            item.setStockStatus("Low Stock");
        } else {
            item.setStockStatus("In Stock");
        }
    }
    
    /**
     * Search inventory by product name or category
     */
    public List<InventoryItem> searchInventory(String searchTerm) {
        try {
            List<InventoryItem> results = inventoryDAO.searchByTerm(searchTerm, getCurrentFarmerId());
            
            for (InventoryItem item : results) {
                updateStockStatus(item);
            }
            
            return results;
        } catch (Exception e) {
            System.err.println("Service error searching inventory: " + e.getMessage());
            return List.of();
        }
    }


    
    private int getCurrentFarmerId() {
        // Get current logged-in farmer ID (replace with actual session management)
        return 1; // Default for testing
    }
    
    
}