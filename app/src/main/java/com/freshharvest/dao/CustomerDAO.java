package com.freshharvest.dao;

import com.freshharvest.app.Customer;
import com.freshharvest.app.CustomerService;
import com.freshharvest.app.Customers.CartItem;
import com.freshharvest.app.Customers.CustomerOrder;
import com.freshharvest.util.DBHandle;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    public List<Customer> findByFarmerId(int farmerId) {
    List<Customer> customers = new ArrayList<>();

    String sql =
        "SELECT c.customer_id, c.first_name, c.last_name, c.email, c.phone, c.address, " +
        "       COUNT(o.order_id) AS total_orders, " +
        "       COALESCE(SUM(o.total_amount), 0) AS total_spent, " +
        "       MAX(o.order_date) AS last_order_date, " +
        "       c.registration_date " +
        "FROM customers c " +
        "LEFT JOIN orders o ON c.customer_id = o.customer_id " +
        "WHERE o.farmer_id = ? " +
        "GROUP BY c.customer_id";

    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, farmerId);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Customer customer = new Customer();

            customer.setCustomerId(rs.getInt("customer_id"));
            customer.setFirstName(rs.getString("first_name"));
            customer.setLastName(rs.getString("last_name"));
            customer.setEmail(rs.getString("email"));
            customer.setPhone(rs.getString("phone"));
            customer.setAddress(rs.getString("address"));

             customer.setTotalOrders(rs.getInt("total_orders"));
             customer.setTotalSpent(rs.getDouble("total_spent"));

            Timestamp lastOrder = rs.getTimestamp("last_order_date");
            if (lastOrder != null) {
                customer.setLastOrderDate(lastOrder.toLocalDateTime().toLocalDate());
            }

            Timestamp regDate = rs.getTimestamp("registration_date");
            if (regDate != null) {
                customer.setCustomerSince(regDate.toLocalDateTime().toLocalDate());
            }

            customers.add(customer);
        }

    } catch (SQLException e) {
        System.err.println("DAO error finding customers: " + e.getMessage());
        throw new RuntimeException(e);
    }

    return customers;
}

    
    public Customer findById(int customerId, int farmerId) {
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setTotalOrders(rs.getInt("total_orders"));
                customer.setTotalSpent(rs.getDouble("total_spent"));
                
                Timestamp lastOrder = rs.getTimestamp("last_order_date");
                if (lastOrder != null) {
                    customer.setLastOrderDate(lastOrder.toLocalDateTime().toLocalDate());
                }
                
                Timestamp customerSince = rs.getTimestamp("customer_since");
                if (customerSince != null) {
                    customer.setCustomerSince(customerSince.toLocalDateTime().toLocalDate());
                }
                
                return customer;
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error finding customer by ID: " + e.getMessage());
        }
        return null;
    }
    
    public List<Customer> searchByTerm(String searchTerm, int farmerId) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setTotalOrders(rs.getInt("total_orders"));
                customer.setTotalSpent(rs.getDouble("total_spent"));
                
                Timestamp lastOrder = rs.getTimestamp("last_order_date");
                if (lastOrder != null) {
                    customer.setLastOrderDate(lastOrder.toLocalDateTime().toLocalDate());
                }
                
                Timestamp customerSince = rs.getTimestamp("customer_since");
                if (customerSince != null) {
                    customer.setCustomerSince(customerSince.toLocalDateTime().toLocalDate());
                }
                
                customers.add(customer);
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error searching customers: " + e.getMessage());
            throw new RuntimeException("Database error while searching customers", e);
        }
        return customers;
    }
    
    public List<Customer> findTopBySpending(int limit, int farmerId) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setTotalOrders(rs.getInt("total_orders"));
                customer.setTotalSpent(rs.getDouble("total_spent"));
                
                Timestamp lastOrder = rs.getTimestamp("last_order_date");
                if (lastOrder != null) {
                    customer.setLastOrderDate(lastOrder.toLocalDateTime().toLocalDate());
                }
                
                Timestamp customerSince = rs.getTimestamp("customer_since");
                if (customerSince != null) {
                    customer.setCustomerSince(customerSince.toLocalDateTime().toLocalDate());
                }
                
                customers.add(customer);
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error finding top customers: " + e.getMessage());
            throw new RuntimeException("Database error while fetching top customers", e);
        }
        return customers;
    }
    
    public List<Customer> findNewCustomers(int farmerId) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setTotalOrders(rs.getInt("total_orders"));
                customer.setTotalSpent(rs.getDouble("total_spent"));
                
                Timestamp lastOrder = rs.getTimestamp("last_order_date");
                if (lastOrder != null) {
                    customer.setLastOrderDate(lastOrder.toLocalDateTime().toLocalDate());
                }
                
                Timestamp customerSince = rs.getTimestamp("customer_since");
                if (customerSince != null) {
                    customer.setCustomerSince(customerSince.toLocalDateTime().toLocalDate());
                }
                
                customers.add(customer);
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error finding new customers: " + e.getMessage());
            throw new RuntimeException("Database error while fetching new customers", e);
        }
        return customers;
    }
    
    public List<Customer> findInactiveCustomers(int farmerId) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setTotalOrders(rs.getInt("total_orders"));
                customer.setTotalSpent(rs.getDouble("total_spent"));
                
                Timestamp lastOrder = rs.getTimestamp("last_order_date");
                if (lastOrder != null) {
                    customer.setLastOrderDate(lastOrder.toLocalDateTime().toLocalDate());
                }
                
                Timestamp customerSince = rs.getTimestamp("customer_since");
                if (customerSince != null) {
                    customer.setCustomerSince(customerSince.toLocalDateTime().toLocalDate());
                }
                
                customers.add(customer);
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error finding inactive customers: " + e.getMessage());
            throw new RuntimeException("Database error while fetching inactive customers", e);
        }
        return customers;
    }
    
    public boolean update(Customer customer, int farmerId) {
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("DAO error updating customer: " + e.getMessage());
            return false;
        }
    }
    
    public CustomerService.CustomerStatistics getStatisticsByFarmerId(int farmerId) {
    // Updated query to match your actual database schema
    String sql = "SELECT " +
            "COUNT(DISTINCT u.id) as total_customers, " +
            "COUNT(DISTINCT CASE WHEN o.order_date >= DATE_SUB(NOW(), INTERVAL 90 DAY) THEN u.id END) as active_customers, " +
            "COUNT(DISTINCT CASE WHEN u.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN u.id END) as new_customers, " +
            "COALESCE(SUM(o.total_amount), 0) as total_revenue " +
            "FROM users u " +
            "LEFT JOIN orders o ON u.id = o.customer_id " +
            "WHERE u.role = 'CUSTOMER'";

    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            return new CustomerService.CustomerStatistics(
                rs.getInt("total_customers"),
                rs.getInt("active_customers"), 
                rs.getInt("new_customers"),
                rs.getDouble("total_revenue")
            );
        }
        
    } catch (SQLException e) {
        System.err.println("DAO error getting customer statistics: " + e.getMessage());
        e.printStackTrace();
    }
    
    // Return default statistics if query fails
    return new CustomerService.CustomerStatistics(0, 0, 0, 0.0);
}
    
    public List<CustomerService.CustomerOrder> getOrderHistory(int customerId, int farmerId) {
        List<CustomerService.CustomerOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                CustomerService.CustomerOrder order = new CustomerService.CustomerOrder(
                    rs.getString("order_number"),
                    rs.getTimestamp("order_date").toString(),
                    rs.getDouble("total_amount"),
                    rs.getString("status"),
                    rs.getInt("item_count")
                );
                orders.add(order);
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error getting customer order history: " + e.getMessage());
            throw new RuntimeException("Database error while fetching customer order history", e);
        }
        return orders;
    }


    public Object[] getCustomerStats(int customerId) {
        String sql = "SELECT " +
                "COUNT(*) as total_orders, " +
                "COALESCE(SUM(total_amount), 0) as total_spent, " +
                "SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) as pending_orders, " +
                "(SELECT p.category FROM order_items oi " +
                " JOIN orders o ON oi.order_id = o.order_id " +
                " JOIN products p ON oi.product_id = p.product_id " +
                " WHERE o.customer_id = ? " +
                " GROUP BY p.category ORDER BY COUNT(*) DESC LIMIT 1) as favorite_category " +
                "FROM orders WHERE customer_id = ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            stmt.setInt(2, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("total_orders"),
                        rs.getDouble("total_spent"),
                        rs.getInt("pending_orders"),
                        rs.getString("favorite_category")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer stats: " + e.getMessage());
            e.printStackTrace();
        }
        return new Object[]{0, 0.0, 0, null};
    }

    public List<Object[]> getMonthlySpending(int customerId) {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m') as month, " +
                     "COALESCE(SUM(total_amount), 0) as total_spent " +
                     "FROM orders " +
                     "WHERE customer_id = ? AND order_date >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
                     "GROUP BY DATE_FORMAT(order_date, '%Y-%m') " +
                     "ORDER BY month";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    data.add(new Object[]{
                        rs.getString("month"),
                        rs.getDouble("total_spent")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting monthly spending: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }

    public List<Object[]> getSpendingByCategory(int customerId) {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT p.category, SUM(oi.total_price) as category_total " +
                     "FROM order_items oi " +
                     "JOIN orders o ON oi.order_id = o.order_id " +
                     "JOIN products p ON oi.product_id = p.product_id " +
                     "WHERE o.customer_id = ? " +
                     "GROUP BY p.category " +
                     "ORDER BY category_total DESC";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    data.add(new Object[]{
                        rs.getString("category"),
                        rs.getDouble("category_total")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting spending by category: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }

    public List<Object[]> getMonthlyOrderCount(int customerId) {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m') as month, " +
                     "COUNT(*) as order_count " +
                     "FROM orders " +
                     "WHERE customer_id = ? AND order_date >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
                     "GROUP BY DATE_FORMAT(order_date, '%Y-%m') " +
                     "ORDER BY month";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    data.add(new Object[]{
                        rs.getString("month"),
                        rs.getLong("order_count")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting monthly order count: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }

    public List<Object[]> getOrderHistory(int customerId) {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m') as month, " +
                     "COUNT(*) as order_count, " +
                     "COALESCE(SUM(total_amount), 0) as total_spent " +
                     "FROM orders " +
                     "WHERE customer_id = ? AND order_date >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
                     "GROUP BY DATE_FORMAT(order_date, '%Y-%m') " +
                     "ORDER BY month";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    data.add(new Object[]{
                        rs.getString("month"),
                        rs.getLong("order_count"),
                        rs.getDouble("total_spent")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order history: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }

    public List<Object[]> getRecentOrders(int customerId, int limit) {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT order_id, order_date, total_amount, status, delivery_date " +
                     "FROM orders " +
                     "WHERE customer_id = ? " +
                     "ORDER BY order_date DESC " +
                     "LIMIT ?";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    data.add(new Object[]{
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getDate("delivery_date")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting recent orders: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }

    // Additional method to get product names for order items
 public List<Object[]> getAllCustomerOrders(int customerId) {
    List<Object[]> data = new ArrayList<>();
    String sql = "SELECT order_id, order_date, status, total_amount, delivery_date, payment_method " +
                 "FROM orders WHERE customer_id = ? ORDER BY order_date DESC";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, customerId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.add(new Object[]{
                    rs.getInt("order_id"),
                    rs.getTimestamp("order_date"),
                    rs.getString("status"),
                    rs.getDouble("total_amount"),
                    rs.getDate("delivery_date"),
                    rs.getString("payment_method")
                });
            }
        }
    } catch (SQLException e) {
        System.err.println("Error getting customer orders: " + e.getMessage());
        e.printStackTrace();
    }
    return data;
}

public List<Object[]> getOrderItems(int orderId) {
    List<Object[]> data = new ArrayList<>();
    String sql = "SELECT p.name as product_name, p.category, oi.quantity, oi.unit_price, " +
                 "(oi.quantity * oi.unit_price) as total_price " +
                 "FROM order_items oi " +
                 "JOIN products p ON oi.product_id = p.product_id " +
                 "WHERE oi.order_id = ?";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, orderId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.add(new Object[]{
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getDouble("unit_price"),
                    rs.getDouble("total_price")
                });
            }
        }
    } catch (SQLException e) {
        System.err.println("Error getting order items: " + e.getMessage());
        e.printStackTrace();
    }
    return data;
}

public boolean cancelOrder(int orderId) {
    String sql = "UPDATE orders SET status = 'Cancelled' WHERE order_id = ? AND status NOT IN ('Delivered', 'Cancelled')";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, orderId);
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.err.println("Error cancelling order: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
public int createOrder(int customerId, List<CartItem> cartItems, String totalAmount, 
                       String paymentMethod) {
    String orderSql = "INSERT INTO orders (customer_id, total_amount, status, order_date, " +
                     "payment_method) " +
                     "VALUES (?, ?, 'Pending', NOW(), ?)";
    
    String orderItemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) " +
                         "VALUES (?, ?, ?, ?, ?)";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
         PreparedStatement itemStmt = conn.prepareStatement(orderItemSql)) {
        
        // Start transaction
        conn.setAutoCommit(false);
        
        try {
            // Insert order - CORRECTED: Only 3 parameters now
            orderStmt.setInt(1, customerId);
            orderStmt.setDouble(2, Double.parseDouble(totalAmount));
            orderStmt.setString(3, paymentMethod); // This is now the 3rd parameter
            
            int affectedRows = orderStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }
            
            // Get generated order ID
            int orderId;
            try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                    System.out.println("Created order with ID: " + orderId);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
            
            // Insert order items
            for (CartItem item : cartItems) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getUnitPrice());
                itemStmt.setDouble(5, item.getSubtotal());
                itemStmt.addBatch();
                System.out.println("Adding item to order: " + item.getProductName() + " x" + item.getQuantity());
            }
            
            int[] batchResults = itemStmt.executeBatch();
            System.out.println("Inserted " + batchResults.length + " order items");
            
            conn.commit();
            return orderId;
            
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
        
    } catch (SQLException e) {
        System.err.println("Error creating order: " + e.getMessage());
        e.printStackTrace();
        return -1;
    }
}
public List<Object[]> getPaymentMethodDistribution(int customerId) {
    List<Object[]> results = new ArrayList<>();
    String sql = "SELECT payment_method, COUNT(*) as count, SUM(total_amount) as total " +
                 "FROM orders WHERE customer_id = ? AND payment_method IS NOT NULL " +
                 "GROUP BY payment_method ORDER BY total DESC";
    
    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Object[] data = new Object[3];
            data[0] = rs.getString("payment_method");
            data[1] = rs.getDouble("total");
            data[2] = rs.getInt("count");
            results.add(data);
        }
        
    } catch (SQLException e) {
        System.err.println("Error getting payment method distribution: " + e.getMessage());
        e.printStackTrace();
    }
    
    // If no data, return sample data
    if (results.isEmpty()) {
        results.add(new Object[]{"Cash", 12500.0, 15});
        results.add(new Object[]{"Credit", 8500.0, 8});
    }
    
    return results;
}


    private LocalDate convertToLocalDate(Object dateObj) {
        if (dateObj == null) {
            return null;
        }
        
        try {
            if (dateObj instanceof java.sql.Timestamp) {
                return ((java.sql.Timestamp) dateObj).toLocalDateTime().toLocalDate();
            } else if (dateObj instanceof java.sql.Date) {
                return ((java.sql.Date) dateObj).toLocalDate();
            } else if (dateObj instanceof java.util.Date) {
                return new java.sql.Timestamp(((java.util.Date) dateObj).getTime()).toLocalDateTime().toLocalDate();
            } else if (dateObj instanceof String) {
                // Handle string dates
                String dateStr = (String) dateObj;
                return LocalDate.parse(dateStr.replace(' ', 'T')); // Handle ISO format
            } else {
                System.err.println("Unknown date type: " + dateObj.getClass().getName());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error converting date: " + dateObj + " - " + e.getMessage());
            return null;
        }
    }

    public int getCustomerIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ? AND role = 'CUSTOMER'";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer ID: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public Object[] getOrderStatistics(int customerId) {
        String sql = "SELECT " +
                "COUNT(*) as total_orders, " +
                "COUNT(CASE WHEN status = 'PENDING' THEN 1 END) as pending_orders, " +
                "COUNT(CASE WHEN status IN ('DELIVERED', 'RECEIVED') THEN 1 END) as delivered_orders, " +
                "COALESCE(SUM(total_amount), 0) as total_spent " +
                "FROM orders " +
                "WHERE customer_id = ? AND status != 'CANCELLED'";

        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("total_orders"),
                        rs.getInt("pending_orders"),
                        rs.getInt("delivered_orders"),
                        rs.getDouble("total_spent")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading order statistics: " + e.getMessage());
            e.printStackTrace();
        }
        return new Object[]{0, 0, 0, 0.0};
    }

    // Alternative method with safer type handling
   // Alternative method with safer type handling
