package com.freshharvest.factory;

import com.freshharvest.strategy.NavigationStrategy;
import com.freshharvest.app.User;

public class NavigationStrategyFactory {

    /**
     * Creates a navigation strategy based on the actual User subclass.
     * Uses polymorphism, no role switching needed.
     */
    public static NavigationStrategy createStrategy(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        // Polymorphic call — each subclass returns its own NavigationStrategy
        return user.getNavigationStrategy();
    }
}
