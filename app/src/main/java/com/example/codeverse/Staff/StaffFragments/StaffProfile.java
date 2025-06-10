package com.example.codeverse.Staff.StaffFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.codeverse.Admin.Helpers.StaffDatabaseHelper;
import com.example.codeverse.Admin.Models.Staff;
import com.example.codeverse.LoginScreens.LogoutStaff;
import com.example.codeverse.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class StaffProfile extends Fragment {

    private TextView tvStaffName, tvDepartment, tvEmail, tvContact, tvNic, tvGender;
    private TextView tvPosition, tvSubject, tvQualification, tvExperience;
    private ImageView ivStaffProfilePic, ivBack;
    private MaterialCardView cvBack;
    private MaterialButton btnLogout;
    private FloatingActionButton fab_edit_profile;
    private StaffDatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private String staffEmail;
    private Staff currentStaff;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_staff_profile, container, false);
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
        tvStaffName = view.findViewById(R.id.tv_staff_name);
        tvDepartment = view.findViewById(R.id.chip_department);
        tvEmail = view.findViewById(R.id.tv_email);
        tvContact = view.findViewById(R.id.tv_contact);
        tvNic = view.findViewById(R.id.tv_nic);
        tvGender = view.findViewById(R.id.tv_gender);
        tvPosition = view.findViewById(R.id.tv_position);
        tvSubject = view.findViewById(R.id.tv_subject);
        tvQualification = view.findViewById(R.id.tv_qualification);
        tvExperience = view.findViewById(R.id.tv_experience);
        ivStaffProfilePic = view.findViewById(R.id.iv_staff_profile_pic);
        ivBack = view.findViewById(R.id.iv_back);
        cvBack = view.findViewById(R.id.cv_back);
        btnLogout = view.findViewById(R.id.btn_logout);
        fab_edit_profile = view.findViewById(R.id.fab_edit_profile);
    }

    private void initializeDatabase() {
        databaseHelper = new StaffDatabaseHelper(getContext());
    }

    private void getStaffEmailFromPreferences() {
        sharedPreferences = getActivity().getSharedPreferences("StaffPrefs", getContext().MODE_PRIVATE);
        staffEmail = sharedPreferences.getString("staff_email", "");

        if (staffEmail == null || staffEmail.isEmpty()) {
            showToast("Email not found. Please login again.");
            logoutStaff();
            return;
        }
    }

    private void loadStaffData() {
        try {
            currentStaff = getStaffByEmail(staffEmail);

            if (currentStaff != null) {
                displayStaffInformation();
            } else {
                showToast("Staff data not found");
                setDefaultValues();
            }
        } catch (Exception e) {
            showToast("Error loading staff data");
            setDefaultValues();
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

    private void displayStaffInformation() {
        tvStaffName.setText(currentStaff.getFullName() != null ? currentStaff.getFullName() : "N/A");
        tvDepartment.setText(currentStaff.getDepartment() != null ? currentStaff.getDepartment() : "N/A");
        tvEmail.setText(currentStaff.getEmail() != null ? currentStaff.getEmail() : "N/A");
        tvContact.setText(currentStaff.getContactNumber() != null ? currentStaff.getContactNumber() : "N/A");
        tvNic.setText(currentStaff.getNicNumber() != null ? currentStaff.getNicNumber() : "N/A");
        tvGender.setText(currentStaff.getGender() != null ? currentStaff.getGender() : "N/A");
        tvPosition.setText(currentStaff.getPosition() != null ? currentStaff.getPosition() : "N/A");
        tvSubject.setText(currentStaff.getTeachingSubject() != null ? currentStaff.getTeachingSubject() : "N/A");
        tvQualification.setText(currentStaff.getHighestQualification() != null ? currentStaff.getHighestQualification() : "N/A");
        tvExperience.setText(currentStaff.getExperienceYears() != null ? currentStaff.getExperienceYears() + " years" : "N/A");

        loadProfilePicture();
    }

    private void loadProfilePicture() {
        if (currentStaff.getPhotoUri() != null && !currentStaff.getPhotoUri().isEmpty()) {
            try {
                Bitmap bitmap = StaffDatabaseHelper.getStaffPhoto(currentStaff.getPhotoUri());
                if (bitmap != null) {
                    ivStaffProfilePic.setImageBitmap(bitmap);
                } else {
                    ivStaffProfilePic.setImageResource(R.drawable.ic_person);
                }
            } catch (Exception e) {
                ivStaffProfilePic.setImageResource(R.drawable.ic_person);
            }
        } else {
            ivStaffProfilePic.setImageResource(R.drawable.ic_person);
        }
    }

    private void setDefaultValues() {
        tvStaffName.setText("N/A");
        tvDepartment.setText("N/A");
        tvEmail.setText("N/A");
        tvContact.setText("N/A");
        tvNic.setText("N/A");
        tvGender.setText("N/A");
        tvPosition.setText("N/A");
        tvSubject.setText("N/A");
        tvQualification.setText("N/A");
        tvExperience.setText("N/A");
        ivStaffProfilePic.setImageResource(R.drawable.ic_person);
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> navigateBack());
        ivBack.setOnClickListener(v -> navigateBack());
        btnLogout.setOnClickListener(v -> logoutStaff());
        fab_edit_profile.setOnClickListener(v->{
            StaffEditProfile editProfile = new StaffEditProfile();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout,editProfile);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    private void navigateBack() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void logoutStaff() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

        Intent intent = new Intent(getActivity(), LogoutStaff.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshStaffData() {
        loadStaffData();
    }

    public Staff getCurrentStaff() {
        return currentStaff;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    public static StaffProfile newInstance() {
        return new StaffProfile();
    }

    public static StaffProfile newInstance(String staffEmail) {
        StaffProfile fragment = new StaffProfile();
        Bundle args = new Bundle();
        args.putString("staff_email", staffEmail);
        fragment.setArguments(args);
        return fragment;
    }
}