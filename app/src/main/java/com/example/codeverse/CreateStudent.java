package com.example.codeverse;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.Editable;
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
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateStudent extends Fragment {

    // UI Components
    private ImageView ivStudentPhoto;
    private FloatingActionButton fabAddPhoto;
    private TextInputEditText etFullName, etUniversityId, etNicNumber, etDateOfBirth;
    private AutoCompleteTextView dropdownGender;
    private MaterialButton btnNextStep, btnCancel;
    private MaterialCardView cvBack, cvHelp;
    private ImageView ivBack, ivHelp;
    private FrameLayout loadingOverlay;

    // TextInputLayouts for validation
    private TextInputLayout tilFullName, tilUniversityId, tilNicNumber, tilDateOfBirth, tilGender;

    // Step indicators
    private MaterialCardView cardBasicInfoIndicator, cardAccountIndicator,
            cardAcademicIndicator, cardContactIndicator;

    // Image picker launcher
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri = null;

    // Database helper
    private StudentDatabaseHelper dbHelper;

    // Constants
    private static final String TAG = "CreateStudentFragment";
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_student, container, false);

        // Initialize database helper
        dbHelper = new StudentDatabaseHelper(getContext());

        // Initialize UI components
        initializeViews(view);

        // Set up dropdown for gender selection
        setupGenderDropdown();

        // Set up image picker
        setupImagePicker();

        // Set up click listeners
        setupClickListeners();

        // Set up text change listeners for validation
        setupTextChangeListeners();

        return view;
    }

    @SuppressLint("WrongViewCast")
    private void initializeViews(View view) {
        try {
            // Photo components
            ivStudentPhoto = view.findViewById(R.id.iv_student_photo);
            fabAddPhoto = view.findViewById(R.id.fab_add_photo);

            // Input fields
            etFullName = view.findViewById(R.id.et_full_name);
            etUniversityId = view.findViewById(R.id.et_university_id);
            etNicNumber = view.findViewById(R.id.et_nic_number);
            etDateOfBirth = view.findViewById(R.id.et_date_of_birth);
            dropdownGender = view.findViewById(R.id.dropdown_gender);

            // TextInputLayouts for validation
            tilFullName = view.findViewById(R.id.til_full_name);
            tilUniversityId = view.findViewById(R.id.til_university_id);
            tilNicNumber = view.findViewById(R.id.til_nic_number);
            tilDateOfBirth = view.findViewById(R.id.til_date_of_birth);
            tilGender = view.findViewById(R.id.til_gender);

            // Buttons
            btnNextStep = view.findViewById(R.id.btn_next_step);
            btnCancel = view.findViewById(R.id.btn_cancel);

            // Navigation components
            cvBack = view.findViewById(R.id.cv_back);
            cvHelp = view.findViewById(R.id.cv_help);
            ivBack = view.findViewById(R.id.iv_back);
            ivHelp = view.findViewById(R.id.iv_help);

            // Step indicators
            cardBasicInfoIndicator = view.findViewById(R.id.card_basic_info_indicator);
            cardAccountIndicator = view.findViewById(R.id.card_account_indicator);
            cardAcademicIndicator = view.findViewById(R.id.card_academic_indicator);
            cardContactIndicator = view.findViewById(R.id.card_contact_indicator);

            // Loading overlay
            loadingOverlay = view.findViewById(R.id.loading_overlay);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            Toast.makeText(getContext(), "Failed to initialize the form", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupGenderDropdown() {
        try {
            // Create an array adapter for the gender dropdown
            String[] genders = new String[]{"Male", "Female", "Other", "Prefer not to say"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    genders);
            dropdownGender.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up gender dropdown: " + e.getMessage());
        }
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            selectedImageUri = imageUri;
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        getActivity().getContentResolver(), imageUri);
                                ivStudentPhoto.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                Log.e(TAG, "Error loading image: " + e.getMessage());
                                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void setupClickListeners() {
        // Back button click listener
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Help button click listener
        cvHelp.setOnClickListener(v -> showHelpDialog());

        // Date of birth field click listener
        etDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        // Add photo button click listener
        fabAddPhoto.setOnClickListener(v -> showImageSourceDialog());

        // Next step button click listener
        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                saveStudentData();
            }
        });

        // Cancel button click listener
        btnCancel.setOnClickListener(v -> {
            // Show confirmation dialog before canceling
            new AlertDialog.Builder(getContext())
                    .setTitle("Cancel Student Creation")
                    .setMessage("Are you sure you want to cancel? All entered information will be lost.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void setupTextChangeListeners() {
        // Create a generic TextWatcher for clearing errors when text changes
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear errors when text changes
                clearErrors();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        };

        // Apply the TextWatcher to all input fields
        etFullName.addTextChangedListener(textWatcher);
        etUniversityId.addTextChangedListener(textWatcher);
        etNicNumber.addTextChangedListener(textWatcher);
        etDateOfBirth.addTextChangedListener(textWatcher);

        // For dropdown, use the item click listener
        dropdownGender.setOnItemClickListener((parent, view, position, id) -> clearErrors());
    }

    private void clearErrors() {
        // Clear all error messages
        tilFullName.setError(null);
        tilUniversityId.setError(null);
        tilNicNumber.setError(null);
        tilDateOfBirth.setError(null);
        tilGender.setError(null);
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate full name
        if (TextUtils.isEmpty(etFullName.getText())) {
            tilFullName.setError("Full name is required");
            isValid = false;
        } else if (etFullName.getText().toString().trim().length() < 3) {
            tilFullName.setError("Name must be at least 3 characters long");
            isValid = false;
        }

        // Validate university ID
        if (TextUtils.isEmpty(etUniversityId.getText())) {
            tilUniversityId.setError("University ID is required");
            isValid = false;
        } else {
            String uniId = etUniversityId.getText().toString().trim();
            // Check if University ID already exists
            if (dbHelper.isUniversityIdExists(uniId)) {
                tilUniversityId.setError("This University ID already exists");
                isValid = false;
            } else if (!uniId.matches("\\d{4}[A-Z]{2}\\d{3}")) {
                tilUniversityId.setError("ID must be in format: YYYYDD000 (e.g., 2023CS001)");
                isValid = false;
            }
        }

        // Validate NIC number
        if (TextUtils.isEmpty(etNicNumber.getText())) {
            tilNicNumber.setError("NIC number is required");
            isValid = false;
        } else {
            String nicNumber = etNicNumber.getText().toString().trim();

            if (dbHelper.isNicExists(nicNumber)) {
                tilNicNumber.setError("This NIC number already exists");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(dropdownGender.getText())) {
            tilGender.setError("Please select a gender");
            isValid = false;
        }

        if (TextUtils.isEmpty(etDateOfBirth.getText())) {
            tilDateOfBirth.setError("Date of birth is required");
            isValid = false;
        }

        return isValid;
    }

    private void saveStudentData() {
        loadingOverlay.setVisibility(View.VISIBLE);

        try {

            Student student = new Student();
            student.setFullName(etFullName.getText().toString().trim());
            student.setUniversityId(etUniversityId.getText().toString().trim());
            student.setNicNumber(etNicNumber.getText().toString().trim());
            student.setDateOfBirth(etDateOfBirth.getText().toString().trim());
            student.setGender(dropdownGender.getText().toString().trim());


            if (selectedImageUri != null) {
                student.setPhotoUri(selectedImageUri.toString());
            }


            long result = dbHelper.insertStudent(student);

            if (result != -1) {
                showToast("Student data saved successfully!");


                loadingOverlay.postDelayed(() -> {
                    loadingOverlay.setVisibility(View.GONE);


                    Intent intent = new Intent(getActivity(), AcademicDetails.class);
                    intent.putExtra("student_id", result);
                    startActivity(intent);

                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }, 1000);

            } else {
                loadingOverlay.setVisibility(View.GONE);
                showToast("Failed to save student data. Please try again.");
            }

        } catch (Exception e) {
            loadingOverlay.setVisibility(View.GONE);
            Log.e(TAG, "Error saving student data: " + e.getMessage());
            showToast("An error occurred while saving data");
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        // Set default to 18 years ago
        calendar.add(Calendar.YEAR, -18);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    // Format the date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                    String formattedDate = dateFormat.format(selectedDate.getTime());

                    // Set the formatted date to the input field
                    etDateOfBirth.setText(formattedDate);
                },
                year, month, day
        );

        // Limit the date picker to be no later than today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void showImageSourceDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Profile Photo");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Take Photo option
                showToast("Camera functionality would be implemented here");
            } else if (which == 1) {
                // Choose from Gallery option
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(pickPhoto);
            }
            // If "Cancel" is clicked, do nothing
        });

        builder.show();
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Adding a New Student")
                .setMessage("This form allows you to add a new student to the system.\n\n" +
                        "• Start by adding basic information\n" +
                        "• Make sure University ID follows the format YYYYDD000\n" +
                        "• All fields marked with * are required\n" +
                        "• Add a photo by clicking the camera button\n\n" +
                        "If you need further assistance, contact the admin team.")
                .setPositiveButton("Got it", null)
                .show();
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