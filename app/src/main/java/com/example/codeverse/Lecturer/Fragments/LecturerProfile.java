package com.example.codeverse.Lecturer.Fragments;

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

import androidx.fragment.app.Fragment;

import com.example.codeverse.Admin.Helpers.StaffDatabaseHelper;
import com.example.codeverse.Admin.Models.Staff;
import com.example.codeverse.LoginScreens.Login;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Utils.StaffSessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.content.Context.MODE_PRIVATE;

public class LecturerProfile extends Fragment {

    private ImageView ivLecturerProfilePic;
    private TextView tvLecturerName;
    private TextView tvLecturerId;
    private Chip chipDepartment;
    private TextView tvLecturerEmail;
    private TextView tvLecturerPhone;
    private TextView tvLecturerPosition;
    private TextView tvLecturerDepartment;
    private TextView tvLecturerSubject;
    private TextView tvLecturerQualification;
    private TextView tvLecturerUniversity;
    private TextView tvLecturerExperience;
    private MaterialButton btnLogout;
    private MaterialCardView cvBack;

    private FloatingActionButton fab_edit_profile;

    private StaffDatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private String currentStaffEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecturer_profile, container, false);

        initViews(view);
        initDatabase();
        loadLecturerData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        ivLecturerProfilePic = view.findViewById(R.id.iv_lecturer_profile_pic);
        tvLecturerName = view.findViewById(R.id.tv_lecturer_name);
        tvLecturerId = view.findViewById(R.id.tv_lecturer_id);
        chipDepartment = view.findViewById(R.id.chip_department);
        tvLecturerEmail = view.findViewById(R.id.tv_lecturer_email);
        tvLecturerPhone = view.findViewById(R.id.tv_lecturer_phone);
        tvLecturerPosition = view.findViewById(R.id.tv_lecturer_position);
        tvLecturerDepartment = view.findViewById(R.id.tv_lecturer_department);
        tvLecturerSubject = view.findViewById(R.id.tv_lecturer_subject);
        tvLecturerQualification = view.findViewById(R.id.tv_lecturer_qualification);
        tvLecturerUniversity = view.findViewById(R.id.tv_lecturer_university);
        tvLecturerExperience = view.findViewById(R.id.tv_lecturer_experience);
        btnLogout = view.findViewById(R.id.btn_logout);
        cvBack = view.findViewById(R.id.cv_back);
        fab_edit_profile = view.findViewById(R.id.fab_edit_profile);
    }

    private void initDatabase() {
        databaseHelper = new StaffDatabaseHelper(getContext());
        sharedPreferences = getActivity().getSharedPreferences("LecturerPrefs", MODE_PRIVATE);
        currentStaffEmail = sharedPreferences.getString("staff_email", null);
    }

    private void loadLecturerData() {
        if (currentStaffEmail != null && !currentStaffEmail.isEmpty()) {
            Staff lecturer = databaseHelper.getStaffByEmail(currentStaffEmail);
            if (lecturer != null) {
                displayLecturerInfo(lecturer);
            } else {
                Toast.makeText(getContext(), "Lecturer data not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "No lecturer logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayLecturerInfo(Staff lecturer) {
        tvLecturerName.setText(lecturer.getFullName());
        chipDepartment.setText(lecturer.getDepartment() + " Department");

        tvLecturerEmail.setText(lecturer.getEmail());
        tvLecturerPhone.setText(lecturer.getContactNumber());

        tvLecturerPosition.setText(lecturer.getPosition());
        tvLecturerDepartment.setText(lecturer.getDepartment());
        tvLecturerSubject.setText(lecturer.getTeachingSubject());

        tvLecturerQualification.setText(lecturer.getHighestQualification());
        tvLecturerUniversity.setText(lecturer.getUniversity());
        tvLecturerExperience.setText(lecturer.getExperienceYears() + " Years");

        loadProfileImage(lecturer.getPhotoUri());
    }

    private void loadProfileImage(String photoUri) {
        if (photoUri != null && !photoUri.isEmpty()) {
            Bitmap bitmap = StaffDatabaseHelper.getStaffPhoto(photoUri);
            if (bitmap != null) {
                ivLecturerProfilePic.setImageBitmap(bitmap);
            }
        }
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> logout());
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        fab_edit_profile.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new LecturerEditProfile())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void logout() {
        // Clear LecturerPrefs SharedPreferences
        SharedPreferences.Editor lecturerEditor = sharedPreferences.edit();
        lecturerEditor.clear();
        lecturerEditor.apply();

        // Also clear StaffPrefs SharedPreferences for complete logout
        SharedPreferences staffPrefs = getActivity().getSharedPreferences("StaffPrefs", MODE_PRIVATE);
        SharedPreferences.Editor staffEditor = staffPrefs.edit();
        staffEditor.clear();
        staffEditor.apply();

        // Clear StaffSessionManager session
        if (getContext() != null) {
            StaffSessionManager staffSessionManager = new StaffSessionManager(getContext());
            staffSessionManager.logoutStaff();
        }

        // Show logout confirmation
        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to Login activity
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finish current activity
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}