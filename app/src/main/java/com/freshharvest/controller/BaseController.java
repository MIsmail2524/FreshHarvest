package com.freshharvest.controller;

import com.freshharvest.service.SessionService;
import com.freshharvest.util.SceneManager;

public abstract class BaseController {
    protected SessionService sessionService;
    protected SceneManager sceneManager;
    
    public BaseController() {
        this.sessionService = SessionService.getInstance();
        this.sceneManager = SceneManager.getInstance();
    }
    
    protected void navigateTo(String sceneId) {
        sceneManager.switchToScene(sceneId);
    }
    
    protected boolean isUserLoggedIn() {
        return sessionService.isLoggedIn();
    }
    
    protected boolean hasRole(com.freshharvest.app.User.Role role) {
        return sessionService.hasRole(role);
    }
}