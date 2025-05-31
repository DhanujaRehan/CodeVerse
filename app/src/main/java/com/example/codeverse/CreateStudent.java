package com.example.codeverse;

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

import com.google.android.material.button.MaterialButton;
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

public class CreateStudent extends Fragment {

    private static final String TAG = "CreateStudent";
    private static final String ARG_STUDENT_ID = "student_id";

    private StudentDatabaseHelper dbHelper;
    private Student currentStudent;
    private long studentId = -1;

    // Views from XML layout
    private ImageView ivStudentPhoto;
    private FloatingActionButton fabAddPhoto;
    private TextInputEditText etFullName;
    private TextInputEditText etUniversityId;
    private TextInputEditText etNicNumber;
    private AutoCompleteTextView dropdownGender;
    private TextInputEditText etDateOfBirth;
    private TextInputLayout tilFullName;
    private TextInputLayout tilUniversityId;
    private TextInputLayout tilNicNumber;
    private TextInputLayout tilGender;
    private TextInputLayout tilDateOfBirth;
    private MaterialButton btnNextStep;
    private MaterialButton btnCancel;
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
                        ivStudentPhoto.setImageURI(selectedImageUri);
                        // Save the image and get the file path
                        String savedImagePath = saveImageToInternalStorage(selectedImageUri);
                        if (savedImagePath != null) {
                            this.selectedImageUri = savedImagePath;
                        }
                    }
                }
            });

    public static CreateStudent newInstance(long studentId) {
        CreateStudent fragment = new CreateStudent();
        Bundle args = new Bundle();
        args.putLong(ARG_STUDENT_ID, studentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize database helper
        dbHelper = new StudentDatabaseHelper(getContext());

        // Get student ID from arguments if provided
        if (getArguments() != null) {
            studentId = getArguments().getLong(ARG_STUDENT_ID, -1);
        }

        // Check if we're editing an existing student or creating new
        Bundle args = getArguments();
        if (args != null && args.containsKey("student")) {
            currentStudent = (Student) args.getSerializable("student");
            Log.d(TAG, "Editing existing student: " + currentStudent.getUniversityId());
        } else if (studentId != -1) {
            // Load existing student by ID
            currentStudent = dbHelper.getStudentById(studentId);
            if (currentStudent != null) {
                Log.d(TAG, "Loaded existing student: " + currentStudent.getUniversityId());
            }
        } else {
            currentStudent = new Student();
            Log.d(TAG, "Creating new student");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_student, container, false);

        // Initialize views
        initializeViews(view);

        // Set up click listeners
        setupClickListeners();

        // Setup text change listeners
        setupTextChangeListeners();

        // Setup gender dropdown
        setupGenderDropdown();

        // Populate fields if editing existing student
        populateFieldsFromStudent();

        return view;
    }

    private void initializeViews(View view) {
        try {
            // Initialize views with exact IDs from XML
            ivStudentPhoto = view.findViewById(R.id.iv_student_photo);
            fabAddPhoto = view.findViewById(R.id.fab_add_photo);
            etFullName = view.findViewById(R.id.et_full_name);
            etUniversityId = view.findViewById(R.id.et_university_id);
            etNicNumber = view.findViewById(R.id.et_nic_number);
            dropdownGender = view.findViewById(R.id.dropdown_gender);
            etDateOfBirth = view.findViewById(R.id.et_date_of_birth);

            // Initialize TextInputLayouts for error handling
            tilFullName = view.findViewById(R.id.til_full_name);
            tilUniversityId = view.findViewById(R.id.til_university_id);
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
        // Photo selection
        fabAddPhoto.setOnClickListener(v -> openImagePicker());

        // Date picker for date of birth
        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Next step button
        btnNextStep.setOnClickListener(v -> {
            if (validateBasicInformation()) {
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
        etUniversityId.addTextChangedListener(textWatcher);
        etNicNumber.addTextChangedListener(textWatcher);
        dropdownGender.addTextChangedListener(textWatcher);
        etDateOfBirth.addTextChangedListener(textWatcher);
    }

    private void clearErrors() {
        if (tilFullName != null) tilFullName.setError(null);
        if (tilUniversityId != null) tilUniversityId.setError(null);
        if (tilNicNumber != null) tilNicNumber.setError(null);
        if (tilGender != null) tilGender.setError(null);
        if (tilDateOfBirth != null) tilDateOfBirth.setError(null);
    }

    private void setupGenderDropdown() {
        String[] genderOptions = {"Male", "Female", "Other", "Prefer not to say"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                genderOptions
        );
        dropdownGender.setAdapter(adapter);
    }

    private void openImagePicker() {
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
                calendar.get(Calendar.YEAR),
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

        // Save basic information to current student object
        saveBasicInformation();

        // Save to database with partial data using Handler for smooth UI
        new Handler().postDelayed(() -> {
            try {
                long resultId;
                if (currentStudent.getId() > 0) {
                    // Update existing student
                    int rowsAffected = dbHelper.updateStudent(currentStudent);
                    resultId = currentStudent.getId();
                    Log.d(TAG, "Student updated in database. Rows affected: " + rowsAffected);
                } else {
                    // Insert new student
                    resultId = dbHelper.insertStudent(currentStudent);
                    currentStudent.setId(resultId);
                    Log.d(TAG, "Student saved to database with ID: " + resultId);
                }

                // Hide loading
                showLoading(false);

                if (resultId > 0) {
                    showToast("Basic information saved successfully!");

                    // Clear form after successful save
                    clearForm();

                    // Navigate to next step (Academic Details)
                    navigateToAcademicDetails();
                } else {
                    Log.e(TAG, "Failed to save student to database");
                    showToast("Failed to save student information");
                }
            } catch (Exception e) {
                // Hide loading
                showLoading(false);
                Log.e(TAG, "Error saving student: " + e.getMessage(), e);
                showToast("Error saving student: " + e.getMessage());
            }
        }, 1500); // 1.5 second delay for smooth loading experience
    }

    private void saveForLater() {
        // This method allows saving incomplete data as draft
        if (etFullName.getText().toString().trim().isEmpty()) {
            showToast("At least full name is required to save");
            return;
        }

        // Show loading
        showLoading(true);

        // Save basic information to current student object
        saveBasicInformation();

        // Save to database using Handler for smooth UI
        new Handler().postDelayed(() -> {
            try {
                long resultId;
                if (currentStudent.getId() > 0) {
                    // Update existing student
                    int rowsAffected = dbHelper.updateStudent(currentStudent);
                    resultId = currentStudent.getId();
                    Log.d(TAG, "Student updated in database. Rows affected: " + rowsAffected);
                } else {
                    // Insert new student
                    resultId = dbHelper.insertStudent(currentStudent);
                    currentStudent.setId(resultId);
                    Log.d(TAG, "Student saved as draft with ID: " + resultId);
                }

                // Hide loading
                showLoading(false);

                if (resultId > 0) {
                    showToast("Information saved as draft!");
                } else {
                    showToast("Failed to save draft");
                }
            } catch (Exception e) {
                // Hide loading
                showLoading(false);
                Log.e(TAG, "Error saving draft: " + e.getMessage(), e);
                showToast("Error saving draft: " + e.getMessage());
            }
        }, 1500); // 1.5 second delay for smooth loading experience
    }

    private boolean validateBasicInformation() {
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

        // Validate university ID
        if (etUniversityId.getText().toString().trim().isEmpty()) {
            if (tilUniversityId != null) {
                tilUniversityId.setError("University ID is required");
            } else {
                etUniversityId.setError("University ID is required");
            }
            etUniversityId.requestFocus();
            isValid = false;
        }

        if (etNicNumber.getText().toString().trim().isEmpty()) {
            if (tilNicNumber != null) {
                tilNicNumber.setError("NIC number is required");
            } else {
                etNicNumber.setError("NIC number is required");
            }
            etNicNumber.requestFocus();
            isValid = false;
        }

        if (dropdownGender.getText().toString().trim().isEmpty()) {
            if (tilGender != null) {
                tilGender.setError("Please select gender");
            } else {
                dropdownGender.setError("Please select gender");
            }
            dropdownGender.requestFocus();
            isValid = false;
        }

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
        String universityId = etUniversityId.getText().toString().trim();
        String nicNumber = etNicNumber.getText().toString().trim();
        try {
            if (dbHelper.isUniversityIdExists(universityId)) {
                if (currentStudent.getId() == 0 || !universityId.equals(currentStudent.getUniversityId())) {
                    if (tilUniversityId != null) {
                        tilUniversityId.setError("University ID already exists");
                    } else {
                        etUniversityId.setError("University ID already exists");
                    }
                    etUniversityId.requestFocus();
                    return true;
                }
            }

            if (dbHelper.isNicExists(nicNumber)) {
                if (currentStudent.getId() == 0 || !nicNumber.equals(currentStudent.getNicNumber())) {
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

    private void saveBasicInformation() {
        // Save basic information to student object
        currentStudent.setFullName(etFullName.getText().toString().trim());
        currentStudent.setUniversityId(etUniversityId.getText().toString().trim());
        currentStudent.setNicNumber(etNicNumber.getText().toString().trim());
        currentStudent.setGender(dropdownGender.getText().toString().trim());
        currentStudent.setDateOfBirth(etDateOfBirth.getText().toString().trim());
        currentStudent.setPhotoUri(selectedImageUri);

        Log.d(TAG, "Basic information saved to student object: " + currentStudent.toString());
    }

    private void navigateToAcademicDetails() {
        try {
            // Create bundle to pass student data to the next fragment
            Bundle args = new Bundle();
            args.putSerializable("student", currentStudent);

            // Create instance of AcademicDetailsFragment
            AcademicDetails academicFragment = new AcademicDetails();
            academicFragment.setArguments(args);

            // Navigate to Academic Details Fragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, academicFragment)
                    .addToBackStack("CreateStudent") // Add to back stack so user can return
                    .commit();

            Log.d(TAG, "Successfully navigated to Academic Details with student ID: " + currentStudent.getId());

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to Academic Details: " + e.getMessage(), e);
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
        etUniversityId.setText("");
        etNicNumber.setText("");
        dropdownGender.setText("");
        etDateOfBirth.setText("");

        ivStudentPhoto.setImageResource(R.drawable.addpropic);
        selectedImageUri = null;

        currentStudent = new Student();
        clearErrors();
    }

    private void showLoading(boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            File photosDir = new File(getContext().getFilesDir(), "student_photos");
            if (!photosDir.exists()) {
                boolean created = photosDir.mkdirs();
                Log.d(TAG, "Photos directory created: " + created);
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String filename = "student_photo_" + timestamp + ".jpg";
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

    // Getter method for other fragments to access the current student data
    public Student getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        populateFieldsFromStudent();
    }

    private void populateFieldsFromStudent() {
        if (currentStudent != null) {
            if (currentStudent.getFullName() != null) {
                etFullName.setText(currentStudent.getFullName());
            }
            if (currentStudent.getUniversityId() != null) {
                etUniversityId.setText(currentStudent.getUniversityId());
            }
            if (currentStudent.getNicNumber() != null) {
                etNicNumber.setText(currentStudent.getNicNumber());
            }
            if (currentStudent.getGender() != null) {
                dropdownGender.setText(currentStudent.getGender());
            }
            if (currentStudent.getDateOfBirth() != null) {
                etDateOfBirth.setText(currentStudent.getDateOfBirth());
            }
            if (currentStudent.getPhotoUri() != null) {
                selectedImageUri = currentStudent.getPhotoUri();
                ivStudentPhoto.setImageURI(Uri.fromFile(new File(selectedImageUri)));
            }
            Log.d(TAG, "Fields populated from student: " + currentStudent.toString());
        }
    }

    private void clearForm() {
        etFullName.setText("");
        etUniversityId.setText("");
        etNicNumber.setText("");
        dropdownGender.setText("");
        etDateOfBirth.setText("");
        ivStudentPhoto.setImageResource(R.drawable.addpropic);
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