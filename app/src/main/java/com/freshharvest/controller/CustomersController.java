package com.freshharvest.controller;

import com.freshharvest.app.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CustomersController implements Initializable {

    // Statistics Labels
    @FXML private Label lblTotalCustomers;
    @FXML private Label lblActiveCustomers;
    @FXML private Label lblNewThisMonth;
    @FXML private Label lblTotalRevenue;
    @FXML private Label lblSearchCount;

    // Search
    @FXML private TextField txtSearch;

    // Table and Columns
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, Integer> colCustomerId;
    @FXML private TableColumn<Customer, String> colCustomerName;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, Integer> colTotalOrders;
    @FXML private TableColumn<Customer, String> colTotalSpent;
    @FXML private TableColumn<Customer, String> colLastOrder;
    @FXML private TableColumn<Customer, String> colCustomerSince;

    private ObservableList<Customer> customersData;
    private FilteredList<Customer> filteredCustomers;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize data
            customersData = FXCollections.observableArrayList();
            filteredCustomers = new FilteredList<>(customersData);

            // Setup UI components
            setupTableColumns();
            setupSearchFilter();
            loadSampleData(); // Load sample data for UI testing
            updateStatistics();
            
        } catch (Exception e) {
            handleError("Initialization Error", "Failed to initialize customers: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        try {
            // Set up cell value factories
            colCustomerId.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCustomerId()).asObject());
            
            colCustomerName.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
            
            colEmail.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
            
            colPhone.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getPhone() != null ? cellData.getValue().getPhone() : "N/A"
                ));
            
            colTotalOrders.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTotalOrders()).asObject());
            
            colTotalSpent.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    String.format("$%.2f", cellData.getValue().getTotalSpent())
                ));
            
            colLastOrder.setCellValueFactory(cellData -> {
                LocalDate lastOrder = cellData.getValue().getLastOrderDate();
                return new javafx.beans.property.SimpleStringProperty(
                    lastOrder != null ? lastOrder.format(dateFormatter) : "No orders"
                );
            });
            
            colCustomerSince.setCellValueFactory(cellData -> {
                LocalDate customerSince = cellData.getValue().getCustomerSince();
                return new javafx.beans.property.SimpleStringProperty(
                    customerSince != null ? customerSince.format(dateFormatter) : "N/A"
                );
            });

            // Set the table data
            customersTable.setItems(filteredCustomers);
            
        } catch (Exception e) {
            handleError("Table Setup Error", "Failed to setup table columns: " + e.getMessage());
        }
    }

    private void setupSearchFilter() {
        try {
            // Add search listener
            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                updateSearchFilter();
            });
        } catch (Exception e) {
            handleError("Search Setup Error", "Failed to setup search: " + e.getMessage());
        }
    }

    private void updateSearchFilter() {
        try {
            String searchText = txtSearch.getText();
            if (searchText == null || searchText.trim().isEmpty()) {
                filteredCustomers.setPredicate(customer -> true);
            } else {
                String lowerCaseFilter = searchText.toLowerCase();
                filteredCustomers.setPredicate(customer -> {
                    if (customer.getFullName().toLowerCase().contains(lowerCaseFilter)) return true;
                    if (customer.getEmail().toLowerCase().contains(lowerCaseFilter)) return true;
                    if (customer.getPhone() != null && customer.getPhone().toLowerCase().contains(lowerCaseFilter)) return true;
                    return false;
                });
            }
            updateSearchCount();
        } catch (Exception e) {
            handleError("Search Error", "Failed to apply search filter: " + e.getMessage());
        }
    }

    private void loadSampleData() {
        try {
            // Add sample data for UI testing
            Customer customer1 = new Customer();
            customer1.setCustomerId(1);
            customer1.setFirstName("John");
            customer1.setLastName("Doe");
            customer1.setEmail("john.doe@email.com");
            customer1.setPhone("+1234567890");
            customer1.setTotalOrders(5);
            customer1.setTotalSpent(245.75);
            customer1.setLastOrderDate(LocalDate.now().minusDays(5));
            customer1.setCustomerSince(LocalDate.now().minusMonths(3));
            customersData.add(customer1);

            Customer customer2 = new Customer();
            customer2.setCustomerId(2);
            customer2.setFirstName("Alice");
            customer2.setLastName("Smith");
            customer2.setEmail("alice.smith@email.com");
            customer2.setPhone("+1987654321");
            customer2.setTotalOrders(12);
            customer2.setTotalSpent(589.50);
            customer2.setLastOrderDate(LocalDate.now().minusDays(15));
            customer2.setCustomerSince(LocalDate.now().minusYears(1));
            customersData.add(customer2);

            Customer customer3 = new Customer();
            customer3.setCustomerId(3);
            customer3.setFirstName("Bob");
            customer3.setLastName("Johnson");
            customer3.setEmail("bob.johnson@email.com");
            customer3.setPhone("+1122334455");
            customer3.setTotalOrders(2);
            customer3.setTotalSpent(89.99);
            customer3.setLastOrderDate(LocalDate.now().minusMonths(2));
            customer3.setCustomerSince(LocalDate.now().minusMonths(2));
            customersData.add(customer3);

            Customer customer4 = new Customer();
            customer4.setCustomerId(4);
            customer4.setFirstName("Sarah");
            customer4.setLastName("Wilson");
            customer4.setEmail("sarah.wilson@email.com");
            customer4.setPhone("+1555666777");
            customer4.setTotalOrders(8);
            customer4.setTotalSpent(420.25);
            customer4.setLastOrderDate(LocalDate.now().minusDays(2));
            customer4.setCustomerSince(LocalDate.now().minusMonths(6));
            customersData.add(customer4);

            Customer customer5 = new Customer();
            customer5.setCustomerId(5);
            customer5.setFirstName("Mike");
            customer5.setLastName("Brown");
            customer5.setEmail("mike.brown@email.com");
            customer5.setPhone("+1444333222");
            customer5.setTotalOrders(0);
            customer5.setTotalSpent(0.0);
            customer5.setLastOrderDate(null);
            customer5.setCustomerSince(LocalDate.now().minusDays(10));
            customersData.add(customer5);
            
        } catch (Exception e) {
            handleError("Sample Data Error", "Failed to load sample data: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewCustomer() {
        Customer selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showAlert("View Customer", "Viewing details for: " + selected.getFullName(), Alert.AlertType.INFORMATION);
        } else {
            showAlert("Warning", "Please select a customer to view details.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleSendMessage() {
        Customer selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showAlert("Send Message", "Message feature for " + selected.getFullName() + " coming soon!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Warning", "Please select a customer to send message.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleCustomerReport() {
        showAlert("Customer Report", "Customer analytics report feature coming soon!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleRefresh() {
        showAlert("Refreshed", "Customer data refreshed successfully!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleBackToDashboard() {
        // Implementation for going back to dashboard
    }

    private void updateStatistics() {
        try {
            int total = customersData.size();
            int active = (int) customersData.stream()
                    .filter(customer -> customer.getTotalOrders() > 0)
                    .count();
            int newThisMonth = (int) customersData.stream()
                    .filter(customer -> {
                        LocalDate customerSince = customer.getCustomerSince();
                        return customerSince != null && 
                               customerSince.getMonth() == LocalDate.now().getMonth() &&
                               customerSince.getYear() == LocalDate.now().getYear();
                    })
                    .count();
            
            double totalRevenue = customersData.stream()
                    .mapToDouble(Customer::getTotalSpent)
                    .sum();

            lblTotalCustomers.setText(String.valueOf(total));
            lblActiveCustomers.setText(String.valueOf(active));
            lblNewThisMonth.setText(String.valueOf(newThisMonth));
            lblTotalRevenue.setText(String.format("$%.2f", totalRevenue));
            updateSearchCount();
            
        } catch (Exception e) {
            handleError("Statistics Error", "Failed to update statistics: " + e.getMessage());
        }
    }

    private void updateSearchCount() {
        try {
            lblSearchCount.setText("Found " + filteredCustomers.size() + " customers");
        } catch (Exception e) {
            lblSearchCount.setText("Error counting customers");
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        try {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing alert: " + e.getMessage());
        }
    }

    private void handleError(String title, String message) {
        System.err.println(title + ": " + message);
        showAlert(title, message, Alert.AlertType.ERROR);
    }
}