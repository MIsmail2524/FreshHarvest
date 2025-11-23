package com.freshharvest.controller.CustomerControllers;

import com.freshharvest.dao.CustomerDAO;
import com.freshharvest.dao.ProductDAO;
import com.freshharvest.app.Customers.CartItem;
import com.freshharvest.app.Customers.Product;
import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;
import com.freshharvest.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AddOrderController implements Initializable, DataChangeListener { 
    
    private NotificationService notificationService;

    @FXML private ComboBox<Product> productComboBox;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button btnAddToCart;

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colCartProduct;
    @FXML private TableColumn<CartItem, Double> colCartPrice;
    @FXML private TableColumn<CartItem, Integer> colCartQuantity;
    @FXML private TableColumn<CartItem, Double> colCartSubtotal;
    @FXML private TableColumn<CartItem, Void> colCartAction;

    @FXML private Label lblTotalAmount;
    @FXML private Label lblItemCount;
    @FXML private ComboBox<String> paymentMethodCombo;

    private ObservableList<Product> productsList;
    private ObservableList<CartItem> cartItems;
    
    private final ProductDAO productDAO = new ProductDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private int currentCustomerId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            currentCustomerId = 1; // Get from session in real implementation
              notificationService = NotificationService.getInstance(); 
            notificationService.addListener(this); 
            initializeProductSelection();
            initializeCartTable();
            setupForm();
            loadProducts();
            
        } catch (Exception e) {
            showAlert("Error", "Failed to initialize order form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDataChanged(String dataType) {
        if ("ORDER_CREATED".equals(dataType) || "PAYMENT_PROCESSED".equals(dataType)) {
            javafx.application.Platform.runLater(() -> {
                // Refresh product availability when orders change
                loadProducts();
                System.out.println("Products automatically refreshed");
            });
        }
    }


    private void initializeProductSelection() {
        // Setup product combo box
        productComboBox.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                } else {
                    setText(product.getName() + " - Rs. " + String.format("%.2f", product.getPrice()) + " (" + product.getStockQuantity() + " available)");
                }
            }
        });
        
        productComboBox.setButtonCell(new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText("Select a product");
                } else {
                    setText(product.getName() + " - Rs. " + String.format("%.2f", product.getPrice()));
                }
            }
        });
        
        // Setup quantity spinner
        quantitySpinner.setValueFactory(new IntegerSpinnerValueFactory(1, 100, 1));
        quantitySpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        
        // Setup add to cart button
        btnAddToCart.setOnAction(event -> addToCart());
        
        // Update quantity spinner max based on selected product
        productComboBox.valueProperty().addListener((obs, oldProduct, newProduct) -> {
            if (newProduct != null) {
                int maxQuantity = newProduct.getStockQuantity();
                quantitySpinner.getValueFactory().setValue(1);
                ((IntegerSpinnerValueFactory) quantitySpinner.getValueFactory()).setMax(maxQuantity);
                
                // Enable/disable add button based on stock
                btnAddToCart.setDisable(maxQuantity <= 0);
                
                if (maxQuantity <= 0) {
                    showAlert("Out of Stock", newProduct.getName() + " is currently out of stock.");
                }
            } else {
                btnAddToCart.setDisable(true);
            }
        });
    }

    private void initializeCartTable() {
        colCartProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCartPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colCartQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCartSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        
        // Format price columns
        colCartPrice.setCellFactory(column -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("Rs. %.2f", price));
                }
            }
        });
        
        colCartSubtotal.setCellFactory(column -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(String.format("Rs. %.2f", subtotal));
                }
            }
        });
        
        // Remove button column
        colCartAction.setCellFactory(param -> new TableCell<CartItem, Void>() {
            private final Button removeButton = new Button("Remove");
            
            {
                removeButton.getStyleClass().add("button-danger");
                removeButton.setPrefWidth(80);
                
                removeButton.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        removeFromCart(item);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });
    }

    private void setupForm() {
        // Setup payment methods
        ObservableList<String> paymentMethods = FXCollections.observableArrayList(
            "Cash", "Credit",  "Digital Wallet"
        );
        paymentMethodCombo.setItems(paymentMethods);
        paymentMethodCombo.setValue("Cash");
        
        // Initialize cart
        cartItems = FXCollections.observableArrayList();
        cartTable.setItems(cartItems);
        updateCartTotals();
    }

    private void loadProducts() {
        try {
            List<Product> products = productDAO.getAvailableProducts();
            productsList = FXCollections.observableArrayList(products);
            productComboBox.setItems(productsList);
            
            System.out.println("Loaded " + products.size() + " products");
            
        } catch (Exception e) {
            showAlert("Error", "Failed to load products: " + e.getMessage());
            e.printStackTrace();
            // Load sample data for testing
            loadSampleProducts();
        }
    }

    private void loadSampleProducts() {
        // Sample data for testing
        productsList = FXCollections.observableArrayList();
        
        productsList.add(new Product(1, "Fresh Tomatoes", "Organic fresh tomatoes", "Vegetables", 120.0, 50, "kg"));
        productsList.add(new Product(2, "Potatoes", "Fresh farm potatoes", "Vegetables", 80.0, 100, "kg"));
        productsList.add(new Product(3, "Carrots", "Organic carrots", "Vegetables", 90.0, 75, "kg"));
        productsList.add(new Product(4, "Apples", "Fresh red apples", "Fruits", 200.0, 30, "kg"));
        productsList.add(new Product(5, "Bananas", "Ripe bananas", "Fruits", 60.0, 40, "dozen"));
        productsList.add(new Product(6, "Milk", "Fresh dairy milk", "Dairy", 60.0, 25, "liter"));
        productsList.add(new Product(7, "Eggs", "Farm fresh eggs", "Dairy", 120.0, 60, "dozen"));
        productsList.add(new Product(8, "Wheat Flour", "Organic wheat flour", "Grains", 45.0, 80, "kg"));
        
        productComboBox.setItems(productsList);
        System.out.println("Loaded sample products for testing");
    }

    private void addToCart() {
        Product selectedProduct = productComboBox.getValue();
        Integer quantity = quantitySpinner.getValue();
        
        if (selectedProduct == null) {
            showAlert("Warning", "Please select a product first.");
            return;
        }
        
        if (quantity == null || quantity <= 0) {
            showAlert("Warning", "Please enter a valid quantity.");
            return;
        }
        
        if (quantity > selectedProduct.getStockQuantity()) {
            showAlert("Stock Limit", "Only " + selectedProduct.getStockQuantity() + " units available.");
            return;
        }
        
        // Check if product already in cart
        for (CartItem item : cartItems) {
            if (item.getProductId() == selectedProduct.getId()) {
                // Update existing item quantity
                int newQuantity = item.getQuantity() + quantity;
                if (newQuantity <= selectedProduct.getStockQuantity()) {
                    item.setQuantity(newQuantity);
                    cartTable.refresh();
                    updateCartTotals();
                    showAlert("Success", "Updated quantity of " + selectedProduct.getName() + " to " + newQuantity);
                } else {
                    showAlert("Stock Limit", "Cannot add more. Only " + selectedProduct.getStockQuantity() + " units available.");
                }
                resetProductSelection();
                return;
            }
        }
        
        // Add new item to cart
        CartItem newItem = new CartItem(
            selectedProduct.getId(),
            selectedProduct.getName(),
            selectedProduct.getPrice(),
            quantity
        );
        cartItems.add(newItem);
        updateCartTotals();
        cartTable.refresh();
        
        showAlert("Success", selectedProduct.getName() + " added to cart!");
        resetProductSelection();
    }
    
    private void resetProductSelection() {
        productComboBox.setValue(null);
        quantitySpinner.getValueFactory().setValue(1);
    }

    private void removeFromCart(CartItem item) {
        cartItems.remove(item);
        updateCartTotals();
        cartTable.refresh();
        showAlert("Removed", item.getProductName() + " removed from cart.");
    }

    private void updateCartTotals() {
        double total = 0.0;
        int itemCount = 0;
        
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
            itemCount += item.getQuantity();
        }
        
        lblTotalAmount.setText(String.format("Rs. %.2f", total));
        lblItemCount.setText(itemCount + " items");
    }

    @FXML
    private void onPlaceOrder() {
        if (cartItems.isEmpty()) {
            showAlert("Warning", "Your cart is empty. Please add items before placing an order.");
            return;
        }
        
        if (paymentMethodCombo.getValue() == null) {
            showAlert("Warning", "Please select a payment method.");
            return;
        }
        
        try {
            // Get total amount without currency symbol
            String totalAmount = String.format("%.2f", 
                cartItems.stream().mapToDouble(CartItem::getSubtotal).sum());
            
            System.out.println("Placing order with:");
            System.out.println("Customer ID: " + currentCustomerId);
            System.out.println("Total Amount: " + totalAmount);
            System.out.println("Payment Method: " + paymentMethodCombo.getValue());
            System.out.println("Items: " + cartItems.size());
            
            // Create order in database
            int orderId = customerDAO.createOrder(
                currentCustomerId,
                cartItems,
                totalAmount,
                paymentMethodCombo.getValue()
            );
            
            if (orderId > 0) {
                showAlert("Order Successful", 
                    "Order placed successfully!\n\n" +
                    "Order ID: #" + orderId + "\n" +
                    "Total Amount: " + lblTotalAmount.getText() + "\n" +
                    "Payment Method: " + paymentMethodCombo.getValue());
                                notificationService.notifyDataChanged("ORDER_CREATED");

                // Clear cart and form
                cartItems.clear();
                updateCartTotals();
                paymentMethodCombo.setValue("Cash on Delivery");
                
            } else {
                showAlert("Error", "Failed to place order. Please try again.");
            }
            
        } catch (Exception e) {
            showAlert("Error", "Failed to place order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onClearCart() {
        if (!cartItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Clear Cart");
            alert.setHeaderText("Clear Shopping Cart");
            alert.setContentText("Are you sure you want to remove all items from your cart?");
            
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            
            if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                cartItems.clear();
                updateCartTotals();
                cartTable.refresh();
                showAlert("Cart Cleared", "All items have been removed from your cart.");
            }
        } else {
            showAlert("Cart Empty", "Your cart is already empty.");
        }
    }

   

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}