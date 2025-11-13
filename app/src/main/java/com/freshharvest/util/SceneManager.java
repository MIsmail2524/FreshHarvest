package com.freshharvest.util;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

/**
 * A utility class for loading FXML views into a main content pane.
 */
public class SceneManager {

    /**
     * Loads a new FXML view into the center of the main AnchorPane.
     *
     * @param anchorPane The AnchorPane (contentPane) to load the view into.
     * @param fxmlFile   The simple name of the FXML file (e.g., "dashboard.fxml")
     * which must be located in the /resources/fxml/ directory.
     */
    public static void loadScene(AnchorPane anchorPane, String fxmlFile) {
        try {
            // Build the full path to the FXML file
            String fxmlPath = "/fxml/" + fxmlFile;
            
            // Get the URL of the resource
            URL fileUrl = SceneManager.class.getResource(fxmlPath);

            if (fileUrl == null) {
                System.err.println("Cannot find FXML file: " + fxmlPath);
                return;
            }

            // Load the FXML file as a new Node
            Node sceneContent = FXMLLoader.load(fileUrl);

            // Clear the old content from the pane
            anchorPane.getChildren().clear();
            
            // Add the new content
            anchorPane.getChildren().add(sceneContent);

            // Set anchors to make the new content fill the AnchorPane
            AnchorPane.setTopAnchor(sceneContent, 0.0);
            AnchorPane.setBottomAnchor(sceneContent, 0.0);
            AnchorPane.setLeftAnchor(sceneContent, 0.0);
            AnchorPane.setRightAnchor(sceneContent, 0.0);

        } catch (IOException e) {
            System.err.println("Failed to load scene: " + fxmlFile);
            e.printStackTrace();
        }
    }
}