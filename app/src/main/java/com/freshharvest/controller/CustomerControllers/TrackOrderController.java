package com.freshharvest.controller.CustomerControllers;

import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;
import com.freshharvest.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.ResourceBundle;

public class TrackOrderController implements Initializable, DataChangeListener { 
    
    private NotificationService notificationService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Customer Orders loaded successfully!");
          notificationService = NotificationService.getInstance(); 
            notificationService.addListener(this); 
            
    }

    @Override
    public void onDataChanged(String dataType) {
        if ("ORDER_STATUS_CHANGED".equals(dataType)) {
            javafx.application.Platform.runLater(() -> {
                // Refresh order tracking data
                onRefresh();
                System.out.println("Order tracking automatically refreshed");
            });
        }
    }
    
    @FXML
    private void onRefresh() {
        showAlert("Refresh", "Orders refreshed successfully!");
    }

    @FXML
    private void onBack() {
        try {
            SceneManager.getInstance().switchToScene("com/freshharvest/fxml/Customers/CustomerDashboard");
        } catch (Exception e) {
            showAlert("Error", "Failed to navigate: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}