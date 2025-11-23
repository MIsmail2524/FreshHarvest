package com.freshharvest.controller.Factory_Manager;

import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
// import com.freshharvest.model.Activity;
// import com.freshharvest.model.Plantation;

public class SeasonalActivityController {

    // Form FXML Fields
    @FXML
    private ComboBox<Object> cmbPlantation; // Replace 'Object' with Plantation model
    @FXML
    private ComboBox<String> cmbActivityType;
    @FXML
    private DatePicker dpActivityDate;
    @FXML
    private TextField txtCost;
    @FXML
    private TextField txtNotes;
    @FXML
    private Button btnLogActivity;

    // Table FXML Fields
    @FXML
    private TableView<Object> activityTable; // Replace 'Object' with Activity model
    // ... (TableColumn fields would go here) ...

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // --- Populate ComboBoxes ---
        // Based on Use Case 2
        cmbActivityType.getItems().addAll("Maintenance", "Fertilizing", "Pesticide Spray", "Harvesting", "Other");

        // You would fetch active plantations from the DBHandler
        // Example: cmbPlantation.setItems(dbHandler.getActivePlantations());
        
        // Set default date
        dpActivityDate.setValue(LocalDate.now());

        // --- Set up TableView ---
        // (You would set cell value factories here)
        // (You would call DBHandler.getActivities() to populate this table)
    }

    /**
     * Called when the user clicks the Log Activity button.
     * This follows the flow from Use Case 2.
     */
    @FXML
    private void onLogActivity() {
        System.out.println("--- Logging Seasonal Activity (Use Case 2) ---");
        System.out.println("Plantation: " + cmbPlantation.getValue());
        System.out.println("Activity: " + cmbActivityType.getValue());
        System.out.println("Date: " + dpActivityDate.getValue());
        System.out.println("Cost: " + txtCost.getText());
        System.out.println("Notes: " + txtNotes.getText());

        // --- Database Logic (using your DBHandler) ---
        // 1. Validate entered information.
        // 2. Call DBHandler to save the activity record.
        // 3. Link record to the plantation and cost ledger.
        // 4. Show a success message and refresh the table.
        
        // Example:
        // boolean success = dbHandler.saveActivity(...);
        // if (success) {
        //     refreshActivityTable();
        //     clearForm();
        // }
    }
}