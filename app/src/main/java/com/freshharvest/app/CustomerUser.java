package com.freshharvest.app;

import com.freshharvest.strategy.CustomerNavigationStrategy;
import com.freshharvest.strategy.NavigationStrategy;

public class CustomerUser extends User {

    public CustomerUser(String username, String password, String fullName, String email) {
        super(username, password, fullName, email, Role.CUSTOMER);
    }

    public void viewOrderHistory() {
        System.out.println("Showing customer order history...");
    }

    @Override
public NavigationStrategy getNavigationStrategy() {
    return new CustomerNavigationStrategy();
}

}
