package com.example.codeverse.Admin.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.Admin.Helpers.StaffDatabaseHelper;
import com.example.codeverse.Admin.Helpers.StudentDatabaseHelper;
import com.example.codeverse.Admin.Models.Staff;
import com.example.codeverse.Admin.Adapters.UserAdapter;
import com.example.codeverse.Admin.Models.User;
import com.example.codeverse.R;
import com.example.codeverse.Students.Models.Student;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class UserShowingFragment extends Fragment {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private List<User> allUsers;
    private List<User> filteredUsers;

    private TextInputEditText etSearch;
    private MaterialButton btnAll, btnStudents, btnStaff;
    private TextView tvUserCount;
    private LinearLayout layoutEmpty;
    private FrameLayout loadingOverlay;

    private StudentDatabaseHelper studentDbHelper;
    private StaffDatabaseHelper staffDbHelper;

    private FilterType currentFilter = FilterType.ALL;

    private enum FilterType {
        ALL, STUDENTS, STAFF
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_showing, container, false);

        initViews(view);
        initDatabase();
        setupRecyclerView();
        setupClickListeners();
        setupSearch();
        loadAllUsers();

        return view;
    }

    private void initViews(View view) {

        rvUsers = view.findViewById(R.id.rv_users);
        etSearch = view.findViewById(R.id.et_search);
        btnAll = view.findViewById(R.id.btn_all);
        btnStudents = view.findViewById(R.id.btn_students);
        btnStaff = view.findViewById(R.id.btn_staff);
        tvUserCount = view.findViewById(R.id.tv_user_count);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
    }

    private void initDatabase() {
        studentDbHelper = new StudentDatabaseHelper(getContext());
        staffDbHelper = new StaffDatabaseHelper(getContext());
    }

    private void setupRecyclerView() {
        allUsers = new ArrayList<>();
        filteredUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), filteredUsers);

        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(userAdapter);

        userAdapter.setOnUserClickListener(user -> {
            // Handle user click - navigate to user detail
        });
    }

    private void setupClickListeners() {


        btnAll.setOnClickListener(v -> {
            currentFilter = FilterType.ALL;
            updateFilterButtons();
            applyFilter();
        });

        btnStudents.setOnClickListener(v -> {
            currentFilter = FilterType.STUDENTS;
            updateFilterButtons();
            applyFilter();
        });

        btnStaff.setOnClickListener(v -> {
            currentFilter = FilterType.STAFF;
            updateFilterButtons();
            applyFilter();
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadAllUsers() {
        showLoading(true);

        new Thread(() -> {
            allUsers.clear();

            List<Student> students = studentDbHelper.getAllStudent();
            for (Student student : students) {
                User user = new User();
                user.setId(student.getId());
                user.setName(student.getFullName());
                user.setIdentifier(student.getUniversityId());
                user.setDepartment(student.getFaculty());
                user.setPhotoUri(student.getPhotoUri());
                user.setUserType(User.UserType.STUDENT);
                user.setEmail(student.getEmail());
                user.setContactNumber(student.getMobileNumber());
                user.setGender(student.getGender());
                user.setDateOfBirth(student.getDateOfBirth());
                allUsers.add(user);
            }

            List<Staff> staffList = staffDbHelper.getAllStaff();
            for (Staff staff : staffList) {
                User user = new User();
                user.setId(staff.getId());
                user.setName(staff.getFullName());
                user.setIdentifier(staff.getNicNumber());
                user.setDepartment(staff.getDepartment());
                user.setPhotoUri(staff.getPhotoUri());
                user.setUserType(User.UserType.STAFF);
                user.setEmail(staff.getEmail());
                user.setContactNumber(staff.getContactNumber());
                user.setGender(staff.getGender());
                user.setDateOfBirth(staff.getDateOfBirth());
                allUsers.add(user);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    showLoading(false);
                    applyFilter();
                });
            }
        }).start();
    }

    private void updateFilterButtons() {
        btnAll.setBackgroundTintList(getContext().getColorStateList(
                currentFilter == FilterType.ALL ? R.color.DarkBlue : R.color.white));
        btnAll.setTextColor(getContext().getColor(
                currentFilter == FilterType.ALL ? R.color.white : R.color.DarkBlue));

        btnStudents.setBackgroundTintList(getContext().getColorStateList(
                currentFilter == FilterType.STUDENTS ? R.color.DarkBlue : R.color.white));
        btnStudents.setTextColor(getContext().getColor(
                currentFilter == FilterType.STUDENTS ? R.color.white : R.color.DarkBlue));

        btnStaff.setBackgroundTintList(getContext().getColorStateList(
                currentFilter == FilterType.STAFF ? R.color.DarkBlue : R.color.white));
        btnStaff.setTextColor(getContext().getColor(
                currentFilter == FilterType.STAFF ? R.color.white : R.color.DarkBlue));
    }

    private void applyFilter() {
        filteredUsers.clear();

        for (User user : allUsers) {
            boolean matchesFilter = false;

            switch (currentFilter) {
                case ALL:
                    matchesFilter = true;
                    break;
                case STUDENTS:
                    matchesFilter = user.getUserType() == User.UserType.STUDENT;
                    break;
                case STAFF:
                    matchesFilter = user.getUserType() == User.UserType.STAFF;
                    break;
            }

            if (matchesFilter) {
                filteredUsers.add(user);
            }
        }

        updateUI();
    }

    private void searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            applyFilter();
            return;
        }

        List<User> searchResults = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        for (User user : allUsers) {
            boolean matchesFilter = false;

            switch (currentFilter) {
                case ALL:
                    matchesFilter = true;
                    break;
                case STUDENTS:
                    matchesFilter = user.getUserType() == User.UserType.STUDENT;
                    break;
                case STAFF:
                    matchesFilter = user.getUserType() == User.UserType.STAFF;
                    break;
            }

            if (matchesFilter) {
                if (user.getName().toLowerCase().contains(lowerQuery) ||
                        user.getIdentifier().toLowerCase().contains(lowerQuery) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerQuery)) ||
                        (user.getDepartment() != null && user.getDepartment().toLowerCase().contains(lowerQuery))) {
                    searchResults.add(user);
                }
            }
        }

        filteredUsers.clear();
        filteredUsers.addAll(searchResults);
        updateUI();
    }

    private void updateUI() {
        if (userAdapter != null) {
            userAdapter.notifyDataSetChanged();
        }

        updateUserCount();

        if (filteredUsers.isEmpty()) {
            rvUsers.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvUsers.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void updateUserCount() {
        String countText = "Total Users: " + filteredUsers.size();
        tvUserCount.setText(countText);
    }

    private void showLoading(boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllUsers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (studentDbHelper != null) {
            studentDbHelper.close();
        }
        if (staffDbHelper != null) {
            staffDbHelper.close();
        }
    }
}