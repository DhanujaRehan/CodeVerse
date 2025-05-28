package com.example.codeverse;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class StaffEditProfile extends Fragment {

    // UI Components
    private ImageView ivProfilePic;
    private FloatingActionButton fabEditPicture;
    private TextInputEditText etName, etStaffId, etEmail, etPhone, etOfficeHours, etOfficeLocation;
    private TextInputEditText etSpecialization, etYearsService, etEducation;
    private TextInputEditText etResearchTags, etResearchDescription, etPublications;
    private TextInputEditText etLinkedin, etResearchGate, etGithub;
    private AutoCompleteTextView dropdownDepartment, dropdownPosition;
    private MaterialButton btnCancel, btnSaveChanges;
    private LottieAnimationView ivBack, ivSave;
    private MaterialCardView cvBack, cvSave;
    private FrameLayout savingOverlay, successOverlay;

    // Text Input Layouts for validation
    private TextInputLayout tilName, tilEmail, tilPhone, tilOfficeHours, tilOfficeLocation;
    private TextInputLayout tilSpecialization, tilYearsService, tilEducation;
    private TextInputLayout tilResearchTags, tilResearchDescription, tilPublications;

    // Image picker launcher
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // Flag to track changes
    private boolean dataChanged = false;

    // Root view reference
    private View rootView;

    // Interface for communicating with parent Activity
    public interface OnProfileEditListener {
        void onProfileSaved(String staffName, String department);
        void onProfileEditCanceled();
    }

    private OnProfileEditListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_staff_edit_profile, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        initUI();

        // Setup dropdowns
        setupDropdowns();

        // Set click listeners
        setClickListeners();

        // Setup image picker
        setupImagePicker();

        // Load faculty data
        loadFacultyData();
    }

    private void initUI() {
        // Profile image components
        ivProfilePic = rootView.findViewById(R.id.iv_profile_pic);
        fabEditPicture = rootView.findViewById(R.id.fab_edit_picture);

        // Top bar components
        cvBack = rootView.findViewById(R.id.cv_back);
        cvSave = rootView.findViewById(R.id.cv_save);
        ivBack = rootView.findViewById(R.id.iv_back);
        ivSave = rootView.findViewById(R.id.iv_save);

        // Basic info inputs
        etName = rootView.findViewById(R.id.et_name);
        etStaffId = rootView.findViewById(R.id.et_staff_id);
        dropdownDepartment = rootView.findViewById(R.id.dropdown_department);
        dropdownPosition = rootView.findViewById(R.id.dropdown_position);

        // Contact information inputs
        etEmail = rootView.findViewById(R.id.et_email);
        etPhone = rootView.findViewById(R.id.et_phone);
        etOfficeHours = rootView.findViewById(R.id.et_office_hours);
        etOfficeLocation = rootView.findViewById(R.id.et_office_location);

        // Professional information inputs
        etSpecialization = rootView.findViewById(R.id.et_specialization);
        etYearsService = rootView.findViewById(R.id.et_years_service);
        etEducation = rootView.findViewById(R.id.et_education);

        // Research information inputs
        etResearchTags = rootView.findViewById(R.id.et_research_tags);
        etResearchDescription = rootView.findViewById(R.id.et_research_description);
        etPublications = rootView.findViewById(R.id.et_publications);

        // Social media inputs
        etLinkedin = rootView.findViewById(R.id.et_linkedin);
        etResearchGate = rootView.findViewById(R.id.et_research_gate);
        etGithub = rootView.findViewById(R.id.et_github);

        // Buttons
        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnSaveChanges = rootView.findViewById(R.id.btn_save_changes);

        // Overlay views
        savingOverlay = rootView.findViewById(R.id.saving_overlay);
        successOverlay = rootView.findViewById(R.id.success_overlay);

        // Get TextInputLayouts for validation
        tilName = (TextInputLayout) etName.getParent().getParent();
        tilEmail = (TextInputLayout) etEmail.getParent().getParent();
        tilPhone = (TextInputLayout) etPhone.getParent().getParent();
        tilOfficeHours = (TextInputLayout) etOfficeHours.getParent().getParent();
        tilOfficeLocation = (TextInputLayout) etOfficeLocation.getParent().getParent();
        tilSpecialization = (TextInputLayout) etSpecialization.getParent().getParent();
        tilYearsService = (TextInputLayout) etYearsService.getParent().getParent();
        tilEducation = (TextInputLayout) etEducation.getParent().getParent();
        tilResearchTags = (TextInputLayout) etResearchTags.getParent().getParent();
        tilResearchDescription = (TextInputLayout) etResearchDescription.getParent().getParent();
        tilPublications = (TextInputLayout) etPublications.getParent().getParent();
    }

    private void setupDropdowns() {
        // Department dropdown options
        List<String> departments = new ArrayList<>(Arrays.asList(
                "Computer Science Department",
                "Electrical Engineering Department",
                "Mechanical Engineering Department",
                "Civil Engineering Department",
                "Business Administration Department",
                "Physics Department",
                "Mathematics Department",
                "Chemistry Department",
                "Biology Department"));

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                departments);
        dropdownDepartment.setAdapter(departmentAdapter);

        // Position dropdown options
        List<String> positions = new ArrayList<>(Arrays.asList(
                "Professor",
                "Associate Professor",
                "Assistant Professor",
                "Lecturer",
                "Senior Lecturer",
                "Department Chair",
                "Visiting Professor",
                "Dean",
                "Research Fellow"));

        ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                positions);
        dropdownPosition.setAdapter(positionAdapter);
    }

    private void setClickListeners() {
        // Track text changes
        setupTextChangeListeners();

        // Office hours date picker
        etOfficeHours.setOnClickListener(v -> showOfficeDatePicker());

        // Back button
        cvBack.setOnClickListener(v -> handleBackPress());

        // Save button in top bar
        cvSave.setOnClickListener(v -> saveProfile());

        // Cancel button
        btnCancel.setOnClickListener(v -> handleBackPress());

        // Save changes button
        btnSaveChanges.setOnClickListener(v -> saveProfile());

        // Edit profile picture button
        fabEditPicture.setOnClickListener(v -> openImagePicker());
    }

    private void showOfficeDatePicker() {
        if (getContext() == null) return;

        // Create an office hours picker dialog
        // This is a simplified example - in a real app, you would use a more complex dialog
        // for setting up office hours
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show a date picker just as an example
        // In real app, this would be a more complex custom dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Use the day of week to set office hours
                    Calendar selected = Calendar.getInstance();
                    selected.set(selectedYear, selectedMonth, selectedDay);
                    int dayOfWeek = selected.get(Calendar.DAY_OF_WEEK);

                    String dayName = "";
                    switch (dayOfWeek) {
                        case Calendar.MONDAY:
                            dayName = "Mon";
                            break;
                        case Calendar.TUESDAY:
                            dayName = "Tue";
                            break;
                        case Calendar.WEDNESDAY:
                            dayName = "Wed";
                            break;
                        case Calendar.THURSDAY:
                            dayName = "Thu";
                            break;
                        case Calendar.FRIDAY:
                            dayName = "Fri";
                            break;
                    }

                    String currentHours = etOfficeHours.getText().toString().trim();

                    // Add the new day to existing office hours
                    String updatedHours;
                    if (TextUtils.isEmpty(currentHours)) {
                        updatedHours = dayName + ": 10:00 AM - 12:00 PM";
                    } else {
                        updatedHours = currentHours + "\n" + dayName + ": 10:00 AM - 12:00 PM";
                    }

                    etOfficeHours.setText(updatedHours);
                    dataChanged = true;
                },
                year, month, day);

        // Show only weekdays (no weekends)
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);

        datePickerDialog.show();
    }

    private void setupTextChangeListeners() {
        // Add text watchers to all editable fields to track changes
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // Apply text watcher to all editable fields
        TextInputEditText[] editableFields = {
                etName, etEmail, etPhone, etOfficeHours, etOfficeLocation,
                etSpecialization, etYearsService, etEducation,
                etResearchTags, etResearchDescription, etPublications,
                etLinkedin, etResearchGate, etGithub
        };

        for (TextInputEditText field : editableFields) {
            field.addTextChangedListener(textWatcher);
        }

        // Add change listeners for dropdown fields
        dropdownDepartment.setOnItemClickListener((parent, view, position, id) -> dataChanged = true);
        dropdownPosition.setOnItemClickListener((parent, view, position, id) -> dataChanged = true);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            if (getContext() != null) {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                                ivProfilePic.setImageBitmap(bitmap);
                                dataChanged = true;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openImagePicker() {
        if (getContext() == null) return;

        // Create an intent to open the image picker
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        // Show options dialog for image source
        new AlertDialog.Builder(requireContext())
                .setTitle("Select Image Source")
                .setItems(new String[]{"Gallery", "Camera"}, (dialog, which) -> {
                    if (which == 0) {
                        // Gallery
                        imagePickerLauncher.launch(intent);
                    } else {
                        Toast.makeText(getContext(), "Camera functionality would be implemented here", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void loadFacultyData() {

        etName.setText("Dr. Sarah Wilson");
        etStaffId.setText("FAC2023104");
        dropdownDepartment.setText("Computer Science Department", false);
        dropdownPosition.setText("Associate Professor", false);

        etEmail.setText("sarah.wilson@university.edu");
        etPhone.setText("(555) 987-6543");
        etOfficeHours.setText("Mon, Wed: 10:00 AM - 12:00 PM\nThu: 2:00 PM - 4:00 PM");
        etOfficeLocation.setText("Computer Science Building, Room 301");

        etSpecialization.setText("Artificial Intelligence, Machine Learning");
        etYearsService.setText("12");
        etEducation.setText("Ph.D. in Computer Science, MIT\nM.S. in Computer Science, Stanford University");

        etResearchTags.setText("Artificial Intelligence, Machine Learning, Computer Vision, Natural Language Processing, Data Science");
        etResearchDescription.setText("Dr. Wilson's research focuses on developing novel machine learning algorithms for computer vision and natural language processing applications. Her recent work involves creating efficient deep learning models for resource-constrained environments.");
        etPublications.setText("42");

        etLinkedin.setText("linkedin.com/in/sarahwilson");
        etResearchGate.setText("researchgate.net/profile/Sarah_Wilson");
        etGithub.setText("github.com/sarahwilson");

        // Load profile image
        // Instead of Glide, use Android's built-in image loading capabilities
        try {
            if (getContext() != null) {
                // Load the image resource directly
                ivProfilePic.setImageResource(R.drawable.ic_profile);

                // Apply circular crop effect manually
                ivProfilePic.setBackgroundResource(R.drawable.circle_background); // You'll need to create this drawable
                ivProfilePic.setClipToOutline(true);

                // Or use a RoundedBitmapDrawable approach
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile);
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);
                roundedDrawable.setCircular(true);
                ivProfilePic.setImageDrawable(roundedDrawable);
            }
        } catch (Exception e) {
            // Simple fallback - just load the image without any special effects
            ivProfilePic.setImageResource(R.drawable.ic_profile);
            Log.e("StaffProfileEditFragment", "Failed to apply circular crop: " + e.getMessage());
        }

        // Reset data changed flag after loading
        dataChanged = false;
    }

    private void saveProfile() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Show saving overlay
        savingOverlay.setVisibility(View.VISIBLE);

        // Create a staff profile object (not implemented here)
        createStaffProfile();

        // Simulate network delay for saving profile
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Hide saving overlay
            savingOverlay.setVisibility(View.GONE);

            // Show success overlay
            successOverlay.setVisibility(View.VISIBLE);

            // Close after delay
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                successOverlay.setVisibility(View.GONE);

                // Notify parent activity/fragment about the successful save
                if (listener != null) {
                    listener.onProfileSaved(
                            etName.getText().toString().trim(),
                            dropdownDepartment.getText().toString().trim()
                    );
                }

                // Reset data changed flag
                dataChanged = false;

                // Navigate back
                navigateBack();
            }, 1500);
        }, 2000);
    }

    private void createStaffProfile() {
        // Create a staff profile object with all the data
        // In a real app, this would be sent to a database or API
        // This is just a placeholder for demonstration purposes

        // Example:
        // StaffProfile profile = new StaffProfile();
        // profile.setName(etName.getText().toString().trim());
        // profile.setStaffId(etStaffId.getText().toString().trim());
        // ... set all other fields

        // Then save to database or API
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Reset all error states
        tilName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilOfficeHours.setError(null);
        tilOfficeLocation.setError(null);
        tilSpecialization.setError(null);
        tilYearsService.setError(null);
        tilPublications.setError(null);

        // Validate name
        String name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            tilName.setError("Name cannot be empty");
            isValid = false;
        }

        // Validate email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email cannot be empty");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            isValid = false;
        }

        // Validate phone
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Phone number cannot be empty");
            isValid = false;
        }

        // Validate office hours
        String officeHours = etOfficeHours.getText().toString().trim();
        if (TextUtils.isEmpty(officeHours)) {
            tilOfficeHours.setError("Office hours cannot be empty");
            isValid = false;
        }

        // Validate office location
        String officeLocation = etOfficeLocation.getText().toString().trim();
        if (TextUtils.isEmpty(officeLocation)) {
            tilOfficeLocation.setError("Office location cannot be empty");
            isValid = false;
        }

        // Validate department
        if (TextUtils.isEmpty(dropdownDepartment.getText())) {
            dropdownDepartment.setError("Department is required");
            isValid = false;
        }

        // Validate position
        if (TextUtils.isEmpty(dropdownPosition.getText())) {
            dropdownPosition.setError("Position is required");
            isValid = false;
        }

        // Validate years of service (must be a number)
        String yearsText = etYearsService.getText().toString().trim();
        if (!TextUtils.isEmpty(yearsText)) {
            try {
                Integer.parseInt(yearsText);
            } catch (NumberFormatException e) {
                tilYearsService.setError("Please enter a valid number");
                isValid = false;
            }
        }

        // Validate publications (must be a number)
        String publicationsText = etPublications.getText().toString().trim();
        if (!TextUtils.isEmpty(publicationsText)) {
            try {
                Integer.parseInt(publicationsText);
            } catch (NumberFormatException e) {
                tilPublications.setError("Please enter a valid number");
                isValid = false;
            }
        }

        return isValid;
    }

    private void handleBackPress() {
        if (dataChanged) {
            // Show dialog to confirm discarding changes
            if (getContext() != null) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Discard Changes")
                        .setMessage("You have unsaved changes. Are you sure you want to discard them?")
                        .setPositiveButton("Discard", (dialog, which) -> {
                            if (listener != null) {
                                listener.onProfileEditCanceled();
                            }
                            navigateBack();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        } else {
            if (listener != null) {
                listener.onProfileEditCanceled();
            }
            navigateBack();
        }
    }

    private void navigateBack() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else if (getActivity() != null) {
            getActivity().finish();
        }
    }

    // Method to set the listener from parent Activity/Fragment
    public void setOnProfileEditListener(OnProfileEditListener listener) {
        this.listener = listener;
    }

    // Method to pass data to the fragment if needed
    public static StaffEditProfile newInstance(String staffId) {
        StaffEditProfile fragment = new StaffEditProfile();
        Bundle args = new Bundle();
        args.putString("STAFF_ID", staffId);
        fragment.setArguments(args);
        return fragment;
    }

    // Method to get staff ID from arguments
    private String getStaffIdFromArguments() {
        if (getArguments() != null) {
            return getArguments().getString("STAFF_ID");
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
        listener = null;
    }
}