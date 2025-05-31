package com.example.codeverse.Students.StudentFragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.codeverse.R;
import com.example.codeverse.Students.Models.Student;
import com.example.codeverse.Students.Helpers.StudentDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Pattern;

public class ContactDetails extends Fragment {

    // UI Components
    private MaterialCardView cvBack, cvHelp;
    private TextInputLayout tilMobileNumber, tilAlternateNumber, tilPermanentAddress;
    private TextInputLayout tilCity, tilProvince, tilPostalCode;
    private TextInputLayout tilEmergencyName, tilEmergencyRelationship, tilEmergencyNumber;
    private TextInputEditText etMobileNumber, etAlternateNumber, etPermanentAddress;
    private TextInputEditText etCity, etPostalCode, etEmergencyName, etEmergencyNumber;
    private AutoCompleteTextView dropdownProvince, dropdownEmergencyRelationship;
    private MaterialButton btnSubmit, btnCancel, btnGoToDashboard;
    private View loadingOverlay, successOverlay;

    // Database helper
    private StudentDatabaseHelper dbHelper;

    // Student ID passed from previous step
    private long studentId = -1;

    // Data
    private final String[] provinceItems = {"Western Province", "Central Province", "Southern Province",
            "Northern Province", "Eastern Province", "North Western Province",
            "North Central Province", "Uva Province", "Sabaragamuwa Province"};

    private final String[] relationshipItems = {"Parent", "Guardian", "Sibling", "Spouse", "Relative", "Friend", "Other"};

    // Validation patterns
    private final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    private final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{5}$");

    // Constants
    private static final String TAG = "ContactDetails";
    private static final String ARG_STUDENT_ID = "student_id";

    public static ContactDetails newInstance(long studentId) {
        ContactDetails fragment = new ContactDetails();
        Bundle args = new Bundle();
        args.putLong(ARG_STUDENT_ID, studentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_details, container, false);

        // Get student ID from arguments
        if (getArguments() != null) {
            studentId = getArguments().getLong(ARG_STUDENT_ID, -1);
        }

        // Initialize database helper
        dbHelper = new StudentDatabaseHelper(getContext());

        // Initialize UI components
        initializeViews(view);
        setupDropdowns();
        setupListeners();
        setupInputValidation();

        // Populate fields with existing data (if any)
        populateFieldsFromData();

        return view;
    }

    private void initializeViews(View view) {
        // Cards
        cvBack = view.findViewById(R.id.cv_back);
        cvHelp = view.findViewById(R.id.cv_help);

        // TextInputLayouts
        tilMobileNumber = view.findViewById(R.id.til_mobile_number);
        tilAlternateNumber = view.findViewById(R.id.til_alternate_number);
        tilPermanentAddress = view.findViewById(R.id.til_permanent_address);
        tilCity = view.findViewById(R.id.til_city);
        tilProvince = view.findViewById(R.id.til_province);
        tilPostalCode = view.findViewById(R.id.til_postal_code);
        tilEmergencyName = view.findViewById(R.id.til_emergency_name);
        tilEmergencyRelationship = view.findViewById(R.id.til_emergency_relationship);
        tilEmergencyNumber = view.findViewById(R.id.til_emergency_number);

        // EditTexts
        etMobileNumber = view.findViewById(R.id.et_mobile_number);
        etAlternateNumber = view.findViewById(R.id.et_alternate_number);
        etPermanentAddress = view.findViewById(R.id.et_permanent_address);
        etCity = view.findViewById(R.id.et_city);
        etPostalCode = view.findViewById(R.id.et_postal_code);
        etEmergencyName = view.findViewById(R.id.et_emergency_name);
        etEmergencyNumber = view.findViewById(R.id.et_emergency_number);

        // Dropdowns
        dropdownProvince = view.findViewById(R.id.dropdown_province);
        dropdownEmergencyRelationship = view.findViewById(R.id.dropdown_emergency_relationship);

        // Buttons
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnGoToDashboard = view.findViewById(R.id.btn_go_to_dashboard);

        // Overlays
        loadingOverlay = view.findViewById(R.id.loading_overlay);
        successOverlay = view.findViewById(R.id.success_overlay);
    }

    private void setupDropdowns() {
        // Set up Province dropdown
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, provinceItems);
        dropdownProvince.setAdapter(provinceAdapter);

