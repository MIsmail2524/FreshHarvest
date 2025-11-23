package com.freshharvest.controller.CustomerControllers;

import com.freshharvest.dao.CustomerDAO;
import com.freshharvest.dao.PaymentDAO;
import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;
import com.freshharvest.util.SceneManager;
import com.freshharvest.service.SessionService;
import com.freshharvest.app.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class MakePaymentController implements Initializable, DataChangeListener { 
    
    private NotificationService notificationService; 

    @FXML private TextField txtOrderId;
    @FXML private Label lblVerifyStatus;
    @FXML private VBox orderDetailsBox;
    @FXML private Label lblOrderId;
  //  @FXML private Label lblOrderNumber;
    @FXML private Label lblOrderDate;
    @FXML private Label lblTotalAmount;
    @FXML private Label lblOrderStatus;
    @FXML private Label lblAmountDue;
    
    @FXML private VBox paymentMethodBox;
    @FXML private RadioButton rbCreditCard;
    @FXML private RadioButton rbDebitCard;
    @FXML private RadioButton rbBankTransfer;
    @FXML private RadioButton rbCash;
    @FXML private RadioButton rbPartialPayment;
    
    @FXML private VBox cardDetailsBox;
    @FXML private TextField txtCardNumber;
    @FXML private TextField txtExpiryDate;
    @FXML private TextField txtCVV;
    @FXML private TextField txtCardholderName;
    
    @FXML private VBox bankDetailsBox;
    @FXML private VBox partialPaymentBox;
    @FXML private TextField txtPaymentAmount;
    @FXML private Label lblFullAmount;
    
    @FXML private Button btnMakePayment;
    @FXML private ProgressIndicator progressIndicator;

    private ToggleGroup paymentMethodGroup;
    private CustomerDAO customerDAO;
    private PaymentDAO paymentDAO;
    private int currentCustomerId;
    private int currentOrderId;
    private double orderAmount;
    private double amountPaid;
    private SessionService sessionService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            
            notificationService = NotificationService.getInstance(); 
            notificationService.addListener(this); 
            
            sessionService = SessionService.getInstance();
            customerDAO = new CustomerDAO();
            paymentDAO = new PaymentDAO();
            initializeCurrentCustomer();
            setupPaymentMethodSelection();
            setupFormValidation();
            
        } catch (Exception e) {
            showAlert("Error", "Failed to initialize payment page: " + e.getMessage());
            e.printStackTrace();
        }
    }




    @Override
    public void onDataChanged(String dataType) {
        // Listen for order status changes that might affect payment
        if ("ORDER_STATUS_CHANGED".equals(dataType)) {
            javafx.application.Platform.runLater(() -> {
                // If we have an order ID, re-verify it
                if (!txtOrderId.getText().trim().isEmpty()) {
                    onVerifyOrder();
                }
            });
        }
    }

    private void initializeCurrentCustomer() {
        User currentUser = sessionService.getCurrentUser();
        if (currentUser != null) {
            currentCustomerId = customerDAO.getCustomerIdByUsername(currentUser.getUsername());
            if (currentCustomerId == -1) {
                showAlert("Error", "Customer profile not found. Please contact administrator.");
            }
        } else {
            showAlert("Error", "No user logged in. Please login again.");
        }
    }

    private void setupPaymentMethodSelection() {
        paymentMethodGroup = new ToggleGroup();
        rbCreditCard.setToggleGroup(paymentMethodGroup);
        rbDebitCard.setToggleGroup(paymentMethodGroup);
        rbBankTransfer.setToggleGroup(paymentMethodGroup);
        rbCash.setToggleGroup(paymentMethodGroup);
        rbPartialPayment.setToggleGroup(paymentMethodGroup);

        // Add listener for payment method changes
        paymentMethodGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updatePaymentDetailsVisibility();
            validatePaymentForm();
        });
    }

    private void setupFormValidation() {
        // Add text field listeners for real-time validation
        txtOrderId.textProperty().addListener((observable, oldValue, newValue) -> {
            validateOrderId();
        });

        txtCardNumber.textProperty().addListener((observable, oldValue, newValue) -> validateCardFields());
        txtExpiryDate.textProperty().addListener((observable, oldValue, newValue) -> validateCardFields());
        txtCVV.textProperty().addListener((observable, oldValue, newValue) -> validateCardFields());
        txtCardholderName.textProperty().addListener((observable, oldValue, newValue) -> validateCardFields());
        
        // Add listener for payment amount field
        if (txtPaymentAmount != null) {
            txtPaymentAmount.textProperty().addListener((observable, oldValue, newValue) -> validatePartialPayment());
        } else {
            System.err.println("txtPaymentAmount is null - check FXML binding");
        }
    }

    @FXML
    private void onVerifyOrder() {
        String orderIdText = txtOrderId.getText().trim();
        
        if (orderIdText.isEmpty()) {
            lblVerifyStatus.setText("Please enter an order ID");
            lblVerifyStatus.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdText);
            progressIndicator.setVisible(true);
            
            // Simulate API call delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Simulate network delay
                    
                    javafx.application.Platform.runLater(() -> {
                        boolean isValid = verifyOrder(orderId);
                        
                        if (isValid) {
                            lblVerifyStatus.setText("Order verified successfully!");
                            lblVerifyStatus.setStyle("-fx-text-fill: #27ae60;");
                            orderDetailsBox.setVisible(true);
                            orderDetailsBox.setManaged(true);
                            paymentMethodBox.setVisible(true);
                            paymentMethodBox.setManaged(true);
                            validatePaymentForm();
                        } else {
                            lblVerifyStatus.setText("Invalid order ID or order not found");
                            lblVerifyStatus.setStyle("-fx-text-fill: #e74c3c;");
                            orderDetailsBox.setVisible(false);
                            orderDetailsBox.setManaged(false);
                            paymentMethodBox.setVisible(false);
                            paymentMethodBox.setManaged(false);
                            btnMakePayment.setDisable(true);
                        }
                        progressIndicator.setVisible(false);
                    });
                    
                } catch (InterruptedException e) {
                    javafx.application.Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        showAlert("Error", "Verification failed: " + e.getMessage());
                    });
                }
            }).start();
            
        } catch (NumberFormatException e) {
            lblVerifyStatus.setText("Please enter a valid order ID (numbers only)");
            lblVerifyStatus.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private boolean verifyOrder(int orderId) {
        try {
            // Verify order belongs to current customer and is pending payment
            Object[] orderDetails = paymentDAO.getOrderForPayment(orderId, currentCustomerId);
            
            if (orderDetails != null) {
                currentOrderId = orderId;
                orderAmount = (Double) orderDetails[0];
                amountPaid = (Double) orderDetails[1]; // Current amount paid
                String status = (String) orderDetails[2];
                LocalDateTime orderDate = (LocalDateTime) orderDetails[3];
            //    String orderNumber = (String) orderDetails[4];
                
                // Update UI with order details
                lblOrderId.setText(String.valueOf(orderId));
             //   lblOrderNumber.setText(orderNumber);
                lblOrderDate.setText(orderDate.toLocalDate().toString());
                lblTotalAmount.setText(String.format("Rs. %.2f", orderAmount));
                lblOrderStatus.setText(status);
                
                double amountDue = orderAmount - amountPaid;
                lblAmountDue.setText(String.format("Rs. %.2f", amountDue));
                lblFullAmount.setText(String.format("Full Amount: Rs. %.2f | Paid: Rs. %.2f | Due: Rs. %.2f", 
                    orderAmount, amountPaid, amountDue));
                
                // Set payment amount field to remaining due
                if (txtPaymentAmount != null) {
                    txtPaymentAmount.setText(String.format("%.2f", amountDue));
                }
                
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error verifying order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private void updatePaymentDetailsVisibility() {
        RadioButton selected = (RadioButton) paymentMethodGroup.getSelectedToggle();
        
        if (selected == null) {
            cardDetailsBox.setVisible(false);
            cardDetailsBox.setManaged(false);
            bankDetailsBox.setVisible(false);
            bankDetailsBox.setManaged(false);
            if (partialPaymentBox != null) {
                partialPaymentBox.setVisible(false);
                partialPaymentBox.setManaged(false);
            }
            return;
        }

        if (selected == rbCreditCard || selected == rbDebitCard) {
            cardDetailsBox.setVisible(true);
            cardDetailsBox.setManaged(true);
            bankDetailsBox.setVisible(false);
            bankDetailsBox.setManaged(false);
            if (partialPaymentBox != null) {
                partialPaymentBox.setVisible(false);
                partialPaymentBox.setManaged(false);
            }
        } else if (selected == rbBankTransfer) {
            cardDetailsBox.setVisible(false);
            cardDetailsBox.setManaged(false);
            bankDetailsBox.setVisible(true);
            bankDetailsBox.setManaged(true);
            if (partialPaymentBox != null) {
                partialPaymentBox.setVisible(false);
                partialPaymentBox.setManaged(false);
            }
        } else if (selected == rbCash) {
            cardDetailsBox.setVisible(false);
            cardDetailsBox.setManaged(false);
            bankDetailsBox.setVisible(false);
            bankDetailsBox.setManaged(false);
            if (partialPaymentBox != null) {
                partialPaymentBox.setVisible(false);
                partialPaymentBox.setManaged(false);
            }
        } else if (selected == rbPartialPayment) {
            cardDetailsBox.setVisible(false);
            cardDetailsBox.setManaged(false);
            bankDetailsBox.setVisible(false);
            bankDetailsBox.setManaged(false);
            if (partialPaymentBox != null) {
                partialPaymentBox.setVisible(true);
                partialPaymentBox.setManaged(true);
            }
        }
    }

    private void validateOrderId() {
        if (txtOrderId.getText().trim().isEmpty()) {
            orderDetailsBox.setVisible(false);
            orderDetailsBox.setManaged(false);
            paymentMethodBox.setVisible(false);
            paymentMethodBox.setManaged(false);
            btnMakePayment.setDisable(true);
        }
    }

    private void validateCardFields() {
        if (cardDetailsBox.isVisible()) {
            boolean cardValid = !txtCardNumber.getText().trim().isEmpty() &&
                               !txtExpiryDate.getText().trim().isEmpty() &&
                               !txtCVV.getText().trim().isEmpty() &&
                               !txtCardholderName.getText().trim().isEmpty();
            
            btnMakePayment.setDisable(!cardValid);
        }
    }

    private void validatePartialPayment() {
        if (partialPaymentBox != null && partialPaymentBox.isVisible() && txtPaymentAmount != null) {
            try {
                String amountText = txtPaymentAmount.getText().trim();
                if (amountText.isEmpty()) {
                    btnMakePayment.setDisable(true);
                    return;
                }
                
                double paymentAmount = Double.parseDouble(amountText);
                double amountDue = orderAmount - amountPaid;
                
                if (paymentAmount <= 0) {
                    btnMakePayment.setDisable(true);
                    lblVerifyStatus.setText("Payment amount must be greater than 0");
                    lblVerifyStatus.setStyle("-fx-text-fill: #e74c3c;");
                } else if (paymentAmount > amountDue) {
                    btnMakePayment.setDisable(true);
                    lblVerifyStatus.setText("Payment amount cannot exceed due amount");
                    lblVerifyStatus.setStyle("-fx-text-fill: #e74c3c;");
                } else {
                    btnMakePayment.setDisable(false);
                    lblVerifyStatus.setText("Partial payment ready");
                    lblVerifyStatus.setStyle("-fx-text-fill: #27ae60;");
                }
            } catch (NumberFormatException e) {
                btnMakePayment.setDisable(true);
                lblVerifyStatus.setText("Please enter a valid payment amount");
                lblVerifyStatus.setStyle("-fx-text-fill: #e74c3c;");
            }
        }
    }

    private void validatePaymentForm() {
        if (!orderDetailsBox.isVisible()) {
            btnMakePayment.setDisable(true);
            return;
        }

        RadioButton selected = (RadioButton) paymentMethodGroup.getSelectedToggle();
        if (selected == null) {
            btnMakePayment.setDisable(true);
            return;
        }

        if (selected == rbCreditCard || selected == rbDebitCard) {
            validateCardFields();
        } else if (selected == rbPartialPayment) {
            validatePartialPayment();
        } else {
            // For bank transfer and cash, enable payment button
            btnMakePayment.setDisable(false);
        }
    }

    @FXML
private void onMakePayment() {
    RadioButton selected = (RadioButton) paymentMethodGroup.getSelectedToggle();
    if (selected == null) {
        showAlert("Error", "Please select a payment method");
        return;
    }

    // FIX: Handle partial payment as cash for database
    String paymentMethod = selected.getText();
    String dbPaymentMethod = paymentMethod; // Default to selected method
    
    // If partial payment, store as "Cash" in database but track as partial in application
    if (selected == rbPartialPayment) {
        dbPaymentMethod = "Cash"; // Store as Cash in database
    }
    
    final String finalDbPaymentMethod = dbPaymentMethod;
    final String displayPaymentMethod = paymentMethod; // Keep original for display
    final String transactionId = generateTransactionId();
    
    // Calculate payment amount
    double paymentAmountValue;
    if (selected == rbPartialPayment) {
        if (txtPaymentAmount == null) {
            showAlert("Error", "Payment amount field not available");
            return;
        }
        try {
            paymentAmountValue = Double.parseDouble(txtPaymentAmount.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid payment amount");
            return;
        }
    } else {
        paymentAmountValue = orderAmount - amountPaid; // Default to full payment
    }
    
    // FIX: Make final for lambda
    final double finalPaymentAmount = paymentAmountValue;
    final int finalOrderId = currentOrderId;
    final int finalCustomerId = currentCustomerId;

    progressIndicator.setVisible(true);
    btnMakePayment.setDisable(true);

    new Thread(() -> {
        try {
            Thread.sleep(2000); // Simulate payment processing
            
            // Use dbPaymentMethod for database storage
            boolean success = paymentDAO.processPayment(finalOrderId, finalCustomerId, 
                                                      finalPaymentAmount, finalDbPaymentMethod, transactionId);
            
            javafx.application.Platform.runLater(() -> {
                progressIndicator.setVisible(false);
                
                if (success) {

                    String message = String.format("Payment processed successfully!\n" +
                            "Amount: Rs. %.2f\nPayment Method: %s\nTransaction ID: %s", 
                            finalPaymentAmount, displayPaymentMethod, transactionId);
                    
                    if (selected == rbPartialPayment) {
                        message += "\n\nNote: This is a partial payment. Remaining balance will be due later.";
                    }
                    
                    showAlert("Success", message);
                                                                notificationService.notifyPaymentProcessed();

                    clearForm();
                } else {
                    showAlert("Error", "Payment failed. Please try again.");
                    btnMakePayment.setDisable(false);
                }
            });
            
        } catch (InterruptedException e) {
            javafx.application.Platform.runLater(() -> {
                progressIndicator.setVisible(false);
                btnMakePayment.setDisable(false);
                showAlert("Error", "Payment processing interrupted: " + e.getMessage());
            });
        }
    }).start();
}

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    private void clearForm() {
        txtOrderId.clear();
        lblVerifyStatus.setText("");
        orderDetailsBox.setVisible(false);
        orderDetailsBox.setManaged(false);
        paymentMethodBox.setVisible(false);
        paymentMethodBox.setManaged(false);
        paymentMethodGroup.selectToggle(null);
        txtCardNumber.clear();
        txtExpiryDate.clear();
        txtCVV.clear();
        txtCardholderName.clear();
        if (txtPaymentAmount != null) {
            txtPaymentAmount.clear();
        }
        btnMakePayment.setDisable(true);
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}