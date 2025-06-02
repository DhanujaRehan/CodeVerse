package com.example.codeverse.Students.StudentFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.R;
import com.example.codeverse.Admin.Helpers.StudentDatabaseHelper;
import com.example.codeverse.Students.Models.Student;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StudentProfile extends Fragment {

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
    private MaterialCardView  cvSettings;

    private StudentDatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
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
        ivProfilePic = view.findViewById(R.id.iv_profile_pic);

        gpaProgress = view.findViewById(R.id.gpa_progress);
        creditsProgress = view.findViewById(R.id.credits_progress);
        coursesCounter = view.findViewById(R.id.courses_counter);

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
    }

    private void initDatabase() {
        databaseHelper = new StudentDatabaseHelper(getContext());
        sharedPreferences = getActivity().getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
    }

    private void loadStudentData() {
        long studentId = sharedPreferences.getLong("current_student_id", -1);

        if (studentId != -1) {
            currentStudent = databaseHelper.getStudentById(studentId);
            if (currentStudent != null) {
                populateStudentData();
            }
        }
    }

    private void populateStudentData() {
        if (currentStudent.getFullName() != null) {
            tvProfileName.setText(currentStudent.getFullName());
        }

        if (currentStudent.getUniversityId() != null) {
            tvProfileStudentId.setText("Student ID: " + currentStudent.getUniversityId());
        }

        if (currentStudent.getEmail() != null) {
            tvProfileEmail.setText(currentStudent.getEmail());
        }

        if (currentStudent.getMobileNumber() != null) {
            tvProfilePhone.setText(currentStudent.getMobileNumber());
        }

        if (currentStudent.getDateOfBirth() != null) {
            tvProfileDob.setText(currentStudent.getDateOfBirth());
        }

        String fullAddress = buildAddress();
        if (!fullAddress.isEmpty()) {
            tvProfileAddress.setText(fullAddress);
        }

        if (currentStudent.getFaculty() != null) {
            tvProfileProgram.setText(currentStudent.getFaculty());
            chipFaculty.setText(currentStudent.getFaculty());
        }

        if (currentStudent.getBatch() != null) {
            tvProfileYear.setText("Batch " + currentStudent.getBatch());
        }

        if (currentStudent.getSemester() != null) {
            tvSemesterValue.setText(currentStudent.getSemester());
        }

        if (currentStudent.getEnrollmentDate() != null) {
            tvProfileGraduation.setText(currentStudent.getEnrollmentDate());
        }

        setDefaultAdvisor();
        loadProfilePhoto();
        setDefaultStats();
    }

    private String buildAddress() {
        StringBuilder address = new StringBuilder();

        if (currentStudent.getPermanentAddress() != null && !currentStudent.getPermanentAddress().isEmpty()) {
            address.append(currentStudent.getPermanentAddress());
        }

        if (currentStudent.getCity() != null && !currentStudent.getCity().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(currentStudent.getCity());
        }

        if (currentStudent.getProvince() != null && !currentStudent.getProvince().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(currentStudent.getProvince());
        }

        if (currentStudent.getPostalCode() != null && !currentStudent.getPostalCode().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(currentStudent.getPostalCode());
        }

        return address.toString();
    }

    private void setDefaultAdvisor() {
        tvProfileAdvisor.setText("Dr. Emily Thompson");
    }

    private void loadProfilePhoto() {
        if (currentStudent.getPhotoUri() != null && !currentStudent.getPhotoUri().isEmpty()) {
            Bitmap photo = StudentDatabaseHelper.getStudentPhoto(currentStudent.getPhotoUri());
            if (photo != null) {
                ivProfilePic.setImageBitmap(photo);
            }
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
    }

    private void openSettings() {

    }

    private void editProfile() {

    }

    private void showHelp() {

    }

    @Override
    public void onResume() {
        super.onResume();
        loadStudentData();
    }
}