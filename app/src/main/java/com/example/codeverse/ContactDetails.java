package com.example.codeverse;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactDetails extends AppCompatActivity {

    // UI Components
    private MaterialCardView cvBack, cvHelp;

    // Contact Information
    private TextInputLayout tilMobileNumber, tilAlternateNumber, tilPermanentAddress,
            tilCity, tilProvince, tilPostalCode;
    private TextInputEditText etMobileNumber, etAlternateNumber, etPermanentAddress,
            etCity, etPostalCode;
    private AutoCompleteTextView dropdownProvince;

    // Emergency Contact
    private TextInputLayout tilEmergencyName, tilEmergencyRelationship, tilEmergencyNumber;
    private TextInputEditText etEmergencyName, etEmergencyNumber;
    private AutoCompleteTextView dropdownEmergencyRelationship;

    private MaterialButton btnSubmit, btnCancel;
    private View loadingOverlay, successOverlay;
    private MaterialButton btnGoToDashboard;

    // Data
    private Student studentData;
    private StudentDatabaseHelper databaseHelper;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        initializeViews();
        initializeDatabase();
        getStudentData();
        setupDropdowns();
        setupClickListeners();
        updateProgressIndicators();
    }

    private void initializeViews() {
        cvBack = findViewById(R.id.cv_back);
        cvHelp = findViewById(R.id.cv_help);

        // Contact Information
        tilMobileNumber = findViewById(R.id.til_mobile_number);
        tilAlternateNumber = findViewById(R.id.til_alternate_number);
        tilPermanentAddress = findViewById(R.id.til_permanent_address);
        tilCity = findViewById(R.id.til_city);
        tilProvince = findViewById(R.id.til_province);
        tilPostalCode = findViewById(R.id.til_postal_code);

        etMobileNumber = findViewById(R.id.et_mobile_number);
        etAlternateNumber = findViewById(R.id.et_alternate_number);
        etPermanentAddress = findViewById(R.id.et_permanent_address);
        etCity = findViewById(R.id.et_city);
        etPostalCode = findViewById(R.id.et_postal_code);
        dropdownProvince = findViewById(R.id.dropdown_province);

        // Emergency Contact
        tilEmergencyName = findViewById(R.id.til_emergency_name);
        tilEmergencyRelationship = findViewById(R.id.til_emergency_relationship);
        tilEmergencyNumber = findViewById(R.id.til_emergency_number);

        etEmergencyName = findViewById(R.id.et_emergency_name);
        etEmergencyNumber = findViewById(R.id.et_emergency_number);
        dropdownEmergencyRelationship = findViewById(R.id.dropdown_emergency_relationship);

        btnSubmit = findViewById(R.id.btn_submit);
        btnCancel = findViewById(R.id.btn_cancel);
        loadingOverlay = findViewById(R.id.loading_overlay);
        successOverlay = findViewById(R.id.success_overlay);
        btnGoToDashboard = findViewById(R.id.btn_go_to_dashboard);

        executorService = Executors.newFixedThreadPool(2);
    }

    private void initializeDatabase() {
        databaseHelper = new StudentDatabaseHelper(this);
    }

    private void getStudentData() {
        studentData = (Student) getIntent().getSerializableExtra("student_data");
        if (studentData == null) {
            Toast.makeText(this, "Error: No student data found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupDropdowns() {
        // Setup Province dropdown (Sri Lankan provinces)
        String[] provinces = {
                "Western Province",
                "Central Province",
                "Southern Province",
                "Northern Province",
                "Eastern Province",
                "North Western Province",
                "North Central Province",
                "Uva Province",
                "Sabaragamuwa Province"
        };
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, provinces);
        dropdownProvince.setAdapter(provinceAdapter);

        // Setup Emergency Relationship dropdown
        String[] relationships = {
                "Father",
                "Mother",
                "Guardian",
                "Spouse",
                "Brother",
                "Sister",
                "Uncle",
                "Aunt",
                "Grandparent",
                "Friend",
                "Other"
        };
        ArrayAdapter<String> relationshipAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, relationships);
        dropdownEmergencyRelationship.setAdapter(relationshipAdapter);
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> onBackPressed());

        cvHelp.setOnClickListener(v -> showHelpDialog());

        btnSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                submitStudentRegistration();
            }
        });

        btnCancel.setOnClickListener(v -> {
            finish();
        });

        btnGoToDashboard.setOnClickListener(v -> {
            goToDashboard();
        });
    }

    private void showHelpDialog() {
        Toast.makeText(this, "Help: Provide your contact information and emergency contact details. All fields marked as required must be filled.", Toast.LENGTH_LONG).show();
    }

    private void updateProgressIndicators() {
        // Update progress indicators to show current step
        // All steps should show as completed or current
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Reset errors
        tilMobileNumber.setError(null);
        tilAlternateNumber.setError(null);
        tilPermanentAddress.setError(null);
        tilCity.setError(null);
        tilProvince.setError(null);
        tilPostalCode.setError(null);
        tilEmergencyName.setError(null);
        tilEmergencyRelationship.setError(null);
        tilEmergencyNumber.setError(null);

        // Validate mobile number
        String mobileNumber = etMobileNumber.getText().toString().trim();
        if (TextUtils.isEmpty(mobileNumber)) {
            tilMobileNumber.setError("Mobile number is required");
            isValid = false;
        } else if (!isValidPhoneNumber(mobileNumber)) {
            tilMobileNumber.setError("Please enter a valid mobile number");
            isValid = false;
        }

        // Validate alternate number (optional, but if provided should be valid)
        String alternateNumber = etAlternateNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(alternateNumber) && !isValidPhoneNumber(alternateNumber)) {
            tilAlternateNumber.setError("Please enter a valid alternate number");
            isValid = false;
        }

        // Validate permanent address
        String permanentAddress = etPermanentAddress.getText().toString().trim();
        if (TextUtils.isEmpty(permanentAddress)) {
            tilPermanentAddress.setError("Permanent address is required");
            isValid = false;
        } else if (permanentAddress.length() < 10) {
            tilPermanentAddress.setError("Please provide a complete address");
            isValid = false;
        }

        // Validate city
        String city = etCity.getText().toString().trim();
        if (TextUtils.isEmpty(city)) {
            tilCity.setError("City is required");
            isValid = false;
        }

        // Validate province
        String province = dropdownProvince.getText().toString().trim();
        if (TextUtils.isEmpty(province)) {
            tilProvince.setError("Please select a province");
            isValid = false;
        }

        // Validate postal code
        String postalCode = etPostalCode.getText().toString().trim();
        if (TextUtils.isEmpty(postalCode)) {
            tilPostalCode.setError("Postal code is required");
            isValid = false;
        } else if (!isValidPostalCode(postalCode)) {
            tilPostalCode.setError("Please enter a valid postal code");
            isValid = false;
        }

        // Validate emergency contact name
        String emergencyName = etEmergencyName.getText().toString().trim();
        if (TextUtils.isEmpty(emergencyName)) {
            tilEmergencyName.setError("Emergency contact name is required");
            isValid = false;
        }

        // Validate emergency relationship
        String emergencyRelationship = dropdownEmergencyRelationship.getText().toString().trim();
        if (TextUtils.isEmpty(emergencyRelationship)) {
            tilEmergencyRelationship.setError("Please select relationship");
            isValid = false;
        }

        // Validate emergency contact number
        String emergencyNumber = etEmergencyNumber.getText().toString().trim();
        if (TextUtils.isEmpty(emergencyNumber)) {
            tilEmergencyNumber.setError("Emergency contact number is required");
            isValid = false;
        } else if (!isValidPhoneNumber(emergencyNumber)) {
            tilEmergencyNumber.setError("Please enter a valid emergency contact number");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Sri Lankan phone number validation
        // Mobile: 07XXXXXXXX (10 digits starting with 07)
        // Landline: 0XXXXXXXXX (10 digits starting with 0)
        // International: +94XXXXXXXXX

        phoneNumber = phoneNumber.replaceAll("\\s+", ""); // Remove spaces

        if (phoneNumber.startsWith("+94")) {
            phoneNumber = "0" + phoneNumber.substring(3);
        }

        return phoneNumber.matches("^0[0-9]{8,9}$");
    }

    private boolean isValidPostalCode(String postalCode) {
        // Sri Lankan postal codes are 5 digits
        return postalCode.matches("^[0-9]{5}$");
    }

    private void submitStudentRegistration() {
        showLoading(true);

        // Update student object with contact details
        studentData.setMobileNumber(etMobileNumber.getText().toString().trim());
        studentData.setAlternateNumber(etAlternateNumber.getText().toString().trim());
        studentData.setPermanentAddress(etPermanentAddress.getText().toString().trim());
        studentData.setCity(etCity.getText().toString().trim());
        studentData.setProvince(dropdownProvince.getText().toString().trim());
        studentData.setPostalCode(etPostalCode.getText().toString().trim());
        studentData.setEmergencyContactName(etEmergencyName.getText().toString().trim());
        studentData.setEmergencyRelationship(dropdownEmergencyRelationship.getText().toString().trim());
        studentData.setEmergencyContactNumber(etEmergencyNumber.getText().toString().trim());

        // Submit to database in background thread
        executorService.execute(() -> {
            try {
                long result = databaseHelper.addStudent(studentData);

                // Update UI on main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    showLoading(false);

                    if (result != -1) {
                        showSuccessMessage();
                    } else {
                        showErrorMessage("Failed to register student. Please try again.");
                    }
                });

            } catch (Exception e) {
                // Handle database error on main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    showLoading(false);
                    showErrorMessage("Database error: " + e.getMessage());
                });
            }
        });
    }

    private void showSuccessMessage() {
        successOverlay.setVisibility(View.VISIBLE);

        // Auto-hide after 3 seconds if user doesn't interact
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (successOverlay.getVisibility() == View.VISIBLE) {
                goToDashboard();
            }
        }, 3000);
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void goToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!show);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}