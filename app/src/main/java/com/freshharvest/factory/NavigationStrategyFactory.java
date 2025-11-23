package com.freshharvest.factory;

import com.freshharvest.strategy.*;
import com.freshharvest.app.User;

/**
 * Factory class for creating navigation strategies based on user roles
 * Implements Factory Pattern for creating role-specific navigation behaviors
 */
public class NavigationStrategyFactory {
    
    /**
     * Creates the appropriate navigation strategy based on user role
     * @param role The user's role from User.Role enum
     * @return NavigationStrategy implementation for the specific role
     * @throws IllegalArgumentException if role is unknown
     */
    public static NavigationStrategy createStrategy(User.Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        
        switch (role) {
            case FARMER:
                return new FarmerNavigationStrategy();
            case FACTORY_MANAGER:
                return new FactoryManagerNavigationStrategy();
            case CUSTOMER:
                return new CustomerNavigationStrategy();
            case ADMIN:
                return new AdminNavigationStrategy(); // Added ADMIN role
            default:
                throw new IllegalArgumentException("Unknown user role: " + role);
        }
    }
    
    /**
     * Creates navigation strategy based on User object
     * @param user The User object containing role information
     * @return NavigationStrategy implementation for the user's role
     */
    public static NavigationStrategy createStrategy(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return createStrategy(user.getRole());
    }
}