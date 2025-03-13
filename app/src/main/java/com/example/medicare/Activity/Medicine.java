package com.example.medicare.Activity;

public class Medicine {
    private String id, name, dose, quantity, time;

    public Medicine() { }  // Required empty constructor for Firestore

    public Medicine(String id, String name, String dose, String quantity, String time) {
        this.id = id;
        this.name = name;
        this.dose = dose;
        this.quantity = quantity;
        this.time = time;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDose() { return dose; }
    public String getQuantity() { return quantity; }
    public String getTime() { return time; }
}
