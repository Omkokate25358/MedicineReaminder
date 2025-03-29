package com.example.medicare.Model;

import java.io.Serializable;

public class AlarmModel implements Serializable {
    private String medicineId;
    private String medicineName;
    private String medicineDose;
    private String medicineTime;
    private String medicineDay;
    private boolean isAlarmEnabled;

    public AlarmModel() {
        // Default constructor
    }

    public AlarmModel(String medicineId, String medicineName, String medicineDose,
                      String medicineTime, String medicineDay, boolean isAlarmEnabled) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.medicineDose = medicineDose;
        this.medicineTime = medicineTime;
        this.medicineDay = medicineDay;
        this.isAlarmEnabled = isAlarmEnabled;
    }

    // Getters and setters
    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineDose() {
        return medicineDose;
    }

    public void setMedicineDose(String medicineDose) {
        this.medicineDose = medicineDose;
    }

    public String getMedicineTime() {
        return medicineTime;
    }

    public void setMedicineTime(String medicineTime) {
        this.medicineTime = medicineTime;
    }

    public String getMedicineDay() {
        return medicineDay;
    }

    public void setMedicineDay(String medicineDay) {
        this.medicineDay = medicineDay;
    }

    public boolean isAlarmEnabled() {
        return isAlarmEnabled;
    }

    public void setAlarmEnabled(boolean alarmEnabled) {
        isAlarmEnabled = alarmEnabled;
    }
}