package com.freshharvest.service;

import com.freshharvest.app.User;

public class SessionService {
    private static SessionService instance;
    private User currentUser;
    private boolean isLoggedIn;
    
    private SessionService() {
        this.isLoggedIn = false;
    }
    
    public static synchronized SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService();
        }
        return instance;
    }
    
    public void login(User user) {
        this.currentUser = user;
        this.isLoggedIn = true;
    }
    
    public void logout() {
        this.currentUser = null;
        this.isLoggedIn = false;
    }
    
    public boolean isLoggedIn() {
        return isLoggedIn && currentUser != null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean hasRole(User.Role role) {
        return isLoggedIn && currentUser != null && currentUser.hasRole(role);
    }
    
    public String getCurrentUserFullName() {
        return currentUser != null ? currentUser.getFullName() : null;
    }
    
    public String getCurrentUserUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
}