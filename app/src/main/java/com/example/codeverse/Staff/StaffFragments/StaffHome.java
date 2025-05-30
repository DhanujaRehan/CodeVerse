package com.example.codeverse.Staff.StaffFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Staff.Adapters.StudentAdapter;
import com.example.codeverse.Staff.Models.StudentModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * StaffHome Fragment
 */
public class StaffHome extends Fragment {

    // UI Components
    private RecyclerView rvStudents;
    private AutoCompleteTextView dropdownYear, dropdownDepartment, dropdownBadge;
    private TextView tvStudentCount;
    private View layoutEmptyState;
    private MaterialButton btnApplyFilters;
    private FloatingActionButton fabAddStudent;

    // Adapters
    private StudentAdapter studentAdapter;

    // Data
    private List<StudentModel> allStudents = new ArrayList<>();
    private List<StudentModel> filteredStudents = new ArrayList<>();
    private String selectedYear = "";
    private String selectedDepartment = "";
    private String selectedBadge = "";

    // Root view reference
    private View rootView;

    public StaffHome() {
        // Required empty public constructor
    }

    public static StaffHome newInstance() {
        return new StaffHome();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_staff_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        initViews();

        // Setup dropdown menus for filters
        setupDropdowns();

        // Setup RecyclerView for students
        setupStudentsList();

        // Setup click listeners
        setupListeners();

        // Load sample data
        loadSampleData();
    }

    /**
     * Initialize UI components
     */
    private void initViews() {
        // RecyclerView
        rvStudents = rootView.findViewById(R.id.rv_students);

        // Dropdowns
        dropdownYear = rootView.findViewById(R.id.dropdown_year);
        dropdownDepartment = rootView.findViewById(R.id.dropdown_department);
        dropdownBadge = rootView.findViewById(R.id.dropdown_badge);

        // TextViews
        tvStudentCount = rootView.findViewById(R.id.tv_student_count);

        // Layouts
        layoutEmptyState = rootView.findViewById(R.id.layout_empty_state);

        // Buttons
        btnApplyFilters = rootView.findViewById(R.id.btn_apply_filters);
    }

    /**
     * Setup dropdown menus for filters
     */
    private void setupDropdowns() {
        // Year dropdown
        String[] years = {"All Years", "1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year"};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                getContext(), R.layout.dropdown_item, years);
        dropdownYear.setAdapter(yearAdapter);
        dropdownYear.setText(years[0], false);

        // Department dropdown
        String[] departments = {"All Departments", "Computer Science", "Electrical Engineering",
                "Mechanical Engineering", "Civil Engineering", "Chemistry", "Physics", "Mathematics"};
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(
                getContext(), R.layout.dropdown_item, departments);
        dropdownDepartment.setAdapter(departmentAdapter);
        dropdownDepartment.setText(departments[0], false);

