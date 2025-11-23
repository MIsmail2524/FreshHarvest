package com.freshharvest.controller;

import com.freshharvest.app.User;
import com.freshharvest.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SignupController extends BaseController implements Initializable {

    @FXML private TextField txtFirstName; // Used as full name field after UI simplification
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private ComboBox<String> cmbRole;
    @FXML private CheckBox chkTerms;
    @FXML private Button btnSignup;
    @FXML private Button btnBackToLogin;
    @FXML private Button btnExit;
    @FXML private Label lblError;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private VBox errorContainer;

    private UserService userService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = UserService.getInstance();
        setupForm();
        setupFormValidation();
        
        errorContainer.setVisible(false);
        errorContainer.setManaged(false);
        progressIndicator.setVisible(false);
        txtFirstName.requestFocus();
    }

    private void setupForm() {
        // Populate role combobox
        cmbRole.getItems().addAll("CUSTOMER", "FARMER", "FACTORY_MANAGER");
        
        // Set default selection
        cmbRole.getSelectionModel().selectFirst();
    }

    @FXML
    private void onSignup(ActionEvent event) {
        performSignup();
    }

    @FXML
    private void onBackToLogin(ActionEvent event) {
        navigateTo("login");
    }

    @FXML
    private void onExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            performSignup();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            onExit(null);
        }
    }

    private void performSignup() {
        String firstName = txtFirstName.getText().trim(); // Treat as full name
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        String role = cmbRole.getValue();
        boolean termsAccepted = chkTerms.isSelected();

        hideError();

        if (!validateInput(firstName, username, email, password, confirmPassword, role, termsAccepted)) {
            return;
        }

        setLoadingState(true);

        try {
            // Check if username already exists
            if (userService.usernameExists(username)) {
                showError("Username already exists. Please choose a different one.");
                txtUsername.requestFocus();
                return;
            }

            // Check if email already exists
            if (userService.emailExists(email)) {
                showError("Email address is already registered. Please use a different email.");
                txtEmail.requestFocus();
                return;
            }

            // Create new user
            User newUser = new User();
            newUser.setFullName(firstName);
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password); // Service should hash this
            newUser.setRole(User.Role.valueOf(role));

            Optional<User> success = userService.register(newUser);

            if (success != null) {
                showAlert("Success", "Account created successfully! Please login with your credentials.");
                navigateTo("login");
            } else {
                showError("Registration failed. Please try again.");
            }

        } catch (Exception e) {
            System.err.println("Signup error: " + e.getMessage());
            e.printStackTrace();
            showError("Registration failed: " + e.getMessage());
        } finally {
            setLoadingState(false);
        }
    }

    private boolean validateInput(String fullName, String username, 
                                 String email, String password, String confirmPassword, 
                                 String role, boolean termsAccepted) {
        
        // Full Name validation
        if (fullName.isEmpty()) {
            showError("Please enter your full name");
            txtFirstName.requestFocus();
            return false;
        }
        if (fullName.length() < 2) {
            showError("Full name must be at least 2 characters long");
            txtFirstName.requestFocus();
            return false;
        }

        // Username validation
        if (username.isEmpty()) {
            showError("Please choose a username");
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
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            txtEmail.requestFocus();
            return false;
        }

        // Password validation
        if (password.isEmpty()) {
            showError("Please create a password");
            txtPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            txtPassword.requestFocus();
            return false;
        }

        // Confirm Password validation
        if (confirmPassword.isEmpty()) {
            showError("Please confirm your password");
            txtConfirmPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            txtConfirmPassword.requestFocus();
            return false;
        }

        // Role validation
        if (role == null || role.isEmpty()) {
            showError("Please select your role");
            cmbRole.requestFocus();
            return false;
        }

        // Terms validation
        if (!termsAccepted) {
            showError("Please accept the Terms and Conditions");
            chkTerms.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private void showError(String message) {
        lblError.setText(message);
        errorContainer.setVisible(true);
        errorContainer.setManaged(true);
    }

    private void hideError() {
        errorContainer.setVisible(false);
        errorContainer.setManaged(false);
    }

    private void setLoadingState(boolean loading) {
        btnSignup.setDisable(loading);
        btnBackToLogin.setDisable(loading);
        btnExit.setDisable(loading);
        txtFirstName.setDisable(loading);
        txtUsername.setDisable(loading);
        txtEmail.setDisable(loading);
        txtPassword.setDisable(loading);
        txtConfirmPassword.setDisable(loading);
        cmbRole.setDisable(loading);
        chkTerms.setDisable(loading);
        progressIndicator.setVisible(loading);
        btnSignup.setText(loading ? "Creating Account..." : "Create Account");
    }

    private void setupFormValidation() {
        // Real-time validation listeners
        txtFirstName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (errorContainer.isVisible()) hideError();
        });


        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (errorContainer.isVisible()) hideError();
            // Auto-format username (remove spaces, convert to lowercase)
            if (newValue != null && !newValue.equals(oldValue)) {
                String formatted = newValue.replaceAll("\\s+", "").toLowerCase();
                if (!newValue.equals(formatted)) {
                    txtUsername.setText(formatted);
                }
            }
        });

        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (errorContainer.isVisible()) hideError();
        });

        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (errorContainer.isVisible()) hideError();
        });

        txtConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (errorContainer.isVisible()) hideError();
            
            // Real-time password matching indicator
            if (!txtPassword.getText().isEmpty() && !newValue.isEmpty()) {
                if (!txtPassword.getText().equals(newValue)) {
                    txtConfirmPassword.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1;");
                } else {
                    txtConfirmPassword.setStyle("-fx-border-color: #27ae60; -fx-border-width: 1;");
                }
            } else {
                txtConfirmPassword.setStyle("");
            }
        });

        cmbRole.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (errorContainer.isVisible()) hideError();
        });

        chkTerms.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (errorContainer.isVisible()) hideError();
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Optional: Add password strength indicator
    private void updatePasswordStrength(String password) {
        if (password.length() == 0) {
            // Reset styling
            txtPassword.setStyle("");
        } else if (password.length() < 6) {
            txtPassword.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1;");
        } else if (password.length() < 8) {
            txtPassword.setStyle("-fx-border-color: #f39c12; -fx-border-width: 1;");
        } else {
            txtPassword.setStyle("-fx-border-color: #27ae60; -fx-border-width: 1;");
        }
    }
}