package com.example.codeverse;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Pattern;

public class ContactDetails extends Fragment {

    // Interface for communication with parent activity
    public interface OnContactInfoListener {
        void onContactInfoCompleted(StudentDetails contactInfo);
        void onContactInfoCancelled();
        void onNavigateToStep(int step);
        void onRegistrationCompleted();
    }

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
    private LottieAnimationView ivBack, ivHelp;

    // Data
    private StudentDetails studentDetails;
    private OnContactInfoListener listener;
    private StudentDatabaseHelper databaseHelper;

    // Constants
    private final String[] provinceItems = {"Western Province", "Central Province", "Southern Province",
            "Northern Province", "Eastern Province", "North Western Province",
            "North Central Province", "Uva Province", "Sabaragamuwa Province"};

    private final String[] relationshipItems = {"Parent", "Guardian", "Sibling", "Spouse", "Relative", "Friend", "Other"};

    // Validation patterns
    private final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    private final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{5}$");

    private static final String TAG = "ContactDetailsFragment";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContactInfoListener) {
            listener = (OnContactInfoListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnContactInfoListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize database helper
        databaseHelper = StudentDatabaseHelper.getInstance(requireContext());

        // Get student details from arguments
        if (getArguments() != null) {
            studentDetails = getArguments().getParcelable("student_details");
        }

        if (studentDetails == null) {
            studentDetails = new StudentDetails();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_details, container, false);

        // Initialize UI components
        initializeViews(view);
        setupDropdowns();
        setupListeners();
        setupInputValidation();
        populateFieldsFromData();

        return view;
    }

    private void initializeViews(View view) {
        // Cards
        cvBack = view.findViewById(R.id.cv_back);
        cvHelp = view.findViewById(R.id.cv_help);

        // Animations
        ivBack = view.findViewById(R.id.iv_back);
        ivHelp = view.findViewById(R.id.iv_help);

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
                requireContext(), android.R.layout.simple_dropdown_item_1line, provinceItems);
        dropdownProvince.setAdapter(provinceAdapter);

        // Set up Relationship dropdown
        ArrayAdapter<String> relationshipAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, relationshipItems);
        dropdownEmergencyRelationship.setAdapter(relationshipAdapter);
    }

    private void setupListeners() {
        // Back button click
        cvBack.setOnClickListener(v -> {
            // Add animation effect
            ivBack.playAnimation();
            // Navigate back to previous screen
            requireActivity().onBackPressed();
        });

        // Help button click
        cvHelp.setOnClickListener(v -> {
            // Add animation effect
            ivHelp.playAnimation();
            // Show help dialog or toast
            Toast.makeText(getContext(),
                    "Fill in your contact details and emergency contact information.",
                    Toast.LENGTH_LONG).show();
        });

        // Submit button click
        btnSubmit.setOnClickListener(v -> {
            if (validateAllFields()) {
                // Show loading overlay
                loadingOverlay.setVisibility(View.VISIBLE);

                // Save the final data
                saveCurrentStepData();

                // Simulate API call or database operation
                new Handler().postDelayed(() -> {
                    // Hide loading overlay
                    loadingOverlay.setVisibility(View.GONE);

                    // Show success overlay
                    successOverlay.setVisibility(View.VISIBLE);

                    // Notify listener that contact info is completed
                    listener.onContactInfoCompleted(studentDetails);
                }, 2000);
            }
        });

        // Cancel button click
        btnCancel.setOnClickListener(v -> listener.onContactInfoCancelled());

        // Go to Dashboard button (in success overlay)
        btnGoToDashboard.setOnClickListener(v -> listener.onRegistrationCompleted());
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
        if (studentDetails != null) {
            if (studentDetails.getMobileNumber() != null) {
                etMobileNumber.setText(studentDetails.getMobileNumber());
            }
            if (studentDetails.getAlternateNumber() != null) {
                etAlternateNumber.setText(studentDetails.getAlternateNumber());
            }
            if (studentDetails.getPermanentAddress() != null) {
                etPermanentAddress.setText(studentDetails.getPermanentAddress());
            }
            if (studentDetails.getCity() != null) {
                etCity.setText(studentDetails.getCity());
            }
            if (studentDetails.getProvince() != null) {
                dropdownProvince.setText(studentDetails.getProvince(), false);
            }
            if (studentDetails.getPostalCode() != null) {
                etPostalCode.setText(studentDetails.getPostalCode());
            }
            if (studentDetails.getEmergencyName() != null) {
                etEmergencyName.setText(studentDetails.getEmergencyName());
            }
            if (studentDetails.getEmergencyRelationship() != null) {
                dropdownEmergencyRelationship.setText(studentDetails.getEmergencyRelationship(), false);
            }
            if (studentDetails.getEmergencyNumber() != null) {
                etEmergencyNumber.setText(studentDetails.getEmergencyNumber());
            }
        }
    }

    private void saveCurrentStepData() {
        // Save contact details
        studentDetails.setMobileNumber(etMobileNumber.getText().toString().trim());
        studentDetails.setAlternateNumber(etAlternateNumber.getText().toString().trim());
        studentDetails.setPermanentAddress(etPermanentAddress.getText().toString().trim());
        studentDetails.setCity(etCity.getText().toString().trim());
        studentDetails.setProvince(dropdownProvince.getText().toString());
        studentDetails.setPostalCode(etPostalCode.getText().toString().trim());
        studentDetails.setEmergencyName(etEmergencyName.getText().toString().trim());
        studentDetails.setEmergencyRelationship(dropdownEmergencyRelationship.getText().toString());
        studentDetails.setEmergencyNumber(etEmergencyNumber.getText().toString().trim());

        // Save to database
        databaseHelper.insertOrUpdateStudent(studentDetails);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}