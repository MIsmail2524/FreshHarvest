package com.freshharvest.service;

import com.freshharvest.model.User;
import com.freshharvest.util.DBHandle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserService - Handles all user-related database operations
 * Provides methods for user authentication, CRUD operations, and user management
 */
public class UserService {

    private static UserService instance;
    private final DBHandle dbHandle;

    private UserService() {
        this.dbHandle = DBHandle.getInstance();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Authenticate user with username and password
     * @param username The username
     * @param password The password
     * @return Optional<User> containing the user if authentication successful
     */
    public Optional<User> authenticate(String username, String password) {
        String sql = "SELECT id, username, password, full_name, email, role, is_active " +
                     "FROM users WHERE username = ? AND password = ? AND is_active = true";

        try (Connection conn = dbHandle.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);    // TODO: HASH PASSWORD

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password, full_name, email, role, is_active " +
                     "FROM users WHERE username = ?";

        try (Connection conn = dbHandle.getConnection();
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

    /**
     * Find user by ID
     */
    public Optional<User> findById(int id) {
        String sql = "SELECT id, username, password, full_name, email, role, is_active " +
                     "FROM users WHERE id = ?";

        try (Connection conn = dbHandle.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

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

    /**
     * Get all users
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, full_name, email, role, is_active FROM users";

        try (Connection conn = dbHandle.getConnection();
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

    /**
     * Save or update user
     */
    public boolean save(User user) {
        if (user.getId() > 0) {
            return update(user);
        } else {
            return create(user);
        }
    }

    /**
     * Create new user
     */
    private boolean create(User user) {
        String sql = "INSERT INTO users (username, password, full_name, email, role, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbHandle.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getRole().name());
            stmt.setBoolean(6, user.isActive());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update existing user
     */
    private boolean update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, full_name = ?, " +
                     "email = ?, role = ?, is_active = ? WHERE id = ?";

        try (Connection conn = dbHandle.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getRole().name());
            stmt.setBoolean(6, user.isActive());
            stmt.setInt(7, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete user by ID
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = dbHandle.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = dbHandle.getConnection();
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

    /**
     * Get users by role
     */
    public List<User> findByRole(User.Role role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, full_name, email, role, is_active " +
                     "FROM users WHERE role = ? AND is_active = true";

        try (Connection conn = dbHandle.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding users by role: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }

    /**
     * Initialize database tables (for development/testing)
     * This method creates the users table if it doesn't exist
     */
    public void initializeDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                full_name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                role ENUM('FARMER', 'FACTORY_MANAGER', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
                is_active BOOLEAN NOT NULL DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
            """;

        try (Connection conn = dbHandle.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createTableSQL);
            System.out.println("Users table initialized successfully");

            // Insert default admin user if not exists
            seed();

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Insert default users for testing
     */
    private void seed() {
        // Check if farmer user exists
        if (!usernameExists("farmer")) {
            User farmer = new User();
            farmer.setUsername("farmer");
            farmer.setPassword("farmer123"); // In production, hash passwords!
            farmer.setFullName("Farmer");
            farmer.setEmail("farmer@freshharvest.com");
            farmer.setRole(User.Role.FARMER);

            if (save(farmer)) {
                System.out.println("Default farmer user created");
            }
        }

        // Check if manager user exists
        if (!usernameExists("manager")) {
            User manager = new User();
            manager.setUsername("manager");
            manager.setPassword("manager123");
            manager.setFullName("Factory Manager");
            manager.setEmail("manager@freshharvest.com");
            manager.setRole(User.Role.FACTORY_MANAGER);

            if (save(manager)) {
                System.out.println("Default manager user created");
            }
        }

        // Check if employee user exists
        if (!usernameExists("customer")) {
            User customer = new User();
            customer.setUsername("customer");
            customer.setPassword("customer123");
            customer.setFullName("Customer");
            customer.setEmail("customer@freshharvest.com");
            customer.setRole(User.Role.CUSTOMER);

            if (save(customer)) {
                System.out.println("Default customer user created");
            }
        }
    }
}