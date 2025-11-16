package com.freshharvest.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SceneManager - Centralized scene navigation and management
 * 
 * This singleton class manages all scene transitions and FXML loading.
 * It provides a clean way to navigate between different screens in the application.
 * 
 */
public class SceneManager {
    
    private static SceneManager instance;
    private Stage primaryStage;
    private Map<String, Scene> sceneCache;
    private Map<String, Object> controllerCache;
    private Map<String, Parent> viewCache;
    
    // FXML file paths mapping
    private static final Map<String, String> SCENE_PATHS = new HashMap<>();
    
    static {
        SCENE_PATHS.put("login", "/fxml/login.fxml");
        SCENE_PATHS.put("main", "/fxml/main.fxml");
        SCENE_PATHS.put("dashboard", "/fxml/dashboard.fxml");
    }
    
    private SceneManager() {
        sceneCache = new HashMap<>();
        controllerCache = new HashMap<>();
        viewCache = new HashMap<>();
    }
    
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }
    
    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    /**
     * Switch to a scene by identifier
     * @param sceneId The identifier of the scene to switch to
     */
    public void switchToScene(String sceneId) {
        try {
            Scene scene = getScene(sceneId);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Error loading scene: " + sceneId);
            e.printStackTrace();
        }
    }
    
    /**
     * Get a scene by identifier (with caching)
     * @param sceneId The identifier of the scene
     * @return The Scene object
     * @throws IOException if FXML loading fails
     */
    private Scene getScene(String sceneId) throws IOException {
        // Check if scene is already cached
        if (sceneCache.containsKey(sceneId)) {
            return sceneCache.get(sceneId);
        }
        
        // Load FXML and create scene
        String fxmlPath = SCENE_PATHS.get(sceneId);
        if (fxmlPath == null) {
            throw new IllegalArgumentException("Scene not found: " + sceneId);
        }
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        // Cache the scene and controller
        sceneCache.put(sceneId, scene);
        controllerCache.put(sceneId, loader.getController());
        
        return scene;
    }

    /**
     * Load and return the FXML root (Parent) for a sceneId.
     * Useful to embed screens into a persistent main layout.
     */
    public javafx.scene.Parent getView(String sceneId) throws IOException {
        if (viewCache.containsKey(sceneId)) {
            return viewCache.get(sceneId);
        }
        String fxmlPath = SCENE_PATHS.get(sceneId);
        if (fxmlPath == null) {
            throw new IllegalArgumentException("View not found: " + sceneId);
        }
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
        javafx.scene.Parent root = loader.load();
        viewCache.put(sceneId, root);
        controllerCache.put(sceneId, loader.getController());
        return root;
    }
    
    /**
     * Get the controller for a specific scene
     * @param sceneId The identifier of the scene
     * @return The controller object
     */
    public Object getController(String sceneId) {
        return controllerCache.get(sceneId);
    }
    
    /**
     * Clear the scene cache (useful for development/debugging)
     */
    public void clearCache() {
        sceneCache.clear();
        controllerCache.clear();
        if (viewCache != null) {
            viewCache.clear();
        }
    }
    
    /**
     * Get the primary stage
     * @return The primary Stage object
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}