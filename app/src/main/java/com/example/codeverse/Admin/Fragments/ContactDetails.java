package com.example.codeverse.Admin.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.codeverse.R;
import com.example.codeverse.Students.Models.Student;
import com.example.codeverse.Students.Helpers.StudentDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class ContactDetails extends Fragment {

    private MaterialCardView cvBack, cvHelp;
    private TextInputLayout tilMobileNumber, tilAlternateNumber, tilPermanentAddress;
    private TextInputLayout tilCity, tilProvince, tilPostalCode;
    private TextInputLayout tilEmergencyName, tilEmergencyRelationship, tilEmergencyNumber;
    private TextInputEditText etMobileNumber, etAlternateNumber, etPermanentAddress;
    private TextInputEditText etCity, etPostalCode, etEmergencyName, etEmergencyNumber;
    private AutoCompleteTextView dropdownProvince, dropdownEmergencyRelationship;
    private MaterialButton btnSubmit, btnCancel, btnGoToDashboard;
    private FrameLayout loadingOverlay, successOverlay;
    private LinearLayout cardBasicInfoIndicator, cardAcademicIndicator,
            cardAccountIndicator, cardContactIndicator;

    private StudentDatabaseHelper dbHelper;

    private long studentId = -1;

    private final String[] provinceItems = {"Western Province", "Central Province", "Southern Province",
            "Northern Province", "Eastern Province", "North Western Province",
            "North Central Province", "Uva Province", "Sabaragamuwa Province"};

    private final String[] relationshipItems = {"Parent", "Guardian", "Sibling", "Spouse", "Relative", "Friend", "Other"};
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{5}$");

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

        if (getArguments() != null) {
            studentId = getArguments().getLong(ARG_STUDENT_ID, -1);
        }

        dbHelper = new StudentDatabaseHelper(getContext());

        initializeViews(view);
        setupDropdowns();
        setupClickListeners();
        setupTextChangeListeners();
        populateFieldsFromData();

        return view;
    }

    private void initializeViews(View view) {
        try {
            cvBack = view.findViewById(R.id.cv_back);
            cvHelp = view.findViewById(R.id.cv_help);

            tilMobileNumber = view.findViewById(R.id.til_mobile_number);
            tilAlternateNumber = view.findViewById(R.id.til_alternate_number);
            tilPermanentAddress = view.findViewById(R.id.til_permanent_address);
            tilCity = view.findViewById(R.id.til_city);
            tilProvince = view.findViewById(R.id.til_province);
            tilPostalCode = view.findViewById(R.id.til_postal_code);
            tilEmergencyName = view.findViewById(R.id.til_emergency_name);
            tilEmergencyRelationship = view.findViewById(R.id.til_emergency_relationship);
            tilEmergencyNumber = view.findViewById(R.id.til_emergency_number);

            etMobileNumber = view.findViewById(R.id.et_mobile_number);
            etAlternateNumber = view.findViewById(R.id.et_alternate_number);
            etPermanentAddress = view.findViewById(R.id.et_permanent_address);
            etCity = view.findViewById(R.id.et_city);
            etPostalCode = view.findViewById(R.id.et_postal_code);
            etEmergencyName = view.findViewById(R.id.et_emergency_name);
            etEmergencyNumber = view.findViewById(R.id.et_emergency_number);

            dropdownProvince = view.findViewById(R.id.dropdown_province);
            dropdownEmergencyRelationship = view.findViewById(R.id.dropdown_emergency_relationship);

            btnSubmit = view.findViewById(R.id.btn_submit);
            btnCancel = view.findViewById(R.id.btn_cancel);
            btnGoToDashboard = view.findViewById(R.id.btn_go_to_dashboard);

            loadingOverlay = view.findViewById(R.id.loading_overlay);
            successOverlay = view.findViewById(R.id.success_overlay);

            cardBasicInfoIndicator = view.findViewById(R.id.card_basic_info_indicator);
            cardAcademicIndicator = view.findViewById(R.id.card_academic_indicator);
            cardAccountIndicator = view.findViewById(R.id.card_account_indicator);
            cardContactIndicator = view.findViewById(R.id.card_contact_indicator);

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            showToast("Failed to initialize the form");
        }
    }

    private void setupDropdowns() {
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, provinceItems);
        dropdownProvince.setAdapter(provinceAdapter);

        ArrayAdapter<String> relationshipAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, relationshipItems);
        dropdownEmergencyRelationship.setAdapter(relationshipAdapter);
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        cvHelp.setOnClickListener(v -> showHelpDialog());

        btnSubmit.setOnClickListener(v -> {
            if (validateAllFields()) {
                saveContactDetails();
            }
        });

        btnCancel.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Cancel Student Creation")
                    .setMessage("Are you sure you want to cancel? All entered information will be lost.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete the student record if it exists
                        if (studentId != -1) {
                            dbHelper.deleteStudent(studentId);
                        }
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        btnGoToDashboard.setOnClickListener(v -> {
            showToast("Student registration completed successfully!");

            clearAllRegistrationData();

            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void setupTextChangeListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearErrors();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etMobileNumber.addTextChangedListener(textWatcher);
        etAlternateNumber.addTextChangedListener(textWatcher);
        etPermanentAddress.addTextChangedListener(textWatcher);
        etCity.addTextChangedListener(textWatcher);
        etPostalCode.addTextChangedListener(textWatcher);
        etEmergencyName.addTextChangedListener(textWatcher);
        etEmergencyNumber.addTextChangedListener(textWatcher);
    }

    private void clearErrors() {
        tilMobileNumber.setError(null);
        tilAlternateNumber.setError(null);
        tilPermanentAddress.setError(null);
        tilCity.setError(null);
        tilProvince.setError(null);
        tilPostalCode.setError(null);
        tilEmergencyName.setError(null);
        tilEmergencyRelationship.setError(null);
        tilEmergencyNumber.setError(null);
    }

    private boolean validateAllFields() {
        boolean isValid = true;

        if (TextUtils.isEmpty(etMobileNumber.getText())) {
            tilMobileNumber.setError("Mobile number is required");
            isValid = false;
        } else if (!PHONE_PATTERN.matcher(etMobileNumber.getText().toString().trim()).matches()) {
            tilMobileNumber.setError("Please enter a valid 10-digit mobile number");
            isValid = false;
        }

        String alternateNumber = etAlternateNumber.getText() != null ? etAlternateNumber.getText().toString().trim() : "";
        if (!TextUtils.isEmpty(alternateNumber) && !PHONE_PATTERN.matcher(alternateNumber).matches()) {
            tilAlternateNumber.setError("Please enter a valid 10-digit mobile number");
            isValid = false;
        }

        if (TextUtils.isEmpty(etPermanentAddress.getText())) {
            tilPermanentAddress.setError("Permanent address is required");
            isValid = false;
        } else if (etPermanentAddress.getText().toString().trim().length() < 5) {
            tilPermanentAddress.setError("Please enter a valid address");
            isValid = false;
        }

        if (TextUtils.isEmpty(etCity.getText())) {
            tilCity.setError("City is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(dropdownProvince.getText())) {
            tilProvince.setError("Province is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etPostalCode.getText())) {
            tilPostalCode.setError("Postal code is required");
            isValid = false;
        } else if (!POSTAL_CODE_PATTERN.matcher(etPostalCode.getText().toString().trim()).matches()) {
            tilPostalCode.setError("Please enter a valid 5-digit postal code");
            isValid = false;
        }

        if (TextUtils.isEmpty(etEmergencyName.getText())) {
            tilEmergencyName.setError("Emergency contact name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(dropdownEmergencyRelationship.getText())) {
            tilEmergencyRelationship.setError("Relationship is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(etEmergencyNumber.getText())) {
            tilEmergencyNumber.setError("Emergency contact number is required");
            isValid = false;
        } else if (!PHONE_PATTERN.matcher(etEmergencyNumber.getText().toString().trim()).matches()) {
            tilEmergencyNumber.setError("Please enter a valid 10-digit mobile number");
            isValid = false;
        }

        if (!isValid) {
            showToast("Please correct the errors before submitting");
        }

        return isValid;
    }

    private void saveContactDetails() {
        loadingOverlay.setVisibility(View.VISIBLE);

        try {
            if (studentId == -1) {
                loadingOverlay.setVisibility(View.GONE);
                showToast("Error: Student ID not found");
                return;
            }

            String mobileNumber = etMobileNumber.getText().toString().trim();
            String alternateNumber = etAlternateNumber.getText().toString().trim();
            String permanentAddress = etPermanentAddress.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String province = dropdownProvince.getText().toString().trim();
            String postalCode = etPostalCode.getText().toString().trim();
            String emergencyName = etEmergencyName.getText().toString().trim();
            String emergencyRelationship = dropdownEmergencyRelationship.getText().toString().trim();
            String emergencyNumber = etEmergencyNumber.getText().toString().trim();

            int result = dbHelper.updateStudentContactDetails(studentId, mobileNumber, alternateNumber,
                    permanentAddress, city, province, postalCode, emergencyName, emergencyRelationship, emergencyNumber);

            new Handler().postDelayed(() -> {
                loadingOverlay.setVisibility(View.GONE);

                if (result > 0) {
                    clearForm();

                    successOverlay.setVisibility(View.VISIBLE);

                    Log.d(TAG, "Student registration completed successfully for ID: " + studentId);
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

    private void showHelpDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Contact Details Help")
                .setMessage("Please fill in your contact information:\n\n" +
                        "• Mobile Number: Your primary contact number (10 digits)\n" +
                        "• Alternate Number: Optional secondary contact (10 digits)\n" +
                        "• Address: Your complete permanent address\n" +
                        "• City: Your city of residence\n" +
                        "• Province: Select your province from the dropdown\n" +
                        "• Postal Code: Your 5-digit postal code\n\n" +
                        "Emergency Contact:\n" +
                        "• Name: Full name of emergency contact person\n" +
                        "• Relationship: How this person relates to you\n" +
                        "• Number: Their contact number (10 digits)\n\n" +
                        "All fields except alternate number are required.")
                .setPositiveButton("Got it", null)
                .show();
    }

    private void populateFieldsFromData() {
        if (studentId == -1) return;

        try {
            Student student = dbHelper.getStudentById(studentId);

            if (student != null) {
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

    private void clearForm() {
        etMobileNumber.setText("");
        etAlternateNumber.setText("");
        etPermanentAddress.setText("");
        etCity.setText("");
        dropdownProvince.setText("");
        etPostalCode.setText("");
        etEmergencyName.setText("");
        dropdownEmergencyRelationship.setText("");
        etEmergencyNumber.setText("");
        clearErrors();
    }

    private void clearAllRegistrationData() {
        clearForm();
        Log.d(TAG, "All registration data cleared for completed student registration");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}