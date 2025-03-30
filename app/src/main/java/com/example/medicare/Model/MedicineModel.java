package com.example.medicare.Model;

public class MedicineModel {
    private String medicineName;
    private String medicineDose;
    private String medicineQuantity;
    private String medicineType;
    private String time;
    private boolean isAlarm;

    // Empty constructor for Firebase
    public MedicineModel() {}

    // Constructor
    public MedicineModel(String medicineName, String medicineDose, String medicineQuantity, 
                        String medicineType, String time, boolean isAlarm) {
        this.medicineName = medicineName;
        this.medicineDose = medicineDose;
        this.medicineQuantity = medicineQuantity;
        this.medicineType = medicineType;
        this.time = time;
        this.isAlarm = isAlarm;
    }

    // Getters and setters
    // Add all necessary getters and setters
} 