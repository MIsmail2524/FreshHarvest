package com.freshharvest.app;

import javafx.beans.property.*;

public class ReportData {
    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty revenue = new SimpleStringProperty();
    private final StringProperty expense = new SimpleStringProperty();
    private final StringProperty profit = new SimpleStringProperty();
    private final StringProperty crop = new SimpleStringProperty();
    private final StringProperty season = new SimpleStringProperty();

    public ReportData(String date, String category, String description,
                      String revenue, String expense, String profit,
                      String crop, String season) {
        this.date.set(date);
        this.category.set(category);
        this.description.set(description);
        this.revenue.set(revenue);
        this.expense.set(expense);
        this.profit.set(profit);
        this.crop.set(crop);
        this.season.set(season);
    }

    // Property getters
    public StringProperty dateProperty() { return date; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty revenueProperty() { return revenue; }
    public StringProperty expenseProperty() { return expense; }
    public StringProperty profitProperty() { return profit; }
    public StringProperty cropProperty() { return crop; }
    public StringProperty seasonProperty() { return season; }

    // Regular getters
    public String getDate() { return date.get(); }
    public String getCategory() { return category.get(); }
    public String getDescription() { return description.get(); }
    public String getRevenue() { return revenue.get(); }
    public String getExpense() { return expense.get(); }
    public String getProfit() { return profit.get(); }
    public String getCrop() { return crop.get(); }
    public String getSeason() { return season.get(); }
}