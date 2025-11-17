package com.freshharvest.controller;

import com.freshharvest.model.User;
import com.freshharvest.model.UserSession;
import com.freshharvest.service.UserService;
import com.freshharvest.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.ScrollPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * SignupController - Handles user registration functionality
 * Manages the signup form and user creation process
 */
public class SignupController implements Initializable {

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtFullName;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private ComboBox<User.Role> cmbRole;

    @FXML
    private Button btnSignup;

    @FXML
    private Button btnBackToLogin;

    @FXML
    private Button btnExit;

    @FXML
    private Label lblError;

    @FXML
    private ScrollPane rootScroll;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private CheckBox chkTerms;

    private UserService userService;
    private UserSession userSession;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = UserService.getInstance();
        userSession = UserSession.getInstance();

        // Initialize role ComboBox
        setupRoleComboBox();

        // Set up form behavior
        setupFormValidation();
        setupKeyboardShortcuts();

        // Hide messages and progress indicator initially
        if (lblError != null) {
            lblError.setVisible(false);
            lblError.setManaged(false);
        }
        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        // Focus on full name field
        txtFullName.requestFocus();
    }

    /**
     * Handle signup button click
     */
    @FXML
    private void onSignup(ActionEvent event) {
        performSignup();
    }

    /**
     * Handle back to login button click
     */
    @FXML
    private void onBackToLogin(ActionEvent event) {
        SceneManager.getInstance().switchToScene("login");
    }

    /**
     * Handle exit button click
     */
    @FXML
    private void onExit(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Handle Enter key press for quick signup
     */
    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            performSignup();
        }
    }

    /**
     * Perform the actual signup logic
     */
    private void performSignup() {
        // Clear previous messages
        hideMessages();

        // Get form data
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        User.Role selectedRole = cmbRole.getValue();
        boolean acceptedTerms = chkTerms.isSelected();

        // Validate input
        if (!validateInput(fullName, username, email, password, confirmPassword, selectedRole, acceptedTerms)) {
            return;
        }

        // Show loading state
        setLoadingState(true);

        try {
            // Check if username already exists
            if (userService.usernameExists(username)) {
                showError("Username already exists. Please choose a different username.");
                return;
            }

            // Create new user
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password); // In production, hash the password!
            newUser.setRole(selectedRole);
            newUser.setActive(true);

            // Save user to database
            boolean success = userService.save(newUser);

            if (success) {
                SceneManager.getInstance().switchToScene("login");
            } else {
                showError("Failed to create account. Please try again.");
            }

        } catch (Exception e) {
            System.err.println("Signup error: " + e.getMessage());
            e.printStackTrace();
            showError("An error occurred during registration. Please try again.");
        } finally {
            setLoadingState(false);
        }
    }

    /**
     * Validate user input
     */
    private boolean validateInput(String fullName, String username, String email, 
                                String password, String confirmPassword, 
                                User.Role role, boolean acceptedTerms) {
        
        // Full name validation
        if (fullName.isEmpty()) {
            showError("Please enter your full name");
            txtFullName.requestFocus();
            return false;
        }

        if (fullName.length() < 2) {
            showError("Full name must be at least 2 characters long");
            txtFullName.requestFocus();
            return false;
        }

        // Username validation
        if (username.isEmpty()) {
            showError("Please enter a username");
            txtUsername.requestFocus();
            return false;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters long");
            txtUsername.requestFocus();
            return false;
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError("Username can only contain letters, numbers, and underscores");
            txtUsername.requestFocus();
            return false;
        }

        // Email validation
        if (email.isEmpty()) {
            showError("Please enter your email address");
            txtEmail.requestFocus();
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("Please enter a valid email address");
            txtEmail.requestFocus();
            return false;
        }

        // Password validation
        if (password.isEmpty()) {
            showError("Please enter a password");
            txtPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            txtPassword.requestFocus();
            return false;
        }

        // Confirm password validation
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            txtConfirmPassword.requestFocus();
            return false;
        }

        // Role validation
        if (role == null) {
            showError("Please select a role");
            cmbRole.requestFocus();
            return false;
        }

        // Terms validation
        if (!acceptedTerms) {
            showError("Please accept the terms and conditions");
            chkTerms.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        System.out.println("Showing error message: " + message); // Debug log
        if (lblError != null) {
            lblError.setText(message);
            lblError.setVisible(true);
            lblError.setManaged(true);
        }
    }

    /**
     * Hide all messages
     */
    private void hideMessages() {
        if (lblError != null) {
            lblError.setVisible(false);
            lblError.setManaged(false);
        }
    }

    /**
     * Set loading state for UI elements
     */
    private void setLoadingState(boolean loading) {
        btnSignup.setDisable(loading);
        btnBackToLogin.setDisable(loading);
        btnExit.setDisable(loading);
        txtFullName.setDisable(loading);
        txtUsername.setDisable(loading);
        txtEmail.setDisable(loading);
        txtPassword.setDisable(loading);
        txtConfirmPassword.setDisable(loading);
        cmbRole.setDisable(loading);
        chkTerms.setDisable(loading);
        progressIndicator.setVisible(loading);

        if (loading) {
            btnSignup.setText("Creating Account...");
        } else {
            btnSignup.setText("Create Account");
        }
    }

    /**
     * Clear the form
     */
    private void clearForm() {
        txtFullName.clear();
        txtUsername.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
        cmbRole.setValue(null);
        chkTerms.setSelected(false);
    }

    /**
     * Setup role ComboBox
     */
    private void setupRoleComboBox() {
        // Add all roles except ADMIN (for security)
        cmbRole.getItems().addAll(
            User.Role.FARMER,
            User.Role.FACTORY_MANAGER,
            User.Role.CUSTOMER
        );
        
        // Set default role to FARMER
        cmbRole.setValue(User.Role.FARMER);
        
        // Custom cell factory for better display
        cmbRole.setButtonCell(new ListCell<User.Role>() {
            @Override
            protected void updateItem(User.Role item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        
        cmbRole.setCellFactory(listView -> new ListCell<User.Role>() {
            @Override
            protected void updateItem(User.Role item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
    }

    /**
     * Setup form validation (real-time feedback)
     */
    private void setupFormValidation() {
        // Clear messages when user starts typing
        txtFullName.textProperty().addListener((observable, oldValue, newValue) -> hideMessages());
        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> hideMessages());
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> hideMessages());
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> hideMessages());
        txtConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> hideMessages());
        cmbRole.valueProperty().addListener((observable, oldValue, newValue) -> hideMessages());
        chkTerms.selectedProperty().addListener((observable, oldValue, newValue) -> hideMessages());
    }

    /**
     * Setup keyboard shortcuts
     */
    private void setupKeyboardShortcuts() {
        // ESC key to go back
        txtFullName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                onBackToLogin(null);
            }
        });
    }
}