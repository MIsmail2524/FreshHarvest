package com.freshharvest.model;

/**
 * UserSession - Singleton class for managing current user session
 * Holds the currently logged-in user information and session state
 */
public class UserSession {

    private static UserSession instance;
    private User currentUser;
    private boolean isLoggedIn;

    private UserSession() {
        this.isLoggedIn = false;
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Login the user and set session information
     */
    public void login(User user) {
        this.currentUser = user;
        this.isLoggedIn = true;
    }

    /**
     * Logout the current user and clear session
     */
    public void logout() {
        this.currentUser = null;
        this.isLoggedIn = false;
    }

    /**
     * Check if a user is currently logged in
     */
    public boolean isLoggedIn() {
        return isLoggedIn && currentUser != null;
    }

    /**
     * Get the currently logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if current user has a specific role
     */
    public boolean hasRole(User.Role role) {
        return isLoggedIn && currentUser != null && currentUser.hasRole(role);
    }

    /**
     * Check if current user has a specific role or higher
     */
    public boolean hasRoleOrHigher(User.Role role) {
        return isLoggedIn && currentUser != null && currentUser.hasRoleOrHigher(role);
    }

    /**
     * Get current user's role
     */
    public User.Role getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    /**
     * Get current user's full name
     */
    public String getCurrentUserFullName() {
        return currentUser != null ? currentUser.getFullName() : null;
    }

    /**
     * Get current user's username
     */
    public String getCurrentUserUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
}