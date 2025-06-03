package com.example.codeverse.Staff.StaffFragments;

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
import com.example.codeverse.R;
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


    private TextInputLayout tilName, tilEmail, tilPhone, tilOfficeHours, tilOfficeLocation;
    private TextInputLayout tilSpecialization, tilYearsService, tilEducation;
    private TextInputLayout tilResearchTags, tilResearchDescription, tilPublications;


    private ActivityResultLauncher<Intent> imagePickerLauncher;


    private boolean dataChanged = false;


    private View rootView;


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


        initUI();


        setupDropdowns();


        setClickListeners();


        setupImagePicker();


        loadFacultyData();
    }

    private void initUI() {

        ivProfilePic = rootView.findViewById(R.id.iv_profile_pic);
        fabEditPicture = rootView.findViewById(R.id.fab_edit_picture);


        cvBack = rootView.findViewById(R.id.cv_back);
        cvSave = rootView.findViewById(R.id.cv_save);
        ivBack = rootView.findViewById(R.id.iv_back);
        ivSave = rootView.findViewById(R.id.iv_save);


        etName = rootView.findViewById(R.id.et_name);
        etStaffId = rootView.findViewById(R.id.et_staff_id);
        dropdownDepartment = rootView.findViewById(R.id.dropdown_department);
        dropdownPosition = rootView.findViewById(R.id.dropdown_position);


        etEmail = rootView.findViewById(R.id.et_email);
        etPhone = rootView.findViewById(R.id.et_phone);
        etOfficeHours = rootView.findViewById(R.id.et_office_hours);
        etOfficeLocation = rootView.findViewById(R.id.et_office_location);


        etSpecialization = rootView.findViewById(R.id.et_specialization);
        etYearsService = rootView.findViewById(R.id.et_years_service);
        etEducation = rootView.findViewById(R.id.et_education);


        etResearchTags = rootView.findViewById(R.id.et_research_tags);
        etResearchDescription = rootView.findViewById(R.id.et_research_description);
        etPublications = rootView.findViewById(R.id.et_publications);


        etLinkedin = rootView.findViewById(R.id.et_linkedin);
        etResearchGate = rootView.findViewById(R.id.et_research_gate);
        etGithub = rootView.findViewById(R.id.et_github);


        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnSaveChanges = rootView.findViewById(R.id.btn_save_changes);


        savingOverlay = rootView.findViewById(R.id.saving_overlay);
        successOverlay = rootView.findViewById(R.id.success_overlay);


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

        setupTextChangeListeners();


        etOfficeHours.setOnClickListener(v -> showOfficeDatePicker());


        cvBack.setOnClickListener(v -> handleBackPress());


        cvSave.setOnClickListener(v -> saveProfile());


        btnCancel.setOnClickListener(v -> handleBackPress());


        btnSaveChanges.setOnClickListener(v -> saveProfile());


        fabEditPicture.setOnClickListener(v -> openImagePicker());
    }

    private void showOfficeDatePicker() {
        if (getContext() == null) return;


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {

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


        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);

        datePickerDialog.show();
    }

    private void setupTextChangeListeners() {

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


        TextInputEditText[] editableFields = {
                etName, etEmail, etPhone, etOfficeHours, etOfficeLocation,
                etSpecialization, etYearsService, etEducation,
                etResearchTags, etResearchDescription, etPublications,
                etLinkedin, etResearchGate, etGithub
        };

        for (TextInputEditText field : editableFields) {
            field.addTextChangedListener(textWatcher);
        }


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


        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");


        new AlertDialog.Builder(requireContext())
                .setTitle("Select Image Source")
                .setItems(new String[]{"Gallery", "Camera"}, (dialog, which) -> {
                    if (which == 0) {

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


        try {
            if (getContext() != null) {

                ivProfilePic.setImageResource(R.drawable.ic_profile);


                ivProfilePic.setBackgroundResource(R.drawable.circle_background);
                ivProfilePic.setClipToOutline(true);


                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile);
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);
                roundedDrawable.setCircular(true);
                ivProfilePic.setImageDrawable(roundedDrawable);
            }
        } catch (Exception e) {

            ivProfilePic.setImageResource(R.drawable.ic_profile);
            Log.e("StaffProfileEditFragment", "Failed to apply circular crop: " + e.getMessage());
        }


        dataChanged = false;
    }

    private void saveProfile() {

        if (!validateInputs()) {
            return;
        }


        savingOverlay.setVisibility(View.VISIBLE);


        createStaffProfile();


        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            savingOverlay.setVisibility(View.GONE);


            successOverlay.setVisibility(View.VISIBLE);


            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                successOverlay.setVisibility(View.GONE);


                if (listener != null) {
                    listener.onProfileSaved(
                            etName.getText().toString().trim(),
                            dropdownDepartment.getText().toString().trim()
                    );
                }


                dataChanged = false;


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


        tilName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilOfficeHours.setError(null);
        tilOfficeLocation.setError(null);
        tilSpecialization.setError(null);
        tilYearsService.setError(null);
        tilPublications.setError(null);


        String name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            tilName.setError("Name cannot be empty");
            isValid = false;
        }


        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email cannot be empty");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            isValid = false;
        }


        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Phone number cannot be empty");
            isValid = false;
        }


        String officeHours = etOfficeHours.getText().toString().trim();
        if (TextUtils.isEmpty(officeHours)) {
            tilOfficeHours.setError("Office hours cannot be empty");
            isValid = false;
        }


        String officeLocation = etOfficeLocation.getText().toString().trim();
        if (TextUtils.isEmpty(officeLocation)) {
            tilOfficeLocation.setError("Office location cannot be empty");
            isValid = false;
        }


        if (TextUtils.isEmpty(dropdownDepartment.getText())) {
            dropdownDepartment.setError("Department is required");
            isValid = false;
        }


        if (TextUtils.isEmpty(dropdownPosition.getText())) {
            dropdownPosition.setError("Position is required");
            isValid = false;
        }


        String yearsText = etYearsService.getText().toString().trim();
        if (!TextUtils.isEmpty(yearsText)) {
            try {
                Integer.parseInt(yearsText);
            } catch (NumberFormatException e) {
                tilYearsService.setError("Please enter a valid number");
                isValid = false;
            }
        }


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


    public void setOnProfileEditListener(OnProfileEditListener listener) {
        this.listener = listener;
    }


    public static StaffEditProfile newInstance(String staffId) {
        StaffEditProfile fragment = new StaffEditProfile();
        Bundle args = new Bundle();
        args.putString("STAFF_ID", staffId);
        fragment.setArguments(args);
        return fragment;
    }


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