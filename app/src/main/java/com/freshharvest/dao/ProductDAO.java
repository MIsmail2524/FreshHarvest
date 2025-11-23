package com.freshharvest.dao;

import com.freshharvest.app.Customers.Product;
import com.freshharvest.util.DBHandle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    public List<Product> getAvailableProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE stock_quantity > 0 AND is_active = 1";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("product_id"));
                
                // Try different possible column names for product name
                if (hasColumn(rs, "product_name")) {
                    product.setName(rs.getString("product_name"));
                } else if (hasColumn(rs, "name")) {
                    product.setName(rs.getString("name"));
                } else if (hasColumn(rs, "title")) {
                    product.setName(rs.getString("title"));
                }
                
                // Try different possible column names for description
                if (hasColumn(rs, "description")) {
                    product.setDescription(rs.getString("description"));
                } else if (hasColumn(rs, "product_description")) {
                    product.setDescription(rs.getString("product_description"));
                }
                
                // Try different possible column names for category
                if (hasColumn(rs, "category")) {
                    product.setCategory(rs.getString("category"));
                } else if (hasColumn(rs, "product_category")) {
                    product.setCategory(rs.getString("product_category"));
                } else if (hasColumn(rs, "type")) {
                    product.setCategory(rs.getString("type"));
                }
                
                // Price columns
                if (hasColumn(rs, "price")) {
                    product.setPrice(rs.getDouble("price"));
                } else if (hasColumn(rs, "unit_price")) {
                    product.setPrice(rs.getDouble("unit_price"));
                } else if (hasColumn(rs, "selling_price")) {
                    product.setPrice(rs.getDouble("selling_price"));
                }
                
                // Stock quantity columns
                if (hasColumn(rs, "stock_quantity")) {
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                } else if (hasColumn(rs, "quantity")) {
                    product.setStockQuantity(rs.getInt("quantity"));
                } else if (hasColumn(rs, "available_stock")) {
                    product.setStockQuantity(rs.getInt("available_stock"));
                }
                
                // Unit columns
                if (hasColumn(rs, "unit")) {
                    product.setUnit(rs.getString("unit"));
                } else if (hasColumn(rs, "measurement_unit")) {
                    product.setUnit(rs.getString("measurement_unit"));
                } else {
                    product.setUnit("piece"); // default unit
                }
                
                products.add(product);
                System.out.println("Loaded product: " + product.getName() + " - Rs." + product.getPrice());
            }
            
            System.out.println("Successfully loaded " + products.size() + " products from database");
            
        } catch (SQLException e) {
            System.err.println("Error getting available products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }
    
    public List<String> getProductCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT ";
        
        // Determine the correct category column name
        if (hasColumnInTable("products", "category")) {
            sql += "category FROM products WHERE category IS NOT NULL ORDER BY category";
        } else if (hasColumnInTable("products", "product_category")) {
            sql += "product_category FROM products WHERE product_category IS NOT NULL ORDER BY product_category";
        } else if (hasColumnInTable("products", "type")) {
            sql += "type FROM products WHERE type IS NOT NULL ORDER BY type";
        } else {
            return categories; // No category column found
        }
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categories.add(rs.getString(1)); // Get first column
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting product categories: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }
    
    // Helper method to check if a column exists in ResultSet
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Helper method to check if a column exists in table
    private boolean hasColumnInTable(String tableName, String columnName) {
        String sql = "SELECT COUNT(*) FROM information_schema.columns " +
                    "WHERE table_name = ? AND column_name = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tableName);
            stmt.setString(2, columnName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking column existence: " + e.getMessage());
        }
        return false;
    }
    
    // Alternative method with explicit column names
    public List<Product> getAvailableProductsExplicit() {
        List<Product> products = new ArrayList<>();
        
        // Try different possible table structures
        String[] possibleQueries = {
            "SELECT product_id, product_name, description, category, price, stock_quantity, unit FROM products WHERE stock_quantity > 0",
            "SELECT product_id, name, description, category, price, quantity, unit FROM products WHERE quantity > 0",
            "SELECT id, title, description, type, selling_price, available_stock, measurement_unit FROM products WHERE available_stock > 0",
            "SELECT product_id, product_name, product_description, product_category, unit_price, stock_quantity, unit FROM products WHERE stock_quantity > 0"
        };
        
        for (String sql : possibleQueries) {
            try (Connection conn = DBHandle.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Product product = new Product();
                    
                    // Map columns based on the query structure
                    try {
                        product.setId(rs.getInt(1)); // First column is always ID
                        product.setName(rs.getString(2)); // Second column is name
                        product.setDescription(rs.getString(3)); // Third column is description
                        product.setCategory(rs.getString(4)); // Fourth column is category
                        product.setPrice(rs.getDouble(5)); // Fifth column is price
                        product.setStockQuantity(rs.getInt(6)); // Sixth column is stock
                        
                        if (rs.getMetaData().getColumnCount() > 6) {
                            product.setUnit(rs.getString(7)); // Seventh column is unit
                        } else {
                            product.setUnit("piece");
                        }
                        
                        products.add(product);
                        System.out.println("Loaded: " + product.getName() + " - Rs." + product.getPrice());
                        
                    } catch (SQLException e) {
                        System.out.println("Skipping row due to mapping error: " + e.getMessage());
                        continue;
                    }
                }
                
                if (!products.isEmpty()) {
                    System.out.println("Successfully loaded " + products.size() + " products using query: " + sql);
                    break;
                }
                
            } catch (SQLException e) {
                System.out.println("Query failed: " + sql + " - " + e.getMessage());
                continue;
            }
        }
        
        return products;
    }
}