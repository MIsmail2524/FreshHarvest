package com.freshharvest.controller;

import com.freshharvest.util.SceneManager;
import java.util.Arrays;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class MainController {

    // Main content pane
    @FXML
    private AnchorPane contentPane;

    // --- Sidebar Navigation Buttons ---
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnProducts;
    @FXML
    private Button btnOrders;
    @FXML
    private Button btnCustomers;
    @FXML
    private Button btnInventory; // This is for "Main Inventory"
    @FXML
    private Button btnReports; // This is for "Main Reports"

    // --- Factory Buttons ---
    @FXML
    private Button btnFactoryDashboard;
    @FXML
    private Button btnBatchManagement;
    
    // --- UPDATED: Added new FXML variables ---
    @FXML
    private Button btnFactoryInventory;
    @FXML
    private Button btnFactoryReport;

    // --- Bottom Sidebar Buttons ---
    @FXML
    private Button btnSettings;
    @FXML
    private Button btnLogout;

    // List to hold all navigation buttons for styling
    private List<Button> navigationButtons;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        // --- UPDATED: Added new buttons to the list ---
        navigationButtons = Arrays.asList(
            btnDashboard, btnProducts, btnOrders, btnCustomers, 
            btnInventory, btnReports, btnFactoryDashboard, 
            btnBatchManagement, btnFactoryInventory, btnFactoryReport
        );

        // Load the default dashboard screen on startup
        onDashboard();
    }

    // --- Navigation Button Handlers ---

    @FXML
    private void onDashboard() {
        updateActiveButton(btnDashboard);
        SceneManager.loadScene(contentPane, "dashboard.fxml");
    }

    @FXML
    private void onProducts() {
        updateActiveButton(btnProducts);
        // SceneManager.loadScene(contentPane, "products.fxml"); // TODO: Create this screen
        System.out.println("Products screen not yet implemented.");
    }

    @FXML
    private void onOrders() {
        updateActiveButton(btnOrders);
        // SceneManager.loadScene(contentPane, "orders.fxml"); // TODO: Create this screen
        System.out.println("Orders screen not yet implemented.");
    }

    @FXML
    private void onCustomers() {
        updateActiveButton(btnCustomers);
        // SceneManager.loadScene(contentPane, "customers.fxml"); // TODO: Create this screen
        System.out.println("Customers screen not yet implemented.");
    }

    /**
     * Handles clicking the Main Inventory button.
     */
    @FXML
    private void onInventory() {
        updateActiveButton(btnInventory);
        // This can load a different inventory screen (e.g., for the farmer)
       // SceneManager.loadScene(contentPane, "inventory.fxml");
    }

    /**
     * Handles clicking the Main Reports button.
     * FIXED: Uncommented the SceneManager line.
     */
    @FXML
    private void onReports() {
        updateActiveButton(btnReports);
        // This can load a different report screen (e.g., for the farmer)
        SceneManager.loadScene(contentPane, "report.fxml"); 
    }

    /**
     * Handles clicking the Factory Dashboard button.
     */
    @FXML
    private void onFactoryDashboard() {
        updateActiveButton(btnFactoryDashboard);
        SceneManager.loadScene(contentPane, "factory_dashboard.fxml");
    }

    /**
     * Handles clicking the Batch Management button (Use Cases 5 & 6).
     */
    @FXML
    private void onBatchManagement() {
        updateActiveButton(btnBatchManagement);
        SceneManager.loadScene(contentPane, "batch_management.fxml");
    }

    // --- NEW: Added handler methods for new buttons ---

    /**
     * Handles clicking the Factory Inventory button.
     */
    @FXML
    private void onFactoryInventory() {
        updateActiveButton(btnFactoryInventory);
        // This loads the "inventory.fxml" you already built
        SceneManager.loadScene(contentPane, "inventory.fxml");
    }

    /**
     * Handles clicking the Factory Report button.
     */
    @FXML
    private void onFactoryReport() {
        updateActiveButton(btnFactoryReport);
        // This loads the "report.fxml" you already built
        SceneManager.loadScene(contentPane, "Report.fxml");
    }

    // --- Bottom Button Handlers ---

    @FXML
    private void onSettings() {
        // Settings doesn't need to be "active"
        System.out.println("Settings clicked. Implement settings logic.");
    }

    @FXML
    private void onLogout() {
        // Logout logic
        System.out.println("Logout clicked. Implement logout logic.");
    }

    /**
     * Helper method to update the 'active' style class on the sidebar buttons.
     *
     * @param activeButton The button that was just clicked.
     */
    private void updateActiveButton(Button activeButton) {
        if (navigationButtons == null) {
            return; // Safety check if called before initialize
        }
        
        // Remove 'active' class from all buttons
        for (Button btn : navigationButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("active");
            }
        }
        
        // Add 'active' class to the button that was clicked
        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }
    }

    @FXML private Button btnShipmentManagement;

// Add to initialize list...

@FXML
private void onShipmentManagement() {
    updateActiveButton(btnShipmentManagement);
    SceneManager.loadScene(contentPane, "shipment_management.fxml");
}
}