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

    private static final String TAG = "StaffPersonalInfo";
    private static final String ARG_STAFF_ID = "staff_id";

    private StaffDatabaseHelper dbHelper;
    private Staff currentStaff;
    private long staffId = -1;

    private ImageView ivStaffPhoto;
    private FloatingActionButton fabAddPhoto;
    private TextInputEditText etFullName, etEmail, etContactNumber, etNicNumber, etDateOfBirth;
    private AutoCompleteTextView dropdownGender;
    private TextInputLayout tilFullName, tilEmail, tilContactNumber, tilNicNumber, tilGender, tilDateOfBirth;
    private MaterialButton btnNextStep, btnCancel;
    private FrameLayout loadingOverlay;

    private String selectedImageUri = null;
    private Calendar calendar = Calendar.getInstance();

    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        ivStaffPhoto.setImageURI(selectedImageUri);
                        ivStaffPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        String savedImagePath = saveImageToInternalStorage(selectedImageUri);
                        if (savedImagePath != null) {
                            this.selectedImageUri = savedImagePath;
                            Log.d(TAG, "Image saved successfully: " + savedImagePath);
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


        dbHelper = new StaffDatabaseHelper(getContext());


        if (getArguments() != null) {
            staffId = getArguments().getLong(ARG_STAFF_ID, -1);
        }


        Bundle args = getArguments();
        if (args != null && args.containsKey("staff")) {
            currentStaff = (Staff) args.getSerializable("staff");
            Log.d(TAG, "Editing existing staff: " + (currentStaff != null ? currentStaff.getFullName() : "null"));
        } else if (staffId != -1) {

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

        initializeViews(view);
        setupClickListeners();
        setupTextChangeListeners();
        setupGenderDropdown();
        populateFieldsFromStaff();
        return view;
    }

    private void setupClickListeners() {

        if (fabAddPhoto != null) {
            fabAddPhoto.setOnClickListener(v -> showImagePickerDialog());
        }

        if (etDateOfBirth != null) {
            etDateOfBirth.setOnClickListener(v -> showDatePicker());
            etDateOfBirth.setFocusable(false);
        }

        if (btnNextStep != null) {
            btnNextStep.setOnClickListener(v -> {
                if (validatePersonalInformation()) {
                    proceedToNextStep();
                }
            });
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> cancelRegistration());
        }
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

        if (etFullName != null) etFullName.addTextChangedListener(textWatcher);
        if (etEmail != null) etEmail.addTextChangedListener(textWatcher);
        if (etContactNumber != null) etContactNumber.addTextChangedListener(textWatcher);
        if (etNicNumber != null) etNicNumber.addTextChangedListener(textWatcher);
        if (dropdownGender != null) dropdownGender.addTextChangedListener(textWatcher);
        if (etDateOfBirth != null) etDateOfBirth.addTextChangedListener(textWatcher);
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
        if (dropdownGender != null) {
            String[] genderOptions = {"Male", "Female", "Other"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    genderOptions
            );
            dropdownGender.setAdapter(adapter);
        }
    }

    private void initializeViews(View view) {
        try {

            ivStaffPhoto = view.findViewById(R.id.iv_staff_photo);
            fabAddPhoto = view.findViewById(R.id.fab_add_photo);
            etFullName = view.findViewById(R.id.et_full_name);
            etEmail = view.findViewById(R.id.et_email);
            etContactNumber = view.findViewById(R.id.et_contact_number);
            etNicNumber = view.findViewById(R.id.et_nic_number);
            etDateOfBirth = view.findViewById(R.id.et_date_of_birth);
            dropdownGender = view.findViewById(R.id.dropdown_gender);


            tilFullName = view.findViewById(R.id.til_full_name);
            tilEmail = view.findViewById(R.id.til_email);
            tilContactNumber = view.findViewById(R.id.til_contact_number);
            tilNicNumber = view.findViewById(R.id.til_nic_number);
            tilGender = view.findViewById(R.id.til_gender);
            tilDateOfBirth = view.findViewById(R.id.til_date_of_birth);

            btnNextStep = view.findViewById(R.id.btn_next_step);
            btnCancel = view.findViewById(R.id.btn_cancel);
            loadingOverlay = view.findViewById(R.id.loading_overlay);

            Log.d(TAG, "All views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            showToast("Failed to initialize the form");
        }
    }


    private void showImagePickerDialog() {
        if (getContext() != null) {
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
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getActivity() != null && cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
        if (getContext() != null) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        if (etDateOfBirth != null) {
                            etDateOfBirth.setText(sdf.format(calendar.getTime()));
                        }
                    },
                    calendar.get(Calendar.YEAR) - 25,
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
    }

    private void proceedToNextStep() {
        Log.d(TAG, "Proceeding to next step...");


        if (checkForDuplicates()) {
            Log.w(TAG, "Duplicate values found, stopping process");
            return;
        }


        showLoading(true);


        savePersonalInformation();


        new Handler().postDelayed(() -> {
            try {
                long resultId;
                if (currentStaff != null && currentStaff.getId() > 0) {

                    Log.d(TAG, "Updating existing staff with ID: " + currentStaff.getId());
                    int rowsAffected = dbHelper.updateStaff(currentStaff);
                    resultId = currentStaff.getId();
                    Log.d(TAG, "Staff updated in database. Rows affected: " + rowsAffected);

                    if (rowsAffected == 0) {
                        Log.e(TAG, "No rows were updated. Staff might not exist.");
                        showLoading(false);
                        showToast("Failed to update staff information");
                        return;
                    }
                } else {

                    Log.d(TAG, "Inserting new staff: " + currentStaff.getFullName());
                    resultId = dbHelper.insertStaff(currentStaff);
                    if (resultId > 0) {
                        currentStaff.setId(resultId);
                        Log.d(TAG, "New staff saved to database with ID: " + resultId);
                    }
                }


                showLoading(false);

                if (resultId > 0) {
                    showToast("Personal information saved successfully!");
                    Log.d(TAG, "Staff operation successful, navigating to next step");


                    navigateToProfessionalDetails();
                } else {
                    Log.e(TAG, "Failed to save staff to database. Result ID: " + resultId);
                    showToast("Failed to save staff information. Please check your data and try again.");
                }
            } catch (Exception e) {

                showLoading(false);
                Log.e(TAG, "Error saving staff: " + e.getMessage(), e);
                showToast("Error saving staff: " + e.getMessage());
            }
        }, 1000);

    }

    private boolean validatePersonalInformation() {
        boolean isValid = true;
        Log.d(TAG, "Validating personal information...");


        if (etFullName == null || etFullName.getText().toString().trim().isEmpty()) {
            setFieldError(tilFullName, etFullName, "Full name is required");
            if (etFullName != null) etFullName.requestFocus();
            isValid = false;
        }


        if (etEmail != null) {
            String email = etEmail.getText().toString().trim();
            if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                setFieldError(tilEmail, etEmail, "Please enter a valid email address");
                etEmail.requestFocus();
                isValid = false;
            }
        }


        if (etContactNumber == null || etContactNumber.getText().toString().trim().isEmpty()) {
            setFieldError(tilContactNumber, etContactNumber, "Contact number is required");
            if (etContactNumber != null) etContactNumber.requestFocus();
            isValid = false;
        } else if (etContactNumber.getText().toString().trim().length() < 10) {
            setFieldError(tilContactNumber, etContactNumber, "Please enter a valid contact number (at least 10 digits)");
            etContactNumber.requestFocus();
            isValid = false;
        }


        if (etNicNumber == null || etNicNumber.getText().toString().trim().isEmpty()) {
            setFieldError(tilNicNumber, etNicNumber, "NIC number is required");
            if (etNicNumber != null) etNicNumber.requestFocus();
            isValid = false;
        }


        if (dropdownGender == null || dropdownGender.getText().toString().trim().isEmpty()) {
            setFieldError(tilGender, dropdownGender, "Please select gender");
            if (dropdownGender != null) dropdownGender.requestFocus();
            isValid = false;
        }


        if (etDateOfBirth == null || etDateOfBirth.getText().toString().trim().isEmpty()) {
            setFieldError(tilDateOfBirth, etDateOfBirth, "Date of birth is required");
            if (etDateOfBirth != null) etDateOfBirth.requestFocus();
            isValid = false;
        }

        Log.d(TAG, "Validation result: " + isValid);
        return isValid;
    }

    private void setFieldError(TextInputLayout layout, View field, String errorMessage) {
        if (layout != null) {
            layout.setError(errorMessage);
        } else if (field != null) {
            if (field instanceof TextInputEditText) {
                ((TextInputEditText) field).setError(errorMessage);
            } else if (field instanceof AutoCompleteTextView) {
                ((AutoCompleteTextView) field).setError(errorMessage);
            }
        }
    }

    private boolean checkForDuplicates() {
        if (etEmail == null || etNicNumber == null) {
            Log.e(TAG, "Required views are null, cannot check duplicates");
            return true;
        }

        String email = etEmail.getText().toString().trim();
        String nicNumber = etNicNumber.getText().toString().trim();

        Log.d(TAG, "Checking duplicates for Email: '" + email + "', NIC: '" + nicNumber + "'");

        try {

            if (!email.isEmpty()) {
                if (dbHelper.isEmailExists(email)) {

                    if (currentStaff == null || currentStaff.getId() == 0 || !email.equals(currentStaff.getEmail())) {
                        setFieldError(tilEmail, etEmail, "Email already exists");
                        etEmail.requestFocus();
                        Log.w(TAG, "Duplicate email found: " + email);
                        return true;
                    }
                }
            }

            if (dbHelper.isNicExists(nicNumber)) {

                if (currentStaff == null || currentStaff.getId() == 0 || !nicNumber.equals(currentStaff.getNicNumber())) {
                    setFieldError(tilNicNumber, etNicNumber, "NIC number already exists");
                    etNicNumber.requestFocus();
                    Log.w(TAG, "Duplicate NIC found: " + nicNumber);
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking duplicates: " + e.getMessage(), e);
            showToast("Error validating data");
            return true;
        }

        Log.d(TAG, "No duplicates found");
        return false;
    }

    private void savePersonalInformation() {
        if (currentStaff == null) {
            currentStaff = new Staff();
        }

        if (etFullName != null) {
            currentStaff.setFullName(etFullName.getText().toString().trim());
        }
        if (etEmail != null) {
            String email = etEmail.getText().toString().trim();
            currentStaff.setEmail(email.isEmpty() ? null : email);
        }
        if (etContactNumber != null) {
            currentStaff.setContactNumber(etContactNumber.getText().toString().trim());
        }
        if (etNicNumber != null) {
            currentStaff.setNicNumber(etNicNumber.getText().toString().trim());
        }
        if (dropdownGender != null) {
            currentStaff.setGender(dropdownGender.getText().toString().trim());
        }
        if (etDateOfBirth != null) {
            currentStaff.setDateOfBirth(etDateOfBirth.getText().toString().trim());
        }
        currentStaff.setPhotoUri(selectedImageUri);

        Log.d(TAG, "Personal information saved to staff object: " + currentStaff.getFullName());
    }

    private void navigateToProfessionalDetails() {
        try {
            if (getParentFragmentManager() == null) {
                Log.e(TAG, "FragmentManager is null, cannot navigate");
                showToast("Navigation error occurred");
                return;
            }


            Bundle args = new Bundle();
            args.putSerializable("staff", currentStaff);


            StaffProfessionalInfo staffProfessionalInfo = new StaffProfessionalInfo();
            staffProfessionalInfo.setArguments(args);


            getParentFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, staffProfessionalInfo)
                    .addToBackStack("StaffPersonalInfo")

                    .commit();

            Log.d(TAG, "Successfully navigated to Professional Details with staff ID: " + currentStaff.getId());

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to Professional Details: " + e.getMessage(), e);
            showToast("Error proceeding to next step");
        }
    }

    private void cancelRegistration() {
        if (getContext() != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Cancel Registration")
                    .setMessage("Are you sure you want to cancel? All entered data will be lost.")
                    .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                        clearAllData();
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        } else if (getParentFragmentManager() != null) {
                            getParentFragmentManager().popBackStack();
                        }
                    })
                    .setNegativeButton("Continue", null)
                    .show();
        }
    }

    private void clearAllData() {
        if (etFullName != null) etFullName.setText("");
        if (etEmail != null) etEmail.setText("");
        if (etContactNumber != null) etContactNumber.setText("");
        if (etNicNumber != null) etNicNumber.setText("");
        if (dropdownGender != null) dropdownGender.setText("");
        if (etDateOfBirth != null) etDateOfBirth.setText("");

        if (ivStaffPhoto != null) {
            ivStaffPhoto.setImageResource(R.drawable.addpropic);
        }
        selectedImageUri = null;

        currentStaff = new Staff();
        clearErrors();
        Log.d(TAG, "All data cleared");
    }

    private void showLoading(boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }


        if (btnNextStep != null) {
            btnNextStep.setEnabled(!show);
        }
        if (btnCancel != null) {
            btnCancel.setEnabled(!show);
        }
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            if (getContext() == null) {
                Log.e(TAG, "Context is null, cannot save image");
                return null;
            }

            File photosDir = new File(getContext().getFilesDir(), "staff_photos");
            if (!photosDir.exists()) {
                boolean created = photosDir.mkdirs();
                Log.d(TAG, "Photos directory created: " + created);
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String filename = "staff_photo_" + timestamp + ".jpg";
            File imageFile = new File(photosDir, filename);


            InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Log.e(TAG, "Cannot open input stream for image");
                return null;
            }

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


    private void populateFieldsFromStaff() {
        if (currentStaff != null) {
            Log.d(TAG, "Populating fields from staff: " + currentStaff.getFullName());

            if (currentStaff.getFullName() != null && etFullName != null) {
                etFullName.setText(currentStaff.getFullName());
            }
            if (currentStaff.getEmail() != null && etEmail != null) {
                etEmail.setText(currentStaff.getEmail());
            }
            if (currentStaff.getContactNumber() != null && etContactNumber != null) {
                etContactNumber.setText(currentStaff.getContactNumber());
            }
            if (currentStaff.getNicNumber() != null && etNicNumber != null) {
                etNicNumber.setText(currentStaff.getNicNumber());
            }
            if (currentStaff.getGender() != null && dropdownGender != null) {
                dropdownGender.setText(currentStaff.getGender());
            }
            if (currentStaff.getDateOfBirth() != null && etDateOfBirth != null) {
                etDateOfBirth.setText(currentStaff.getDateOfBirth());
            }
            if (currentStaff.getPhotoUri() != null && ivStaffPhoto != null) {
                selectedImageUri = currentStaff.getPhotoUri();
                try {
                    ivStaffPhoto.setImageURI(Uri.fromFile(new File(selectedImageUri)));
                    ivStaffPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading existing photo: " + e.getMessage());
                    ivStaffPhoto.setImageResource(R.drawable.addpropic);
                }
            }
            Log.d(TAG, "Fields populated successfully");
        }
    }


    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "Toast: " + message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
            Log.d(TAG, "Database helper closed");
        }
    }
}