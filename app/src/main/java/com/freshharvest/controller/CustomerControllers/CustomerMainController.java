package com.freshharvest.controller.CustomerControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.freshharvest.app.FreshHarvestApp;
import com.freshharvest.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class CustomerMainController implements Initializable {

    @FXML
    private AnchorPane contentPane;

    // Navigation buttons
    @FXML private Button btnDashboard;
    @FXML private Button btnProducts;
    @FXML private Button btnAddOrder;
    @FXML private Button btnMakePayment;
    @FXML private Button btnReports;
    @FXML private Button btnLogout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load dashboard as default
        loadView("customer_dashboard");
        setActiveButton(btnDashboard);
    }

    // Navigation handlers
    @FXML 
    private void onDashboard(ActionEvent e) {
        loadView("customer_dashboard");
        setActiveButton(btnDashboard);
    }

    @FXML 
    private void onProducts(ActionEvent e) {
        loadView("customer_orders");
        setActiveButton(btnProducts);
    }

    @FXML 
    private void onAddOrder(ActionEvent e) {
        loadView("add_order");
        setActiveButton(btnAddOrder);
    }

    @FXML 
    private void onMakePayment(ActionEvent e) { // Add ActionEvent parameter
        loadView("make_payment"); // Changed from "payment" to "make_payment"
        setActiveButton(btnMakePayment);
    }

    @FXML 
    private void onReports(ActionEvent e) {
        loadView("customer_reports");
        setActiveButton(btnReports);
    }

  

    @FXML 
    private void onLogout(ActionEvent e) {
        // Redirect to login without closing primary stage
        SceneManager.getInstance().switchToScene("login");
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
     * Opens a popup window with the given FXML file.
     * @param fxmlFile FXML file name (e.g., AddCrop.fxml)
     */
    public void openPopup(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initOwner(FreshHarvestApp.getPrimaryStage());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets the active button style in sidebar navigation.
     * @param activeButton The button to mark as active
     */
    private void setActiveButton(Button activeButton) {
        Button[] buttons = {btnDashboard, btnProducts, btnAddOrder, btnMakePayment,
                            btnReports};

        for (Button btn : buttons) {
            btn.getStyleClass().remove("active");
        }

        if (activeButton != null && !activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
}