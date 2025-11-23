package com.freshharvest.app;

public class User {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private Role role;
    
    public enum Role {
        CUSTOMER, FARMER, FACTORY_MANAGER, ADMIN;
        
        public int getHierarchy() {
            return switch (this) {
                case CUSTOMER -> 1;
                case FARMER -> 2;
                case FACTORY_MANAGER -> 3;
                case ADMIN -> 4;
            };
        }
        
        public boolean hasRoleOrHigher(Role other) {
            return this.getHierarchy() >= other.getHierarchy();
        }
    }
    
    // Constructors
    public User() {}
    
    public User(String username, String password, String fullName, String email, Role role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public boolean hasRole(Role role) {
        return this.role == role;
    }
    
    public boolean hasRoleOrHigher(Role role) {
        return this.role.getHierarchy() >= role.getHierarchy();
    }
}