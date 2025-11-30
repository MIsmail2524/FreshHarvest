
package com.freshharvest.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.freshharvest.app.Factory_Manager.Batch;
import com.freshharvest.app.Factory_Manager.InventoryLot;
import com.freshharvest.app.Factory_Manager.Shipment;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DBHandle {

    private static DBHandle instance;
    private Connection connection;

    private final String url;
    private final String user;
    private final String password;


    private DBHandle() {
        
        Dotenv dotenv = Dotenv.configure()
                              .ignoreIfMissing()  
                              .load();

        this.url = getenvOrDotenv("DB_URL", dotenv);
        this.user = getenvOrDotenv("DB_USER", dotenv);
        this.password = getenvOrDotenv("DB_PASS", dotenv);
        
        connect();
    }

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