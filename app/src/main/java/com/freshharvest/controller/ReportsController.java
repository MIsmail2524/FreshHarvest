package com.freshharvest.controller;

import com.freshharvest.app.ReportData;
import com.freshharvest.dao.ReportsDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML private TableView<ReportData> reportTable;
    @FXML private TableColumn<ReportData, String> colReportDate;
    @FXML private TableColumn<ReportData, String> colReportCategory;
    @FXML private TableColumn<ReportData, String> colReportDescription;
    @FXML private TableColumn<ReportData, String> colReportAmount;

    // Chart containers
    @FXML private VBox chartDisplayArea;
    @FXML private StackPane chartContainer;
    @FXML private Label currentChartTitle;

    private final ObservableList<ReportData> reportData = FXCollections.observableArrayList();
    private final ReportsDAO dao = new ReportsDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadTransactions();
        
        // Show default chart
        showCropPerformanceChart();
    }

    private void setupTable() {
        colReportDate.setCellValueFactory(cell -> cell.getValue().dateProperty());
        colReportCategory.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        colReportDescription.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        colReportAmount.setCellValueFactory(cell -> cell.getValue().amountProperty());
        reportTable.setItems(reportData);
    }

    @FXML
    private void handleCropPerformance() {
        showCropPerformanceChart();
    }

    @FXML
    private void handleRevenueAnalysis() {
        showRevenueAnalysisChart();
    }

    @FXML
    private void handleExpenseBreakdown() {
        showExpenseBreakdownChart();
    }


    @FXML
    private void handleProfitDistribution() {
        showProfitDistributionChart();
    }

    private void showCropPerformanceChart() {
        try {
            currentChartTitle.setText("🌱 Crop Performance Analysis");
            
            BarChart<String, Number> barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
            barChart.setTitle("Crop Profit Performance");
            barChart.setLegendVisible(false);
            barChart.setPrefSize(800, 450);
            
            CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
            xAxis.setLabel("Crops");
            xAxis.setTickLabelRotation(45);
            
            NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
            yAxis.setLabel("Profit ($)");
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Profit");
            
            // Real data from database
            List<ReportData> cropData = dao.getCropPerformance();
            for (ReportData data : cropData) {
                String cropName = data.getDescription();
                double profit = Double.parseDouble(data.getAmount().replace("$", "").replace(",", ""));
                
                if (profit != 0) {
                    series.getData().add(new XYChart.Data<>(cropName, profit));
                }
            }
            
            barChart.getData().clear();
            barChart.getData().add(series);
            
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(barChart);
            
        } catch (Exception e) {
            System.err.println("Error showing crop performance chart: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load crop performance data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showRevenueAnalysisChart() {
        try {
            currentChartTitle.setText("💰 Revenue Analysis by Crop");
            
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Revenue Distribution by Crop");
            pieChart.setLabelsVisible(true);
            pieChart.setLegendVisible(true);
            pieChart.setPrefSize(800, 450);
            
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            
            // Real data from database
            List<ReportData> revenueData = dao.getCropRevenueDistribution();
            double totalRevenue = dao.getTotalRevenue();
            
            for (ReportData data : revenueData) {
                String cropName = data.getDescription();
                double revenue = Double.parseDouble(data.getAmount().replace("$", "").replace(",", ""));
                
                if (revenue > 0 && totalRevenue > 0) {
                    double percentage = (revenue / totalRevenue) * 100;
                    pieData.add(new PieChart.Data(
                        String.format("%s (%.1f%%)", cropName, percentage), 
                        revenue
                    ));
                }
            }
            
            pieChart.setData(pieData);
            
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(pieChart);
            
        } catch (Exception e) {
            System.err.println("Error showing revenue analysis chart: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load revenue analysis data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showExpenseBreakdownChart() {
        try {
            currentChartTitle.setText("📈 Farm Expense Breakdown");
            
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Expense Distribution by Category");
            pieChart.setLabelsVisible(true);
            pieChart.setLegendVisible(true);
            pieChart.setPrefSize(800, 450);
            
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            
            // Real data from database
            List<ReportData> expenses = dao.getExpenseByCategory();
            double totalExpenses = dao.getTotalExpenses();
            
            for (ReportData data : expenses) {
                String category = data.getDescription();
                double amount = Double.parseDouble(data.getAmount().replace("$", "").replace(",", ""));
                
                if (amount > 0 && totalExpenses > 0) {
                    double percentage = (amount / totalExpenses) * 100;
                    pieData.add(new PieChart.Data(
                        String.format("%s (%.1f%%)", category, percentage), 
                        amount
                    ));
                }
            }
            
            pieChart.setData(pieData);
            
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(pieChart);
            
        } catch (Exception e) {
            System.err.println("Error showing expense breakdown chart: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load expense data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showMonthlyTrendsChart() {
        try {
            currentChartTitle.setText("📅 Monthly Revenue & Profit Trends");
            
            LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
            lineChart.setTitle("Monthly Performance Trends");
            lineChart.setLegendVisible(true);
            lineChart.setPrefSize(800, 450);
            
            CategoryAxis xAxis = (CategoryAxis) lineChart.getXAxis();
            xAxis.setLabel("Month");
            
            NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
            yAxis.setLabel("Amount ($)");
            
            // Revenue Series - Real data from database
            XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
            revenueSeries.setName("Revenue");
            
            List<ReportData> monthlyData = dao.getMonthlyRevenue();
            for (ReportData data : monthlyData) {
                String month = formatMonthForDisplay(data.getDescription());
                double revenue = Double.parseDouble(data.getAmount().replace("$", "").replace(",", ""));
                revenueSeries.getData().add(new XYChart.Data<>(month, revenue));
            }
            
            // Profit Series - Calculate profit from revenue and expenses
            XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();
            profitSeries.setName("Profit");
            
            for (ReportData data : monthlyData) {
                String month = data.getDescription();
                double revenue = Double.parseDouble(data.getAmount().replace("$", "").replace(",", ""));
                double expenses = dao.getMonthlyExpenses(month);
                double profit = revenue - expenses;
                String displayMonth = formatMonthForDisplay(month);
                profitSeries.getData().add(new XYChart.Data<>(displayMonth, profit));
            }
            
            lineChart.getData().clear();
            lineChart.getData().addAll(revenueSeries, profitSeries);
            
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(lineChart);
            
        } catch (Exception e) {
            System.err.println("Error showing monthly trends chart: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load monthly trends data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showProfitDistributionChart() {
        try {
            currentChartTitle.setText("🥧 Profit Margin Analysis");
            
            BarChart<String, Number> barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
            barChart.setTitle("Profit Margins by Crop");
            barChart.setLegendVisible(false);
            barChart.setPrefSize(800, 450);
            
            CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
            xAxis.setLabel("Crops");
            xAxis.setTickLabelRotation(45);
            
            NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
            yAxis.setLabel("Profit Margin (%)");
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Profit Margin");
            
            // Real data from database - Calculate profit margins
            List<ReportData> cropPerformance = dao.getCropPerformance();
            List<ReportData> revenueDistribution = dao.getCropRevenueDistribution();
            
            for (ReportData perfData : cropPerformance) {
                String cropName = perfData.getDescription();
                double profit = Double.parseDouble(perfData.getAmount().replace("$", "").replace(",", ""));
                
                // Find corresponding revenue for this crop
                double revenue = 0;
                for (ReportData revData : revenueDistribution) {
                    if (revData.getDescription().equals(cropName)) {
                        revenue = Double.parseDouble(revData.getAmount().replace("$", "").replace(",", ""));
                        break;
                    }
                }
                
                // Calculate profit margin
                if (revenue > 0 && profit != 0) {
                    double profitMargin = (profit / revenue) * 100;
                    series.getData().add(new XYChart.Data<>(cropName, profitMargin));
                }
            }
            
            barChart.getData().clear();
            barChart.getData().add(series);
            
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(barChart);
            
        } catch (Exception e) {
            System.err.println("Error showing profit distribution chart: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load profit distribution data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String formatMonthForDisplay(String month) {
        try {
            // Convert "2024-03" to "Mar 2024"
            String[] parts = month.split("-");
            if (parts.length == 2) {
                int year = Integer.parseInt(parts[0]);
                int monthNum = Integer.parseInt(parts[1]);
                String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                      "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                return monthNames[monthNum - 1] + " '" + String.valueOf(year).substring(2);
            }
        } catch (Exception e) {
            // If formatting fails, return original
        }
        return month;
    }

    @FXML
    private void handleRefreshTransactions() {
        loadTransactions();
    }

    private void loadTransactions() {
        try {
            reportData.clear();
            
            // Real data from database
            List<ReportData> transactions = dao.getAllTransactions();
            if (transactions != null && !transactions.isEmpty()) {
                reportData.addAll(transactions);
                System.out.println("Loaded " + transactions.size() + " transactions from database");
            } else {
                System.out.println("No transactions found in database.");
                // Fallback to sample data if no database data
                reportData.add(new ReportData("2024-04-15", "Revenue", "Tomatoes - Local Market", "$250.00"));
                reportData.add(new ReportData("2024-04-14", "Expense", "Labor - Harvesting", "$120.00"));
            }
            
        } catch (Exception e) {
            System.err.println("Error loading transactions: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load transactions: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
private void handleSeasonalAnalysis() {
    showSeasonalAnalysisChart();
}

private void showSeasonalAnalysisChart() {
    try {
        currentChartTitle.setText("🌤️ Seasonal Profit & Loss Analysis");
        
        BarChart<String, Number> barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        barChart.setTitle("Seasonal Performance Overview");
        barChart.setLegendVisible(true);
        barChart.setPrefSize(800, 450);
        
        CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
        xAxis.setLabel("Season");
        
        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
        yAxis.setLabel("Amount ($)");
        
        // Profit Series
        XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();
        profitSeries.setName("Profit");
        
        // Loss Series (negative values)
        XYChart.Series<String, Number> lossSeries = new XYChart.Series<>();
        lossSeries.setName("Loss");
        
        // Real data from database
        List<ReportData> seasonalData = dao.getSeasonalAnalysis();
        
        for (ReportData data : seasonalData) {
            String season = data.getDescription();
            double profitLoss = Double.parseDouble(data.getAmount().replace("$", "").replace(",", ""));
            
            if (profitLoss >= 0) {
                profitSeries.getData().add(new XYChart.Data<>(season, profitLoss));
                lossSeries.getData().add(new XYChart.Data<>(season, 0));
            } else {
                profitSeries.getData().add(new XYChart.Data<>(season, 0));
                lossSeries.getData().add(new XYChart.Data<>(season, Math.abs(profitLoss)));
            }
        }
        
        barChart.getData().clear();
        barChart.getData().addAll(profitSeries, lossSeries);
        
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(barChart);
        
    } catch (Exception e) {
        System.err.println("Error showing seasonal analysis chart: " + e.getMessage());
        e.printStackTrace();
        showAlert("Error", "Failed to load seasonal analysis data: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}

}