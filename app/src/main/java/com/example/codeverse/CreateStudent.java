package com.example.codeverse;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateStudent extends Fragment {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    // UI Components
    private MaterialCardView cvBack, cvHelp;
    private ImageView ivStudentPhoto;
    private FloatingActionButton fabAddPhoto;
    private TextInputEditText etFullName, etUniversityId, etNicNumber, etDateOfBirth;
    private TextInputLayout tilFullName, tilUniversityId, tilNicNumber, tilGender, tilDateOfBirth;
    private AutoCompleteTextView dropdownGender;
    private MaterialButton btnNextStep, btnCancel;
    private View loadingOverlay;

    // Data
    private String selectedPhotoPath = "";
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private StudentDatabaseHelper databaseHelper;

    // Image picker launcher
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // Interface for fragment communication
    public interface OnFragmentInteractionListener {
        void onCreateStudentNext(Student student);
        void onCreateStudentCancel();
    }

    private OnFragmentInteractionListener mListener;

    public CreateStudent() {
        // Required empty public constructor
    }

    public static CreateStudent newInstance() {
        return new CreateStudent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupImagePicker();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_student, container, false);

        initializeViews(view);
        initializeDatabase();
        setupGenderDropdown();
        setupDatePicker();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        cvBack = view.findViewById(R.id.cv_back);
        cvHelp = view.findViewById(R.id.cv_help);
        ivStudentPhoto = view.findViewById(R.id.iv_student_photo);
        fabAddPhoto = view.findViewById(R.id.fab_add_photo);
        etFullName = view.findViewById(R.id.et_full_name);
        etUniversityId = view.findViewById(R.id.et_university_id);
        etNicNumber = view.findViewById(R.id.et_nic_number);
        etDateOfBirth = view.findViewById(R.id.et_date_of_birth);
        tilFullName = view.findViewById(R.id.til_full_name);
        tilUniversityId = view.findViewById(R.id.til_university_id);
        tilNicNumber = view.findViewById(R.id.til_nic_number);
        tilGender = view.findViewById(R.id.til_gender);
        tilDateOfBirth = view.findViewById(R.id.til_date_of_birth);
        dropdownGender = view.findViewById(R.id.dropdown_gender);
        btnNextStep = view.findViewById(R.id.btn_next_step);
        btnCancel = view.findViewById(R.id.btn_cancel);
        loadingOverlay = view.findViewById(R.id.loading_overlay);

        calendar = Calendar.getInstance();
    }

    private void initializeDatabase() {
        databaseHelper = new StudentDatabaseHelper(getContext());
    }

    private void setupGenderDropdown() {
        String[] genderOptions = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, genderOptions);
        dropdownGender.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etDateOfBirth.setOnClickListener(v -> showDatePicker());
        tilDateOfBirth.setEndIconOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    updateDateDisplay();
                }, year, month, day);

        // Set maximum date to today (students can't be born in the future)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Set minimum date (e.g., 100 years ago)
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -100);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etDateOfBirth.setText(dateFormat.format(calendar.getTime()));
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            ivStudentPhoto.setImageURI(imageUri);
                            selectedPhotoPath = imageUri.toString();
                        }
                    }
                }
        );
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCreateStudentCancel();
            }
        });

        cvHelp.setOnClickListener(v -> showHelpDialog());

        fabAddPhoto.setOnClickListener(v -> openImageChooser());

        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                proceedToAcademicDetails();
            }
        });

        btnCancel.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCreateStudentCancel();
            }
        });
    }

    private void showHelpDialog() {
        Toast.makeText(getContext(), "Help: Fill in all required basic information fields", Toast.LENGTH_LONG).show();
    }

    private void openImageChooser() {
        if (checkPermissions()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(getContext(), "Storage permission required to select photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInputs() {
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
        String universityId = etUniversityId.getText().toString().trim();
        if (TextUtils.isEmpty(universityId)) {
            tilUniversityId.setError("University ID is required");
            isValid = false;
        } else if (universityId.length() < 5) {
            tilUniversityId.setError("Invalid University ID format");
            isValid = false;
        } else if (databaseHelper.isUniversityIdExists(universityId)) {
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
        } else if (databaseHelper.isNicExists(nicNumber)) {
            tilNicNumber.setError("NIC number already exists");
            isValid = false;
        }

        // Validate gender
        String gender = dropdownGender.getText().toString().trim();
        if (TextUtils.isEmpty(gender)) {
            tilGender.setError("Please select gender");
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
        // Basic NIC validation for Sri Lankan format
        // Old format: 9 digits + V/X (e.g., 123456789V)
        // New format: 12 digits (e.g., 199812345678)
        if (nic.length() == 10) {
            String digits = nic.substring(0, 9);
            String lastChar = nic.substring(9).toUpperCase();
            return digits.matches("\\d{9}") && (lastChar.equals("V") || lastChar.equals("X"));
        } else if (nic.length() == 12) {
            return nic.matches("\\d{12}");
        }
        return false;
    }

    private void proceedToAcademicDetails() {
        showLoading(true);

        // Create Student object with basic info
        Student student = new Student();
        student.setFullName(etFullName.getText().toString().trim());
        student.setUniversityId(etUniversityId.getText().toString().trim());
        student.setNicNumber(etNicNumber.getText().toString().trim());
        student.setGender(dropdownGender.getText().toString().trim());
        student.setDateOfBirth(etDateOfBirth.getText().toString().trim());
        student.setPhotoPath(selectedPhotoPath);

        // Call listener to proceed to next fragment
        if (mListener != null) {
            mListener.onCreateStudentNext(student);
        }

        showLoading(false);
    }

    private void showLoading(boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (btnNextStep != null) {
            btnNextStep.setEnabled(!show);
        }
    }

    public void setOnFragmentInteractionListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}