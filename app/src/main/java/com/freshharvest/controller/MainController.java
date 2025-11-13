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
    private Button btnInventory;
    @FXML
    private Button btnReports;

    // --- New Factory Buttons ---
    @FXML
    private Button btnFactoryDashboard;
    @FXML
    private Button btnBatchManagement;

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
        // Add all navigation buttons to the list
        navigationButtons = Arrays.asList(
            btnDashboard, btnProducts, btnOrders, btnCustomers, 
            btnInventory, btnReports, btnFactoryDashboard, btnBatchManagement
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
     * Handles clicking the Inventory button (Use Case 7).
     */
    @FXML
    private void onInventory() {
        updateActiveButton(btnInventory);
        SceneManager.loadScene(contentPane, "inventory.fxml");
    }

    @FXML
    private void onReports() {
        updateActiveButton(btnReports);
        // SceneManager.loadScene(contentPane, "reports.fxml"); // TODO: Create this screen
        System.out.println("Reports screen not yet implemented.");
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
}