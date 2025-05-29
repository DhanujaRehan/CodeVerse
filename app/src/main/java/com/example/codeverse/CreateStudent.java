package com.example.codeverse;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.codeverse.R;
import com.example.codeverse.StudentDatabaseHelper;
import com.example.codeverse.Student;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateStudent extends Fragment {

    private static final String TAG = "CreateStudentFragment";
    private static final int PICK_IMAGE_REQUEST = 1;

    // Views
    private TextInputEditText etFullName, etUniversityId, etNicNumber, etDateOfBirth;
    private TextInputLayout tilFullName, tilUniversityId, tilNicNumber, tilGender, tilDateOfBirth;
    private AutoCompleteTextView dropdownGender;
    private ImageView ivStudentPhoto;
    private FloatingActionButton fabAddPhoto;
    private MaterialButton btnNextStep, btnCancel;
    private MaterialCardView cvBack;

    // Data
    private Student currentStudent;
    private StudentDatabaseHelper dbHelper;
    private String selectedPhotoPath;
    private ExecutorService executorService;

    // Activity result launcher for image picking
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public interface OnStepCompleteListener {
        void onStepCompleted(Student student, int nextStep);
        void onCancel();
        void onBack();
    }

    private OnStepCompleteListener stepCompleteListener;

    public static CreateStudent newInstance() {
        return new CreateStudent();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = StudentDatabaseHelper.getInstance(getContext());
        currentStudent = new Student();
        executorService = Executors.newSingleThreadExecutor();

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            if (imageUri != null) {
                                ivStudentPhoto.setImageURI(imageUri);
                                selectedPhotoPath = imageUri.toString();
                                currentStudent.setPhotoPath(selectedPhotoPath);
                            }
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_student, container, false);
        initViews(view);
        setupListeners();
        setupGenderDropdown();
        return view;
    }

    private void initViews(View view) {
        // Text input fields
        etFullName = view.findViewById(R.id.et_full_name);
        etUniversityId = view.findViewById(R.id.et_university_id);
        etNicNumber = view.findViewById(R.id.et_nic_number);
        etDateOfBirth = view.findViewById(R.id.et_date_of_birth);

        // Text input layouts
        tilFullName = view.findViewById(R.id.til_full_name);
        tilUniversityId = view.findViewById(R.id.til_university_id);
        tilNicNumber = view.findViewById(R.id.til_nic_number);
        tilGender = view.findViewById(R.id.til_gender);
        tilDateOfBirth = view.findViewById(R.id.til_date_of_birth);

        // Dropdown
        dropdownGender = view.findViewById(R.id.dropdown_gender);

        // Image and FAB
        ivStudentPhoto = view.findViewById(R.id.iv_student_photo);
        fabAddPhoto = view.findViewById(R.id.fab_add_photo);

        // Buttons
        btnNextStep = view.findViewById(R.id.btn_next_step);
        btnCancel = view.findViewById(R.id.btn_cancel);
        cvBack = view.findViewById(R.id.cv_back);
    }

    private void setupListeners() {
        fabAddPhoto.setOnClickListener(v -> openImageChooser());

        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        btnNextStep.setOnClickListener(v -> {
            if (validateInput()) {
                saveBasicInfoToDatabase();
            }
        });

        btnCancel.setOnClickListener(v -> {
            if (stepCompleteListener != null) {
                stepCompleteListener.onCancel();
            }
        });

        cvBack.setOnClickListener(v -> {
            if (stepCompleteListener != null) {
                stepCompleteListener.onBack();
            }
        });
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

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Student Photo"));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 18; // Default to 18 years ago
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        etDateOfBirth.setText(selectedDate);
                    }
                },
                year, month, day
        );

        // Set max date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private boolean validateInput() {
        boolean isValid = true;

        // Reset errors
        tilFullName.setError(null);
        tilUniversityId.setError(null);
        tilNicNumber.setError(null);
        tilGender.setError(null);
        tilDateOfBirth.setError(null);

        // Validate full name
        String fullName = etFullName.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Full name is required");
            isValid = false;
        } else if (fullName.length() < 2) {
            tilFullName.setError("Full name must be at least 2 characters");
            isValid = false;
        }

        // Validate university ID
        String universityId = etUniversityId.getText().toString().trim().toUpperCase();
        if (TextUtils.isEmpty(universityId)) {
            tilUniversityId.setError("University ID is required");
            isValid = false;
        } else if (universityId.length() < 5) {
            tilUniversityId.setError("University ID must be at least 5 characters");
            isValid = false;
        } else if (dbHelper.isUniversityIdExists(universityId, currentStudent.getId())) {
            tilUniversityId.setError("University ID already exists");
            isValid = false;
        }

        // Validate NIC number
        String nicNumber = etNicNumber.getText().toString().trim();
        if (TextUtils.isEmpty(nicNumber)) {
            tilNicNumber.setError("NIC number is required");
            isValid = false;
        } else if (!isValidNIC(nicNumber)) {
            tilNicNumber.setError("Invalid NIC number format");
            isValid = false;
        }

        // Validate gender
        String gender = dropdownGender.getText().toString().trim();
        if (TextUtils.isEmpty(gender)) {
            tilGender.setError("Gender is required");
            isValid = false;
        }

        // Validate date of birth
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        if (TextUtils.isEmpty(dateOfBirth)) {
            tilDateOfBirth.setError("Date of birth is required");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidNIC(String nic) {
        // Sri Lankan NIC validation
        // Old format: 9 digits + V (e.g., 123456789V)
        // New format: 12 digits (e.g., 199812345678)
        return nic.matches("^([0-9]{9}[VXvx]|[0-9]{12})$");
    }

    private void saveBasicInfoToDatabase() {
        // Set button to loading state
        btnNextStep.setEnabled(false);
        btnNextStep.setText("Saving...");

        // Collect data from form
        currentStudent.setFullName(etFullName.getText().toString().trim());
        currentStudent.setUniversityId(etUniversityId.getText().toString().trim().toUpperCase());
        currentStudent.setNicNumber(etNicNumber.getText().toString().trim().toUpperCase());
        currentStudent.setGender(dropdownGender.getText().toString().trim());
        currentStudent.setDateOfBirth(etDateOfBirth.getText().toString().trim());
        currentStudent.setPhotoPath(selectedPhotoPath);

        // Save to database in background thread
        executorService.execute(() -> {
            try {
                long studentId = dbHelper.insertBasicStudentInfo(currentStudent);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        btnNextStep.setEnabled(true);
                        btnNextStep.setText("Continue to Academic Details");

                        if (studentId > 0) {
                            currentStudent.setId((int) studentId);
                            Toast.makeText(getContext(), "Basic information saved successfully", Toast.LENGTH_SHORT).show();

                            // Move to next fragment
                            if (stepCompleteListener != null) {
                                stepCompleteListener.onStepCompleted(currentStudent, 2);
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to save basic information. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        btnNextStep.setEnabled(true);
                        btnNextStep.setText("Continue to Academic Details");
                        Toast.makeText(getContext(), "Error saving data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    public void setOnStepCompleteListener(OnStepCompleteListener listener) {
        this.stepCompleteListener = listener;
    }

    public void setStudentData(Student student) {
        if (student != null) {
            this.currentStudent = student;
            populateFields();
        }
    }

    private void populateFields() {
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
                dropdownGender.setText(currentStudent.getGender(), false);
            }
            if (currentStudent.getDateOfBirth() != null) {
                etDateOfBirth.setText(currentStudent.getDateOfBirth());
            }
            if (currentStudent.getPhotoPath() != null) {
                selectedPhotoPath = currentStudent.getPhotoPath();
                try {
                    Uri imageUri = Uri.parse(selectedPhotoPath);
                    ivStudentPhoto.setImageURI(imageUri);
                } catch (Exception e) {
                    // Handle invalid URI
                }
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