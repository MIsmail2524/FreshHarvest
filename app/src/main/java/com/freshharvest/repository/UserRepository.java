package com.freshharvest.repository;

import com.freshharvest.app.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndPassword(String username, String password);
    Optional<User> findById(String id);
    List<User> findAll();
    boolean save(User user);
    boolean update(User user);
    boolean delete(String id);
    boolean usernameExists(String username);
    boolean emailExists(String email);
    void initializeDatabase();
}