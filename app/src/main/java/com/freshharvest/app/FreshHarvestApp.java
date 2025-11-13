package com.freshharvest.app;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FreshHarvestApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Get the URL for the main FXML file
            URL fxmlUrl = getClass().getResource("/fxml/main.fxml");
            if (fxmlUrl == null) {
                System.err.println("Cannot find main.fxml. Check path.");
                return;
            }

            // 2. Load the FXML file
            Parent root = FXMLLoader.load(fxmlUrl);

            // 3. Create the main scene
            Scene scene = new Scene(root);

            // 4. Get the URL for the CSS file
            URL cssUrl = getClass().getResource("/css/application.css");
            if (cssUrl != null) {
                // 5. Apply the stylesheet
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Cannot find application.css. Check path.");
            }

            // 6. Set up and show the stage (the main window)
            primaryStage.setTitle("FreshHarvest - Farm Management System");
            primaryStage.setScene(scene);
            primaryStage.setMinHeight(720);
            primaryStage.setMinWidth(1280);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}