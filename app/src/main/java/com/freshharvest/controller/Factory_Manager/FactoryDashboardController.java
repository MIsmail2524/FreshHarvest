package com.freshharvest.controller.Factory_Manager;

import com.freshharvest.app.Factory_Manager.Batch;
import com.freshharvest.dao.DAOFactory;
import com.freshharvest.dao.FactoryDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.freshharvest.util.DataChangeListener;
import com.freshharvest.util.NotificationService;

public class FactoryDashboardController implements DataChangeListener {

    // Stat Cards
    @FXML private Label lblPendingBatches;
    @FXML private Label lblInProgressBatches;
    @FXML private Label lblCompletedBatches;
    @FXML private Label lblPendingShipments;
    @FXML private Label lblTotalInventory;
    @FXML private Label lblTodayShipments;
    
    // Chart containers
    @FXML private VBox chartContainer;
    @FXML private HBox chartButtonsContainer;

    private FactoryDAO factoryDAO;
    private String currentChart = "status";

    // Chart objects - we'll create them dynamically
    private PieChart batchStatusChart;
    private BarChart<String, Number> dailyProductionChart;
    private LineChart<String, Number> weeklyTrendChart;
    private TableView<Batch> recentBatchesTable;
    private TableView<Batch> pendingActionsTable;

    @FXML
    public void initialize() {
        factoryDAO = DAOFactory.getFactoryDAO();
        initializeCharts();
        initializeTables();
        setupChartButtons();
        loadDashboardData();
        showChart("status");
        NotificationService.getInstance().addListener(this);

    }
    
    @Override
public void onDataChanged(String dataType) {
    System.out.println("🏭 Dashboard received notification: " + dataType);
    
    // Use Platform.runLater to update JavaFX components from background threads
    javafx.application.Platform.runLater(() -> {
        switch (dataType) {
            case "BATCH_UPDATED":
            case "INVENTORY_CHANGED":
                System.out.println("🔄 Refreshing dashboard data...");
                loadDashboardStats(); // Refresh the top counters
                refreshCurrentChart(); // Refresh the current chart/table view
                break;
                
            case "ORDER_STATUS_CHANGED":
            case "PAYMENT_PROCESSED":
                System.out.println("📊 Refreshing dashboard stats...");
                loadDashboardStats(); // Only refresh counters, not full charts
                break;
                
            case "SHIPMENT_UPDATED":
                System.out.println("🚚 Refreshing shipment data...");
                loadDashboardStats(); // Refresh shipment counters
                break;
                
            default:
                System.out.println("ℹ️ Unknown data type: " + dataType);
        }
    });
}

// Add this helper method to refresh the current chart/table
private void refreshCurrentChart() {
    // Re-load whatever chart/table is currently visible
    switch (currentChart) {
        case "status":
            loadBatchStatusChart();
            break;
        case "production":
            loadDailyProductionChart();
            break;
        case "trend":
            loadWeeklyTrendChart();
            break;
        case "batches":
            loadRecentBatches();
            break;
        case "actions":
            loadPendingActions();
            break;
    }
}

    private void initializeCharts() {
        // Initialize Batch Status Pie Chart
        batchStatusChart = new PieChart();
        batchStatusChart.setTitle("Batch Status Distribution");
        batchStatusChart.setLegendVisible(true);
        batchStatusChart.setLabelsVisible(true);
        batchStatusChart.setPrefSize(600, 400);

        // Initialize Daily Production Bar Chart
        CategoryAxis xAxisBar = new CategoryAxis();
        NumberAxis yAxisBar = new NumberAxis();
        xAxisBar.setLabel("Process Type");
        yAxisBar.setLabel("Count");
        dailyProductionChart = new BarChart<>(xAxisBar, yAxisBar);
        dailyProductionChart.setTitle("Daily Production Overview");
        dailyProductionChart.setPrefSize(600, 400);

        // Initialize Weekly Trend Line Chart
        CategoryAxis xAxisLine = new CategoryAxis();
        NumberAxis yAxisLine = new NumberAxis();
        xAxisLine.setLabel("Day");
        yAxisLine.setLabel("Batches Processed");
        weeklyTrendChart = new LineChart<>(xAxisLine, yAxisLine);
        weeklyTrendChart.setTitle("Weekly Production Trend");
        weeklyTrendChart.setCreateSymbols(true);
        weeklyTrendChart.setPrefSize(600, 400);
    }

