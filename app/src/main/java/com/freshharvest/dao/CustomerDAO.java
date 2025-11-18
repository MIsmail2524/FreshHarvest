package com.freshharvest.dao;

import com.freshharvest.app.Customer;
import com.freshharvest.app.CustomerService;
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
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = DBHandle.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new CustomerService.CustomerStatistics(
                    rs.getInt("total_customers"),
                    rs.getInt("active_customers"),
                    rs.getInt("new_this_month"),
                    rs.getDouble("total_revenue")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("DAO error getting customer statistics: " + e.getMessage());
        }
        
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
}