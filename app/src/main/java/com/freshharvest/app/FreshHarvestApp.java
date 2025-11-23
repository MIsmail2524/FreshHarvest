package com.freshharvest.app;

import com.freshharvest.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class FreshHarvestApp extends Application {
    private static Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        FreshHarvestApp.primaryStage = primaryStage;
        
        // Initialize SceneManager
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.initialize(primaryStage);
        
        // Configure primary stage
        primaryStage.setTitle("Fresh Harvest - Farm Management System");
        primaryStage.setMaximized(true);
        
        // Start with login screen
        sceneManager.switchToScene("login");
        
        primaryStage.show();
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}