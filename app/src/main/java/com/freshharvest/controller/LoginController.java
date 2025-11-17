package com.freshharvest.controller;

import com.freshharvest.model.User;
import com.freshharvest.model.UserSession;
import com.freshharvest.service.UserService;
import com.freshharvest.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * LoginController - Handles user authentication and login functionality
 * Manages the login form and transitions to the main application upon successful login
 */
public class LoginController implements Initializable {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnSignup;

    @FXML
    private Label lblError;

    @FXML
    private Label lblWelcome;

    @FXML
    private ProgressIndicator progressIndicator;

    private UserService userService;
    private UserSession userSession;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = UserService.getInstance();
        userSession = UserSession.getInstance();

        // Initialize database (for development)
        userService.initializeDatabase();

        // Set up form behavior
        setupFormValidation();
        setupKeyboardShortcuts();

        // Hide error message and progress indicator initially
        lblError.setVisible(false);
        progressIndicator.setVisible(false);

        // Focus on username field
        txtUsername.requestFocus();
    }

    /**
     * Handle login button click
     */
    @FXML
    private void onLogin(ActionEvent event) {
        performLogin();
    }

    /**
     * Handle exit button click
     */
    @FXML
    private void onExit(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Handle signup button click
     */
    @FXML
    private void onSignup(ActionEvent event) {
        SceneManager.getInstance().switchToScene("signup");
    }

    /**
     * Handle Enter key press for quick login
     */
    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            performLogin();
        }
    }

    /**
     * Perform the actual login logic
     */
    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // Clear previous error
        lblError.setVisible(false);

        // Validate input
        if (!validateInput(username, password)) {
            return;
        }

        // Show loading state
        setLoadingState(true);

        try {
            // Authenticate user
            Optional<User> userOptional = userService.authenticate(username, password);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Create user session
                userSession.login(user);

                // Log successful login
                System.out.println("User logged in: " + user.getUsername() + " (" + user.getRole() + ")");

                // Navigate to main application
                SceneManager.getInstance().switchToScene("main");

            } else {
                showError("Invalid username or password");
            }

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            showError("Login failed. Please try again.");
        } finally {
            setLoadingState(false);
        }
    }

    /**
     * Validate user input
     */
    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            showError("Please enter your username");
            txtUsername.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            showError("Please enter your password");
            txtPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    /**
     * Set loading state for UI elements
     */
    private void setLoadingState(boolean loading) {
        btnLogin.setDisable(loading);
        btnExit.setDisable(loading);
        txtUsername.setDisable(loading);
        txtPassword.setDisable(loading);
        progressIndicator.setVisible(loading);

        if (loading) {
            btnLogin.setText("Logging in...");
        } else {
            btnLogin.setText("Login");
        }
    }

    /**
     * Setup form validation (real-time feedback)
     */
    private void setupFormValidation() {
        // Clear error when user starts typing
        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (lblError.isVisible()) {
                lblError.setVisible(false);
            }
        });

        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (lblError.isVisible()) {
                lblError.setVisible(false);
            }
        });
    }

    /**
     * Setup keyboard shortcuts
     */
    private void setupKeyboardShortcuts() {
        // ESC key to exit
        txtUsername.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                onExit(null);
            }
        });

        txtPassword.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                onExit(null);
            }
        });
    }

    /**
     * Get current user session (for other controllers to access)
     */
    public static UserSession getCurrentSession() {
        return UserSession.getInstance();
    }
}