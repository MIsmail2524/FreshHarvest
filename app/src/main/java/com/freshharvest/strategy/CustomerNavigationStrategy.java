package com.freshharvest.strategy;

import javafx.scene.control.Button;
import java.util.Arrays;
import java.util.List;

/**
 * Navigation strategy for Customer role
 * Provides access to order management, product browsing, and personal settings
 */
public class CustomerNavigationStrategy implements NavigationStrategy {
    
    @Override
    public String getRoleName() {
        return "CUSTOMER";
    }
    
    @Override
    public List<Button> createNavigationButtons() {
        Button btnDashboard = new Button("Dashboard");
        btnDashboard.setId("btnDashboard");
        btnDashboard.getStyleClass().add("nav-button");
        
        Button btnMyOrders = new Button("My Orders");
        btnMyOrders.setId("btnMyOrders");
        btnMyOrders.getStyleClass().add("nav-button");
        
        Button btnTrackOrder = new Button("Track Order");
        btnTrackOrder.setId("btnTrackOrder");
        btnTrackOrder.getStyleClass().add("nav-button");
        
        Button btnAddOrder = new Button("Add Order");
        btnAddOrder.setId("btnAddOrder");
        btnAddOrder.getStyleClass().add("nav-button");
        
        Button btnBrowseProducts = new Button("Browse Products");
        btnBrowseProducts.setId("btnBrowseProducts");
        btnBrowseProducts.getStyleClass().add("nav-button");
        
        Button btnReports = new Button("Reports");
        btnReports.setId("btnReports");
        btnReports.getStyleClass().add("nav-button");
        
        Button btnSettings = new Button("Settings");
        btnSettings.setId("btnSettings");
        btnSettings.getStyleClass().add("nav-button");
        
        Button btnLogout = new Button("Logout");
        btnLogout.setId("btnLogout");
        btnLogout.getStyleClass().addAll("nav-button", "button-secondary");
        
        return Arrays.asList(btnDashboard, btnMyOrders, btnTrackOrder, btnAddOrder, 
                           btnBrowseProducts, btnReports, btnSettings, btnLogout);
    }
    
    @Override
    public String getDefaultView() {
        return "customer_dashboard";
    }
    
    @Override
    public String getMainFXML() {
        return "CustomerMain";
    }
    
    @Override
    public String getWelcomeMessage() {
        return "Welcome to FreshHarvest Customer Portal!";
    }
    
    /**
     * Customer-specific view mappings
     */
    public String getViewForButton(String buttonId) {
        switch (buttonId) {
            case "btnDashboard": return "customer_dashboard";
            case "btnMyOrders": return "customer_orders";
            case "btnTrackOrder": return "track_orders";
            case "btnAddOrder": return "add_orders";
            case "btnBrowseProducts": return "browse_products";
            case "btnReports": return "customer_reports";
            case "btnSettings": return "customer_settings";
            default: return getDefaultView();
        }
    }
}