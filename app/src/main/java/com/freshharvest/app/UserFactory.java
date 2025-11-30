package com.freshharvest.app;
import com.freshharvest.app.User;

public class UserFactory {

    public static User createUser(String username, String password, String fullName, String email, User.Role role) {
        return switch (role) {
            case CUSTOMER -> new CustomerUser(username, password, fullName, email);
            case FARMER -> new FarmerUser(username, password, fullName, email);
            case FACTORY_MANAGER -> new FactoryManagerUser(username, password, fullName, email);
            default -> throw new IllegalArgumentException("Unexpected value: " + role);
        };
    }
}
