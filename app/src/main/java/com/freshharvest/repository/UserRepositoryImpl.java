package com.freshharvest.repository;

import com.freshharvest.app.User;
import com.freshharvest.util.DBHandle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = TRUE";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = TRUE";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ? AND is_active = TRUE";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_active = TRUE ORDER BY created_at DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO users (id, username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String userId = user.getId() != null ? user.getId() : UUID.randomUUID().toString();
            stmt.setString(1, userId);
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getRole().name());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                user.setId(userId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, full_name = ?, email = ?, role = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole().name());
            stmt.setString(5, user.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        String sql = "UPDATE users SET is_active = FALSE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND is_active = TRUE";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND is_active = TRUE";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void initializeDatabase() {
        // Check if users table exists and create if not
        String checkTableSql = "SHOW TABLES LIKE 'users'";
        String createTableSql = 
            "CREATE TABLE IF NOT EXISTS users (" +
            "id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()), " +
            "username VARCHAR(50) UNIQUE NOT NULL, " +
            "password VARCHAR(255) NOT NULL, " +
            "full_name VARCHAR(100) NOT NULL, " +
            "email VARCHAR(100) UNIQUE NOT NULL, " +
            "role ENUM('CUSTOMER', 'FARMER', 'FACTORY_MANAGER', 'ADMIN') NOT NULL, " +
            "is_active BOOLEAN DEFAULT TRUE, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkTableSql);
             ResultSet rs = checkStmt.executeQuery()) {
            
            if (!rs.next()) {
                // Table doesn't exist, create it
                try (PreparedStatement createStmt = conn.prepareStatement(createTableSql)) {
                    createStmt.executeUpdate();
                    System.out.println("Users table created successfully");
                    
                    // Insert sample users
                    insertSampleUsers(conn);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void insertSampleUsers(Connection conn) throws SQLException {
        String insertSql = "INSERT INTO users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
        
        String[][] sampleUsers = {
            {"Ismail", "123", "Ismail", "ismail@freshharvest.com", "FARMER"},
            {"Haris", "123", "Haris", "haris@freshharvest.com", "FACTORY_MANAGER"},
            {"Saeed", "123", "Saeed", "saeed@freshharvest.com", "CUSTOMER"},
            {"Ibrahim", "123", "Ibrahim", "ibrahim@freshharvest.com", "CUSTOMER"}
        };
        
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (String[] userData : sampleUsers) {
                stmt.setString(1, userData[0]);
                stmt.setString(2, userData[1]);
                stmt.setString(3, userData[2]);
                stmt.setString(4, userData[3]);
                stmt.setString(5, userData[4]);
                stmt.addBatch();
            }
            stmt.executeBatch();
            System.out.println("Sample users inserted successfully");
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        return user;
    }
}