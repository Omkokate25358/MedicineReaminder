package com.example.medicare.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medicare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register_Activity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Set up button listeners
        btnRegister.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(Register_Activity.this, LoginActivity.class));
            finish();
        });
    }


        private void registerUser() {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

        // Create user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Save basic user info to Firestore
                                saveUserToFirestore(user.getUid(), name, email);
                            }
                        } else {
                            // Hide progress indicator if you added one
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() :
                                    "Registration failed";
                            Toast.makeText(this, "Registration Failed: " + errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        private void saveUserToFirestore(String userId, String name, String email) {
            DocumentReference userRef = db.collection("Users").document(userId);

            // Create basic user info map
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("name", name);
            userMap.put("email", email);
            userMap.put("uid", userId);

            // Set basic user data
            userRef.set(userMap)
                    .addOnSuccessListener(aVoid -> {
                        // Create default collections for user
                        createDefaultCollections(userId);

                        Toast.makeText(Register_Activity.this, "User Registered Successfully",
                                Toast.LENGTH_SHORT).show();

                        // Redirect to LoginActivity instead of UserProfileActivity
                        Intent intent = new Intent(Register_Activity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Log the error for debugging
                        Log.e("RegisterActivity", "Firestore error: " + e.getMessage());
                        Toast.makeText(Register_Activity.this, "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }

    private void createDefaultCollections(String userId) {
        // Create default days of the week documents for medicine collection
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        // Create an empty document for each day under Medicines collection
        for (String day : daysOfWeek) {
            db.collection("Users")
                    .document(userId)
                    .collection("Medicines")
                    .document(day)
                    .set(new HashMap<>());
        }

        // Create empty collections for user profile information
        Map<String, Object> emptyData = new HashMap<>();

        // Create empty document for personal info
        db.collection("Users")
                .document(userId)
                .collection("UserProfile")
                .document("PersonalInfo")
                .set(emptyData);

        // Create empty document for medical info
        db.collection("Users")
                .document(userId)
                .collection("UserProfile")
                .document("MedicalInfo")
                .set(emptyData);
    }
}