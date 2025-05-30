package com.example.codeverse.Students.StudentFragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.fragment.app.Fragment;

import com.example.codeverse.R;
import com.example.codeverse.Students.Models.Student;
import com.example.codeverse.Students.Helpers.StudentDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

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
    private StudentDatabaseHelper dbHelper;
    private Student currentStudent;

    // Views from XML layout
    private ImageView ivStudentPhoto;
    private FloatingActionButton fabAddPhoto;
    private TextInputEditText etFullName;
    private TextInputEditText etUniversityId;
    private TextInputEditText etNicNumber;
    private AutoCompleteTextView dropdownGender;
    private TextInputEditText etDateOfBirth;
    private MaterialButton btnNextStep;
    private MaterialButton btnCancel;
    private MaterialButton btnSaveForLater;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize database helper
        dbHelper = new StudentDatabaseHelper(getContext());

        // Check if we're editing an existing student or creating new
        Bundle args = getArguments();
        if (args != null && args.containsKey("student")) {
            currentStudent = (Student) args.getSerializable("student");
            Log.d(TAG, "Editing existing student: " + currentStudent.getUniversityId());
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

        // Setup gender dropdown
        setupGenderDropdown();

        // Populate fields if editing existing student
        populateFieldsFromStudent();

        return view;
    }

    private void initializeViews(View view) {
        // Initialize views with exact IDs from XML
        ivStudentPhoto = view.findViewById(R.id.iv_student_photo);
        fabAddPhoto = view.findViewById(R.id.fab_add_photo);
        etFullName = view.findViewById(R.id.et_full_name);
        etUniversityId = view.findViewById(R.id.et_university_id);
        etNicNumber = view.findViewById(R.id.et_nic_number);
        dropdownGender = view.findViewById(R.id.dropdown_gender);
        etDateOfBirth = view.findViewById(R.id.et_date_of_birth);
        btnNextStep = view.findViewById(R.id.btn_next_step);
        btnCancel = view.findViewById(R.id.btn_cancel);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
    }

    private void setupClickListeners() {
        // Photo selection
        fabAddPhoto.setOnClickListener(v -> openImagePicker());

        // Date picker for date of birth
        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Next step button
        btnNextStep.setOnClickListener(v -> proceedToNextStep());

        // Cancel button
        btnCancel.setOnClickListener(v -> cancelRegistration());

        // Save for later button
        if (btnSaveForLater != null) {
            btnSaveForLater.setOnClickListener(v -> saveForLater());
        }
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
        // Validate basic information
        if (!validateBasicInformation()) {
            return;
        }

        // Check for duplicate values
        if (checkForDuplicates()) {
            return;
        }

        // Show loading
        showLoading(true);

        // Save basic information to current student object
        saveBasicInformation();

        // Save to database with partial data
        try {
            long studentId;
            if (currentStudent.getId() > 0) {
                // Update existing student
                int rowsAffected = dbHelper.updateStudent(currentStudent);
                studentId = currentStudent.getId();
                Log.d(TAG, "Student updated in database. Rows affected: " + rowsAffected);
            } else {
                // Insert new student
                studentId = dbHelper.insertStudent(currentStudent);
                currentStudent.setId(studentId);
                Log.d(TAG, "Student saved to database with ID: " + studentId);
            }

            if (studentId > 0) {
                Toast.makeText(getContext(), "Basic information saved successfully!", Toast.LENGTH_SHORT).show();

                // Clear form after successful save
                clearform();

                // Navigate to next step (Academic Details)
                navigateToAcademicDetails();
            } else {
                Log.e(TAG, "Failed to save student to database");
                Toast.makeText(getContext(), "Failed to save student information", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving student: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error saving student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            // Hide loading
            showLoading(false);
        }
    }

    private void saveForLater() {
        // This method allows saving incomplete data as draft
        if (etFullName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "At least full name is required to save", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        saveBasicInformation();

        try {
            long studentId;
            if (currentStudent.getId() > 0) {
                // Update existing student
                int rowsAffected = dbHelper.updateStudent(currentStudent);
                studentId = currentStudent.getId();
                Log.d(TAG, "Student updated in database. Rows affected: " + rowsAffected);
            } else {
                // Insert new student
                studentId = dbHelper.insertStudent(currentStudent);
                currentStudent.setId(studentId);
                Log.d(TAG, "Student saved as draft with ID: " + studentId);
            }

            if (studentId > 0) {
                Toast.makeText(getContext(), "Information saved as draft!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to save draft", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving draft: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error saving draft: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            showLoading(false);
        }
    }

    private boolean validateBasicInformation() {
        boolean isValid = true;

        // Validate full name
        if (etFullName.getText().toString().trim().isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            isValid = false;
        }

        // Validate university ID
        if (etUniversityId.getText().toString().trim().isEmpty()) {
            etUniversityId.setError("University ID is required");
            etUniversityId.requestFocus();
            isValid = false;
        }

        // Validate NIC number
        if (etNicNumber.getText().toString().trim().isEmpty()) {
            etNicNumber.setError("NIC number is required");
            etNicNumber.requestFocus();
            isValid = false;
        }

        // Validate gender
        if (dropdownGender.getText().toString().trim().isEmpty()) {
            dropdownGender.setError("Please select gender");
            dropdownGender.requestFocus();
            isValid = false;
        }

        // Validate date of birth
        if (etDateOfBirth.getText().toString().trim().isEmpty()) {
            etDateOfBirth.setError("Date of birth is required");
            etDateOfBirth.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private boolean checkForDuplicates() {
        String universityId = etUniversityId.getText().toString().trim();
        String nicNumber = etNicNumber.getText().toString().trim();

        // Skip duplicate check if we're editing the same student
        try {
            if (dbHelper.isUniversityIdExists(universityId)) {
                // Check if it's the same student we're editing
                if (currentStudent.getId() == 0 || !universityId.equals(currentStudent.getUniversityId())) {
                    etUniversityId.setError("University ID already exists");
                    etUniversityId.requestFocus();
                    return true;
                }
            }

            if (dbHelper.isNicExists(nicNumber)) {
                // Check if it's the same student we're editing
                if (currentStudent.getId() == 0 || !nicNumber.equals(currentStudent.getNicNumber())) {
                    etNicNumber.setError("NIC number already exists");
                    etNicNumber.requestFocus();
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking duplicates: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error validating data", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Error proceeding to next step", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelRegistration() {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Cancel Registration")
                .setMessage("Are you sure you want to cancel? All entered data will be lost.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    clearAllData();
                    getParentFragmentManager().popBackStack();
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
            Toast.makeText(getContext(), "Error saving image", Toast.LENGTH_SHORT).show();
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

    private void clearform() {
        etFullName.setText("");
        etUniversityId.setText("");
        etNicNumber.setText("");
        dropdownGender.setText("");
        etDateOfBirth.setText("");
        ivStudentPhoto.setImageResource(R.drawable.addpropic);
        selectedImageUri = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}