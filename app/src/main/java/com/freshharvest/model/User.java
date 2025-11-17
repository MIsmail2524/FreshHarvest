package com.freshharvest.model;

/**
 * User Model Class
 * Represents a user in the FreshHarvest system with role-based access
 */
public class User {

    public enum Role {
        ADMIN("Administrator"),
        FARMER("Farmer"),
        FACTORY_MANAGER("Factory Manager"),
        CUSTOMER("Customer");

        private final String displayName;

        Role(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private Role role;
    private boolean isActive;

    // Default constructor
    public User() {
        this.isActive = true;
    }

    // Constructor with parameters
    public User(int id, String username, String password, String fullName, String email, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.isActive = true;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Utility methods
    public boolean hasRole(Role role) {
        return this.role == role;
    }

    public boolean hasRoleOrHigher(Role role) {
        return this.role.ordinal() <= role.ordinal();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}