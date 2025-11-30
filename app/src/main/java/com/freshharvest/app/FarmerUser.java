package com.freshharvest.app;

import com.freshharvest.strategy.FarmerNavigationStrategy;
import com.freshharvest.strategy.NavigationStrategy;

public class FarmerUser extends User {

    public FarmerUser(String username, String password, String fullName, String email) {
        super(username, password, fullName, email, Role.FARMER);
    }

    public void manageCrops() {
        System.out.println("Managing farmer crops");
    }

    @Override
public NavigationStrategy getNavigationStrategy() {
    return new FarmerNavigationStrategy();
}

}
