package com.example.codeverse;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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

    // Interface for communication with parent activity
    public interface OnBasicInfoListener {
        void onBasicInfoCompleted(StudentDetails basicInfo);
        void onBasicInfoCancelled();
        void onNavigateToStep(int step);
    }

    // UI Components
    private ImageView ivStudentPhoto;
    private FloatingActionButton fabAddPhoto;
    private TextInputEditText etFullName, etUniversityId, etNicNumber, etDateOfBirth;
    private AutoCompleteTextView dropdownGender;
    private MaterialButton btnNextStep, btnCancel;
    private MaterialCardView cvBack, cvHelp;
    private FrameLayout loadingOverlay;

    // TextInputLayouts for validation
    private TextInputLayout tilFullName, tilUniversityId, tilNicNumber, tilDateOfBirth, tilGender;

    // Step indicators
    private LinearLayout cardBasicInfoIndicator, cardAccountIndicator,
            cardAcademicIndicator, cardContactIndicator;

    // Image picker launcher
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri = null;

    // Data
    private StudentDetails studentDetails;
    private OnBasicInfoListener listener;
    private StudentDatabaseHelper databaseHelper;

    // Fragment view reference
    private View fragmentView;

    // Handler for delayed operations
    private Handler mainHandler;

    // Constants
    private static final String TAG = "CreateStudent";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBasicInfoListener) {
            listener = (OnBasicInfoListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBasicInfoListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize handler
        mainHandler = new Handler(Looper.getMainLooper());

        // Initialize database helper
        databaseHelper = StudentDatabaseHelper.getInstance(requireContext());

        // Initialize student details
        if (getArguments() != null) {
            studentDetails = getArguments().getParcelable("student_details");
        }

        if (studentDetails == null) {
            studentDetails = new StudentDetails();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_create_student, container, false);

        // Initialize UI components
        initializeViews(fragmentView);

        // Set up dropdown for gender selection
        setupGenderDropdown();

        // Set up image picker
        setupImagePicker();

        // Set up click listeners
        setupClickListeners();

        // Set up text change listeners for validation
        setupTextChangeListeners();

        // Populate fields with existing data
        populateFields();

        return fragmentView;
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
                    requireContext(),
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
                    if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            selectedImageUri = imageUri;
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                                ivStudentPhoto.setImageBitmap(bitmap);
                                showToast("Photo selected successfully");
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

        // Step indicators click listeners
        cardAccountIndicator.setOnClickListener(v -> {
            if (validateInputs()) {
                saveCurrentData();
                if (listener != null) {
                    listener.onNavigateToStep(2);
                }
            }
        });

        cardAcademicIndicator.setOnClickListener(v -> {
            if (validateInputs()) {
                saveCurrentData();
                if (listener != null) {
                    listener.onNavigateToStep(3);
                }
            }
        });

        cardContactIndicator.setOnClickListener(v -> {
            if (validateInputs()) {
                saveCurrentData();
                if (listener != null) {
                    listener.onNavigateToStep(4);
                }
            }
        });

        // Next step button click listener
        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                showToast("Validation successful! Proceeding to Academic Details...");
                saveCurrentData();

                // Show loading state
                showLoading(true);

                // Use handler for delayed operation instead of view.postDelayed
                mainHandler.postDelayed(() -> {
                    if (isAdded() && getActivity() != null) {
                        showLoading(false);
                        if (listener != null) {
                            listener.onBasicInfoCompleted(studentDetails);
                        }
                    }
                }, 1500);
            }
        });

        // Cancel button click listener
        btnCancel.setOnClickListener(v -> {
            // Show confirmation dialog before canceling
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cancel Student Creation")
                    .setMessage("Are you sure you want to cancel? All entered information will be lost.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (listener != null) {
                            listener.onBasicInfoCancelled();
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
        if (tilFullName != null) tilFullName.setError(null);
        if (tilUniversityId != null) tilUniversityId.setError(null);
        if (tilNicNumber != null) tilNicNumber.setError(null);
        if (tilDateOfBirth != null) tilDateOfBirth.setError(null);
        if (tilGender != null) tilGender.setError(null);
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
            // Example validation: Must follow pattern like 2023CS001
            if (!uniId.matches("\\d{4}[A-Z]{2}\\d{3}")) {
                tilUniversityId.setError("ID must be in format: YYYYDD000 (e.g., 2023CS001)");
                isValid = false;
            } else {
                // Check if University ID already exists (excluding current student)
                long excludeId = studentDetails != null ? studentDetails.getId() : -1;
                if (databaseHelper.isUniversityIdExists(uniId, excludeId)) {
                    tilUniversityId.setError("University ID already exists");
                    isValid = false;
                }
            }
        }

        // Validate NIC number
        if (TextUtils.isEmpty(etNicNumber.getText())) {
            tilNicNumber.setError("NIC number is required");
            isValid = false;
        }

        // Validate Gender selection
        if (TextUtils.isEmpty(dropdownGender.getText())) {
            tilGender.setError("Please select a gender");
            isValid = false;
        }

        // Validate Date of Birth
        if (TextUtils.isEmpty(etDateOfBirth.getText())) {
            tilDateOfBirth.setError("Date of birth is required");
            isValid = false;
        }

        return isValid;
    }

    private void saveCurrentData() {
        try {
            // Update student details with current form data
            studentDetails.setFullName(etFullName.getText().toString().trim());
            studentDetails.setUniversityId(etUniversityId.getText().toString().trim());
            studentDetails.setNicNumber(etNicNumber.getText().toString().trim());
            studentDetails.setGender(dropdownGender.getText().toString());
            studentDetails.setDateOfBirth(etDateOfBirth.getText().toString());

            if (selectedImageUri != null) {
                studentDetails.setStudentPhoto(selectedImageUri.toString());
            }

            // Save to database
            long result = databaseHelper.insertOrUpdateStudent(studentDetails);
            if (result > 0) {
                studentDetails.setId(result);
                Log.d(TAG, "Student data saved successfully with ID: " + result);
            } else {
                Log.w(TAG, "Failed to save student data");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving student data", e);
            showToast("Error saving data: " + e.getMessage());
        }
    }

    private void populateFields() {
        if (studentDetails != null) {
            if (studentDetails.getFullName() != null) {
                etFullName.setText(studentDetails.getFullName());
            }
            if (studentDetails.getUniversityId() != null) {
                etUniversityId.setText(studentDetails.getUniversityId());
            }
            if (studentDetails.getNicNumber() != null) {
                etNicNumber.setText(studentDetails.getNicNumber());
            }
            if (studentDetails.getGender() != null) {
                dropdownGender.setText(studentDetails.getGender(), false);
            }
            if (studentDetails.getDateOfBirth() != null) {
                etDateOfBirth.setText(studentDetails.getDateOfBirth());
            }
            if (studentDetails.getStudentPhoto() != null) {
                selectedImageUri = Uri.parse(studentDetails.getStudentPhoto());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                    ivStudentPhoto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Log.e(TAG, "Error loading saved image", e);
                }
            }
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
                requireContext(),
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

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Profile Photo");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Take Photo option
                showToast("Camera functionality would be implemented here");
                // TODO: Implement camera functionality
                /*
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                    imagePickerLauncher.launch(takePictureIntent);
                }
                */
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
        new AlertDialog.Builder(requireContext())
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

    private void showLoading(boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up handler callbacks to prevent memory leaks
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        fragmentView = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}