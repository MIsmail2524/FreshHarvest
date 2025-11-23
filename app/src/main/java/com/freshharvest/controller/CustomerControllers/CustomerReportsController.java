package com.freshharvest.controller.CustomerControllers;

import com.freshharvest.dao.CustomerDAO;
import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;
import com.freshharvest.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerReportsController implements Initializable, DataChangeListener { 
    
    private NotificationService notificationService; 

    @FXML private VBox chartContainer;
    @FXML private Label lblTotalOrders;
    @FXML private Label lblTotalSpent;
    @FXML private Label lblAvgOrder;
    @FXML private Label lblFavoriteCategory;
    @FXML private Label lblSuccessRate;
    @FXML private ComboBox<String> cbTimeRange;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private int currentCustomerId = 1; // Get from session in real implementation
    private LocalDate startDateFilter;
    private LocalDate endDateFilter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

                  notificationService = NotificationService.getInstance(); 
            notificationService.addListener(this);
            
            setupDateRangeSelector();
            loadQuickStats();
            showSpendingOverview(); // Default report
            
        } catch (Exception e) {
            showAlert("Error", "Failed to initialize reports: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void onDataChanged(String dataType) {
        switch (dataType) {
            case "ORDER_CREATED":
            case "ORDER_STATUS_CHANGED":
            case "PAYMENT_PROCESSED":
                javafx.application.Platform.runLater(() -> {
                    loadQuickStats();
                    refreshCurrentReport();
                    System.out.println("Reports automatically refreshed");
                });
                break;
        }
    }

    
    private void setupDateRangeSelector() {
        // Setup time range options
        ObservableList<String> timeRanges = FXCollections.observableArrayList(
            "Last 7 Days",
            "Last 30 Days", 
            "Last 3 Months",
            "Last 6 Months",
            "Last Year",
            "All Time",
            "Custom Range"
        );
        cbTimeRange.setItems(timeRanges);
        cbTimeRange.setValue("Last 30 Days");

        // Set default dates
        dpEndDate.setValue(LocalDate.now());
        dpStartDate.setValue(LocalDate.now().minusDays(30));

        // Add listener for time range changes
        cbTimeRange.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateDateRange(newVal);
        });
    }

    private void updateDateRange(String timeRange) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (timeRange) {
            case "Last 7 Days":
                startDate = endDate.minusDays(7);
                break;
            case "Last 30 Days":
                startDate = endDate.minusDays(30);
                break;
            case "Last 3 Months":
                startDate = endDate.minusMonths(3);
                break;
            case "Last 6 Months":
                startDate = endDate.minusMonths(6);
                break;
            case "Last Year":
                startDate = endDate.minusYears(1);
                break;
            case "All Time":
                startDate = LocalDate.of(2020, 1, 1); // Adjust based on your data
                break;
            default:
                return; // Custom range - don't auto-update
        }

        dpStartDate.setValue(startDate);
        dpEndDate.setValue(endDate);
    }

    @FXML
    private void applyDateFilter() {
        startDateFilter = dpStartDate.getValue();
        endDateFilter = dpEndDate.getValue();
        
        if (startDateFilter != null && endDateFilter != null && startDateFilter.isAfter(endDateFilter)) {
            showAlert("Error", "Start date cannot be after end date");
            return;
        }
        
        loadQuickStats();
        refreshCurrentReport();
    }

    private void refreshCurrentReport() {
        // Re-display the current report with new filters
        // This would need to track the current report type
        showSpendingOverview(); // For now, just refresh the default
    }

    // REPORT 1: SPENDING OVERVIEW
    @FXML
 private void showSpendingOverview() {
    try {
        VBox reportContainer = new VBox(20);
        reportContainer.setStyle("-fx-padding: 20;");

        // Title
        Label title = new Label("💰 Spending Overview Report");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Create a grid of charts
        HBox chartsRow = new HBox(20);
        chartsRow.setPrefHeight(300);

        // Spending by Payment Method
        VBox paymentMethodBox = new VBox(10);
        paymentMethodBox.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");
        Label paymentLabel = new Label("Spending by Payment Method");
        paymentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        PieChart paymentChart = createPaymentMethodChart();
        paymentChart.setPrefSize(350, 250);
        paymentMethodBox.getChildren().addAll(paymentLabel, paymentChart);

        // Average Order Value Over Time
        VBox avgOrderBox = new VBox(10);
        avgOrderBox.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");
        Label avgOrderLabel = new Label("Average Order Value Trend");
        avgOrderLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        LineChart<String, Number> avgOrderChart = createAvgOrderValueChart();
        avgOrderChart.setPrefSize(400, 250);
        avgOrderBox.getChildren().addAll(avgOrderLabel, avgOrderChart);

        chartsRow.getChildren().addAll(paymentMethodBox, avgOrderBox);
        reportContainer.getChildren().addAll(title, chartsRow);
        updateChartContainer(reportContainer);

    } catch (Exception e) {
        showAlert("Error", "Failed to load spending overview: " + e.getMessage());
        e.printStackTrace();
    }
}

    private LineChart<String, Number> createSpendingTrendsChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Month");
        yAxis.setLabel("Amount (Rs)");
        chart.setTitle("Monthly Spending");
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Spending");

        // Get data from DAO
        List<Object[]> spendingData = customerDAO.getMonthlySpending(currentCustomerId);
        
        if (spendingData.isEmpty()) {
            // Sample data
            series.getData().add(new XYChart.Data<>("Jan", 4500));
            series.getData().add(new XYChart.Data<>("Feb", 6200));
            series.getData().add(new XYChart.Data<>("Mar", 3800));
            series.getData().add(new XYChart.Data<>("Apr", 7500));
            series.getData().add(new XYChart.Data<>("May", 5200));
        } else {
            for (Object[] data : spendingData) {
                series.getData().add(new XYChart.Data<>(data[0].toString(), (Double) data[1]));
            }
        }

        chart.getData().add(series);
        return chart;
    }

    private PieChart createPaymentMethodChart() {
        PieChart chart = new PieChart();
        
        // Get data from DAO
        List<Object[]> paymentData = customerDAO.getPaymentMethodDistribution(currentCustomerId);
        
        if (paymentData.isEmpty()) {
            chart.getData().add(new PieChart.Data("Cash on Delivery", 45));
            chart.getData().add(new PieChart.Data("Credit Card", 25));
            chart.getData().add(new PieChart.Data("Digital Wallet", 20));
            chart.getData().add(new PieChart.Data("Debit Card", 10));
        } else {
            for (Object[] data : paymentData) {
                chart.getData().add(new PieChart.Data(data[0].toString(), (Double) data[1]));
            }
        }

        return chart;
    }

    private LineChart<String, Number> createAvgOrderValueChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Month");
        yAxis.setLabel("Average Amount (Rs)");
        chart.setTitle("Average Order Value");
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        // Sample data - replace with actual DAO call
        series.getData().add(new XYChart.Data<>("Jan", 1250));
        series.getData().add(new XYChart.Data<>("Feb", 1550));
        series.getData().add(new XYChart.Data<>("Mar", 1100));
        series.getData().add(new XYChart.Data<>("Apr", 1875));
        series.getData().add(new XYChart.Data<>("May", 1300));

        chart.getData().add(series);
        return chart;
    }

    private BarChart<String, Number> createSpendingDistributionChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Amount Range");
        yAxis.setLabel("Number of Orders");
        chart.setTitle("Spending Distribution");
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        series.getData().add(new XYChart.Data<>("< Rs.500", 8));
        series.getData().add(new XYChart.Data<>("Rs.500-1000", 12));
        series.getData().add(new XYChart.Data<>("Rs.1000-2000", 6));
        series.getData().add(new XYChart.Data<>("> Rs.2000", 4));

        chart.getData().add(series);
        return chart;
    }

    // REPORT 2: ORDER TRENDS
 @FXML
