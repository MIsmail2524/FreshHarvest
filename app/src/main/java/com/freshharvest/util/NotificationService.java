package com.freshharvest.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Observer pattern implementation for real-time notifications
 */
public class NotificationService {
    private static NotificationService instance;
    private List<DataChangeListener> listeners = new ArrayList<>();
    
    private NotificationService() {}
    
    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }
    
    // Add listener
    public void addListener(DataChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    // Remove listener
    public void removeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }
    
    // Notify all listeners
    public void notifyDataChanged(String dataType) {
        for (DataChangeListener listener : listeners) {
            listener.onDataChanged(dataType);
        }
    }
    
    // Specific notification methods for common events
    public void notifyBatchUpdated() {
        notifyDataChanged("BATCH_UPDATED");
    }
    
    public void notifyOrderStatusChanged() {
        notifyDataChanged("ORDER_STATUS_CHANGED");
    }
    
    public void notifyPaymentProcessed() {
        notifyDataChanged("PAYMENT_PROCESSED");
    }
    
    public void notifyInventoryChanged() {
        notifyDataChanged("INVENTORY_CHANGED");
    }

        public void notifyCropAdded() {
        notifyDataChanged("CROP_ADDED");
    }

     public void notifyCropUpdated() {
        notifyDataChanged("CROP_UPDATED");
    }
    
    public void notifyShipmentCreated() {
        notifyDataChanged("SHIPMENT_CREATED");
    }

     public void notifyOrderCreated() {
        notifyDataChanged("ORDER_CREATED");
    }
    
   
}