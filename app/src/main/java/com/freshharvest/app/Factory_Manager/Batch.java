package com.freshharvest.app.Factory_Manager;

import java.sql.Timestamp;

public class Batch {
    private int processId;
    private String processName;
    private String processStatus;
    private Timestamp startTime;
    private Timestamp endTime;
    private String operatorName;
    private String notes;
    private String grade;
    private Timestamp processedDate;
    private Timestamp createdAt;
    
    // Constructor
    public Batch(int processId, String processName, String processStatus, 
                 Timestamp startTime, Timestamp endTime, String operatorName, 
                 String notes, String grade, Timestamp processedDate, Timestamp createdAt) {
        this.processId = processId;
        this.processName = processName;
        this.processStatus = processStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.operatorName = operatorName;
        this.notes = notes;
        this.grade = grade;
        this.processedDate = processedDate;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getProcessId() { return processId; }
    public void setProcessId(int processId) { this.processId = processId; }
    
    public String getProcessName() { return processName; }
    public void setProcessName(String processName) { this.processName = processName; }
    
    public String getProcessStatus() { return processStatus; }
    public void setProcessStatus(String processStatus) { this.processStatus = processStatus; }
    
    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }
    
    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }
    
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    
    public Timestamp getProcessedDate() { return processedDate; }
    public void setProcessedDate(Timestamp processedDate) { this.processedDate = processedDate; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    // For backward compatibility with table columns
    public int getBatchId() { return processId; }
    public String getStatus() { return processStatus; }
    public String getProductName() { return processName; }
}