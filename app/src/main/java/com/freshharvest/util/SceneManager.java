package com.freshharvest.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    
    private static SceneManager instance;
    private Stage primaryStage;
    private Map<String, Scene> sceneCache;
    private Map<String, Object> controllerCache;
    private Map<String, Parent> viewCache;
    
    private static final Map<String, String> SCENE_PATHS = new HashMap<>();
    
    static {
        // Farmer scenes
        SCENE_PATHS.put("main", "/fxml/main.fxml");
        SCENE_PATHS.put("dashboard", "/fxml/dashboard.fxml");
        SCENE_PATHS.put("crops", "/fxml/crops.fxml");
        SCENE_PATHS.put("addcrop", "/fxml/AddCrop.fxml");
        SCENE_PATHS.put("updatecrop", "/fxml/UpdateCrop.fxml");
        SCENE_PATHS.put("order", "/fxml/Order.fxml");
        SCENE_PATHS.put("batch", "/fxml/FarmerBatch.fxml");

        SCENE_PATHS.put("customer", "/fxml/Customers.fxml");
        SCENE_PATHS.put("inventory", "/fxml/inventory.fxml");
        SCENE_PATHS.put("reports", "/fxml/reports.fxml");
        SCENE_PATHS.put("make_payment", "/fxml/Customers/payment.fxml");
        
        
        

        
        // Customer scenes
        SCENE_PATHS.put("customermain", "/fxml/Customers/customerMain.fxml");
        SCENE_PATHS.put("customer_dashboard", "/fxml/Customers/CustomerDashboard.fxml");
        SCENE_PATHS.put("customer_orders", "/fxml/Customers/CustomerOrders.fxml");
        SCENE_PATHS.put("customer_reports", "/fxml/Customers/CustomerReports.fxml");
        SCENE_PATHS.put("add_order", "/fxml/Customers/AddOrder.fxml");
        SCENE_PATHS.put("track_order", "/fxml/Customers/TrackOrder.fxml");
        
        // Auth scenes
        SCENE_PATHS.put("login", "/fxml/login.fxml");
        SCENE_PATHS.put("signup", "/fxml/signup.fxml");
        
        // Factory scenes
        SCENE_PATHS.put("factorymain", "/fxml/Factory_Manager/main.fxml");
        SCENE_PATHS.put("factory_dashboard", "/fxml/Factory_Manager/factory_dashboard.fxml");
        SCENE_PATHS.put("batch_management", "/fxml/Factory_Manager/batch_management.fxml");
        SCENE_PATHS.put("factory_inventory", "/fxml/Factory_Manager/inventory.fxml");
        SCENE_PATHS.put("factory_report", "/fxml/Factory_Manager/Report.fxml");
        SCENE_PATHS.put("shipment_management", "/fxml/Factory_Manager/shipment_management.fxml");
        SCENE_PATHS.put("pack", "/fxml/Factory_Manager/package_and_label.fxml");

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
    
    public void switchToScene(String sceneId) {
        try {
            Scene scene = getScene(sceneId);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            handleSceneLoadError(sceneId, e);
        }
    }
    
    private Scene getScene(String sceneId) throws IOException {
        if (sceneCache.containsKey(sceneId)) {
            return sceneCache.get(sceneId);
        }
        
        String fxmlPath = SCENE_PATHS.get(sceneId);
        if (fxmlPath == null) {
            throw new IllegalArgumentException("Scene not found: " + sceneId);
        }
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        sceneCache.put(sceneId, scene);
        controllerCache.put(sceneId, loader.getController());
        
        return scene;
    }

    public Parent getView(String sceneId) throws IOException {
        if (viewCache.containsKey(sceneId)) {
            return viewCache.get(sceneId);
        }
        
        String fxmlPath = SCENE_PATHS.get(sceneId);
        if (fxmlPath == null) {
            throw new IllegalArgumentException("View not found: " + sceneId);
        }
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        viewCache.put(sceneId, root);
        controllerCache.put(sceneId, loader.getController());
        
        return root;
    }
    
    /**
     * Loads a scene into a specific AnchorPane (for embedded views)
     * @param contentPane The AnchorPane to load the scene into
     * @param sceneId The scene identifier
     */
    public void loadScene(AnchorPane contentPane, String sceneId) {
        try {
            Parent view = getView(sceneId);
            contentPane.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (IOException ex) {
            System.err.println("Failed to load scene: " + sceneId);
            ex.printStackTrace();
        }
    }
    
    public Object getController(String sceneId) {
        return controllerCache.get(sceneId);
    }
    
    public void clearCache() {
        sceneCache.clear();
        controllerCache.clear();
        viewCache.clear();
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    private void handleSceneLoadError(String sceneId, Exception e) {
        System.err.println("Error loading scene: " + sceneId);
        e.printStackTrace();
        // You could show an alert dialog here
    }
}