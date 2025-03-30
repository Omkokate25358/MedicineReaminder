package com.example.medicare.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medicare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    // Firebase
    private FirebaseFirestore db;
    private String userId;
    private DocumentReference userRef;

    // Profile data
    private Map<String, Object> currentUserData = new HashMap<>();

    // Profile Views
    private TextView tvUsername, tvEmail;
    private TextView tvPhone, tvDob, tvAddress;
    private TextView tvBloodGroup, tvAllergies, tvDoctor;
    private TextView tvEmergencyName, tvRelationship, tvEmergencyPhone;

    // Edit Views
    private EditText etPhone, etDob, etAddress;
    private EditText etBloodGroup, etAllergies, etDoctor;
    private EditText etEmergencyName, etRelationship, etEmergencyPhone;

    // Buttons
    private Button btnEditProfile, btnSaveProfile, btnLogout;

    // View for the fragment
    private View rootView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "EluqHjWXIVf9Z7NEhSlhftKx1yy1";
        userRef = db.collection("Users").document(userId);

        initViews();
        setupListeners();
        loadUserData(); // Load data after setting up everything

        return rootView;
    }

    private void initViews() {
        // Profile header
        tvUsername = rootView.findViewById(R.id.tvUsername);
        tvEmail = rootView.findViewById(R.id.tvEmail);

        // Personal information
        tvPhone = rootView.findViewById(R.id.tvPhone);
        tvDob = rootView.findViewById(R.id.tvDob);
        tvAddress = rootView.findViewById(R.id.tvAddress);

        // Medical information
        tvBloodGroup = rootView.findViewById(R.id.tvBloodGroup);
        tvAllergies = rootView.findViewById(R.id.tvAllergies);
        tvDoctor = rootView.findViewById(R.id.tvDoctor);

        // Emergency contact
        tvEmergencyName = rootView.findViewById(R.id.tvEmergencyName);
        tvRelationship = rootView.findViewById(R.id.tvRelationship);
        tvEmergencyPhone = rootView.findViewById(R.id.tvEmergencyPhone);

        // Edit text fields
        etPhone = rootView.findViewById(R.id.etPhone);
        etDob = rootView.findViewById(R.id.etDob);
        etAddress = rootView.findViewById(R.id.etAddress);
        etBloodGroup = rootView.findViewById(R.id.etBloodGroup);
        etAllergies = rootView.findViewById(R.id.etAllergies);
        etDoctor = rootView.findViewById(R.id.etDoctor);
        etEmergencyName = rootView.findViewById(R.id.etEmergencyName);
        etRelationship = rootView.findViewById(R.id.etRelationship);
        etEmergencyPhone = rootView.findViewById(R.id.etEmergencyPhone);

        // Buttons
        btnEditProfile = rootView.findViewById(R.id.btnEditProfile);
        btnSaveProfile = rootView.findViewById(R.id.btnSaveProfile);
        btnLogout = rootView.findViewById(R.id.btnLogout);
    }

    private void loadUserData() {
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Store the current data
                currentUserData = documentSnapshot.getData();
                if (currentUserData == null) {
                    currentUserData = new HashMap<>();
                }

                // Basic info
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                if (name != null) tvUsername.setText(name);
                if (email != null) tvEmail.setText(email);

                // Personal info
                String phone = documentSnapshot.getString("phone");
                String dob = documentSnapshot.getString("dob");
                String address = documentSnapshot.getString("address");
                tvPhone.setText(phone != null && !phone.isEmpty() ? phone : "Not set");
                tvDob.setText(dob != null && !dob.isEmpty() ? dob : "Not set");
                tvAddress.setText(address != null && !address.isEmpty() ? address : "Not set");

                // Medical info
                String bloodGroup = documentSnapshot.getString("bloodGroup");
                String allergies = documentSnapshot.getString("allergies");
                String doctor = documentSnapshot.getString("primaryDoctor");
                tvBloodGroup.setText(bloodGroup != null && !bloodGroup.isEmpty() ? bloodGroup : "Not set");
                tvAllergies.setText(allergies != null && !allergies.isEmpty() ? allergies : "Not set");
                tvDoctor.setText(doctor != null && !doctor.isEmpty() ? doctor : "Not set");

                // Emergency contact
                String emergencyName = documentSnapshot.getString("emergencyName");
                String relationship = documentSnapshot.getString("relationship");
                String emergencyPhone = documentSnapshot.getString("emergencyPhone");
                tvEmergencyName.setText(emergencyName != null && !emergencyName.isEmpty() ? emergencyName : "Not set");
                tvRelationship.setText(relationship != null && !relationship.isEmpty() ? relationship : "Not set");
                tvEmergencyPhone.setText(emergencyPhone != null && !emergencyPhone.isEmpty() ? emergencyPhone : "Not set");
            }
        }).addOnFailureListener(e -> {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        // Edit profile button click
        btnEditProfile.setOnClickListener(v -> {
            setEditMode(true);
        });

        // Save profile button click
        btnSaveProfile.setOnClickListener(v -> {
            saveUserData();
        });

        // Date of birth field click
        etDob.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        // Logout button click
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            if (getActivity() != null) {
                getActivity().finish();
                // You may want to start login activity here
            }
        });
    }

    private void setEditMode(boolean editing) {
        // Toggle visibility of TextView and EditText
        int textViewVisibility = editing ? View.GONE : View.VISIBLE;
        int editTextVisibility = editing ? View.VISIBLE : View.GONE;

        // Toggle buttons
        btnEditProfile.setVisibility(editing ? View.GONE : View.VISIBLE);
        btnSaveProfile.setVisibility(editing ? View.VISIBLE : View.GONE);

        // Personal info
        tvPhone.setVisibility(textViewVisibility);
        tvDob.setVisibility(textViewVisibility);
        tvAddress.setVisibility(textViewVisibility);
        etPhone.setVisibility(editTextVisibility);
        etDob.setVisibility(editTextVisibility);
        etAddress.setVisibility(editTextVisibility);

        // Medical info
        tvBloodGroup.setVisibility(textViewVisibility);
        tvAllergies.setVisibility(textViewVisibility);
        tvDoctor.setVisibility(textViewVisibility);
        etBloodGroup.setVisibility(editTextVisibility);
        etAllergies.setVisibility(editTextVisibility);
        etDoctor.setVisibility(editTextVisibility);

        // Emergency contact
        tvEmergencyName.setVisibility(textViewVisibility);
        tvRelationship.setVisibility(textViewVisibility);
        tvEmergencyPhone.setVisibility(textViewVisibility);
        etEmergencyName.setVisibility(editTextVisibility);
        etRelationship.setVisibility(editTextVisibility);
        etEmergencyPhone.setVisibility(editTextVisibility);

        // Populate EditText fields with current values
        if (editing) {
            String phoneText = tvPhone.getText().toString();
            etPhone.setText(phoneText.equals("Not set") ? "" : phoneText);

            String dobText = tvDob.getText().toString();
            etDob.setText(dobText.equals("Not set") ? "" : dobText);

            String addressText = tvAddress.getText().toString();
            etAddress.setText(addressText.equals("Not set") ? "" : addressText);

            String bloodGroupText = tvBloodGroup.getText().toString();
            etBloodGroup.setText(bloodGroupText.equals("Not set") ? "" : bloodGroupText);

            String allergiesText = tvAllergies.getText().toString();
            etAllergies.setText(allergiesText.equals("Not set") ? "" : allergiesText);

            String doctorText = tvDoctor.getText().toString();
            etDoctor.setText(doctorText.equals("Not set") ? "" : doctorText);

            String emergencyNameText = tvEmergencyName.getText().toString();
            etEmergencyName.setText(emergencyNameText.equals("Not set") ? "" : emergencyNameText);

            String relationshipText = tvRelationship.getText().toString();
            etRelationship.setText(relationshipText.equals("Not set") ? "" : relationshipText);

            String emergencyPhoneText = tvEmergencyPhone.getText().toString();
            etEmergencyPhone.setText(emergencyPhoneText.equals("Not set") ? "" : emergencyPhoneText);
        }
    }

    private void saveUserData() {
        // Create a map with only the changed user data
        Map<String, Object> updatedData = new HashMap<>();

        // Only add fields that have changed or are not empty
        addIfChanged(updatedData, "phone", etPhone.getText().toString().trim());
        addIfChanged(updatedData, "dob", etDob.getText().toString().trim());
        addIfChanged(updatedData, "address", etAddress.getText().toString().trim());
        addIfChanged(updatedData, "bloodGroup", etBloodGroup.getText().toString().trim());
        addIfChanged(updatedData, "allergies", etAllergies.getText().toString().trim());
        addIfChanged(updatedData, "primaryDoctor", etDoctor.getText().toString().trim());
        addIfChanged(updatedData, "emergencyName", etEmergencyName.getText().toString().trim());
        addIfChanged(updatedData, "relationship", etRelationship.getText().toString().trim());
        addIfChanged(updatedData, "emergencyPhone", etEmergencyPhone.getText().toString().trim());

        // Only update if there are changes
        if (!updatedData.isEmpty()) {
            userRef.update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        }
                        // Update current data with new values
                        currentUserData.putAll(updatedData);
                        // Update the TextViews with new values
                        updateProfileTextViews();
                        // Set back to view mode
                        setEditMode(false);
                    })
                    .addOnFailureListener(e -> {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // No changes were made, just switch back to view mode
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "No changes were made", Toast.LENGTH_SHORT).show();
            }
            setEditMode(false);
        }
    }

    private void addIfChanged(Map<String, Object> updates, String key, String newValue) {
        // Only add to updates if the value has changed and is not empty
        Object currentValue = currentUserData.get(key);
        String currentStringValue = (currentValue != null) ? currentValue.toString() : "";

        if (!newValue.equals(currentStringValue)) {
            // Only update if the field actually changed
            if (!newValue.isEmpty()) {
                updates.put(key, newValue);
            } else if (!currentStringValue.isEmpty()) {
                // If new value is empty but current value exists, we need to explicitly set to empty string
                // rather than null to avoid the field disappearing
                updates.put(key, "");
            }
        }
    }

    private void updateProfileTextViews() {
        // Update TextViews with values from EditTexts
        tvPhone.setText(getDisplayText(etPhone.getText().toString().trim()));
        tvDob.setText(getDisplayText(etDob.getText().toString().trim()));
        tvAddress.setText(getDisplayText(etAddress.getText().toString().trim()));
        tvBloodGroup.setText(getDisplayText(etBloodGroup.getText().toString().trim()));
        tvAllergies.setText(getDisplayText(etAllergies.getText().toString().trim()));
        tvDoctor.setText(getDisplayText(etDoctor.getText().toString().trim()));
        tvEmergencyName.setText(getDisplayText(etEmergencyName.getText().toString().trim()));
        tvRelationship.setText(getDisplayText(etRelationship.getText().toString().trim()));
        tvEmergencyPhone.setText(getDisplayText(etEmergencyPhone.getText().toString().trim()));
    }

    private String getDisplayText(String value) {
        return value.isEmpty() ? "Not set" : value;
    }

    private void showDatePickerDialog() {
        if (getActivity() == null) return;

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Parse current date if it exists
        String currentDate = etDob.getText().toString();
        if (!currentDate.isEmpty() && !currentDate.equals("Not set")) {
            try {
                String[] dateParts = currentDate.split("/");
                if (dateParts.length == 3) {
                    day = Integer.parseInt(dateParts[0]);
                    month = Integer.parseInt(dateParts[1]) - 1; // Month is 0-based
                    year = Integer.parseInt(dateParts[2]);
                }
            } catch (Exception e) {
                // Use current date if parsing fails
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDob.setText(date);
                },
                year, month, day);
        datePickerDialog.show();
    }
}