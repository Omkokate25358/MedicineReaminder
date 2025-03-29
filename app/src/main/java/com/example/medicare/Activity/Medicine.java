package com.example.medicare.Activity;

public class Medicine {
    private String id;
    private String medicineName;
    private String medicineDose;
    private String medicineQuantity;
    private String medicineTime;
    private String medicineDay;

    // Constructor
    public Medicine(String id, String medicineName, String medicineDose,
                    String medicineQuantity, String medicineTime) {
        this.id = id;
        this.medicineName = medicineName;
        this.medicineDose = medicineDose;
        this.medicineQuantity = medicineQuantity;
        this.medicineTime = medicineTime;
    }



//    public boolean isAlarmEnabled() {
//        return alarmEnabled;
//    }
//
//    public void setAlarmEnabled(boolean alarmEnabled) {
//        this.alarmEnabled = alarmEnabled;
//    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDose() {
        return medicineDose;
    }

    public void setMedicineDose(String medicineDose) {
        this.medicineDose = medicineDose;
    }

    public String getQuantity() {
        return medicineQuantity;
    }

    public void setMedicineQuantity(String medicineQuantity) {
        this.medicineQuantity = medicineQuantity;
    }

    public String getTime() {
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


}
