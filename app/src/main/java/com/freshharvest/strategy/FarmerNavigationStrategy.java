package com.freshharvest.strategy;

import javafx.scene.control.Button;
import java.util.Arrays;
import java.util.List;


public class FarmerNavigationStrategy implements NavigationStrategy {
    
    @Override
    public String getRoleName() {
        return "FARMER";
    }
    
    @Override
    public List<Button> createNavigationButtons() {
        Button btnDashboard = new Button("Dashboard");
        btnDashboard.setId("btnDashboard");
        btnDashboard.getStyleClass().add("nav-button");
        
        Button btnCrops = new Button("My Crops");
        btnCrops.setId("btnCrops");
        btnCrops.getStyleClass().add("nav-button");
        
        Button btnOrders = new Button("Orders");
        btnOrders.setId("btnOrders");
        btnOrders.getStyleClass().add("nav-button");
        
        Button btnCustomers = new Button("Customers");
        btnCustomers.setId("btnCustomers");
        btnCustomers.getStyleClass().add("nav-button");
        
        Button btnInventory = new Button("Inventory");
        btnInventory.setId("btnInventory");
        btnInventory.getStyleClass().add("nav-button");
        
        Button btnReports = new Button("Reports");
        btnReports.setId("btnReports");
        btnReports.getStyleClass().add("nav-button");
        
        Button btnSettings = new Button("Settings");
        btnSettings.setId("btnSettings");
        btnSettings.getStyleClass().add("nav-button");
        
        Button btnLogout = new Button("Logout");
        btnLogout.setId("btnLogout");
        btnLogout.getStyleClass().addAll("nav-button", "button-secondary");
        
        return Arrays.asList(btnDashboard, btnCrops, btnOrders, btnCustomers, 
                           btnInventory, btnReports, btnSettings, btnLogout);
    }
    
    @Override
    public String getDefaultView() {
        return "dashboard";
    }
    
    @Override
    public String getMainFXML() {
        return "FarmerMain";
    }
    
    @Override
    public String getWelcomeMessage() {
        return "Welcome to Your Farm Management Dashboard!";
    }
    
 
    public String getViewForButton(String buttonId) {
        switch (buttonId) {
            case "btnDashboard": return "dashboard";
            case "btnCrops": return "crops";
            case "btnOrders": return "order";
            case "btnCustomers": return "customer";
            case "btnInventory": return "inventory";
            case "btnReports": return "reports";
            case "btnSettings": return "settings";
            default: return getDefaultView();
        }
    }
}