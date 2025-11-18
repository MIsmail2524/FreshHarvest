package com.freshharvest.controller;

import com.freshharvest.app.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class OrderDetailsController {

    @FXML private Label lblOrderId;
    @FXML private Label lblOrderNumber;
    @FXML private Label lblCustomerName;
    @FXML private Label lblCustomerEmail;
    @FXML private Label lblCustomerPhone;
    @FXML private Label lblOrderDate;
    @FXML private Label lblTotalAmount;
    @FXML private Label lblStatus;
    @FXML private Label lblDeliveryType;
    @FXML private Label lblDeliveryAddress;
    @FXML private Label lblNotes;

    public void setOrderData(Order order) {
        lblOrderId.setText(String.valueOf(order.getOrderId()));
        lblOrderNumber.setText(order.getOrderNumber());
        lblCustomerName.setText(order.getCustomerName());
        lblCustomerEmail.setText(order.getCustomerEmail());
        lblCustomerPhone.setText(order.getCustomerPhone());
        lblOrderDate.setText(order.getOrderDate().toString());
        lblTotalAmount.setText(String.format("%.2f", order.getTotalAmount()));
        lblStatus.setText(order.getStatus());
        lblDeliveryType.setText(order.getDeliveryType());
        lblDeliveryAddress.setText(order.getDeliveryAddress());
        lblNotes.setText(order.getNotes() != null ? order.getNotes() : "-");
    }

    @FXML
    private void closePopup() {
        Stage stage = (Stage) lblOrderId.getScene().getWindow();
        stage.close();
    }
}
