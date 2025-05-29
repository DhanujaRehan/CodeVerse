package com.example.codeverse;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.codeverse.R;
import com.example.codeverse.StudentDatabaseHelper;
import com.example.codeverse.Student;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactDetails extends Fragment {

    private static final String TAG = "ContactDetailsFragment";

    // Views
    private TextInputEditText etMobileNumber, etAlternateNumber, etPermanentAddress,
            etCity, etPostalCode, etEmergencyName, etEmergencyNumber;
    private AutoCompleteTextView dropdownProvince, dropdownEmergencyRelationship;
    private TextInputLayout tilMobileNumber, tilAlternateNumber, tilPermanentAddress,
            tilCity, tilProvince, tilPostalCode, tilEmergencyName,
            tilEmergencyRelationship, tilEmergencyNumber;
    private MaterialButton btnSubmit, btnCancel;
    private MaterialCardView cvBack;
    private FrameLayout loadingOverlay, successOverlay;
    private MaterialButton btnGoToDashboard;

    // Data
    private Student currentStudent;
    private StudentDatabaseHelper dbHelper;
    private ExecutorService executorService;

    public interface OnRegistrationCompleteListener {
        void onRegistrationComplete(Student student);
        void onCancel();
        void onBack();
        void onGoToDashboard();
    }

    private OnRegistrationCompleteListener registrationCompleteListener;

    public static ContactDetails newInstance() {
        return new ContactDetails();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (currentStudent == null) {
            currentStudent = new Student();
        }
        dbHelper = StudentDatabaseHelper.getInstance(getContext());
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_details, container, false);
        initViews(view);
        setupListeners();
        setupDropdowns();
        return view;
    }

    private void initViews(View view) {
        // Text input fields
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

        // Text input layouts
        tilMobileNumber = view.findViewById(R.id.til_mobile_number);
        tilAlternateNumber = view.findViewById(R.id.til_alternate_number);
        tilPermanentAddress = view.findViewById(R.id.til_permanent_address);
        tilCity = view.findViewById(R.id.til_city);
        tilProvince = view.findViewById(R.id.til_province);
        tilPostalCode = view.findViewById(R.id.til_postal_code);
        tilEmergencyName = view.findViewById(R.id.til_emergency_name);
        tilEmergencyRelationship = view.findViewById(R.id.til_emergency_relationship);
        tilEmergencyNumber = view.findViewById(R.id.til_emergency_number);

        // Buttons
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnCancel = view.findViewById(R.id.btn_cancel);
        cvBack = view.findViewById(R.id.cv_back);
        btnGoToDashboard = view.findViewById(R.id.btn_go_to_dashboard);

        // Overlays
        loadingOverlay = view.findViewById(R.id.loading_overlay);
        successOverlay = view.findViewById(R.id.success_overlay);
    }

    private void setupListeners() {
        btnSubmit.setOnClickListener(v -> {
            if (validateInput()) {
                saveContactInfo();
                submitRegistration();
            }
        });

        btnCancel.setOnClickListener(v -> {
            if (registrationCompleteListener != null) {
                registrationCompleteListener.onCancel();
            }
        });

        cvBack.setOnClickListener(v -> {
            if (registrationCompleteListener != null) {
                registrationCompleteListener.onBack();
            }
        });

        btnGoToDashboard.setOnClickListener(v -> {
            if (registrationCompleteListener != null) {
                registrationCompleteListener.onGoToDashboard();
            }
        });
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
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                provinces
        );
        dropdownProvince.setAdapter(provinceAdapter);

        // Setup Emergency Relationship dropdown
        String[] relationships = {
                "Parent",
                "Guardian",
                "Spouse",
                "Sibling",
                "Relative",
                "Friend",
                "Other"
        };
        ArrayAdapter<String> relationshipAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                relationships
        );
        dropdownEmergencyRelationship.setAdapter(relationshipAdapter);
    }

    private boolean validateInput() {
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
        } else if (!isValidSriLankanPhoneNumber(mobileNumber)) {
            tilMobileNumber.setError("Invalid Sri Lankan phone number");
            isValid = false;
        }

        // Validate alternate number (optional)
        String alternateNumber = etAlternateNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(alternateNumber) && !isValidSriLankanPhoneNumber(alternateNumber)) {
            tilAlternateNumber.setError("Invalid phone number format");
            isValid = false;
        }

        // Validate permanent address
        String permanentAddress = etPermanentAddress.getText().toString().trim();
        if (TextUtils.isEmpty(permanentAddress)) {
            tilPermanentAddress.setError("Permanent address is required");
            isValid = false;
        } else if (permanentAddress.length() < 10) {
            tilPermanentAddress.setError("Address must be at least 10 characters");
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
            tilProvince.setError("Province is required");
            isValid = false;
        }

        // Validate postal code
        String postalCode = etPostalCode.getText().toString().trim();
        if (TextUtils.isEmpty(postalCode)) {
            tilPostalCode.setError("Postal code is required");
            isValid = false;
        } else if (!postalCode.matches("^[0-9]{5}$")) {
            tilPostalCode.setError("Postal code must be 5 digits");
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
            tilEmergencyRelationship.setError("Emergency contact relationship is required");
            isValid = false;
        }

        // Validate emergency contact number
        String emergencyNumber = etEmergencyNumber.getText().toString().trim();
        if (TextUtils.isEmpty(emergencyNumber)) {
            tilEmergencyNumber.setError("Emergency contact number is required");
            isValid = false;
        } else if (!isValidSriLankanPhoneNumber(emergencyNumber)) {
            tilEmergencyNumber.setError("Invalid phone number format");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidSriLankanPhoneNumber(String phoneNumber) {
        // Sri Lankan phone number patterns:
        // Mobile: 07XXXXXXXX (10 digits starting with 07)
        // Landline: 0XXXXXXXXX (10 digits starting with 0, not 07)
        // International format: +94XXXXXXXXX
        phoneNumber = phoneNumber.replaceAll("\\s+", ""); // Remove spaces

        return phoneNumber.matches("^(\\+94|0)(7[0-9]{8}|[1-9][0-9]{8})$");
    }

    private void saveContactInfo() {
        currentStudent.setMobileNumber(etMobileNumber.getText().toString().trim());
        currentStudent.setAlternateNumber(etAlternateNumber.getText().toString().trim());
        currentStudent.setPermanentAddress(etPermanentAddress.getText().toString().trim());
        currentStudent.setCity(etCity.getText().toString().trim());
        currentStudent.setProvince(dropdownProvince.getText().toString().trim());
        currentStudent.setPostalCode(etPostalCode.getText().toString().trim());
        currentStudent.setEmergencyContactName(etEmergencyName.getText().toString().trim());
        currentStudent.setEmergencyRelationship(dropdownEmergencyRelationship.getText().toString().trim());
        currentStudent.setEmergencyContactNumber(etEmergencyNumber.getText().toString().trim());
    }

    private void submitRegistration() {
        showLoadingOverlay(true);

        executorService.execute(() -> {
            try {
                // Simulate processing time
                Thread.sleep(2000);

                long studentId = dbHelper.addStudent(currentStudent);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoadingOverlay(false);

                        if (studentId > 0) {
                            currentStudent.setId((int) studentId);
                            showSuccessOverlay(true);
                            Toast.makeText(getContext(), "Student registered successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoadingOverlay(false);
                        Toast.makeText(getContext(), "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private void showLoadingOverlay(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showSuccessOverlay(boolean show) {
        successOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show && registrationCompleteListener != null) {
            registrationCompleteListener.onRegistrationComplete(currentStudent);
        }
    }

    public void setOnRegistrationCompleteListener(OnRegistrationCompleteListener listener) {
        this.registrationCompleteListener = listener;
    }

    public void setStudentData(Student student) {
        if (student != null) {
            this.currentStudent = student;
            populateFields();
        }
    }

    private void populateFields() {
        if (currentStudent != null) {
            if (currentStudent.getMobileNumber() != null) {
                etMobileNumber.setText(currentStudent.getMobileNumber());
            }
            if (currentStudent.getAlternateNumber() != null) {
                etAlternateNumber.setText(currentStudent.getAlternateNumber());
            }
            if (currentStudent.getPermanentAddress() != null) {
                etPermanentAddress.setText(currentStudent.getPermanentAddress());
            }
            if (currentStudent.getCity() != null) {
                etCity.setText(currentStudent.getCity());
            }
            if (currentStudent.getProvince() != null) {
                dropdownProvince.setText(currentStudent.getProvince(), false);
            }
            if (currentStudent.getPostalCode() != null) {
                etPostalCode.setText(currentStudent.getPostalCode());
            }
            if (currentStudent.getEmergencyContactName() != null) {
                etEmergencyName.setText(currentStudent.getEmergencyContactName());
            }
            if (currentStudent.getEmergencyRelationship() != null) {
                dropdownEmergencyRelationship.setText(currentStudent.getEmergencyRelationship(), false);
            }
            if (currentStudent.getEmergencyContactNumber() != null) {
                etEmergencyNumber.setText(currentStudent.getEmergencyContactNumber());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        populateFields();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
