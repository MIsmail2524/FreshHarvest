package com.freshharvest.app;

import com.freshharvest.strategy.FactoryManagerNavigationStrategy;
import com.freshharvest.strategy.NavigationStrategy;

public class FactoryManagerUser extends User {

    public FactoryManagerUser(String username, String password, String fullName, String email) {
        super(username, password, fullName, email, Role.FACTORY_MANAGER);
    }

    public void superviseProcessing() {
        System.out.println("Supervising processing...");
    }
    @Override
public NavigationStrategy getNavigationStrategy() {
    return new FactoryManagerNavigationStrategy();
}

}
