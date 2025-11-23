package com.freshharvest.service;

import com.freshharvest.repository.UserRepository;
import com.freshharvest.repository.UserRepositoryImpl;
import com.freshharvest.app.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private static UserService instance;
    private UserRepository userDAO;
    
    private UserService() {
        this.userDAO = new UserRepositoryImpl();
    }
    
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    public Optional<User> authenticate(String username, String password) {
        return userDAO.findByUsernameAndPassword(username, password);
    }
    
    public Optional<User> register(User user) {
        // Check if username already exists
        if (userDAO.usernameExists(user.getUsername())) {
            return Optional.empty();
        }
        
        // Check if email already exists
        if (userDAO.emailExists(user.getEmail())) {
            return Optional.empty();
        }
        
        boolean success = userDAO.save(user);
        return success ? Optional.of(user) : Optional.empty();
    }
    
    public Optional<User> getUserById(String id) {
        return userDAO.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
    
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
    
    public boolean updateUser(User user) {
        return userDAO.update(user);
    }
    
    public boolean deleteUser(String id) {
        return userDAO.delete(id);
    }
    
    public boolean usernameExists(String username) {
        return userDAO.usernameExists(username);
    }
    
    public boolean emailExists(String email) {
        return userDAO.emailExists(email);
    }
    
    public void initializeDatabase() {
        userDAO.initializeDatabase();
    }
    
    // For testing/dependency injection
    void setUserDAO(UserRepository userDAO) {
        this.userDAO = userDAO;
    }
}