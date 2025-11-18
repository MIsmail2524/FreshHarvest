package com.freshharvest.app;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReportData {
    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty amount = new SimpleStringProperty();

    public ReportData(String date, String category, String description, String amount) {
        this.date.set(date);
        this.category.set(category);
        this.description.set(description);
        this.amount.set(amount);
    }

    // Property getters
    public StringProperty dateProperty() { return date; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty amountProperty() { return amount; }

    // Regular getters
    public String getDate() { return date.get(); }
    public String getCategory() { return category.get(); }
    public String getDescription() { return description.get(); }
    public String getAmount() { return amount.get(); }

    // Setters
    public void setDate(String date) { this.date.set(date); }
    public void setCategory(String category) { this.category.set(category); }
    public void setDescription(String description) { this.description.set(description); }
    public void setAmount(String amount) { this.amount.set(amount); }
}