package com.example.codeverse;

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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.example.codeverse.Admin.Helpers.StaffDatabaseHelper;
import com.example.codeverse.Admin.Models.Staff;

public class LecturerProfile extends Fragment {

    private TextView tvLecturerName, tvLecturerId, tvLecturerEmail, tvLecturerContact;
    private TextView tvLecturerNic, tvLecturerPosition, tvTeachingSubject, tvLecturerEducation;
    private TextView tvUniversity, tvFieldOfStudy, tvExperienceYears, tvSubjectsCount, tvGraduationYear;
    private ImageView ivLecturerPhoto, ivBack, ivEdit;
    private Chip chipDepartment;
    private MaterialCardView cvBack;

    private StaffDatabaseHelper staffDatabaseHelper;
    private long lecturerId = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecturer_profile, container, false);

        initViews(view);
        setupDatabase();
        setupClickListeners();
        loadLecturerData();

        return view;
    }

    private void initViews(View view) {
        tvLecturerName = view.findViewById(R.id.tv_lecturer_name);
        tvLecturerId = view.findViewById(R.id.tv_lecturer_id);
        tvLecturerEmail = view.findViewById(R.id.tv_lecturer_email);
        tvLecturerContact = view.findViewById(R.id.tv_lecturer_contact);
        tvLecturerNic = view.findViewById(R.id.tv_lecturer_nic);
        tvLecturerPosition = view.findViewById(R.id.tv_lecturer_position);
        tvTeachingSubject = view.findViewById(R.id.tv_teaching_subject);
        tvLecturerEducation = view.findViewById(R.id.tv_lecturer_education);
        tvUniversity = view.findViewById(R.id.tv_university);
        tvFieldOfStudy = view.findViewById(R.id.tv_field_of_study);
        tvExperienceYears = view.findViewById(R.id.tv_experience_years);
        tvSubjectsCount = view.findViewById(R.id.tv_subjects_count);
        tvGraduationYear = view.findViewById(R.id.tv_graduation_year);

        ivLecturerPhoto = view.findViewById(R.id.iv_lecturer_photo);
        ivBack = view.findViewById(R.id.iv_back);
        ivEdit = view.findViewById(R.id.iv_edit);

        chipDepartment = view.findViewById(R.id.chip_department);
        cvBack = view.findViewById(R.id.cv_back);
    }

    private void setupDatabase() {
        staffDatabaseHelper = new StaffDatabaseHelper(getContext());
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        ivEdit.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit functionality", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadLecturerData() {
        try {
            Staff staff = staffDatabaseHelper.getStaffById(lecturerId);

            if (staff != null) {
                LecturerModel lecturer = convertStaffToLecturer(staff);
                displayLecturerData(lecturer);
            } else {
                showNoDataMessage();
            }
        } catch (Exception e) {
            Log.e("LecturerProfile", "Error loading data: " + e.getMessage());
            showErrorMessage();
        }
    }

    private LecturerModel convertStaffToLecturer(Staff staff) {
        LecturerModel lecturer = new LecturerModel();
        lecturer.setId(staff.getId());
        lecturer.setFullName(staff.getFullName());
        lecturer.setEmail(staff.getEmail());
        lecturer.setContactNumber(staff.getContactNumber());
        lecturer.setNicNumber(staff.getNicNumber());
        lecturer.setGender(staff.getGender());
        lecturer.setDateOfBirth(staff.getDateOfBirth());
        lecturer.setPhotoUri(staff.getPhotoUri());
        lecturer.setPosition(staff.getPosition());
        lecturer.setDepartment(staff.getDepartment());
        lecturer.setTeachingSubject(staff.getTeachingSubject());
        lecturer.setProgramCoordinating(staff.getProgramCoordinating());
        lecturer.setPassword(staff.getPassword());
        lecturer.setHighestQualification(staff.getHighestQualification());
        lecturer.setFieldOfStudy(staff.getFieldOfStudy());
        lecturer.setUniversity(staff.getUniversity());
        lecturer.setGraduationYear(staff.getGraduationYear());
        lecturer.setExperienceYears(staff.getExperienceYears());
        lecturer.setCreatedAt(staff.getCreatedAt());
        lecturer.setUpdatedAt(staff.getUpdatedAt());
        return lecturer;
    }

    private void displayLecturerData(LecturerModel lecturer) {
        tvLecturerName.setText(lecturer.getFullName() != null ? lecturer.getFullName() : "N/A");
        tvLecturerId.setText("ID: " + lecturer.getId());
        tvLecturerEmail.setText(lecturer.getEmail() != null ? lecturer.getEmail() : "N/A");
        tvLecturerContact.setText(lecturer.getContactNumber() != null ? lecturer.getContactNumber() : "N/A");
        tvLecturerNic.setText(lecturer.getNicNumber() != null ? lecturer.getNicNumber() : "N/A");
        tvLecturerPosition.setText(lecturer.getPosition() != null ? lecturer.getPosition() : "N/A");
        tvTeachingSubject.setText(lecturer.getTeachingSubject() != null ? lecturer.getTeachingSubject() : "N/A");
        tvLecturerEducation.setText(lecturer.getHighestQualification() != null ? lecturer.getHighestQualification() : "N/A");
        tvUniversity.setText(lecturer.getUniversity() != null ? lecturer.getUniversity() : "N/A");
        tvFieldOfStudy.setText(lecturer.getFieldOfStudy() != null ? lecturer.getFieldOfStudy() : "N/A");
        tvExperienceYears.setText(lecturer.getExperienceYears() != null ? lecturer.getExperienceYears() : "0");
        tvGraduationYear.setText(lecturer.getGraduationYear() != null ? lecturer.getGraduationYear() : "-");

        chipDepartment.setText(lecturer.getDepartment() != null ? lecturer.getDepartment() : "N/A");

        tvSubjectsCount.setText("1");

        loadProfilePhoto(lecturer.getPhotoUri());
    }

    private void loadProfilePhoto(String photoUri) {
        if (photoUri != null && !photoUri.isEmpty()) {
            try {
                Bitmap bitmap = StaffDatabaseHelper.getStaffPhoto(photoUri);
                if (bitmap != null) {
                    ivLecturerPhoto.setImageBitmap(bitmap);
                } else {
                    setDefaultPhoto();
                }
            } catch (Exception e) {
                Log.e("LecturerProfile", "Error loading photo: " + e.getMessage());
                setDefaultPhoto();
            }
        } else {
            setDefaultPhoto();
        }
    }

    private void setDefaultPhoto() {
        ivLecturerPhoto.setImageResource(R.drawable.ic_person);
    }

    private void showNoDataMessage() {
        tvLecturerName.setText("No Data Found");
        tvLecturerId.setText("ID: N/A");
        tvLecturerEmail.setText("N/A");
        tvLecturerContact.setText("N/A");
        tvLecturerNic.setText("N/A");
        tvLecturerPosition.setText("N/A");
        tvTeachingSubject.setText("N/A");
        tvLecturerEducation.setText("N/A");
        tvUniversity.setText("N/A");
        tvFieldOfStudy.setText("N/A");
        tvExperienceYears.setText("0");
        tvGraduationYear.setText("-");
        chipDepartment.setText("N/A");
        tvSubjectsCount.setText("0");

        Toast.makeText(getContext(), "No lecturer data found", Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessage() {
        Toast.makeText(getContext(), "Error loading lecturer data", Toast.LENGTH_SHORT).show();
    }

    public void setLecturerId(long lecturerId) {
        this.lecturerId = lecturerId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (staffDatabaseHelper != null) {
            staffDatabaseHelper.close();
        }
    }
}