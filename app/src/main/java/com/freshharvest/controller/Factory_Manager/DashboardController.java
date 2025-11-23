package com.freshharvest.controller.Factory_Manager;

import javafx.event.ActionEvent; // Import this
import javafx.fxml.FXML;
import javafx.scene.Node; // Import this
import javafx.stage.Stage; // Import this

public class DashboardController {

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // You can add initialization logic here if needed
    }

    /**
     * Called when the user clicks the "Close Application" button.
     * We add ActionEvent event as a parameter so we can find the window.
     */
    @FXML
    private void closeApplication(ActionEvent event) {
        // Get the button that was clicked
        Node source = (Node) event.getSource();
        
        // Get the stage (the window) from the button
        Stage stage = (Stage) source.getScene().getWindow();
        
        // Close the window
        stage.close();
    }
}