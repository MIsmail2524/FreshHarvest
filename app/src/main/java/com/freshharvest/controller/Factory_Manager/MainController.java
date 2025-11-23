package com.freshharvest.controller.Factory_Manager;

import com.freshharvest.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainController {

    // Main content pane
    @FXML
    private AnchorPane contentPane;


    // --- Factory Buttons ---
    @FXML
    private Button btnFactoryDashboard;
    @FXML
    private Button btnBatchManagement;
    
    // --- Factory-specific buttons ---
    @FXML
    private Button btnFactoryInventory;
    @FXML
    private Button btnFactoryReport;
    @FXML
    private Button btnShipmentManagement;

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
        // Initialize the navigation buttons list
        navigationButtons = Arrays.asList(
           btnFactoryDashboard, 
            btnBatchManagement, btnFactoryInventory, btnFactoryReport,
            btnShipmentManagement
        );

        // Load the default dashboard screen on startup
    }

    /**
     * Loads the specified FXML into the contentPane.
     * @param viewId The FXML identifier in SceneManager
     */
    private void loadView(String viewId) {
        try {
            Parent view = SceneManager.getInstance().getView(viewId);
            contentPane.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (IOException ex) {
            System.err.println("Failed to load view: " + viewId);
            ex.printStackTrace();
        }
    }

    /**
     * Helper method to update the 'active' style class on the sidebar buttons.
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

    // --- Navigation Button Handlers ---

 

 
    /**
     * Handles clicking the Main Inventory button.
     */
 

    /**
     * Handles clicking the Main Reports button.
     */

    /**
     * Handles clicking the Factory Dashboard button.
     */
    @FXML
    private void onFactoryDashboard() {
        updateActiveButton(btnFactoryDashboard);
        loadView("factory_dashboard");
    }

    /**
     * Handles clicking the Batch Management button (Use Cases 5 & 6).
     */
    @FXML
    private void onBatchManagement() {
        updateActiveButton(btnBatchManagement);
        loadView("batch_management");
    }

    /**
     * Handles clicking the Factory Inventory button.
     */
    @FXML
    private void onFactoryInventory() {
        updateActiveButton(btnFactoryInventory);
        loadView("factory_inventory");
    }

    /**
     * Handles clicking the Factory Report button.
     */
    @FXML
    private void onFactoryReport() {
        updateActiveButton(btnFactoryReport);
        loadView("factory_report");
    }

    /**
     * Handles clicking the Shipment Management button.
     */
    @FXML
    private void onShipmentManagement() {
        updateActiveButton(btnShipmentManagement);
        loadView("shipment_management");
    }

    // --- Bottom Button Handlers ---


    @FXML
    private void onLogout() {
        // Logout logic - switch back to login screen
        SceneManager.getInstance().switchToScene("login");
    }
}