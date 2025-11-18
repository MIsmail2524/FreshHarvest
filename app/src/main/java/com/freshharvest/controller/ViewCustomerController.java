package com.freshharvest.controller;

import com.freshharvest.app.Customer;
import com.freshharvest.app.CustomerService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class ViewCustomerController {

    @FXML private Label lblCustomerName;
    @FXML private Label lblCustomerEmail;
    @FXML private Label lblCustomerPhone;
    @FXML private Label lblCustomerAddress;
    @FXML private Label lblTotalOrders;
    @FXML private Label lblTotalSpent;
    @FXML private Label lblLastOrder;
    @FXML private Label lblCustomerSince;

    private Stage dialogStage;
    private Customer customer;
    private CustomerService customerService;

    @FXML
    public void initialize() {
        customerService = new CustomerService();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCustomerData(Customer customer) {
        this.customer = customer;
        populateCustomerData();
    }

    private void populateCustomerData() {
        if (customer != null) {
            lblCustomerName.setText(customer.getFullName());
            lblCustomerEmail.setText(customer.getEmail());
            lblCustomerPhone.setText(customer.getPhone() != null ? customer.getPhone() : "Not provided");
            lblCustomerAddress.setText(customer.getAddress() != null ? customer.getAddress() : "Not provided");
            lblTotalOrders.setText(String.valueOf(customer.getTotalOrders()));
            lblTotalSpent.setText(String.format("$%.2f", customer.getTotalSpent()));
            
            if (customer.getLastOrderDate() != null) {
                lblLastOrder.setText(customer.getLastOrderDate().toString());
            } else {
                lblLastOrder.setText("No orders yet");
            }
            
            if (customer.getCustomerSince() != null) {
                lblCustomerSince.setText(customer.getCustomerSince().toString());
            } else {
                lblCustomerSince.setText("N/A");
            }
        }
    }

    @FXML
    private void handleClose() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    @FXML
    private void handleViewOrderHistory() {
        try {
            if (customer != null) {
                showAlert("Order History", 
                         "Order history for " + customer.getFullName() + ":\n\n" +
                         "Total Orders: " + customer.getTotalOrders() + "\n" +
                         "Total Spent: $" + String.format("%.2f", customer.getTotalSpent()) + "\n" +
                         "Last Order: " + (customer.getLastOrderDate() != null ? 
                             customer.getLastOrderDate().toString() : "No orders"),
                         Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load order history: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSendMessage() {
        try {
            if (customer != null) {
                showAlert("Send Message", 
                         "Message feature for " + customer.getFullName() + " coming soon!\n\n" +
                         "Email: " + customer.getEmail() + "\n" +
                         "Phone: " + (customer.getPhone() != null ? customer.getPhone() : "Not available"),
                         Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to send message: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Static method to open customer details dialog
    public static void showCustomerDetailsDialog(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewCustomerController.class.getResource("/fxml/view-customer.fxml"));
            Parent root = loader.load();

            ViewCustomerController controller = loader.getController();
            controller.setCustomerData(customer);

            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);

            dialogStage.setTitle("Customer Details - " + customer.getFullName());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);

            Scene scene = new Scene(root, 500, 400);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Failed to load customer details dialog: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open customer details");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
}