private void showOrderTrends() {
    try {
        VBox reportContainer = new VBox(20);
        reportContainer.setStyle("-fx-padding: 20;");

        Label title = new Label("📈 Order Trends Analysis");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox chartsRow = new HBox(20);

       

        

        // Order Frequency Analysis
        VBox frequencyBox = new VBox(10);
        frequencyBox.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");
        Label frequencyLabel = new Label("Order Frequency Pattern");
        frequencyLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        PieChart frequencyChart = createOrderFrequencyChart();
        frequencyChart.setPrefSize(350, 300);
        frequencyBox.getChildren().addAll(frequencyLabel, frequencyChart);

        chartsRow.getChildren().addAll(frequencyLabel, frequencyBox);
        reportContainer.getChildren().addAll(title, chartsRow);
        updateChartContainer(reportContainer);

    } catch (Exception e) {
        showAlert("Error", "Failed to load order trends: " + e.getMessage());
        e.printStackTrace();
    }
}
    private BarChart<String, Number> createOrderVolumeChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Month");
        yAxis.setLabel("Number of Orders");
        chart.setTitle("Order Volume Trend");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Orders");

        List<Object[]> orderData = customerDAO.getMonthlyOrderCount(currentCustomerId);
        
        if (orderData.isEmpty()) {
            series.getData().add(new XYChart.Data<>("Jan", 8));
            series.getData().add(new XYChart.Data<>("Feb", 12));
            series.getData().add(new XYChart.Data<>("Mar", 6));
            series.getData().add(new XYChart.Data<>("Apr", 15));
            series.getData().add(new XYChart.Data<>("May", 10));
        } else {
            for (Object[] data : orderData) {
                series.getData().add(new XYChart.Data<>(data[0].toString(), ((Long) data[1]).intValue()));
            }
        }

        chart.getData().add(series);
        return chart;
    }

    private PieChart createOrderFrequencyChart() {
        PieChart chart = new PieChart();
        
        chart.getData().add(new PieChart.Data("Weekly", 35));
        chart.getData().add(new PieChart.Data("Bi-Weekly", 25));
        chart.getData().add(new PieChart.Data("Monthly", 20));
        chart.getData().add(new PieChart.Data("Occasional", 20));

        return chart;
    }

    private BarChart<String, Number> createDayOfWeekChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Day of Week");
        yAxis.setLabel("Number of Orders");
        chart.setTitle("Orders by Day");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        series.getData().add(new XYChart.Data<>("Monday", 8));
        series.getData().add(new XYChart.Data<>("Tuesday", 12));
        series.getData().add(new XYChart.Data<>("Wednesday", 10));
        series.getData().add(new XYChart.Data<>("Thursday", 15));
        series.getData().add(new XYChart.Data<>("Friday", 18));
        series.getData().add(new XYChart.Data<>("Saturday", 20));
        series.getData().add(new XYChart.Data<>("Sunday", 5));

        chart.getData().add(series);
        return chart;
    }

    // REPORT 3: CATEGORY ANALYSIS
    @FXML
    private void showCategoryAnalysis() {
        try {
            VBox reportContainer = new VBox(20);
            reportContainer.setStyle("-fx-padding: 20;");

            Label title = new Label("🥦 Category Spending Analysis");
            title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            HBox chartsRow = new HBox(20);

            // Spending by Category
            VBox categorySpendingBox = new VBox(10);
            categorySpendingBox.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");
            Label spendingLabel = new Label("Spending by Category");
            spendingLabel.setStyle("-fx-font-weight: bold;");

            PieChart spendingChart = createCategorySpendingChart();
            spendingChart.setPrefSize(400, 300);
            categorySpendingBox.getChildren().addAll(spendingLabel, spendingChart);

            // Category Trend Over Time
            VBox categoryTrendBox = new VBox(10);
            categoryTrendBox.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");
            Label trendLabel = new Label("Category Trends");
            trendLabel.setStyle("-fx-font-weight: bold;");

            LineChart<String, Number> trendChart = createCategoryTrendChart();
            trendChart.setPrefSize(400, 300);
            categoryTrendBox.getChildren().addAll(trendLabel, trendChart);

            chartsRow.getChildren().addAll(categorySpendingBox, categoryTrendBox);

            reportContainer.getChildren().addAll(title, chartsRow);
            updateChartContainer(reportContainer);

        } catch (Exception e) {
            showAlert("Error", "Failed to load category analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private PieChart createCategorySpendingChart() {
        PieChart chart = new PieChart();
        
        List<Object[]> categoryData = customerDAO.getSpendingByCategory(currentCustomerId);
        
        if (categoryData.isEmpty()) {
            chart.getData().add(new PieChart.Data("Vegetables", 35));
            chart.getData().add(new PieChart.Data("Fruits", 25));
            chart.getData().add(new PieChart.Data("Dairy", 20));
            chart.getData().add(new PieChart.Data("Grains", 15));
            chart.getData().add(new PieChart.Data("Meat", 5));
        } else {
            for (Object[] data : categoryData) {
                chart.getData().add(new PieChart.Data(data[0].toString(), (Double) data[1]));
            }
        }

        return chart;
    }

    private LineChart<String, Number> createCategoryTrendChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Month");
        yAxis.setLabel("Spending (Rs)");
        chart.setTitle("Category Trends Over Time");

        // Create multiple series for different categories
        XYChart.Series<String, Number> vegetables = new XYChart.Series<>();
        vegetables.setName("Vegetables");
        vegetables.getData().addAll(
            new XYChart.Data<>("Jan", 1500),
            new XYChart.Data<>("Feb", 1800),
            new XYChart.Data<>("Mar", 1200),
            new XYChart.Data<>("Apr", 2000)
        );

        XYChart.Series<String, Number> fruits = new XYChart.Series<>();
        fruits.setName("Fruits");
        fruits.getData().addAll(
            new XYChart.Data<>("Jan", 800),
            new XYChart.Data<>("Feb", 1200),
            new XYChart.Data<>("Mar", 900),
            new XYChart.Data<>("Apr", 1500)
        );

        XYChart.Series<String, Number> dairy = new XYChart.Series<>();
        dairy.setName("Dairy");
        dairy.getData().addAll(
            new XYChart.Data<>("Jan", 600),
            new XYChart.Data<>("Feb", 700),
            new XYChart.Data<>("Mar", 650),
            new XYChart.Data<>("Apr", 800)
        );

        chart.getData().addAll(vegetables, fruits, dairy);
        return chart;
    }

    // REPORT 4: STATUS REPORT
 @FXML
private void showStatusReport() {
    try {
        VBox reportContainer = new VBox(20);
        reportContainer.setStyle("-fx-padding: 20;");

        Label title = new Label("📋 Order Status Report");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox chartsRow = new HBox(20);

    

        // Status Trend Over Time
        VBox statusTrendBox = new VBox(10);
        statusTrendBox.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");
        Label trendLabel = new Label("Status Trends");
        trendLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        StackedBarChart<String, Number> trendChart = createStatusTrendChart();
        trendChart.setPrefSize(400, 300);
        statusTrendBox.getChildren().addAll(trendLabel, trendChart);

        chartsRow.getChildren().addAll(trendLabel, statusTrendBox);
        reportContainer.getChildren().addAll(title, chartsRow);
        updateChartContainer(reportContainer);

    } catch (Exception e) {
        showAlert("Error", "Failed to load status report: " + e.getMessage());
        e.printStackTrace();
    }
}

    private PieChart createStatusDistributionChart() {
        PieChart chart = new PieChart();
        
        List<Object[]> ordersData = customerDAO.getAllCustomerOrders(currentCustomerId);
        int delivered = 0, pending = 0, processing = 0, cancelled = 0;

        for (Object[] data : ordersData) {
            String status = data[3].toString().toLowerCase();
            switch (status) {
                case "delivered":
                case "received":
                    delivered++;
                    break;
                case "pending":
                    pending++;
                    break;
                case "processing":
                case "accepted":
                    processing++;
                    break;
                case "cancelled":
                case "rejected":
                    cancelled++;
                    break;
            }
        }

        if (delivered > 0) chart.getData().add(new PieChart.Data("Delivered", delivered));
        if (pending > 0) chart.getData().add(new PieChart.Data("Pending", pending));
        if (processing > 0) chart.getData().add(new PieChart.Data("Processing", processing));
        if (cancelled > 0) chart.getData().add(new PieChart.Data("Cancelled", cancelled));

        return chart;
    }

    private StackedBarChart<String, Number> createStatusTrendChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        StackedBarChart<String, Number> chart = new StackedBarChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Month");
        yAxis.setLabel("Number of Orders");
        chart.setTitle("Order Status Trends");

        XYChart.Series<String, Number> delivered = new XYChart.Series<>();
        delivered.setName("Delivered");
        delivered.getData().addAll(
            new XYChart.Data<>("Jan", 8),
            new XYChart.Data<>("Feb", 10),
            new XYChart.Data<>("Mar", 7),
            new XYChart.Data<>("Apr", 12)
        );

        XYChart.Series<String, Number> pending = new XYChart.Series<>();
        pending.setName("Pending");
        pending.getData().addAll(
            new XYChart.Data<>("Jan", 2),
            new XYChart.Data<>("Feb", 1),
            new XYChart.Data<>("Mar", 3),
            new XYChart.Data<>("Apr", 1)
        );

        XYChart.Series<String, Number> processing = new XYChart.Series<>();
        processing.setName("Processing");
        processing.getData().addAll(
            new XYChart.Data<>("Jan", 1),
            new XYChart.Data<>("Feb", 2),
            new XYChart.Data<>("Mar", 1),
            new XYChart.Data<>("Apr", 3)
        );

        chart.getData().addAll(delivered, pending, processing);
        return chart;
    }

    private BarChart<String, Number> createDeliveryPerformanceChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Performance Metric");
        yAxis.setLabel("Value");
        chart.setTitle("Delivery Performance");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        series.getData().add(new XYChart.Data<>("On-Time Delivery", 85));
        series.getData().add(new XYChart.Data<>("Early Delivery", 10));
        series.getData().add(new XYChart.Data<>("Late Delivery", 5));
        series.getData().add(new XYChart.Data<>("Success Rate", 95));

        chart.getData().add(series);
        return chart;
    }

    // REPORT 5: MONTHLY SUMMARY
    @FXML
    private void showMonthlySummary() {
        try {
            VBox reportContainer = new VBox(20);
            reportContainer.setStyle("-fx-padding: 20;");

            Label title = new Label("💰 Monthly Summary Report");
            title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Create a comprehensive summary with multiple visualizations
            HBox summaryCharts = new HBox(20);

            // Monthly Comparison
            VBox comparisonBox = new VBox(10);
            comparisonBox.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");
            Label comparisonLabel = new Label("Monthly Comparison");
            comparisonLabel.setStyle("-fx-font-weight: bold;");

            BarChart<String, Number> comparisonChart = createMonthlyComparisonChart();
            comparisonChart.setPrefSize(500, 350);
            comparisonBox.getChildren().addAll(comparisonLabel, comparisonChart);

            // Key Metrics
            VBox metricsBox = new VBox(15);
            metricsBox.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");
            Label metricsLabel = new Label("Key Monthly Metrics");
            metricsLabel.setStyle("-fx-font-weight: bold;");

            // Add metric cards
            metricsBox.getChildren().addAll(
                createMetricCard("Total Orders", "24", "#3498db"),
                createMetricCard("Total Spent", "Rs. 18,500", "#27ae60"),
                createMetricCard("Avg Order Value", "Rs. 770", "#f39c12"),
                createMetricCard("Most Ordered Category", "Vegetables", "#9b59b6"),
                createMetricCard("Success Rate", "92%", "#2ecc71")
            );

            summaryCharts.getChildren().addAll(comparisonBox, metricsBox);
            reportContainer.getChildren().addAll(title, summaryCharts);
            updateChartContainer(reportContainer);

        } catch (Exception e) {
            showAlert("Error", "Failed to load monthly summary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private BarChart<String, Number> createMonthlyComparisonChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Month");
        yAxis.setLabel("Value");
        chart.setTitle("Monthly Performance Comparison");

        XYChart.Series<String, Number> orders = new XYChart.Series<>();
        orders.setName("Orders");
        orders.getData().addAll(
            new XYChart.Data<>("Jan", 18),
            new XYChart.Data<>("Feb", 24),
            new XYChart.Data<>("Mar", 15),
            new XYChart.Data<>("Apr", 28),
            new XYChart.Data<>("May", 22)
        );

        XYChart.Series<String, Number> spending = new XYChart.Series<>();
        spending.setName("Spending (K Rs)");
        spending.getData().addAll(
            new XYChart.Data<>("Jan", 12.5),
            new XYChart.Data<>("Feb", 18.5),
            new XYChart.Data<>("Mar", 9.8),
            new XYChart.Data<>("Apr", 22.3),
            new XYChart.Data<>("May", 16.7)
        );

        chart.getData().addAll(orders, spending);
        return chart;
    }

    private HBox createMetricCard(String title, String value, String color) {
        HBox card = new HBox(10);
        card.setStyle("-fx-padding: 10; -fx-background-color: " + color + "; -fx-background-radius: 5;");
        card.setPrefWidth(200);

        VBox content = new VBox(5);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        content.getChildren().addAll(titleLabel, valueLabel);
        card.getChildren().add(content);
        
        return card;
    }

    // UTILITY METHODS
    private void loadQuickStats() {
        try {
            // Get stats from DAO
            Object[] stats = customerDAO.getCustomerStats(currentCustomerId);
            
            if (stats != null) {
                lblTotalOrders.setText(String.valueOf(stats[0]));
                lblTotalSpent.setText("Rs. " + stats[1]);
                lblAvgOrder.setText("Rs. " + String.format("%.2f", (Double) stats[1] / (Integer) stats[0]));
                lblFavoriteCategory.setText(stats[3] != null ? stats[3].toString() : "N/A");
                
                // Calculate success rate (delivered orders / total orders)
                double successRate = ((Integer) stats[0] - (Integer) stats[2]) / (double) (Integer) stats[0] * 100;
                lblSuccessRate.setText(String.format("%.1f%%", successRate));
            }
        } catch (Exception e) {
            System.err.println("Error loading quick stats: " + e.getMessage());
        }
    }

    @FXML
    private void refreshAllData() {
        loadQuickStats();
        refreshCurrentReport();
        showAlert("Success", "Reports data refreshed successfully!");
    }

    @FXML
    private void exportAsPDF() {
        showAlert("Info", "PDF export feature coming soon!");
    }

    @FXML
    private void exportAsExcel() {
        showAlert("Info", "Excel export feature coming soon!");
    }

    @FXML
    private void printReport() {
        showAlert("Info", "Print feature coming soon!");
    }



    private void updateChartContainer(javafx.scene.Node content) {
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(content);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}