        // Badge dropdown
        String[] badges = {"All Badges", "Honor Roll", "Dean's List", "President's List",
                "Scholarship", "Academic Warning", "At Risk"};
        ArrayAdapter<String> badgeAdapter = new ArrayAdapter<>(
                getContext(), R.layout.dropdown_item, badges);
        dropdownBadge.setAdapter(badgeAdapter);
        dropdownBadge.setText(badges[0], false);
    }

    /**
     * Setup RecyclerView for students
     */
    private void setupStudentsList() {
        // Initialize adapter
        studentAdapter = new StudentAdapter(filteredStudents, this::onStudentAction);

        // Configure RecyclerView
        rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStudents.setAdapter(studentAdapter);
        rvStudents.setNestedScrollingEnabled(false);
    }

    /**
     * Setup click listeners
     */
    private void setupListeners() {
        // Apply filters button
        btnApplyFilters.setOnClickListener(v -> applyFilters());

        // Quick actions
        rootView.findViewById(R.id.card_schedule).setOnClickListener(v ->
                navigateToStaffSchedule());

        rootView.findViewById(R.id.card_attendance).setOnClickListener(v ->
                Toast.makeText(getContext(), "Attendance feature coming soon", Toast.LENGTH_SHORT).show());

        rootView.findViewById(R.id.card_grades).setOnClickListener(v ->
                Toast.makeText(getContext(), "Grades feature coming soon", Toast.LENGTH_SHORT).show());

        rootView.findViewById(R.id.card_messages).setOnClickListener(v ->
                Toast.makeText(getContext(), "Messages feature coming soon", Toast.LENGTH_SHORT).show());

        // Profile image
        rootView.findViewById(R.id.cv_profile).setOnClickListener(v ->
                Toast.makeText(getContext(), "Profile feature coming soon", Toast.LENGTH_SHORT).show());

        // Add student FAB
        if (fabAddStudent != null) {
            fabAddStudent.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Add student feature coming soon", Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Navigate to Staff Schedule Fragment
     */
    private void navigateToStaffSchedule() {
        try {
            StaffSchedule staffScheduleFragment = new StaffSchedule();

            // Navigate to staff schedule fragment
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, staffScheduleFragment) // Use your container ID
                    .addToBackStack(null)
                    .commit();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Schedule not available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle student card actions
     */
    private void onStudentAction(String action, StudentModel student) {
        switch (action) {
            case "profile":
                Toast.makeText(getContext(), "Viewing profile of " + student.getName(), Toast.LENGTH_SHORT).show();
                break;

            case "grades":
                Toast.makeText(getContext(), "Viewing grades of " + student.getName(), Toast.LENGTH_SHORT).show();
                break;

            case "message":
                Toast.makeText(getContext(), "Messaging " + student.getName(), Toast.LENGTH_SHORT).show();
                break;

            case "more":

                Toast.makeText(getContext(), "More options for " + student.getName(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Apply filters to student list
     */
    private void applyFilters() {

        selectedYear = dropdownYear.getText().toString();
        selectedDepartment = dropdownDepartment.getText().toString();
        selectedBadge = dropdownBadge.getText().toString();

        filteredStudents.clear();

        for (StudentModel student : allStudents) {
            boolean yearMatch = selectedYear.equals("All Years") || student.getYear().equals(selectedYear);
            boolean deptMatch = selectedDepartment.equals("All Departments") || student.getDepartment().equals(selectedDepartment);
            boolean badgeMatch = selectedBadge.equals("All Badges") || student.getBadge().equals(selectedBadge);

            if (yearMatch && deptMatch && badgeMatch) {
                filteredStudents.add(student);
            }
        }

        studentAdapter.notifyDataSetChanged();
        updateStudentCount();
        updateEmptyState();
        Toast.makeText(getContext(), filteredStudents.size() + " students found", Toast.LENGTH_SHORT).show();
    }


    private void updateStudentCount() {
        tvStudentCount.setText(filteredStudents.size() + " Students");
    }


    private void updateEmptyState() {
        if (filteredStudents.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvStudents.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvStudents.setVisibility(View.VISIBLE);
        }
    }

    private void loadSampleData() {
        allStudents.add(new StudentModel(
                "STU20230001",
                "John Smith",
                "Computer Science",
                "2nd Year",
                "Honor Roll",
                R.drawable.ic_person));

        allStudents.add(new StudentModel(
                "STU20230002",
                "Emily Johnson",
                "Electrical Engineering",
                "3rd Year",
                "Dean's List",
                R.drawable.ic_person));

        allStudents.add(new StudentModel(
                "STU20230003",
                "Michael Brown",
                "Mechanical Engineering",
                "1st Year",
                "Scholarship",
                R.drawable.ic_person));

        allStudents.add(new StudentModel(
                "STU20230004",
                "Jessica Davis",
                "Computer Science",
                "4th Year",
                "President's List",
                R.drawable.ic_person));

        allStudents.add(new StudentModel(
                "STU20230005",
                "Daniel Wilson",
                "Physics",
                "2nd Year",
                "Academic Warning",
                R.drawable.ic_person));

        allStudents.add(new StudentModel(
                "STU20230006",
                "Sophia Martinez",
                "Mathematics",
                "3rd Year",
                "Honor Roll",
                R.drawable.ic_person));

        allStudents.add(new StudentModel(
                "STU20230007",
                "David Taylor",
                "Civil Engineering",
                "5th Year",
                "Scholarship",
                R.drawable.ic_person));

        allStudents.add(new StudentModel(
                "STU20230008",
                "Olivia Anderson",
                "Chemistry",
                "1st Year",
                "At Risk",
                R.drawable.ic_person));

        filteredStudents.addAll(allStudents);

        studentAdapter.notifyDataSetChanged();

        updateStudentCount();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
        studentAdapter = null;
        allStudents = null;
        filteredStudents = null;
    }
}