    private void initializeTables() {
        // Initialize Recent Batches Table
        recentBatchesTable = new TableView<>();
        recentBatchesTable.setPrefSize(600, 400);
        
        TableColumn<Batch, Integer> batchIdColumn = new TableColumn<>("ID");
        batchIdColumn.setCellValueFactory(new PropertyValueFactory<>("processId"));
        batchIdColumn.setPrefWidth(60);
        
        TableColumn<Batch, String> processColumn = new TableColumn<>("Process");
        processColumn.setCellValueFactory(new PropertyValueFactory<>("processName"));
        processColumn.setPrefWidth(150);
        
        TableColumn<Batch, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("processStatus"));
        statusColumn.setPrefWidth(100);
        
        TableColumn<Batch, Timestamp> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        dateColumn.setPrefWidth(80);
        dateColumn.setCellFactory(column -> new TableCell<Batch, Timestamp>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("N/A");
                } else {
                    setText(formatTimestamp(item));
                }
            }
        });
        
        recentBatchesTable.getColumns().addAll(batchIdColumn, processColumn, statusColumn, dateColumn);

        // Initialize Pending Actions Table
        pendingActionsTable = new TableView<>();
        pendingActionsTable.setPrefSize(600, 400);
        
        TableColumn<Batch, Integer> colBatchId = new TableColumn<>("ID");
        colBatchId.setCellValueFactory(new PropertyValueFactory<>("processId"));
        colBatchId.setPrefWidth(50);
        
        TableColumn<Batch, String> colProcessName = new TableColumn<>("Process");
        colProcessName.setCellValueFactory(new PropertyValueFactory<>("processName"));
        colProcessName.setPrefWidth(120);
        
        TableColumn<Batch, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("processStatus"));
        colStatus.setPrefWidth(100);
        
        TableColumn<Batch, String> colOperator = new TableColumn<>("Operator");
        colOperator.setCellValueFactory(new PropertyValueFactory<>("operatorName"));
        colOperator.setPrefWidth(80);
        
        pendingActionsTable.getColumns().addAll(colBatchId, colProcessName, colStatus, colOperator);
    }

    private void setupChartButtons() {
        ToggleGroup chartToggleGroup = new ToggleGroup();
        
        ToggleButton statusChartBtn = createToggleButton(" Status Distribution", chartToggleGroup, "status");
        ToggleButton productionChartBtn = createToggleButton(" Production Overview", chartToggleGroup, "production");
        ToggleButton trendChartBtn = createToggleButton("Weekly Trend", chartToggleGroup, "trend");
        ToggleButton batchesTableBtn = createToggleButton("Recent Batches", chartToggleGroup, "batches");
        ToggleButton actionsTableBtn = createToggleButton(" Action Required", chartToggleGroup, "actions");
        
        // Set default selection
        statusChartBtn.setSelected(true);
        updateButtonStyle(statusChartBtn, true);
        
        chartButtonsContainer.getChildren().addAll(
            statusChartBtn, productionChartBtn, trendChartBtn, 
            batchesTableBtn, actionsTableBtn
        );
    }

    private ToggleButton createToggleButton(String text, ToggleGroup group, String chartType) {
        ToggleButton button = new ToggleButton(text);
        button.setToggleGroup(group);
        updateButtonStyle(button, false);
        
        button.setOnAction(e -> {
            // Update all button styles
            chartButtonsContainer.getChildren().forEach(node -> {
                if (node instanceof ToggleButton) {
                    updateButtonStyle((ToggleButton) node, node == button);
                }
            });
            showChart(chartType);
        });
        
        return button;
    }

    private void updateButtonStyle(ToggleButton button, boolean selected) {
        if (selected) {
            button.setStyle("""
                -fx-background-color: #3498db;
                -fx-border-color: #2980b9;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-padding: 8 15;
                -fx-font-weight: bold;
                -fx-text-fill: white;
            """);
        } else {
            button.setStyle("""
                -fx-background-color: #f8f9fa;
                -fx-border-color: #dee2e6;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-padding: 8 15;
                -fx-font-weight: bold;
                -fx-text-fill: #495057;
            """);
        }
    }

    private void showChart(String chartType) {
        currentChart = chartType;
        chartContainer.getChildren().clear();
        
        switch (chartType) {
            case "status":
                loadBatchStatusChart();
                chartContainer.getChildren().add(createChartContainer("Batch Status Distribution", batchStatusChart));
                break;
            case "production":
                loadDailyProductionChart();
                chartContainer.getChildren().add(createChartContainer("Daily Production Overview", dailyProductionChart));
                break;
            case "trend":
                loadWeeklyTrendChart();
                chartContainer.getChildren().add(createChartContainer("Weekly Production Trend", weeklyTrendChart));
                break;
            case "batches":
                loadRecentBatches();
                chartContainer.getChildren().add(createTableContainer("Recent Batches", recentBatchesTable));
                break;
            case "actions":
                loadPendingActions();
                chartContainer.getChildren().add(createTableContainer("Action Required", pendingActionsTable));
                break;
        }
    }

    private VBox createChartContainer(String title, Chart chart) {
        VBox container = new VBox(10);
        container.setStyle("""
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-background-color: #ffffff;
            -fx-padding: 20;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
        """);
        container.setPrefHeight(400);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        VBox.setVgrow(chart, javafx.scene.layout.Priority.ALWAYS);
        container.getChildren().addAll(titleLabel, chart);
        
        return container;
    }

    private VBox createTableContainer(String title, TableView<Batch> table) {
        VBox container = new VBox(10);
        container.setStyle("""
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-background-color: #ffffff;
            -fx-padding: 20;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
        """);
        container.setPrefHeight(400);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
        container.getChildren().addAll(titleLabel, table);
        
        return container;
    }

    private void loadDashboardData() {
        loadDashboardStats();
        // Charts will be loaded when selected
    }

    private void loadDashboardStats() {
        try {
            Map<String, Integer> statusCounts = factoryDAO.getBatchStatusCounts();
            int pendingShipments = factoryDAO.getPendingShipmentCount();
            int totalInventory = factoryDAO.getTotalInventoryQuantity();
            
            int todayShipments = calculateTodayShipments();
            
            lblPendingBatches.setText(String.valueOf(statusCounts.getOrDefault("Pending", 0)));
            lblInProgressBatches.setText(String.valueOf(statusCounts.getOrDefault("In Progress", 0)));
            lblCompletedBatches.setText(String.valueOf(statusCounts.getOrDefault("Completed", 0)));
            lblPendingShipments.setText(String.valueOf(pendingShipments));
            lblTotalInventory.setText(String.valueOf(totalInventory));
            lblTodayShipments.setText(String.valueOf(todayShipments));
            
        } catch (Exception e) {
            System.err.println("Error loading dashboard stats: " + e.getMessage());
            e.printStackTrace();
            setDefaultValues();
        }
    }

    private void loadBatchStatusChart() {
        try {
            Map<String, Integer> statusCounts = factoryDAO.getBatchStatusCounts();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
                if (entry.getValue() > 0) {
                    pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
                }
            }
            
            batchStatusChart.setData(pieChartData);
            applyPieChartColors();
            
        } catch (Exception e) {
            System.err.println("Error loading batch status chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDailyProductionChart() {
        try {
            Map<String, Integer> processDistribution = getProcessTypeDistribution();
            
            dailyProductionChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Process Distribution");
            
            for (Map.Entry<String, Integer> entry : processDistribution.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            
            dailyProductionChart.getData().add(series);
            applyBarChartColors();
            
        } catch (Exception e) {
            System.err.println("Error loading daily production chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadWeeklyTrendChart() {
        try {
            Map<String, Integer> weeklyTrend = getWeeklyProductionTrend();
            
            weeklyTrendChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Batches Processed");
            
            for (Map.Entry<String, Integer> entry : weeklyTrend.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            
            weeklyTrendChart.getData().add(series);
            
        } catch (Exception e) {
            System.err.println("Error loading weekly trend chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadRecentBatches() {
        try {
            List<Batch> recentBatches = factoryDAO.getRecentBatches(10);
            ObservableList<Batch> batchList = FXCollections.observableArrayList(recentBatches);
            recentBatchesTable.setItems(batchList);
            applyTableRowStyling(recentBatchesTable);
            
        } catch (Exception e) {
            System.err.println("Error loading recent batches: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPendingActions() {
        try {
            List<Batch> pendingBatches = factoryDAO.getBatchesByStatus("Pending");
            List<Batch> inProgressBatches = factoryDAO.getBatchesByStatus("In Progress");
            
            ObservableList<Batch> actionBatches = FXCollections.observableArrayList();
            actionBatches.addAll(pendingBatches);
            actionBatches.addAll(inProgressBatches);
            
            actionBatches.sort((b1, b2) -> {
                Timestamp t1 = b1.getCreatedAt() != null ? b1.getCreatedAt() : new Timestamp(0);
                Timestamp t2 = b2.getCreatedAt() != null ? b2.getCreatedAt() : new Timestamp(0);
                return t2.compareTo(t1);
            });
            
            if (actionBatches.size() > 8) {
                actionBatches = FXCollections.observableArrayList(actionBatches.subList(0, 8));
            }
            
            pendingActionsTable.setItems(actionBatches);
            applyTableRowStyling(pendingActionsTable);
            
        } catch (Exception e) {
            System.err.println("Error loading pending actions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper methods (same as before)
    private int calculateTodayShipments() {
        try {
            List<Map<String, Object>> shipments = factoryDAO.getShipmentReport();
            LocalDate today = LocalDate.now();
            
            return (int) shipments.stream()
                .filter(shipment -> {
                    Object date = shipment.get("shipment_date");
                    if (date instanceof Timestamp) {
                        return ((Timestamp) date).toLocalDateTime().toLocalDate().equals(today);
                    }
                    return false;
                })
                .count();
        } catch (Exception e) {
            return 0;
        }
    }

    private Map<String, Integer> getProcessTypeDistribution() {
        try {
            List<Batch> allBatches = factoryDAO.getAllBatches();
            Map<String, Integer> distribution = new java.util.HashMap<>();
            
            for (Batch batch : allBatches) {
                String processName = batch.getProcessName();
                distribution.put(processName, distribution.getOrDefault(processName, 0) + 1);
            }
            
            return distribution;
        } catch (Exception e) {
            return java.util.Map.of("Washing", 0, "Sorting", 0, "Grading", 0, "Packaging", 0);
        }
    }

    private Map<String, Integer> getWeeklyProductionTrend() {
        try {
            // For now, using sample data. You can replace with real data from your database
            return Map.of(
                "Mon", 12, "Tue", 18, "Wed", 15, "Thu", 22,
                "Fri", 20, "Sat", 8, "Sun", 5
            );
        } catch (Exception e) {
            return Map.of("Mon", 0, "Tue", 0, "Wed", 0, "Thu", 0, "Fri", 0, "Sat", 0, "Sun", 0);
        }
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        try {
            return timestamp.toLocalDateTime().toLocalDate().toString();
        } catch (Exception e) {
            return "Invalid Date";
        }
    }

    private void applyPieChartColors() {
        String[] colors = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7", "#DDA0DD"};
        int colorIndex = 0;
        
        if (batchStatusChart.getData() != null) {
            for (PieChart.Data data : batchStatusChart.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-pie-color: " + colors[colorIndex % colors.length] + ";");
                    colorIndex++;
                }
            }
        }
    }

    private void applyBarChartColors() {
        String barColor = "-fx-bar-fill: #3498db;";
        if (dailyProductionChart.getData() != null) {
            for (XYChart.Series<String, Number> series : dailyProductionChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        data.getNode().setStyle(barColor);
                    }
                }
            }
        }
    }

    private void applyTableRowStyling(TableView<Batch> table) {
        table.setRowFactory(tv -> new TableRow<Batch>() {
            @Override
            protected void updateItem(Batch batch, boolean empty) {
                super.updateItem(batch, empty);
                
                if (empty || batch == null) {
                    setStyle("");
                } else {
                    String status = batch.getProcessStatus();
                    if (status != null) {
                        switch (status.toLowerCase()) {
                            case "pending":
                                setStyle("-fx-background-color: #fff3cd;");
                                break;
                            case "in progress":
                                setStyle("-fx-background-color: #d4edda;");
                                break;
                            case "completed":
                                setStyle("-fx-background-color: #cce7ff;");
                                break;
                            case "failed":
                                setStyle("-fx-background-color: #f8d7da;");
                                break;
                            default:
                                setStyle("");
                        }
                    }
                }
            }
        });
    }

    private void setDefaultValues() {
        lblPendingBatches.setText("0");
        lblInProgressBatches.setText("0");
        lblCompletedBatches.setText("0");
        lblPendingShipments.setText("0");
        lblTotalInventory.setText("0");
        lblTodayShipments.setText("0");
    }

    @FXML
    private void onRefresh() {
        loadDashboardData();
        showChart(currentChart); // Refresh current view
        showSuccessAlert("Dashboard Refreshed", "All data has been updated successfully!");
    }
    
   
  

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}