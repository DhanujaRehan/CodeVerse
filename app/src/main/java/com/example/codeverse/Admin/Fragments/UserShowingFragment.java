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
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class UserShowingFragment extends Fragment {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private List<User> allUsers;
    private List<User> filteredUsers;
    private TextInputEditText etSearch;
    private MaterialButton btnAll;
    private MaterialButton btnStudents;
    private MaterialButton btnStaff;
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
        userAdapter.setOnUserClickListener(this::handleUserClick);
    }

    private void handleUserClick(User user) {
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadAllUsers() {
        showLoading(true);
        new Thread(this::loadUsersFromDatabase).start();
    }

    private void loadUsersFromDatabase() {
        allUsers.clear();
        loadStudents();
        loadStaff();

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                showLoading(false);
                applyFilter();
            });
        }
    }

    private void loadStudents() {
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
            user.setFaculty(student.getFaculty());
            user.setBatch(student.getBatch());
            allUsers.add(user);
        }
    }

    private void loadStaff() {
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
            user.setPosition(staff.getPosition());
            allUsers.add(user);
        }
    }

    private void updateFilterButtons() {
        updateButtonAppearance(btnAll, currentFilter == FilterType.ALL);
        updateButtonAppearance(btnStudents, currentFilter == FilterType.STUDENTS);
        updateButtonAppearance(btnStaff, currentFilter == FilterType.STAFF);
    }

    private void updateButtonAppearance(MaterialButton button, boolean isSelected) {
        button.setBackgroundTintList(getContext().getColorStateList(
                isSelected ? R.color.DarkBlue : R.color.white));
        button.setTextColor(getContext().getColor(
                isSelected ? R.color.white : R.color.DarkBlue));
    }

    private void applyFilter() {
        filteredUsers.clear();
        for (User user : allUsers) {
            if (matchesCurrentFilter(user)) {
                filteredUsers.add(user);
            }
        }
        updateUI();
    }

    private boolean matchesCurrentFilter(User user) {
        switch (currentFilter) {
            case ALL:
                return true;
            case STUDENTS:
                return user.getUserType() == User.UserType.STUDENT;
            case STAFF:
                return user.getUserType() == User.UserType.STAFF;
            default:
                return false;
        }
    }

    private void searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            applyFilter();
            return;
        }

        List<User> searchResults = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        for (User user : allUsers) {
            if (matchesCurrentFilter(user) && matchesSearchQuery(user, lowerQuery)) {
                searchResults.add(user);
            }
        }

        filteredUsers.clear();
        filteredUsers.addAll(searchResults);
        updateUI();
    }

    private boolean matchesSearchQuery(User user, String lowerQuery) {
        return user.getName().toLowerCase().contains(lowerQuery) ||
                user.getIdentifier().toLowerCase().contains(lowerQuery) ||
                (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerQuery)) ||
                (user.getDepartment() != null && user.getDepartment().toLowerCase().contains(lowerQuery)) ||
                (user.getFaculty() != null && user.getFaculty().toLowerCase().contains(lowerQuery)) ||
                (user.getBatch() != null && user.getBatch().toLowerCase().contains(lowerQuery)) ||
                (user.getPosition() != null && user.getPosition().toLowerCase().contains(lowerQuery));
    }

    private void updateUI() {
        if (userAdapter != null) {
            userAdapter.notifyDataSetChanged();
        }
        updateUserCount();
        updateEmptyState();
    }

    private void updateEmptyState() {
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