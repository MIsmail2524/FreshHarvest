package com.freshharvest.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.freshharvest.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class MainController implements Initializable {

    @FXML
    private AnchorPane contentPane;

    // Navigation buttons
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
    @FXML
    private Button btnSettings;
    @FXML
    private Button btnLogout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load the dashboard by default
        loadView("dashboard");
        setActiveButton(btnDashboard);
    }

    // Navigation action methods
    @FXML
    private void onDashboard(ActionEvent e) { 
        loadView("dashboard");
        setActiveButton(btnDashboard);
    }

    @FXML
    private void onProducts(ActionEvent e) { 
        // TODO: Implement products view
        loadView("dashboard"); // Placeholder - load dashboard for now
        setActiveButton(btnProducts);
    }

    @FXML
    private void onOrders(ActionEvent e) { 
        // TODO: Implement orders view
        loadView("dashboard"); // Placeholder - load dashboard for now
        setActiveButton(btnOrders);
    }

    @FXML
    private void onCustomers(ActionEvent e) { 
        // TODO: Implement customers view
        loadView("dashboard"); // Placeholder - load dashboard for now
        setActiveButton(btnCustomers);
    }

    @FXML
    private void onInventory(ActionEvent e) { 
        // TODO: Implement inventory view
        loadView("dashboard"); // Placeholder - load dashboard for now
        setActiveButton(btnInventory);
    }

    @FXML
    private void onReports(ActionEvent e) { 
        // TODO: Implement reports view
        loadView("dashboard"); // Placeholder - load dashboard for now
        setActiveButton(btnReports);
    }

    @FXML
    private void onSettings(ActionEvent e) { 
        // TODO: Implement settings view
        loadView("dashboard"); // Placeholder - load dashboard for now
        setActiveButton(btnSettings);
    }

    @FXML
    private void onLogout(ActionEvent e) { 
        // TODO: Implement proper logout logic (save state, confirm dialog, etc.)
        System.out.println("Logout clicked - implement proper logout logic");
        
        // For now, just close the application
        if (com.freshharvest.app.FreshHarvestApp.getPrimaryStage() != null) {
            com.freshharvest.app.FreshHarvestApp.getPrimaryStage().close();
        }
    }

    private void loadView(String sceneId) {
        try {
            Parent view = SceneManager.getInstance().getView(sceneId);
            contentPane.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (IOException ex) {
            System.err.println("Failed to load view: " + sceneId);
            ex.printStackTrace();
        }
    }

    private void setActiveButton(Button activeButton) {
        // Remove active class from all navigation buttons
        Button[] navButtons = {btnDashboard, btnProducts, btnOrders, btnCustomers, 
                              btnInventory, btnReports, btnSettings};
        
        for (Button button : navButtons) {
            if (button != null) {
                button.getStyleClass().remove("active");
            }
        }
        
        // Add active class to the selected button
        if (activeButton != null && !activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
}
