package com.freshharvest.controller;

import javafx.fxml.FXML;

/**
 * Dashboard Controller
 * 
 * Used for screens that are under development.
 * Provides basic navigation back to the dashboard.
 */
public class DashboardController {
    
    @FXML
    private void closeApplication() {
        if (com.freshharvest.app.FreshHarvestApp.getPrimaryStage() != null) {
            com.freshharvest.app.FreshHarvestApp.getPrimaryStage().close();
        }
    }
}