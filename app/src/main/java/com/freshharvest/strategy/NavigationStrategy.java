package com.freshharvest.strategy;

import javafx.scene.control.Button;
import java.util.List;


public interface NavigationStrategy {
    
   
    String getRoleName();
    
   
    List<Button> createNavigationButtons();
    
    String getDefaultView();
    
   
    String getMainFXML();
    
  
    String getWelcomeMessage();
    
   
    default String getViewForButton(String buttonId) {
        return getDefaultView();
    }
}