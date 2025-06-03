package com.example.codeverse.Students.StudentFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.LoginScreens.Logout;
import com.example.codeverse.R;
import com.example.codeverse.Admin.Helpers.StudentDatabaseHelper;
import com.example.codeverse.Students.Models.Student;
import com.example.codeverse.Utils.StudentSessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StudentProfile extends Fragment {

    private static final String TAG = "StudentProfile";

    private LottieAnimationView ivBack, ivSettings, gpaProgress, creditsProgress, coursesCounter;
    private LottieAnimationView userInfoAnimation, emailAnimation, phoneAnimation, calendarAnimation;
    private LottieAnimationView mapPinAnimation, academicCapAnimation, computerAnimation;
    private LottieAnimationView levelUpAnimation, advisorAnimation, academicCapAnimation2;

    private ImageView ivProfilePic;
    private TextView tvProfileName, tvProfileStudentId, tvProfileEmail, tvProfilePhone;
    private TextView tvProfileDob, tvProfileAddress, tvProfileProgram, tvProfileYear;
    private TextView tvProfileAdvisor, tvProfileGraduation, tvGpaValue, tvCreditsValue, tvSemesterValue;
    private Chip chipFaculty;
    private FloatingActionButton fabEditProfile, fabHelp;
    private MaterialCardView cvSettings;
    private MaterialButton btnLogout;

    private StudentDatabaseHelper databaseHelper;
    private StudentSessionManager sessionManager;
    private Student currentStudent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_profile, container, false);

        initViews(view);
        initDatabase();
        loadStudentData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        ivBack = view.findViewById(R.id.iv_back);
        ivSettings = view.findViewById(R.id.iv_settings);
        gpaProgress = view.findViewById(R.id.gpa_progress);
        creditsProgress = view.findViewById(R.id.credits_progress);
        coursesCounter = view.findViewById(R.id.courses_counter);

        ivProfilePic = view.findViewById(R.id.iv_profile_pic);

        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileStudentId = view.findViewById(R.id.tv_profile_student_id);
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);
        tvProfilePhone = view.findViewById(R.id.tv_profile_phone);
        tvProfileDob = view.findViewById(R.id.tv_profile_dob);
        tvProfileAddress = view.findViewById(R.id.tv_profile_address);
        tvProfileProgram = view.findViewById(R.id.tv_profile_program);
        tvProfileYear = view.findViewById(R.id.tv_profile_year);
        tvProfileAdvisor = view.findViewById(R.id.tv_profile_advisor);
        tvProfileGraduation = view.findViewById(R.id.tv_profile_graduation);

        tvGpaValue = view.findViewById(R.id.tv_gpa_value);
        tvCreditsValue = view.findViewById(R.id.tv_credits_value);
        tvSemesterValue = view.findViewById(R.id.tv_semester_value);

        chipFaculty = view.findViewById(R.id.chip_faculty);
        fabEditProfile = view.findViewById(R.id.fab_edit_profile);
        fabHelp = view.findViewById(R.id.fab_help);
        cvSettings = view.findViewById(R.id.cv_settings);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void initDatabase() {
        databaseHelper = new StudentDatabaseHelper(getContext());
        sessionManager = new StudentSessionManager(getContext());
    }

    private void loadStudentData() {
        try {
            if (!sessionManager.isLoggedIn()) {
                Log.e(TAG, "No student session found");
                Toast.makeText(getContext(), "Please login again", Toast.LENGTH_SHORT).show();
                return;
            }

            long studentId = sessionManager.getStudentId();

            if (studentId != -1) {
                Log.d(TAG, "Loading student from database with ID: " + studentId);
                currentStudent = databaseHelper.getStudentById(studentId);

                if (currentStudent != null) {
                    Log.d(TAG, "Student loaded successfully: " + currentStudent.getFullName());
                    populateStudentData();
                } else {
                    Log.e(TAG, "Student not found in database");
                    showErrorMessage("Student data not found");
                }
            } else {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
                long fallbackStudentId = sharedPreferences.getLong("current_student_id", -1);

                if (fallbackStudentId != -1) {
                    Log.d(TAG, "Using fallback student ID: " + fallbackStudentId);
                    currentStudent = databaseHelper.getStudentById(fallbackStudentId);

                    if (currentStudent != null) {
                        populateStudentData();
                    } else {
                        Log.e(TAG, "Student not found in database using fallback ID");
                        showErrorMessage("Student data not found");
                    }
                } else {
                    Log.e(TAG, "No student ID found in session or preferences");
                    showErrorMessage("No student session found");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading student data: " + e.getMessage(), e);
            showErrorMessage("Error loading profile data");
        }
    }

    private void populateStudentData() {
        try {
            if (currentStudent.getFullName() != null && !currentStudent.getFullName().trim().isEmpty()) {
                tvProfileName.setText(currentStudent.getFullName());
            } else {
                tvProfileName.setText("N/A");
            }

            if (currentStudent.getUniversityId() != null && !currentStudent.getUniversityId().trim().isEmpty()) {
                tvProfileStudentId.setText("Student ID: " + currentStudent.getUniversityId());
            } else {
                tvProfileStudentId.setText("Student ID: N/A");
            }

            if (currentStudent.getEmail() != null && !currentStudent.getEmail().trim().isEmpty()) {
                tvProfileEmail.setText(currentStudent.getEmail());
            } else {
                tvProfileEmail.setText("No email provided");
            }

            if (currentStudent.getMobileNumber() != null && !currentStudent.getMobileNumber().trim().isEmpty()) {
                tvProfilePhone.setText(currentStudent.getMobileNumber());
            } else {
                tvProfilePhone.setText("No phone provided");
            }

            if (currentStudent.getDateOfBirth() != null && !currentStudent.getDateOfBirth().trim().isEmpty()) {
                tvProfileDob.setText(currentStudent.getDateOfBirth());
            } else {
                tvProfileDob.setText("Not provided");
            }

            String fullAddress = buildAddress();
            if (!fullAddress.isEmpty()) {
                tvProfileAddress.setText(fullAddress);
            } else {
                tvProfileAddress.setText("No address provided");
            }

            if (currentStudent.getFaculty() != null && !currentStudent.getFaculty().trim().isEmpty()) {
                tvProfileProgram.setText(currentStudent.getFaculty());
                chipFaculty.setText(currentStudent.getFaculty());
            } else {
                tvProfileProgram.setText("Faculty not assigned");
                chipFaculty.setText("N/A");
            }

            if (currentStudent.getBatch() != null && !currentStudent.getBatch().trim().isEmpty()) {
                tvProfileYear.setText("Batch " + currentStudent.getBatch());
            } else {
                tvProfileYear.setText("Batch not assigned");
            }

            if (currentStudent.getSemester() != null && !currentStudent.getSemester().trim().isEmpty()) {
                tvSemesterValue.setText(currentStudent.getSemester());
            } else {
                tvSemesterValue.setText("N/A");
            }

            if (currentStudent.getEnrollmentDate() != null && !currentStudent.getEnrollmentDate().trim().isEmpty()) {
                tvProfileGraduation.setText("Enrolled: " + currentStudent.getEnrollmentDate());
            } else {
                tvProfileGraduation.setText("Enrollment date not available");
            }

            loadProfilePhoto();
            setDefaultAdvisor();
            setDefaultStats();

            Log.d(TAG, "Student profile populated successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error populating student data: " + e.getMessage(), e);
            showErrorMessage("Error displaying profile data");
        }
    }

    private String buildAddress() {
        StringBuilder address = new StringBuilder();

        try {
            if (currentStudent.getPermanentAddress() != null && !currentStudent.getPermanentAddress().trim().isEmpty()) {
                address.append(currentStudent.getPermanentAddress().trim());
            }

            if (currentStudent.getCity() != null && !currentStudent.getCity().trim().isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append(currentStudent.getCity().trim());
            }

            if (currentStudent.getProvince() != null && !currentStudent.getProvince().trim().isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append(currentStudent.getProvince().trim());
            }

            if (currentStudent.getPostalCode() != null && !currentStudent.getPostalCode().trim().isEmpty()) {
                if (address.length() > 0) address.append(" ");
                address.append(currentStudent.getPostalCode().trim());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error building address: " + e.getMessage());
        }

        return address.toString();
    }

    private void setDefaultAdvisor() {
        tvProfileAdvisor.setText("Dr. Emily Thompson");
    }

    private void loadProfilePhoto() {
        try {
            if (currentStudent.getPhotoUri() != null && !currentStudent.getPhotoUri().trim().isEmpty()) {
                Bitmap photo = StudentDatabaseHelper.getStudentPhoto(currentStudent.getPhotoUri());
                if (photo != null) {
                    ivProfilePic.setImageBitmap(photo);
                    Log.d(TAG, "Profile photo loaded successfully");
                } else {
                    Log.d(TAG, "Could not load profile photo from path: " + currentStudent.getPhotoUri());
                }
            } else {
                Log.d(TAG, "No profile photo path available");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading profile photo: " + e.getMessage());
        }
    }

    private void setDefaultStats() {
        tvGpaValue.setText("3.8");
        tvCreditsValue.setText("68");
    }

    private void setupClickListeners() {
        cvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        fabEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

        fabHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelp();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void openSettings() {
        Toast.makeText(getContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
    }

    private void editProfile() {
        FragmentTransaction transaction = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.framelayout, new StudentProfileEdit());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showHelp() {
        Toast.makeText(getContext(), "Help clicked", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        Intent intent = new Intent(getActivity(), Logout.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void showErrorMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
        Log.e(TAG, message);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStudentData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}