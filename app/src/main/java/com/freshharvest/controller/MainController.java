package com.freshharvest.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.freshharvest.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
        loadView("crops"); // Placeholder - load dashboard for now
        setActiveButton(btnProducts);
    }

    @FXML
    private void onOrders(ActionEvent e) { 
        // TODO: Implement orders view
       loadView("order"); // Placeholder - load dashboard for now
        setActiveButton(btnOrders);
    }

    @FXML
    private void onCustomers(ActionEvent e) { 
        // TODO: Implement customers view
        loadView("customer"); 
        setActiveButton(btnCustomers);
    }

    @FXML
    private void onInventory(ActionEvent e) { 
        // TODO: Implement inventory view
        loadView("inventory"); 
        setActiveButton(btnInventory);
    }

    @FXML
    private void onReports(ActionEvent e) { 
        // TODO: Implement reports view
        loadView("reports"); // Placeholder - load dashboard for now
        setActiveButton(btnReports);
    }

    
    @FXML
    private void onLogout(ActionEvent e) { 
        // Redirect farmer (main) context to login
        SceneManager.getInstance().switchToScene("login");
    }

    @FXML 
    void openAddCrop() {
    loadPopup("AddCrop.fxml");
}







public void openUpdateCrop() {
    // This method should get the selected crop from CropsController and pass it to UpdateCropController
    // If you're using a tabbed interface, you might need to get the current controller
    System.out.println("📝 Opening update crop dialog...");
    
    Object cropsController = null;
    // You might need to adjust this based on your MainController structure
    // For now, let's assume we can access the CropsController instance
   
}

private void loadPopup(String fxml) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxml));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Crop Form");
        stage.show();
    } catch (Exception e) {
        e.printStackTrace();
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

    public static MainController getInstance() {
       return new MainController();
    }

    public void setActiveCropsController(CropsController cropsController) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setActiveCropsController'");
    }
}