        // Set up Relationship dropdown
        ArrayAdapter<String> relationshipAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, relationshipItems);
        dropdownEmergencyRelationship.setAdapter(relationshipAdapter);
    }

    private void setupListeners() {
        // Back button click
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Help button click
        cvHelp.setOnClickListener(v -> {
            Toast.makeText(getContext(),
                    "Fill in your contact details and emergency contact information.",
                    Toast.LENGTH_LONG).show();
        });

        // Submit button click
        btnSubmit.setOnClickListener(v -> {
            if (validateAllFields()) {
                saveContactDetails();
            }
        });

        // Cancel button click
        btnCancel.setOnClickListener(v -> {
            // Delete the student record if it exists
            if (studentId != -1) {
                dbHelper.deleteStudent(studentId);
            }

            // Navigate to home/dashboard screen
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        // Go to Dashboard button (in success overlay)
        btnGoToDashboard.setOnClickListener(v -> {
            // Navigate to dashboard screen
            showToast("Student registration completed successfully!");
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void saveContactDetails() {
        // Show loading
        loadingOverlay.setVisibility(View.VISIBLE);

        try {
            if (studentId == -1) {
                loadingOverlay.setVisibility(View.GONE);
                showToast("Error: Student ID not found");
                return;
            }

            // Get input values
            String mobileNumber = etMobileNumber.getText().toString().trim();
            String alternateNumber = etAlternateNumber.getText().toString().trim();
            String permanentAddress = etPermanentAddress.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String province = dropdownProvince.getText().toString().trim();
            String postalCode = etPostalCode.getText().toString().trim();
            String emergencyName = etEmergencyName.getText().toString().trim();
            String emergencyRelationship = dropdownEmergencyRelationship.getText().toString().trim();
            String emergencyNumber = etEmergencyNumber.getText().toString().trim();

            // Update contact details in database
            int result = dbHelper.updateStudentContactDetails(studentId, mobileNumber, alternateNumber,
                    permanentAddress, city, province, postalCode, emergencyName, emergencyRelationship, emergencyNumber);

            // Simulate some processing time
            new Handler().postDelayed(() -> {
                loadingOverlay.setVisibility(View.GONE);

                if (result > 0) {
                    // Show success overlay
                    successOverlay.setVisibility(View.VISIBLE);
                } else {
                    showToast("Failed to save contact details. Please try again.");
                }
            }, 2000);

        } catch (Exception e) {
            loadingOverlay.setVisibility(View.GONE);
            Log.e(TAG, "Error saving contact details: " + e.getMessage());
            showToast("An error occurred while saving data");
        }
    }

    private void setupInputValidation() {
        // Mobile Number validation
        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateMobileNumber();
            }
        });

        // Alternate Number validation (optional)
        etAlternateNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    validateAlternateNumber();
                } else {
                    tilAlternateNumber.setError(null);
                }
            }
        });

        // Permanent Address validation
        etPermanentAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validatePermanentAddress();
            }
        });

        // City validation
        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateCity();
            }
        });

        // Postal Code validation
        etPostalCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validatePostalCode();
            }
        });

        // Emergency Name validation
        etEmergencyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateEmergencyName();
            }
        });

        // Emergency Number validation
        etEmergencyNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateEmergencyNumber();
            }
        });
    }

    // Validation methods
    private boolean validateMobileNumber() {
        String mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString().trim();
        if (mobileNumber.isEmpty()) {
            tilMobileNumber.setError("Mobile number is required");
            return false;
        } else if (!PHONE_PATTERN.matcher(mobileNumber).matches()) {
            tilMobileNumber.setError("Please enter a valid 10-digit mobile number");
            return false;
        } else {
            tilMobileNumber.setError(null);
            return true;
        }
    }

    private boolean validateAlternateNumber() {
        String alternateNumber = Objects.requireNonNull(etAlternateNumber.getText()).toString().trim();
        if (alternateNumber.isEmpty()) {
            // Optional field, no error needed
            return true;
        } else if (!PHONE_PATTERN.matcher(alternateNumber).matches()) {
            tilAlternateNumber.setError("Please enter a valid 10-digit mobile number");
            return false;
        } else {
            tilAlternateNumber.setError(null);
            return true;
        }
    }

    private boolean validatePermanentAddress() {
        String address = Objects.requireNonNull(etPermanentAddress.getText()).toString().trim();
        if (address.isEmpty()) {
            tilPermanentAddress.setError("Permanent address is required");
            return false;
        } else if (address.length() < 5) {
            tilPermanentAddress.setError("Please enter a valid address");
            return false;
        } else {
            tilPermanentAddress.setError(null);
            return true;
        }
    }

    private boolean validateCity() {
        String city = Objects.requireNonNull(etCity.getText()).toString().trim();
        if (city.isEmpty()) {
            tilCity.setError("City is required");
            return false;
        } else {
            tilCity.setError(null);
            return true;
        }
    }

    private boolean validateProvince() {
        String province = dropdownProvince.getText().toString().trim();
        if (province.isEmpty()) {
            tilProvince.setError("Province is required");
            return false;
        } else {
            tilProvince.setError(null);
            return true;
        }
    }

    private boolean validatePostalCode() {
        String postalCode = Objects.requireNonNull(etPostalCode.getText()).toString().trim();
        if (postalCode.isEmpty()) {
            tilPostalCode.setError("Postal code is required");
            return false;
        } else if (!POSTAL_CODE_PATTERN.matcher(postalCode).matches()) {
            tilPostalCode.setError("Please enter a valid 5-digit postal code");
            return false;
        } else {
            tilPostalCode.setError(null);
            return true;
        }
    }

    private boolean validateEmergencyName() {
        String name = Objects.requireNonNull(etEmergencyName.getText()).toString().trim();
        if (name.isEmpty()) {
            tilEmergencyName.setError("Emergency contact name is required");
            return false;
        } else {
            tilEmergencyName.setError(null);
            return true;
        }
    }

    private boolean validateEmergencyRelationship() {
        String relationship = dropdownEmergencyRelationship.getText().toString().trim();
        if (relationship.isEmpty()) {
            tilEmergencyRelationship.setError("Relationship is required");
            return false;
        } else {
            tilEmergencyRelationship.setError(null);
            return true;
        }
    }

    private boolean validateEmergencyNumber() {
        String emergencyNumber = Objects.requireNonNull(etEmergencyNumber.getText()).toString().trim();
        if (emergencyNumber.isEmpty()) {
            tilEmergencyNumber.setError("Emergency contact number is required");
            return false;
        } else if (!PHONE_PATTERN.matcher(emergencyNumber).matches()) {
            tilEmergencyNumber.setError("Please enter a valid 10-digit mobile number");
            return false;
        } else {
            tilEmergencyNumber.setError(null);
            return true;
        }
    }

    private boolean validateAllFields() {
        boolean isValid = true;

        // Validate all fields
        if (!validateMobileNumber()) isValid = false;
        if (!validateAlternateNumber()) isValid = false;
        if (!validatePermanentAddress()) isValid = false;
        if (!validateCity()) isValid = false;
        if (!validateProvince()) isValid = false;
        if (!validatePostalCode()) isValid = false;
        if (!validateEmergencyName()) isValid = false;
        if (!validateEmergencyRelationship()) isValid = false;
        if (!validateEmergencyNumber()) isValid = false;

        // If any field is invalid, show toast message
        if (!isValid) {
            Toast.makeText(getContext(), "Please correct the errors before submitting", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private void populateFieldsFromData() {
        if (studentId == -1) return;

        try {
            // Get existing student data from database
            Student student = dbHelper.getStudentById(studentId);

            if (student != null) {
                // Populate fields with existing contact data if available
                if (student.getMobileNumber() != null && !student.getMobileNumber().isEmpty()) {
                    etMobileNumber.setText(student.getMobileNumber());
                }

                if (student.getAlternateNumber() != null && !student.getAlternateNumber().isEmpty()) {
                    etAlternateNumber.setText(student.getAlternateNumber());
                }

                if (student.getPermanentAddress() != null && !student.getPermanentAddress().isEmpty()) {
                    etPermanentAddress.setText(student.getPermanentAddress());
                }

                if (student.getCity() != null && !student.getCity().isEmpty()) {
                    etCity.setText(student.getCity());
                }

                if (student.getProvince() != null && !student.getProvince().isEmpty()) {
                    dropdownProvince.setText(student.getProvince(), false);
                }

                if (student.getPostalCode() != null && !student.getPostalCode().isEmpty()) {
                    etPostalCode.setText(student.getPostalCode());
                }

                if (student.getEmergencyName() != null && !student.getEmergencyName().isEmpty()) {
                    etEmergencyName.setText(student.getEmergencyName());
                }

                if (student.getEmergencyRelationship() != null && !student.getEmergencyRelationship().isEmpty()) {
                    dropdownEmergencyRelationship.setText(student.getEmergencyRelationship(), false);
                }

                if (student.getEmergencyNumber() != null && !student.getEmergencyNumber().isEmpty()) {
                    etEmergencyNumber.setText(student.getEmergencyNumber());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating fields: " + e.getMessage());
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}