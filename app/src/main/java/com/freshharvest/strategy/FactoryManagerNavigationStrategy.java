package com.freshharvest.strategy;

import javafx.scene.control.Button;
import java.util.Arrays;
import java.util.List;

/**
 * Navigation strategy for Factory Manager role
 * Provides access to production, inventory, quality control, and supplier management
 */
public class FactoryManagerNavigationStrategy implements NavigationStrategy {
    
    @Override
    public String getRoleName() {
        return "FACTORY_MANAGER";
    }
    
    @Override
    public List<Button> createNavigationButtons() {
        Button btnDashboard = new Button("Dashboard");
        btnDashboard.setId("btnDashboard");
        btnDashboard.getStyleClass().add("nav-button");
        
        Button btnProduction = new Button("Production");
        btnProduction.setId("btnProduction");
        btnProduction.getStyleClass().add("nav-button");
        
        Button btnInventory = new Button("Inventory");
        btnInventory.setId("btnInventory");
        btnInventory.getStyleClass().add("nav-button");
        
        Button btnQuality = new Button("Quality Control");
        btnQuality.setId("btnQuality");
        btnQuality.getStyleClass().add("nav-button");
        
        Button btnSuppliers = new Button("Suppliers");
        btnSuppliers.setId("btnSuppliers");
        btnSuppliers.getStyleClass().add("nav-button");
        
        Button btnReports = new Button("Reports");
        btnReports.setId("btnReports");
        btnReports.getStyleClass().add("nav-button");
        
        Button btnSettings = new Button("Settings");
        btnSettings.setId("btnSettings");
        btnSettings.getStyleClass().add("nav-button");
        
        Button btnLogout = new Button("Logout");
        btnLogout.setId("btnLogout");
        btnLogout.getStyleClass().addAll("nav-button", "button-secondary");
        
        return Arrays.asList(btnDashboard, btnProduction, btnInventory, btnQuality, 
                           btnSuppliers, btnReports, btnSettings, btnLogout);
    }
    
    @Override
    public String getDefaultView() {
        return "factory_dashboard";
    }
    
    @Override
    public String getMainFXML() {
        return "FactoryManagerMain";
    }
    
    @Override
    public String getWelcomeMessage() {
        return "Welcome to Factory Management System!";
    }
    
    /**
     * Factory Manager-specific view mappings
     */
    public String getViewForButton(String buttonId) {
        switch (buttonId) {
            case "btnDashboard": return "factory_dashboard";
            case "btnProduction": return "factory_production";
            case "btnInventory": return "factory_inventory";
            case "btnQuality": return "factory_quality";
            case "btnSuppliers": return "factory_suppliers";
            case "btnReports": return "factory_reports";
            case "btnSettings": return "factory_settings";
            default: return getDefaultView();
        }
    }
}