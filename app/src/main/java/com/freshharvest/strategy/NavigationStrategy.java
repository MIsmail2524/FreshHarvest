package com.freshharvest.strategy;

import javafx.scene.control.Button;
import java.util.List;

/**
 * Strategy interface for role-based navigation
 * Each user role implements this interface to define its specific navigation behavior
 */
public interface NavigationStrategy {
    
    /**
     * @return The role name as string (e.g., "FARMER", "CUSTOMER")
     */
    String getRoleName();
    
    /**
     * Creates the navigation buttons specific to this role
     * @return List of Button objects for the navigation sidebar
     */
    List<Button> createNavigationButtons();
    
    /**
     * @return The default view to load when user logs in
     */
    String getDefaultView();
    
    /**
     * @return The FXML file name for the main layout of this role
     */
    String getMainFXML();
    
    /**
     * @return Welcome message specific to this role
     */
    String getWelcomeMessage();
    
    /**
     * Optional: Get the view name for a specific button ID
     * @param buttonId The ID of the navigation button
     * @return The corresponding view name
     */
    default String getViewForButton(String buttonId) {
        return getDefaultView();
    }
}