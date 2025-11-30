package com.freshharvest.strategy;

import javafx.scene.control.Button;
import java.util.Arrays;
import java.util.List;


public class AdminNavigationStrategy implements NavigationStrategy {
    
    @Override
    public String getRoleName() {
        return "ADMIN";
    }
    
    @Override
    public List<Button> createNavigationButtons() {
        Button btnDashboard = new Button("Dashboard");
        btnDashboard.setId("btnDashboard");
        
        Button btnUsers = new Button("User Management");
        btnUsers.setId("btnUsers");
        
        Button btnFarmers = new Button("Farmers");
        btnFarmers.setId("btnFarmers");
        
        Button btnFactoryManagers = new Button("Factory Managers");
        btnFactoryManagers.setId("btnFactoryManagers");
        
        Button btnCustomers = new Button("Customers");
        btnCustomers.setId("btnCustomers");
        
        Button btnSystem = new Button("System Settings");
        btnSystem.setId("btnSystem");
        
        Button btnReports = new Button("Analytics");
        btnReports.setId("btnReports");
        
        Button btnLogout = new Button("Logout");
        btnLogout.setId("btnLogout");
        btnLogout.getStyleClass().add("button-secondary");
        
        return Arrays.asList(btnDashboard, btnUsers, btnFarmers, btnFactoryManagers, 
                           btnCustomers, btnSystem, btnReports, btnLogout);
    }
    
    @Override
    public String getDefaultView() {
        return "admin_dashboard";
    }
    
    @Override
    public String getMainFXML() {
        return "AdminMain";
    }
    
    @Override
    public String getWelcomeMessage() {
        return "Welcome System Administrator!";
    }
}