package com.freshharvest.controller;

import com.freshharvest.app.User;
import com.freshharvest.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController extends BaseController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Button btnExit;
    @FXML private Button btnSignup;
    @FXML private Label lblError;
    @FXML private ProgressIndicator progressIndicator;

    private UserService userService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = UserService.getInstance();
        userService.initializeDatabase();

        setupFormValidation();
        setupKeyboardShortcuts();
        
        lblError.setVisible(false);
        progressIndicator.setVisible(false);
        txtUsername.requestFocus();
    }

    @FXML
    private void onLogin(ActionEvent event) {
        performLogin();
    }

    @FXML
    private void onExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void onSignup(ActionEvent event) {
        navigateTo("signup");
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            performLogin();
        }
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        lblError.setVisible(false);

        if (!validateInput(username, password)) {
            return;
        }

        setLoadingState(true);

        try {
            Optional<User> userOptional = userService.authenticate(username, password);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                sessionService.login(user);
                
                System.out.println("User logged in: " + user.getUsername() + " (" + user.getRole() + ")");

                // Role-based navigation
                switch (user.getRole()) {
                    case CUSTOMER -> navigateTo("customermain");
                    case FARMER -> navigateTo("main");
                    case FACTORY_MANAGER -> navigateTo("factorymain");
                    default -> showError("Unknown role: " + user.getRole());
                }
            } else {
                showError("Invalid username or password");
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            showError("Login failed. Please try again.");
        } finally {
            setLoadingState(false);
        }
    }

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

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    private void setLoadingState(boolean loading) {
        btnLogin.setDisable(loading);
        btnExit.setDisable(loading);
        txtUsername.setDisable(loading);
        txtPassword.setDisable(loading);
        progressIndicator.setVisible(loading);
        btnLogin.setText(loading ? "Logging in..." : "Login");
    }

    private void setupFormValidation() {
        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (lblError.isVisible()) lblError.setVisible(false);
        });

        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (lblError.isVisible()) lblError.setVisible(false);
        });
    }

    private void setupKeyboardShortcuts() {
        txtUsername.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) onExit(null);
        });

        txtPassword.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) onExit(null);
        });
    }



}