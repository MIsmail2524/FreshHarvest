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

    @FXML
    private Button btnDashboard;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load the dashboard by default
        loadView("dashboard");
    }

    @FXML
    private void onDashboard(ActionEvent e) { loadView("dashboard"); }

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
}
