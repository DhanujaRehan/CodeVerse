package com.example.codeverse.Lecturer.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.codeverse.R;
import com.example.codeverse.Staff.Helper.StaffDatabaseHelper;
import com.example.codeverse.Staff.Models.StaffDetails;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class LecturerEditProfile extends Fragment {

    private ImageView ivProfilePicture, cv;
    private TextInputEditText etCurrentPassword;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnSelectImage;
    private MaterialButton btnRemoveImage;
    private MaterialButton btnCancel,cvBack;
    private MaterialButton btnSaveChanges;
    private FrameLayout loadingOverlay;

    private StaffDatabaseHelper dbHelper;
    private StaffDetails currentStaff;
    private String selectedImagePath = "";
    private String staffId = "FAC2023104";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecturer_edit_profile, container, false);

        initViews(view);
        setupDatabase();
        loadStaffData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        ivProfilePicture = view.findViewById(R.id.iv_profile_picture);
        etCurrentPassword = view.findViewById(R.id.et_current_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnSelectImage = view.findViewById(R.id.btn_select_image);
        btnRemoveImage = view.findViewById(R.id.btn_remove_image);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnSaveChanges = view.findViewById(R.id.btn_save_changes);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
        cvBack = view.findViewById(R.id.cv_back);

    }

    private void setupDatabase() {
        dbHelper = StaffDatabaseHelper.getInstance(getContext());
    }

    private void loadStaffData() {
        currentStaff = dbHelper.getStaffByStaffId(staffId);

        if (currentStaff != null && currentStaff.getProfileImagePath() != null) {
            loadProfileImage(currentStaff.getProfileImagePath());
        }
    }

    private void setupClickListeners() {
        findViewById(R.id.iv_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnSelectImage.setOnClickListener(v -> selectImage());
        btnRemoveImage.setOnClickListener(v -> removeImage());
        btnCancel.setOnClickListener(v -> clearFields());
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private View findViewById(int id) {
        return getView().findViewById(id);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    private void removeImage() {
        ivProfilePicture.setImageResource(R.drawable.ic_person);
        selectedImagePath = "";
    }

    private void clearFields() {
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
        loadStaffData();
    }

    private void saveChanges() {
        if (!validateInput()) {
            return;
        }

        showLoading();

        new Handler().postDelayed(() -> {
            try {
                boolean updated = false;

                if (!selectedImagePath.isEmpty()) {
                    currentStaff.setProfileImagePath(selectedImagePath);
                    updated = true;
                }

                String newPassword = etNewPassword.getText().toString().trim();
                if (!newPassword.isEmpty()) {
                    updated = true;
                }

                if (updated) {
                    int result = dbHelper.updateStaff(currentStaff);

                    hideLoading();

                    if (result > 0) {
                        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                    } else {
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hideLoading();
                    Toast.makeText(getContext(), "No changes to save", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                hideLoading();
                Toast.makeText(getContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    private boolean validateInput() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!newPassword.isEmpty()) {
            if (currentPassword.isEmpty()) {
                etCurrentPassword.setError("Current password required");
                etCurrentPassword.requestFocus();
                return false;
            }

            if (newPassword.length() < 6) {
                etNewPassword.setError("Password must be at least 6 characters");
                etNewPassword.requestFocus();
                return false;
            }

            if (!newPassword.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                etConfirmPassword.requestFocus();
                return false;
            }
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                String imagePath = saveImageToInternalStorage(selectedImageUri);
                if (imagePath != null) {
                    selectedImagePath = imagePath;
                    loadProfileImage(imagePath);
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error selecting image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

            File directory = new File(getContext().getFilesDir(), "profile_images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = "profile_" + staffId + "_" + System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            return null;
        }
    }

    private void loadProfileImage(String imagePath) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    ivProfilePicture.setImageBitmap(bitmap);
                } else {
                    ivProfilePicture.setImageResource(R.drawable.ic_person);
                }
            } else {
                ivProfilePicture.setImageResource(R.drawable.ic_person);
            }
        } catch (Exception e) {
            ivProfilePicture.setImageResource(R.drawable.ic_person);
        }
    }

    private void showLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
        }
    }
}