public List<CustomerOrder> getCustomerOrdersSafe(int customerId) {
    List<CustomerOrder> orders = new ArrayList<>();
    String sql = "SELECT " +
       " o.order_id, " +
       " o.order_number, " + 
       " o.order_date, " + 
       " o.status,  " + 
       " o.total_amount, " +  
       " o.delivery_date,  " + 
       " o.payment_method,  " + 
       " o.delivery_address,  " + 
       " u.full_name as customer_name " + 
       " FROM orders o  " + 
       " JOIN users u ON o.customer_id = u.id  " + 
       " WHERE o.customer_id = ? " + 
       " ORDER BY o.order_date DESC";

    try (Connection conn = DBHandle.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, customerId);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int orderId = rs.getInt("order_id"); // CHANGED: from "id" to "order_id"
                String orderNumber = rs.getString("order_number");
                
                // Safe date handling
                LocalDate orderDate = null;
                Object orderDateObj = rs.getObject("order_date");
                if (orderDateObj != null) {
                    orderDate = convertToLocalDate(orderDateObj);
                }
                
                String status = rs.getString("status");
                double totalAmount = rs.getDouble("total_amount");
                String paymentMethod = rs.getString("payment_method");
                
                LocalDate deliveryDate = null;
                Object deliveryDateObj = rs.getObject("delivery_date");
                if (deliveryDateObj != null) {
                    deliveryDate = convertToLocalDate(deliveryDateObj);
                }
                
                String shippingAddress = rs.getString("delivery_address"); // CHANGED: from "shipping_address" to "delivery_address"
                String paymentStatus = "Unknown"; // Not in SELECT, set default
                String customerName = rs.getString("customer_name");
                int itemCount = 0; // Not in SELECT, set default
                
                CustomerOrder order = new CustomerOrder(
                    orderId, orderNumber, orderDate, status, totalAmount, paymentMethod,
                    deliveryDate, shippingAddress, paymentStatus, customerName, itemCount
                );
                orders.add(order);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error loading customer orders: " + e.getMessage());
        e.printStackTrace();
    }
    return orders;
}

}