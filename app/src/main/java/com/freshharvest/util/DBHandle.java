package com.freshharvest.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class DBHandle {

    private static DBHandle instance;
    private Connection connection;

    private final String url;
    private final String user;
    private final String password;

    private DBHandle() {
        // Try to load .env (optional)
        Dotenv dotenv = Dotenv.configure()
                              .ignoreIfMissing()  // won't crash if no .env found
                              .load();

        // Prefer real environment vars (if running in production)
        this.url = getenvOrDotenv("DB_URL", dotenv);
        this.user = getenvOrDotenv("DB_USER", dotenv);
        this.password = getenvOrDotenv("DB_PASS", dotenv);
        
        connect();
    }

    // Helper to prioritize system environment over .env
    private String getenvOrDotenv(String key, Dotenv dotenv) {
        String envVal = System.getenv(key);
        return envVal != null ? envVal : dotenv.get(key);
    }

    public static synchronized DBHandle getInstance() {
        if (instance == null) {
            instance = new DBHandle();
        }
        return instance;
    }

    private void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connected to MySQL successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to MySQL.");
            e.printStackTrace();
        }
    }

    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Reconnecting to MySQL...");
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection state.");
            e.printStackTrace();
        }
        return connection;
    }

    public synchronized void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
