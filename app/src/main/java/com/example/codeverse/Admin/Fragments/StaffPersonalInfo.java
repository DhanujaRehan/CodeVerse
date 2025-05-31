package com.example.codeverse.Admin.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.codeverse.R;
import com.example.codeverse.Admin.Models.Staff;
import com.example.codeverse.Admin.Helpers.StaffDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StaffPersonalInfo extends Fragment {

    private static final String TAG = "AddStaffPersonalFragment";
    private static final String ARG_STAFF_ID = "staff_id";

    private StaffDatabaseHelper dbHelper;
    private Staff currentStaff;
    private long staffId = -1;

    // Views from XML layout
    private MaterialCardView cvBack, cvHelp;
    private ImageView ivStaffPhoto;
    private FloatingActionButton fabAddPhoto;
    private TextInputEditText etFullName, etEmail, etContactNumber, etNicNumber, etDateOfBirth;
    private AutoCompleteTextView dropdownGender;
    private TextInputLayout tilFullName, tilEmail, tilContactNumber, tilNicNumber, tilGender, tilDateOfBirth;
    private MaterialButton btnNextStep, btnCancel;
    private FrameLayout loadingOverlay;

    private String selectedImageUri = null;
    private Calendar calendar = Calendar.getInstance();

    // Initialize image picker launcher
    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        ivStaffPhoto.setImageURI(selectedImageUri);
                        ivStaffPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        // Save the image and get the file path
                        String savedImagePath = saveImageToInternalStorage(selectedImageUri);
                        if (savedImagePath != null) {
                            this.selectedImageUri = savedImagePath;
                        }
                    }
                }
            });

    public static StaffPersonalInfo newInstance(long staffId) {
        StaffPersonalInfo fragment = new StaffPersonalInfo();
        Bundle args = new Bundle();
        args.putLong(ARG_STAFF_ID, staffId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize database helper
        dbHelper = new StaffDatabaseHelper(getContext());

        // Get staff ID from arguments if provided
        if (getArguments() != null) {
            staffId = getArguments().getLong(ARG_STAFF_ID, -1);
        }

        // Check if we're editing an existing staff or creating new
        Bundle args = getArguments();
        if (args != null && args.containsKey("staff")) {
            currentStaff = (Staff) args.getSerializable("staff");
            Log.d(TAG, "Editing existing staff: " + currentStaff.getFullName());
        } else if (staffId != -1) {
            // Load existing staff by ID
            currentStaff = dbHelper.getStaffById(staffId);
            if (currentStaff != null) {
                Log.d(TAG, "Loaded existing staff: " + currentStaff.getFullName());
            }
        } else {
            currentStaff = new Staff();
            Log.d(TAG, "Creating new staff");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_personal_info, container, false);

        // Initialize views
        initializeViews(view);

        // Set up click listeners
        setupClickListeners();

        // Setup text change listeners
        setupTextChangeListeners();

        // Setup gender dropdown
        setupGenderDropdown();

        // Populate fields if editing existing staff
        populateFieldsFromStaff();

        return view;
    }

    private void initializeViews(View view) {
        try {
            // Initialize views with exact IDs from XML
            cvBack = view.findViewById(R.id.cv_back);
            cvHelp = view.findViewById(R.id.cv_help);
            ivStaffPhoto = view.findViewById(R.id.iv_staff_photo);
            fabAddPhoto = view.findViewById(R.id.fab_add_photo);
            etFullName = view.findViewById(R.id.et_full_name);
            etEmail = view.findViewById(R.id.et_email);
            etContactNumber = view.findViewById(R.id.et_contact_number);
            etNicNumber = view.findViewById(R.id.et_nic_number);
            etDateOfBirth = view.findViewById(R.id.et_date_of_birth);
            dropdownGender = view.findViewById(R.id.dropdown_gender);

            // Initialize TextInputLayouts for error handling
            tilFullName = view.findViewById(R.id.til_full_name);
            tilEmail = view.findViewById(R.id.til_email);
            tilContactNumber = view.findViewById(R.id.til_contact_number);
            tilNicNumber = view.findViewById(R.id.til_nic_number);
            tilGender = view.findViewById(R.id.til_gender);
            tilDateOfBirth = view.findViewById(R.id.til_date_of_birth);

            btnNextStep = view.findViewById(R.id.btn_next_step);
            btnCancel = view.findViewById(R.id.btn_cancel);
            loadingOverlay = view.findViewById(R.id.loading_overlay);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            showToast("Failed to initialize the form");
        }
    }

    private void setupClickListeners() {
        // Back button
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Help button
        cvHelp.setOnClickListener(v -> showHelpDialog());

        // Photo selection
        fabAddPhoto.setOnClickListener(v -> showImagePickerDialog());

        // Date picker for date of birth
        etDateOfBirth.setOnClickListener(v -> showDatePicker());
        etDateOfBirth.setFocusable(false);

        // Next step button
        btnNextStep.setOnClickListener(v -> {
            if (validatePersonalInformation()) {
                proceedToNextStep();
            }
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> cancelRegistration());
    }

    private void setupTextChangeListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearErrors();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        etFullName.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);
        etContactNumber.addTextChangedListener(textWatcher);
        etNicNumber.addTextChangedListener(textWatcher);
        dropdownGender.addTextChangedListener(textWatcher);
        etDateOfBirth.addTextChangedListener(textWatcher);
    }

    private void clearErrors() {
        if (tilFullName != null) tilFullName.setError(null);
        if (tilEmail != null) tilEmail.setError(null);
        if (tilContactNumber != null) tilContactNumber.setError(null);
        if (tilNicNumber != null) tilNicNumber.setError(null);
        if (tilGender != null) tilGender.setError(null);
        if (tilDateOfBirth != null) tilDateOfBirth.setError(null);
    }

    private void setupGenderDropdown() {
        String[] genderOptions = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                genderOptions
        );
        dropdownGender.setAdapter(adapter);
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Help")
                .setMessage("Fill in all required personal information fields. Photo is optional but recommended.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};
        new AlertDialog.Builder(getContext())
                .setTitle("Select Photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            imagePickerLauncher.launch(cameraIntent);
        } else {
            showToast("Camera not available");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    etDateOfBirth.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR) - 25, // Default to 25 years ago
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set maximum date to today (can't select future dates)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void proceedToNextStep() {
        // Check for duplicate values
        if (checkForDuplicates()) {
            return;
        }

        // Show loading
        showLoading(true);

        // Save personal information to current staff object
        savePersonalInformation();

        // Save to database with partial data using Handler for smooth UI
        new Handler().postDelayed(() -> {
            try {
                long resultId;
                if (currentStaff.getId() > 0) {
                    // Update existing staff
                    int rowsAffected = dbHelper.updateStaff(currentStaff);
                    resultId = currentStaff.getId();
                    Log.d(TAG, "Staff updated in database. Rows affected: " + rowsAffected);
                } else {
                    // Insert new staff
                    resultId = dbHelper.insertStaff(currentStaff);
                    currentStaff.setId(resultId);
                    Log.d(TAG, "Staff saved to database with ID: " + resultId);
                }

                // Hide loading
                showLoading(false);

                if (resultId > 0) {
                    showToast("Personal information saved successfully!");

                    // Clear form after successful save
                    clearForm();

                    // Navigate to next step (Professional Details)
                    navigateToProfessionalDetails();
                } else {
                    Log.e(TAG, "Failed to save staff to database");
                    showToast("Failed to save staff information");
                }
            } catch (Exception e) {
                // Hide loading
                showLoading(false);
                Log.e(TAG, "Error saving staff: " + e.getMessage(), e);
                showToast("Error saving staff: " + e.getMessage());
            }
        }, 1500); // 1.5 second delay for smooth loading experience
    }

    private boolean validatePersonalInformation() {
        boolean isValid = true;

        // Validate full name
        if (etFullName.getText().toString().trim().isEmpty()) {
            if (tilFullName != null) {
                tilFullName.setError("Full name is required");
            } else {
                etFullName.setError("Full name is required");
            }
            etFullName.requestFocus();
            isValid = false;
        }

        // Validate email (optional but if provided, should be valid)
        String email = etEmail.getText().toString().trim();
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (tilEmail != null) {
                tilEmail.setError("Please enter a valid email address");
            } else {
                etEmail.setError("Please enter a valid email address");
            }
            etEmail.requestFocus();
            isValid = false;
        }

        // Validate contact number
        if (etContactNumber.getText().toString().trim().isEmpty()) {
            if (tilContactNumber != null) {
                tilContactNumber.setError("Contact number is required");
            } else {
                etContactNumber.setError("Contact number is required");
            }
            etContactNumber.requestFocus();
            isValid = false;
        } else if (etContactNumber.getText().toString().trim().length() < 10) {
            if (tilContactNumber != null) {
                tilContactNumber.setError("Please enter a valid contact number");
            } else {
                etContactNumber.setError("Please enter a valid contact number");
            }
            etContactNumber.requestFocus();
            isValid = false;
        }

        // Validate NIC number
        if (etNicNumber.getText().toString().trim().isEmpty()) {
            if (tilNicNumber != null) {
                tilNicNumber.setError("NIC number is required");
            } else {
                etNicNumber.setError("NIC number is required");
            }
            etNicNumber.requestFocus();
            isValid = false;
        }

        // Validate gender
        if (dropdownGender.getText().toString().trim().isEmpty()) {
            if (tilGender != null) {
                tilGender.setError("Please select gender");
            } else {
                dropdownGender.setError("Please select gender");
            }
            dropdownGender.requestFocus();
            isValid = false;
        }

        // Validate date of birth
        if (etDateOfBirth.getText().toString().trim().isEmpty()) {
            if (tilDateOfBirth != null) {
                tilDateOfBirth.setError("Date of birth is required");
            } else {
                etDateOfBirth.setError("Date of birth is required");
            }
            etDateOfBirth.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private boolean checkForDuplicates() {
        String email = etEmail.getText().toString().trim();
        String nicNumber = etNicNumber.getText().toString().trim();

        try {
            // Check email if provided
            if (!email.isEmpty() && dbHelper.isEmailExists(email)) {
                if (currentStaff.getId() == 0 || !email.equals(currentStaff.getEmail())) {
                    if (tilEmail != null) {
                        tilEmail.setError("Email already exists");
                    } else {
                        etEmail.setError("Email already exists");
                    }
                    etEmail.requestFocus();
                    return true;
                }
            }

            // Check NIC
            if (dbHelper.isNicExists(nicNumber)) {
                if (currentStaff.getId() == 0 || !nicNumber.equals(currentStaff.getNicNumber())) {
                    if (tilNicNumber != null) {
                        tilNicNumber.setError("NIC number already exists");
                    } else {
                        etNicNumber.setError("NIC number already exists");
                    }
                    etNicNumber.requestFocus();
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking duplicates: " + e.getMessage(), e);
            showToast("Error validating data");
            return true;
        }

        return false;
    }

    private void savePersonalInformation() {
        // Save personal information to staff object
        currentStaff.setFullName(etFullName.getText().toString().trim());
        currentStaff.setEmail(etEmail.getText().toString().trim());
        currentStaff.setContactNumber(etContactNumber.getText().toString().trim());
        currentStaff.setNicNumber(etNicNumber.getText().toString().trim());
        currentStaff.setGender(dropdownGender.getText().toString().trim());
        currentStaff.setDateOfBirth(etDateOfBirth.getText().toString().trim());
        currentStaff.setPhotoUri(selectedImageUri);

        Log.d(TAG, "Personal information saved to staff object: " + currentStaff.toString());
    }

    private void navigateToProfessionalDetails() {
        try {
            // Create bundle to pass staff data to the next fragment
            Bundle args = new Bundle();
            args.putSerializable("staff", currentStaff);

            // Create instance of AddStaffProfessionalFragment
            StaffProfessionalInfo staffProfessionalInfo = new StaffProfessionalInfo();
            staffProfessionalInfo.setArguments(args);

            // Navigate to Professional Details Fragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, staffProfessionalInfo)
                    .addToBackStack("AddStaffPersonal") // Add to back stack so user can return
                    .commit();

            Log.d(TAG, "Successfully navigated to Professional Details with staff ID: " + currentStaff.getId());

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to Professional Details: " + e.getMessage(), e);
            showToast("Error proceeding to next step");
        }
    }

    private void cancelRegistration() {
        new AlertDialog.Builder(getContext())
                .setTitle("Cancel Registration")
                .setMessage("Are you sure you want to cancel? All entered data will be lost.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    clearAllData();
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    } else {
                        getParentFragmentManager().popBackStack();
                    }
                })
                .setNegativeButton("Continue", null)
                .show();
    }

    private void clearAllData() {
        etFullName.setText("");
        etEmail.setText("");
        etContactNumber.setText("");
        etNicNumber.setText("");
        dropdownGender.setText("");
        etDateOfBirth.setText("");

        ivStaffPhoto.setImageResource(R.drawable.addpropic);
        selectedImageUri = null;

        currentStaff = new Staff();
        clearErrors();
    }

    private void showLoading(boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            File photosDir = new File(getContext().getFilesDir(), "staff_photos");
            if (!photosDir.exists()) {
                boolean created = photosDir.mkdirs();
                Log.d(TAG, "Photos directory created: " + created);
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String filename = "staff_photo_" + timestamp + ".jpg";
            File imageFile = new File(photosDir, filename);

            // Copy image from URI to internal storage
            InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            Log.d(TAG, "Image saved to: " + imageFile.getAbsolutePath());
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error saving image: " + e.getMessage(), e);
            showToast("Error saving image");
            return null;
        }
    }

    // Getter method for other fragments to access the current staff data
    public Staff getCurrentStaff() {
        return currentStaff;
    }

    public void setCurrentStaff(Staff staff) {
        this.currentStaff = staff;
        populateFieldsFromStaff();
    }

    private void populateFieldsFromStaff() {
        if (currentStaff != null) {
            if (currentStaff.getFullName() != null) {
                etFullName.setText(currentStaff.getFullName());
            }
            if (currentStaff.getEmail() != null) {
                etEmail.setText(currentStaff.getEmail());
            }
            if (currentStaff.getContactNumber() != null) {
                etContactNumber.setText(currentStaff.getContactNumber());
            }
            if (currentStaff.getNicNumber() != null) {
                etNicNumber.setText(currentStaff.getNicNumber());
            }
            if (currentStaff.getGender() != null) {
                dropdownGender.setText(currentStaff.getGender());
            }
            if (currentStaff.getDateOfBirth() != null) {
                etDateOfBirth.setText(currentStaff.getDateOfBirth());
            }
            if (currentStaff.getPhotoUri() != null) {
                selectedImageUri = currentStaff.getPhotoUri();
                ivStaffPhoto.setImageURI(Uri.fromFile(new File(selectedImageUri)));
                ivStaffPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            Log.d(TAG, "Fields populated from staff: " + currentStaff.toString());
        }
    }

    private void clearForm() {
        etFullName.setText("");
        etEmail.setText("");
        etContactNumber.setText("");
        etNicNumber.setText("");
        dropdownGender.setText("");
        etDateOfBirth.setText("");
        ivStaffPhoto.setImageResource(R.drawable.addpropic);
        selectedImageUri = null;
        clearErrors();
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}