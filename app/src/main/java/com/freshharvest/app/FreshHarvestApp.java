package com.freshharvest.app;

import java.sql.Connection;

import com.freshharvest.util.DBHandle;
import com.freshharvest.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main Application Class for FreshHarvest
 * 
 * This class serves as the entry point for the JavaFX application.
 * 
 */
public class FreshHarvestApp extends Application {
    
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        
        // Configure the primary stage
        stage.setTitle("FreshHarvest - Farm Management System");
        stage.setMinWidth(1400);
        stage.setMinHeight(800);
        stage.setMaximized(false);
        
        // Initialize the scene manager and show the main screen
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.initialize(stage);
        
    // Load the main layout (contains sidebar + content area)
    sceneManager.switchToScene("main");
        
        stage.show();
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void main(String[] args) {
        DBHandle db = DBHandle.getInstance();
        try (Connection conn = db.getConnection()) {
            System.out.println("DB connection valid? " + conn.isValid(2));
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
            primaryStage.close();
        }
    }
}