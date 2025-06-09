package com.example.codeverse;

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

import com.example.codeverse.Admin.Helpers.StaffDatabaseHelper;
import com.example.codeverse.Admin.Models.Staff;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class LecturerProfile extends Fragment {

    private ImageView ivBack, ivEdit, ivLecturerPhoto;
    private TextView tvLecturerName, tvLecturerId, tvExperienceYears, tvSubjectsCount, tvGraduationYear;
    private TextView tvLecturerEmail, tvLecturerContact, tvLecturerNic, tvLecturerPosition;
    private TextView tvTeachingSubject, tvLecturerEducation;
    private Chip chipDepartment;
    private ChipGroup chipGroupSpecializations;
    private MaterialButton btnViewTimetable;
    private StaffDatabaseHelper dbHelper;
    private long lecturerId;
    private Staff currentLecturer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecturer_profile, container, false);
        initViews(view);
        setupClickListeners();
        loadLecturerData();
        return view;
    }

    private void initViews(View view) {
        ivBack = view.findViewById(R.id.iv_back);
        ivEdit = view.findViewById(R.id.iv_edit);
        ivLecturerPhoto = view.findViewById(R.id.iv_lecturer_photo);

        tvLecturerName = view.findViewById(R.id.tv_lecturer_name);
        tvLecturerId = view.findViewById(R.id.tv_lecturer_id);
        tvExperienceYears = view.findViewById(R.id.tv_experience_years);
        tvSubjectsCount = view.findViewById(R.id.tv_subjects_count);
        tvGraduationYear = view.findViewById(R.id.tv_graduation_year);

        tvLecturerEmail = view.findViewById(R.id.tv_lecturer_email);
        tvLecturerContact = view.findViewById(R.id.tv_lecturer_contact);
        tvLecturerNic = view.findViewById(R.id.tv_lecturer_nic);
        tvLecturerPosition = view.findViewById(R.id.tv_lecturer_position);
        tvTeachingSubject = view.findViewById(R.id.tv_teaching_subject);
        tvLecturerEducation = view.findViewById(R.id.tv_lecturer_education);

        chipDepartment = view.findViewById(R.id.chip_department);
        /*chipGroupSpecializations = view.findViewById(R.id.chip_group_specializations);
        btnViewTimetable = view.findViewById(R.id.btn_view_timetable);*/

        dbHelper = new StaffDatabaseHelper(getContext());

        Bundle args = getArguments();
        if (args != null) {
            lecturerId = args.getLong("lecturer_id", -1);
        }
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditProfile();
            }
        });

        /*btnViewTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimetable();
            }
        });*/
    }

    private void loadLecturerData() {
        if (lecturerId == -1) {
            loadFirstLecturer();
        } else {
            currentLecturer = dbHelper.getStaffById(lecturerId);
            if (currentLecturer != null && "Lecturer".equalsIgnoreCase(currentLecturer.getPosition())) {
                displayLecturerData(currentLecturer);
            } else {
                loadFirstLecturer();
            }
        }
    }

    private void loadFirstLecturer() {
        List<Staff> allStaff = dbHelper.getAllStaff();
        for (Staff staff : allStaff) {
            if ("Lecturer".equalsIgnoreCase(staff.getPosition())) {
                currentLecturer = staff;
                lecturerId = staff.getId();
                displayLecturerData(staff);
                return;
            }
        }
        showNoLecturerFound();
    }

    private void displayLecturerData(Staff lecturer) {
        tvLecturerName.setText(lecturer.getFullName() != null ? lecturer.getFullName() : "Unknown");
        tvLecturerId.setText("ID: LEC" + String.format("%06d", lecturer.getId()));
        tvLecturerEmail.setText(lecturer.getEmail() != null ? lecturer.getEmail() : "Not available");
        tvLecturerContact.setText(lecturer.getContactNumber() != null ? lecturer.getContactNumber() : "Not available");
        tvLecturerNic.setText(lecturer.getNicNumber() != null ? lecturer.getNicNumber() : "Not available");
        tvLecturerPosition.setText(lecturer.getPosition() != null ? lecturer.getPosition() : "Lecturer");

        String department = lecturer.getDepartment() != null ? lecturer.getDepartment() : "Computer Science";
        chipDepartment.setText(department);

        tvTeachingSubject.setText(lecturer.getTeachingSubject() != null ? lecturer.getTeachingSubject() : "Not specified");

        String education = lecturer.getHighestQualification();
        if (education != null && lecturer.getUniversity() != null) {
            education += ", " + lecturer.getUniversity();
        }
        tvLecturerEducation.setText(education != null ? education : "Not available");

        String experienceYears = lecturer.getExperienceYears();
        tvExperienceYears.setText(experienceYears != null ? experienceYears : "0");

        String graduationYear = lecturer.getGraduationYear();
        tvGraduationYear.setText(graduationYear != null ? graduationYear : "N/A");

        int subjectCount = calculateSubjectCount(lecturer.getTeachingSubject());
        tvSubjectsCount.setText(String.valueOf(subjectCount));

        loadLecturerPhoto(lecturer);
        setupSpecializationChips(lecturer);
    }

    private void loadLecturerPhoto(Staff lecturer) {
        if (lecturer.getPhotoUri() != null && !lecturer.getPhotoUri().isEmpty()) {
            Bitmap photo = StaffDatabaseHelper.getStaffPhoto(lecturer.getPhotoUri());
            if (photo != null) {
                ivLecturerPhoto.setImageBitmap(photo);
            } else {
                ivLecturerPhoto.setImageResource(R.drawable.ic_person);
            }
        } else {
            ivLecturerPhoto.setImageResource(R.drawable.ic_person);
        }
    }

    private void setupSpecializationChips(Staff lecturer) {
        chipGroupSpecializations.removeAllViews();

        String fieldOfStudy = lecturer.getFieldOfStudy();
        String teachingSubject = lecturer.getTeachingSubject();

        if (fieldOfStudy != null && !fieldOfStudy.isEmpty()) {
            addSpecializationChip(fieldOfStudy);
        }

        if (teachingSubject != null && !teachingSubject.isEmpty()) {
            String[] subjects = teachingSubject.split(",");
            for (String subject : subjects) {
                if (!subject.trim().isEmpty()) {
                    addSpecializationChip(subject.trim());
                }
            }
        }

        if (chipGroupSpecializations.getChildCount() == 0) {
            addSpecializationChip("General Teaching");
        }
    }

    private void addSpecializationChip(String text) {
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setTextColor(getResources().getColor(R.color.white));
        chip.setChipBackgroundColorResource(R.color.DarkBlue);
        chip.setChipStrokeColorResource(R.color.DarkBlue2);
        chip.setChipStrokeWidth(2);
        chipGroupSpecializations.addView(chip);
    }

    private int calculateSubjectCount(String teachingSubject) {
        if (teachingSubject == null || teachingSubject.trim().isEmpty()) {
            return 0;
        }
        return teachingSubject.split(",").length;
    }

    private void openEditProfile() {
        if (currentLecturer != null) {
            Toast.makeText(getContext(), "Edit profile functionality", Toast.LENGTH_SHORT).show();
        }
    }

    /*private void openTimetable() {
        if (currentLecturer != null) {
            Bundle args = new Bundle();
            args.putLong("lecturer_id", currentLecturer.getId());

            TimetableDownloadFragment timetableFragment = new TimetableDownloadFragment();
            timetableFragment.setArguments(args);

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, timetableFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }*/

    private void showNoLecturerFound() {
        tvLecturerName.setText("No Lecturer Found");
        tvLecturerId.setText("ID: N/A");
        tvLecturerEmail.setText("No lecturer data available");
        tvLecturerContact.setText("Please add lecturer data");
        tvLecturerNic.setText("N/A");
        Toast.makeText(getContext(), "No lecturer found in database", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    /*public static LecturerProfileFragment newInstance(long lecturerId) {
        LecturerProfileFragment fragment = new LecturerProfileFragment();
        Bundle args = new Bundle();
        args.putLong("lecturer_id", lecturerId);
        fragment.setArguments(args);
        return fragment;
    }*/
}