package com.example.codeverse.Staff.StaffFragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.codeverse.Admin.Helpers.StaffDatabaseHelper;
import com.example.codeverse.Admin.Models.Staff;
import com.example.codeverse.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class StaffEditProfile extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivProfilePicture, ivBack;
    private MaterialCardView cvBack;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnSelectImage, btnRemoveImage, btnCancel, btnSaveChanges;
    private FrameLayout loadingOverlay;

    private StaffDatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private String staffEmail;
    private Staff currentStaff;
    private String selectedImagePath;
    private boolean isImageRemoved = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        initializeDatabase();
        getStaffEmailFromPreferences();
        loadStaffData();
        setupClickListeners();
    }

    private void initializeViews(View view) {
        ivProfilePicture = view.findViewById(R.id.iv_profile_picture);
        ivBack = view.findViewById(R.id.iv_back);
        cvBack = view.findViewById(R.id.cv_back);
        etCurrentPassword = view.findViewById(R.id.et_current_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnSelectImage = view.findViewById(R.id.btn_select_image);
        btnRemoveImage = view.findViewById(R.id.btn_remove_image);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnSaveChanges = view.findViewById(R.id.btn_save_changes);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
    }

    private void initializeDatabase() {
        databaseHelper = new StaffDatabaseHelper(getContext());
    }

    private void getStaffEmailFromPreferences() {
        sharedPreferences = getActivity().getSharedPreferences("StaffPrefs", getContext().MODE_PRIVATE);
        staffEmail = sharedPreferences.getString("staff_email", "");

        if (staffEmail == null || staffEmail.isEmpty()) {
            staffEmail = sharedPreferences.getString("email", "");
        }

        if (staffEmail == null || staffEmail.isEmpty()) {
            staffEmail = sharedPreferences.getString("user_email", "");
        }

        if (staffEmail == null || staffEmail.isEmpty()) {
            showToast("Email not found. Please login again.");
            navigateBack();
            return;
        }
    }

    private void loadStaffData() {
        try {
            currentStaff = getStaffByEmail(staffEmail);

            if (currentStaff != null) {
                loadCurrentProfilePicture();
            } else {
                showToast("Staff data not found");
                navigateBack();
            }
        } catch (Exception e) {
            showToast("Error loading staff data");
            navigateBack();
        }
    }

    private Staff getStaffByEmail(String email) {
        List<Staff> allStaff = databaseHelper.getAllStaff();
        for (Staff staff : allStaff) {
            if (staff.getEmail() != null && staff.getEmail().equals(email)) {
                return staff;
            }
        }
        return null;
    }

    private void loadCurrentProfilePicture() {
        if (currentStaff.getPhotoUri() != null && !currentStaff.getPhotoUri().isEmpty()) {
            try {
                Bitmap bitmap = StaffDatabaseHelper.getStaffPhoto(currentStaff.getPhotoUri());
                if (bitmap != null) {
                    ivProfilePicture.setImageBitmap(bitmap);
                } else {
                    ivProfilePicture.setImageResource(R.drawable.ic_person);
                }
            } catch (Exception e) {
                ivProfilePicture.setImageResource(R.drawable.ic_person);
            }
        } else {
            ivProfilePicture.setImageResource(R.drawable.ic_person);
        }
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> navigateBack());
        ivBack.setOnClickListener(v -> navigateBack());

        btnSelectImage.setOnClickListener(v -> openImagePicker());

        btnRemoveImage.setOnClickListener(v -> removeProfilePicture());

        btnCancel.setOnClickListener(v -> navigateBack());

        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void removeProfilePicture() {
        ivProfilePicture.setImageResource(R.drawable.ic_person);
        selectedImagePath = null;
        isImageRemoved = true;
        showToast("Profile picture will be removed when you save changes");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    selectedImagePath = saveImageToInternalStorage(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                    ivProfilePicture.setImageBitmap(bitmap);
                    isImageRemoved = false;
                    showToast("Image selected successfully");
                } catch (Exception e) {
                    showToast("Error selecting image");
                }
            }
        }
    }

    private String saveImageToInternalStorage(Uri imageUri) throws IOException {
        InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);

        File directory = new File(getContext().getFilesDir(), "staff_photos");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = "staff_" + currentStaff.getId() + "_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);

        FileOutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();

        return file.getAbsolutePath();
    }

    private void saveChanges() {
        if (!validateInputs()) {
            return;
        }

        showLoading(true);

        try {
            boolean isUpdated = false;

            if (isImageRemoved || selectedImagePath != null) {
                String newPhotoUri = isImageRemoved ? "" : selectedImagePath;
                int result = databaseHelper.updateStaffPersonalDetails(
                        currentStaff.getId(),
                        currentStaff.getFullName(),
                        currentStaff.getEmail(),
                        currentStaff.getContactNumber(),
                        currentStaff.getNicNumber(),
                        currentStaff.getGender(),
                        currentStaff.getDateOfBirth(),
                        newPhotoUri
                );

                if (result > 0) {
                    isUpdated = true;
                    currentStaff.setPhotoUri(newPhotoUri);

                    if (isImageRemoved && currentStaff.getPhotoUri() != null && !currentStaff.getPhotoUri().isEmpty()) {
                        deleteOldProfilePicture(currentStaff.getPhotoUri());
                    }
                }
            }

            String newPassword = etNewPassword.getText().toString().trim();
            if (!newPassword.isEmpty()) {
                int result = databaseHelper.updateStaffProfessionalDetails(
                        currentStaff.getId(),
                        currentStaff.getPosition(),
                        currentStaff.getDepartment(),
                        currentStaff.getTeachingSubject(),
                        currentStaff.getProgramCoordinating(),
                        newPassword,
                        currentStaff.getHighestQualification(),
                        currentStaff.getFieldOfStudy(),
                        currentStaff.getUniversity(),
                        currentStaff.getGraduationYear(),
                        currentStaff.getExperienceYears()
                );

                if (result > 0) {
                    isUpdated = true;
                    currentStaff.setPassword(newPassword);
                }
            }

            showLoading(false);

            if (isUpdated) {
                showToast("Profile updated successfully");
                clearPasswordFields();
                selectedImagePath = null;
                isImageRemoved = false;
            } else {
                showToast("No changes were made");
            }

        } catch (Exception e) {
            showLoading(false);
            showToast("Error updating profile");
        }
    }

    private boolean validateInputs() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!newPassword.isEmpty() || !confirmPassword.isEmpty()) {
            if (currentPassword.isEmpty()) {
                showToast("Please enter current password");
                return false;
            }

            if (!currentPassword.equals(currentStaff.getPassword())) {
                showToast("Current password is incorrect");
                return false;
            }

            if (newPassword.isEmpty()) {
                showToast("Please enter new password");
                return false;
            }

            if (newPassword.length() < 6) {
                showToast("New password must be at least 6 characters");
                return false;
            }

            if (!newPassword.equals(confirmPassword)) {
                showToast("New passwords do not match");
                return false;
            }
        }

        return true;
    }

    private void clearPasswordFields() {
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
    }

    private void deleteOldProfilePicture(String photoPath) {
        try {
            File file = new File(photoPath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // Ignore deletion errors
        }
    }

    private void showLoading(boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void navigateBack() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else if (getActivity() != null) {
            getActivity().finish();
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
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    public static StaffEditProfile newInstance() {
        return new StaffEditProfile();
    }

    public static StaffEditProfile newInstance(String staffEmail) {
        StaffEditProfile fragment = new StaffEditProfile();
        Bundle args = new Bundle();
        args.putString("staff_email", staffEmail);
        fragment.setArguments(args);
        return fragment;
    }
}