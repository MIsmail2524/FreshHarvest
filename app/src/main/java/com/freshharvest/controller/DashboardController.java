package com.freshharvest.controller;

import com.freshharvest.dao.DashboardDAO;
import com.freshharvest.service.SessionService;
import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;
import com.freshharvest.app.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable, DataChangeListener { 
    
    private NotificationService notificationService; 

    @FXML private Label lblOrders;
    @FXML private Label lblCrops;
    @FXML private Label lblBatches;
    @FXML private Label lblTotalHarvested;
    @FXML private Label lblTotalExpense;
    @FXML private Label lblProfitLoss;
    @FXML private Label lblWeather;
    
    @FXML private VBox chartContainer;

    private DashboardDAO dashboardDAO;
    private SessionService sessionService;
    private int currentFarmerId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initializeServices();
            loadDashboardData();
            notificationService = NotificationService.getInstance();
            notificationService.addListener(this); 
            
        } catch (Exception e) {
            System.err.println("Error initializing Dashboard: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to initialize dashboard: " + e.getMessage());
        }
    }
@Override
    public void onDataChanged(String dataType) {
        // Refresh dashboard for any data changes
        javafx.application.Platform.runLater(() -> {
            loadDashboardData();
            System.out.println("Dashboard automatically refreshed");
        });
    }

    private void initializeServices() {
        dashboardDAO = new DashboardDAO();
        sessionService = SessionService.getInstance();
        
        // Get current farmer ID from session
        User currentUser = sessionService.getCurrentUser();
        if (currentUser != null && currentUser.getRole() == com.freshharvest.app.User.Role.FARMER) {
            currentFarmerId = 1; // Default farmer ID for now
        } else {
            currentFarmerId = 1; // Fallback for testing
        }
    }

    private void loadDashboardData() {
        try {
            // Load all dashboard statistics
            loadOrderStatistics();
            loadCropStatistics();
            loadBatchStatistics();
            loadHarvestStatistics();
            loadFinancialStatistics();
            loadWeatherInfo();
            
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
            loadSampleData(); // Fallback to sample data
        }
    }

    private void loadOrderStatistics() {
        try {
            Object[] orderStats = dashboardDAO.getOrderStatistics(currentFarmerId);
            if (orderStats != null) {
                int totalOrders = (Integer) orderStats[0];
                int weeklyOrders = (Integer) orderStats[1];
                String trend = weeklyOrders > 0 ? "+" + weeklyOrders : String.valueOf(weeklyOrders);
                
                lblOrders.setText(totalOrders + " Total");
                // You can add trend label if needed
            } else {
                lblOrders.setText("0 Total");
            }
        } catch (Exception e) {
            System.err.println("Error loading order statistics: " + e.getMessage());
            lblOrders.setText("0 Total");
        }
    }

    private void loadCropStatistics() {
        try {
            Object[] cropStats = dashboardDAO.getCropStatistics(currentFarmerId);
            if (cropStats != null) {
                int totalCrops = (Integer) cropStats[0];
                int activeCrops = (Integer) cropStats[1];
                
                lblCrops.setText(totalCrops + " Total");
            } else {
                lblCrops.setText("0 Total");
            }
        } catch (Exception e) {
            System.err.println("Error loading crop statistics: " + e.getMessage());
            lblCrops.setText("0 Total");
        }
    }

    private void loadBatchStatistics() {
        try {
            Object[] batchStats = dashboardDAO.getBatchStatistics(currentFarmerId);
            if (batchStats != null) {
                int totalBatches = (Integer) batchStats[0];
                int activeBatches = (Integer) batchStats[1];
                
                lblBatches.setText(totalBatches + " Total");
            } else {
                lblBatches.setText("0 Total");
            }
        } catch (Exception e) {
            System.err.println("Error loading batch statistics: " + e.getMessage());
            lblBatches.setText("0 Total");
        }
    }

    private void loadHarvestStatistics() {
        try {
            Object[] harvestStats = dashboardDAO.getHarvestStatistics(currentFarmerId);
            if (harvestStats != null) {
                double totalHarvest = (Double) harvestStats[0];
                double weeklyHarvest = (Double) harvestStats[1];
                
                lblTotalHarvested.setText(String.format("%.1f kg", totalHarvest));
            } else {
                lblTotalHarvested.setText("0 kg");
            }
        } catch (Exception e) {
            System.err.println("Error loading harvest statistics: " + e.getMessage());
            lblTotalHarvested.setText("0 kg");
        }
    }

    private void loadFinancialStatistics() {
        try {
            Object[] financialStats = dashboardDAO.getFinancialStatistics(currentFarmerId);
            if (financialStats != null) {
                double totalRevenue = (Double) financialStats[0];
                double totalExpenses = (Double) financialStats[1];
                double profitLoss = totalRevenue - totalExpenses;
                
                lblTotalExpense.setText(String.format("Rs. %.2f", totalExpenses));
                lblProfitLoss.setText(String.format("Rs. %.2f", profitLoss));
                
                // Color code profit/loss
                if (profitLoss >= 0) {
                    lblProfitLoss.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;"); // Green
                } else {
                    lblProfitLoss.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Red
                }
            } else {
                lblTotalExpense.setText("Rs. 0.00");
                lblProfitLoss.setText("Rs. 0.00");
            }
        } catch (Exception e) {
            System.err.println("Error loading financial statistics: " + e.getMessage());
            lblTotalExpense.setText("Rs. 0.00");
            lblProfitLoss.setText("Rs. 0.00");
        }
    }

    private void loadWeatherInfo() {
        try {
            // For now, set a placeholder. You can integrate with a weather API later
            lblWeather.setText("Sunny, 28°C");
        } catch (Exception e) {
            System.err.println("Error loading weather info: " + e.getMessage());
            lblWeather.setText("No data");
        }
    }

    private void loadSampleData() {
        // Fallback sample data
        lblOrders.setText("142 Total");
        lblCrops.setText("24 Total");
        lblBatches.setText("18 Total");
        lblTotalHarvested.setText("1250.5 kg");
        lblTotalExpense.setText("Rs. 45,250.00");
        lblProfitLoss.setText("Rs. 12,750.00");
        lblWeather.setText("Sunny, 28°C");
    }

   

    @FXML
    private void showCropPerformanceChart() {
        try {
            List<Object[]> cropData = dashboardDAO.getCropPerformance(currentFarmerId);
            
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            
            barChart.setTitle("Crop Performance (Revenue)");
            barChart.setLegendVisible(false);
            xAxis.setLabel("Crops");
            yAxis.setLabel("Revenue (Rs.)");
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            
            if (cropData.isEmpty()) {
                // Sample data
                series.getData().add(new XYChart.Data<>("Tomatoes", 25000));
                series.getData().add(new XYChart.Data<>("Potatoes", 18000));
                series.getData().add(new XYChart.Data<>("Onions", 12000));
                series.getData().add(new XYChart.Data<>("Carrots", 8000));
            } else {
                for (Object[] data : cropData) {
                    String cropName = data[0].toString();
                    double revenue = (Double) data[1];
                    series.getData().add(new XYChart.Data<>(cropName, revenue));
                }
            }
            
            barChart.getData().add(series);
            updateChartContainer(barChart);
            
        } catch (Exception e) {
            System.err.println("Error loading crop performance chart: " + e.getMessage());
            showAlert("Error", "Failed to load crop performance chart: " + e.getMessage());
        }
    }

    @FXML
    private void showOrderStatusChart() {
        try {
            List<Object[]> statusData = dashboardDAO.getOrderStatusDistribution(currentFarmerId);
            
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Order Status Distribution");
            
            if (statusData.isEmpty()) {
                // Sample data
                pieChart.getData().add(new PieChart.Data("Delivered", 45));
                pieChart.getData().add(new PieChart.Data("Processing", 25));
                pieChart.getData().add(new PieChart.Data("Pending", 15));
                pieChart.getData().add(new PieChart.Data("Cancelled", 5));
            } else {
                for (Object[] data : statusData) {
                    String status = data[0].toString();
                    int count = (Integer) data[1];
                    pieChart.getData().add(new PieChart.Data(status, count));
                }
            }
            
            updateChartContainer(pieChart);
            
        } catch (Exception e) {
            System.err.println("Error loading order status chart: " + e.getMessage());
            showAlert("Error", "Failed to load order status chart: " + e.getMessage());
        }
    }

    @FXML
    private void showExpenseBreakdownChart() {
        try {
            List<Object[]> expenseData = dashboardDAO.getExpenseBreakdown(currentFarmerId);
            
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Expense Breakdown by Category");
            
            if (expenseData.isEmpty()) {
                // Sample data
                pieChart.getData().add(new PieChart.Data("Seeds", 15000));
                pieChart.getData().add(new PieChart.Data("Fertilizers", 12000));
                pieChart.getData().add(new PieChart.Data("Labor", 18000));
                pieChart.getData().add(new PieChart.Data("Equipment", 8000));
            } else {
                for (Object[] data : expenseData) {
                    String category = data[0].toString();
                    double amount = (Double) data[1];
                    pieChart.getData().add(new PieChart.Data(category, amount));
                }
            }
            
            updateChartContainer(pieChart);
            
        } catch (Exception e) {
            System.err.println("Error loading expense breakdown chart: " + e.getMessage());
            showAlert("Error", "Failed to load expense breakdown chart: " + e.getMessage());
        }
    }

   

    private void updateChartContainer(javafx.scene.Node chart) {
        try {
            chartContainer.getChildren().clear();
            ((Region) chart).setPrefSize(800, 400);
            chartContainer.getChildren().add(chart);
        } catch (Exception e) {
            System.err.println("Error updating chart container: " + e.getMessage());
        }
    }

    @FXML
    private void refreshDashboard() {
        try {
            loadDashboardData();
            showAlert("Success", "Dashboard data refreshed!");
        } catch (Exception e) {
            showAlert("Error", "Failed to refresh dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void closeApplication() {
        try {
            System.exit(0);
        } catch (Exception e) {
            showAlert("Error", "Failed to close application: " + e.getMessage());
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