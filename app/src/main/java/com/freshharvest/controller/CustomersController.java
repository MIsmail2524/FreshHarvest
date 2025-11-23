package com.freshharvest.controller;

import com.freshharvest.app.Customer;
import com.freshharvest.app.CustomerService;
import com.freshharvest.dao.CustomerDAO;
import com.freshharvest.service.SessionService;
import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;
import com.freshharvest.app.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CustomersController implements Initializable, DataChangeListener { // IMPLEMENT LISTENER
    private NotificationService notificationService; // ADD THIS

    @FXML private Label lblTotalCustomers;
    @FXML private Label lblActiveCustomers;
    @FXML private Label lblNewThisMonth;
    @FXML private Label lblTotalRevenue;
    @FXML private TextField txtSearch;
    @FXML private Label lblSearchCount;
    
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, Integer> colCustomerId;
    @FXML private TableColumn<Customer, String> colCustomerName;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, Integer> colTotalOrders;
    @FXML private TableColumn<Customer, Double> colTotalSpent;
    @FXML private TableColumn<Customer, String> colLastOrder;
    @FXML private TableColumn<Customer, String> colCustomerSince;

    private ObservableList<Customer> customersList;
    private CustomerDAO customerDAO;
    private CustomerService customerService;
    private SessionService sessionService;
    private int currentFarmerId;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initializeServices();
            initializeTableColumns();
            loadCustomerData();
            updateStatistics();
             notificationService = NotificationService.getInstance(); // ADD THIS
            notificationService.addListener(this); // ADD THIS
            
        } catch (Exception e) {
            System.err.println("Error initializing CustomersController: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to initialize customer management: " + e.getMessage());
        }
    }

    private void initializeServices() {
        customerDAO = new CustomerDAO();
        customerService = CustomerService.getInstance();
        sessionService = SessionService.getInstance();
        
        // Get current farmer ID from session
        User currentUser = sessionService.getCurrentUser();
        if (currentUser != null && currentUser.getRole() == com.freshharvest.app.User.Role.FARMER) {
            // For now, use a default farmer ID since we don't have farmer_id in users table
            currentFarmerId = 1; // Default farmer ID
        } else {
            currentFarmerId = 1; // Fallback for testing
        }
    }

     @Override
    public void onDataChanged(String dataType) {
        if ("ORDER_STATUS_CHANGED".equals(dataType) || "PAYMENT_PROCESSED".equals(dataType)) {
            javafx.application.Platform.runLater(() -> {
                loadCustomerData();
                updateStatistics();
                System.out.println("Customers data automatically refreshed");
            });
        }}

    private void initializeTableColumns() {
        // Initialize table columns
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colTotalOrders.setCellValueFactory(new PropertyValueFactory<>("totalOrders"));
        colTotalSpent.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));

        // Fix: Handle LocalDate columns properly
        colLastOrder.setCellValueFactory(cellData -> {
            LocalDate lastOrderDate = cellData.getValue().getLastOrderDate();
            if (lastOrderDate != null) {
                return new javafx.beans.property.SimpleStringProperty(lastOrderDate.format(dateFormatter));
            } else {
                return new javafx.beans.property.SimpleStringProperty("No orders");
            }
        });

        colCustomerSince.setCellValueFactory(cellData -> {
            LocalDate customerSince = cellData.getValue().getCustomerSince();
            if (customerSince != null) {
                return new javafx.beans.property.SimpleStringProperty(customerSince.format(dateFormatter));
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });

        // Format columns
        colTotalSpent.setCellFactory(column -> new TableCell<Customer, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("Rs. %.2f", amount));
                }
            }
        });

        // Make customer name column show both first and last name
        colCustomerName.setCellFactory(column -> new TableCell<Customer, String>() {
            @Override
            protected void updateItem(String firstName, boolean empty) {
                super.updateItem(firstName, empty);
                if (empty || firstName == null) {
                    setText(null);
                } else {
                    Customer customer = getTableView().getItems().get(getIndex());
                    String lastName = customer.getLastName();
                    if (lastName != null && !lastName.isEmpty()) {
                        setText(firstName + " " + lastName);
                    } else {
                        setText(firstName);
                    }
                }
            }
        });
    }

    private void loadCustomerData() {
        try {
            // Load customers from DAO
            List<Customer> customers = customerDAO.findByFarmerId(currentFarmerId);
            customersList = FXCollections.observableArrayList(customers);
            
            // If no customers found in database, load sample data
            if (customersList.isEmpty()) {
                loadSampleCustomers();
            }
            
            customersTable.setItems(customersList);
            updateSearchCount();
            
        } catch (Exception e) {
            System.err.println("Error loading customer data: " + e.getMessage());
            e.printStackTrace();
            loadSampleCustomers(); // Fallback to sample data
        }
    }

    private void loadSampleCustomers() {
        customersList = FXCollections.observableArrayList();
        
        // Add sample customers for demonstration
        customersList.add(new Customer(1, "Saeed", "Customer", "saeed@freshharvest.com", 
            "0300-1234567", 5, 12500.0, 
            LocalDate.now().minusDays(10), LocalDate.now().minusMonths(3)));
        
        customersList.add(new Customer(2, "Ibrahim", "Client", "ibrahim@freshharvest.com", 
            "0312-7654321", 8, 18750.0, 
            LocalDate.now().minusDays(5), LocalDate.now().minusMonths(2)));
        
        
        customersTable.setItems(customersList);
        System.out.println("Loaded sample customers for demonstration");
    }

    private void updateSearchCount() {
        if (customersList != null) {
            int count = customersList.size();
            lblSearchCount.setText("Total: " + count + " customer" + (count != 1 ? "s" : ""));
        } else {
            lblSearchCount.setText("Total: 0 customers");
        }
    }

    private void updateStatistics() {
        try {
            // For now, use calculated statistics to avoid database issues
            calculateStatisticsFromData();
            
        } catch (Exception e) {
            System.err.println("Error loading statistics: " + e.getMessage());
            // Set default values
            setDefaultStatistics();
        }
    }

    private void calculateStatisticsFromData() {
        if (customersList == null || customersList.isEmpty()) {
            setDefaultStatistics();
            return;
        }

        int totalCustomers = customersList.size();
        int activeCustomers = 0;
        int newThisMonth = 0;
        double totalRevenue = 0.0;

        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);

        for (Customer customer : customersList) {
            totalRevenue += customer.getTotalSpent();
            
            // Consider customer active if they have orders in last 3 months
            if (customer.getLastOrderDate() != null && 
                customer.getLastOrderDate().isAfter(threeMonthsAgo)) {
                activeCustomers++;
            }
            
            // Consider customer new if registered this month
            if (customer.getCustomerSince() != null && 
                customer.getCustomerSince().isAfter(oneMonthAgo)) {
                newThisMonth++;
            }
        }

        lblTotalCustomers.setText(String.valueOf(totalCustomers));
        lblActiveCustomers.setText(String.valueOf(activeCustomers));
        lblNewThisMonth.setText(String.valueOf(newThisMonth));
        lblTotalRevenue.setText(String.format("Rs. %,.2f", totalRevenue));
    }

    private void setDefaultStatistics() {
        lblTotalCustomers.setText("0");
        lblActiveCustomers.setText("0");
        lblNewThisMonth.setText("0");
        lblTotalRevenue.setText("Rs. 0.00");
    }

    @FXML
    private void handleViewCustomer() {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert("Selection Required", "Please select a customer to view details.");
            return;
        }
        
        try {
            // Show customer details in a dialog
            showCustomerDetails(selectedCustomer);
        } catch (Exception e) {
            showAlert("Error", "Failed to view customer details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCustomerReport() {
        try {
            // Generate customer report
            generateCustomerReport();
        } catch (Exception e) {
            showAlert("Error", "Failed to generate customer report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        try {
            loadCustomerData();
            updateStatistics();
            txtSearch.clear();
            showAlert("Success", "Customer data refreshed successfully!");
        } catch (Exception e) {
            showAlert("Error", "Failed to refresh customer data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showCustomerDetails(Customer customer) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Customer Details");
        alert.setHeaderText("Customer Information: " + customer.getFirstName() + " " + customer.getLastName());
        
        StringBuilder content = new StringBuilder();
        content.append("Customer ID: ").append(customer.getCustomerId()).append("\n");
        content.append("Name: ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append("\n");
        content.append("Email: ").append(customer.getEmail()).append("\n");
        content.append("Phone: ").append(customer.getPhone()).append("\n");
        content.append("Address: ").append(customer.getAddress()).append("\n");
        content.append("Total Orders: ").append(customer.getTotalOrders()).append("\n");
        content.append("Total Spent: Rs. ").append(String.format("%.2f", customer.getTotalSpent())).append("\n");
        content.append("Last Order: ").append(customer.getLastOrderDate() != null ? 
            customer.getLastOrderDate().format(dateFormatter) : "No orders").append("\n");
        content.append("Customer Since: ").append(customer.getCustomerSince() != null ? 
            customer.getCustomerSince().format(dateFormatter) : "N/A");
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    private void generateCustomerReport() {
        if (customersList == null || customersList.isEmpty()) {
            showAlert("No Data", "No customer data available to generate report.");
            return;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("CUSTOMER MANAGEMENT REPORT\n");
        report.append("Generated on: ").append(LocalDate.now().format(dateFormatter)).append("\n\n");
        report.append("Summary:\n");
        report.append("- Total Customers: ").append(lblTotalCustomers.getText()).append("\n");
        report.append("- Active Customers: ").append(lblActiveCustomers.getText()).append("\n");
        report.append("- New This Month: ").append(lblNewThisMonth.getText()).append("\n");
        report.append("- Total Revenue: ").append(lblTotalRevenue.getText()).append("\n\n");
        
        report.append("Customer Details:\n");
        report.append(String.format("%-5s %-25s %-25s %-15s %-12s %-12s\n", 
            "ID", "Name", "Email", "Phone", "Orders", "Total Spent"));
        report.append("------------------------------------------------------------------------------------------------\n");
        
        for (Customer customer : customersList) {
            report.append(String.format("%-5d %-25s %-25s %-15s %-12d Rs. %-9.2f\n",
                customer.getCustomerId(),
                (customer.getFirstName() + " " + customer.getLastName()).substring(0, Math.min(24, (customer.getFirstName() + " " + customer.getLastName()).length())),
                customer.getEmail().substring(0, Math.min(24, customer.getEmail().length())),
                customer.getPhone(),
                customer.getTotalOrders(),
                customer.getTotalSpent()));
        }
        
        // Show report in dialog
        TextArea textArea = new TextArea(report.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(600, 400);
        
        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Customer Report");
        alert.setHeaderText("Customer Management Report");
        alert.getDialogPane().setContent(scrollPane);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}