package com.example.codeverse;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.codeverse.StudentAdapter;
import com.example.codeverse.Admin.Helpers.StudentDatabaseHelper;
import com.example.codeverse.R;
import com.example.codeverse.Students.Models.Student;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class StudentListFragment extends Fragment implements StudentAdapter.OnStudentActionListener {

    private RecyclerView rvStudents;
    private StudentAdapter studentAdapter;
    private StudentDatabaseHelper dbHelper;
    private SwipeRefreshLayout swipeRefresh;
    private TextInputEditText etSearch;
    private FloatingActionButton fabAddStudent;

    private List<Student> allStudents;
    private List<Student> filteredStudents;

    public StudentListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearchFilter();
        setupSwipeRefresh();
        loadStudents();

        fabAddStudent.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Add Student clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void initViews(View view) {
        rvStudents = view.findViewById(R.id.rvStudents);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        etSearch = view.findViewById(R.id.etSearch);
        fabAddStudent = view.findViewById(R.id.fabAddStudent);

        dbHelper = new StudentDatabaseHelper(getContext());
        allStudents = new ArrayList<>();
        filteredStudents = new ArrayList<>();
    }

    private void setupRecyclerView() {
        studentAdapter = new StudentAdapter(filteredStudents, this);
        rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStudents.setAdapter(studentAdapter);
    }

    private void setupSearchFilter() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::loadStudents);
        swipeRefresh.setColorSchemeResources(R.color.primary_color);
    }

    private void loadStudents() {
        swipeRefresh.setRefreshing(true);

        new Thread(() -> {
            allStudents = dbHelper.getAllStudent();

            getActivity().runOnUiThread(() -> {
                filteredStudents.clear();
                filteredStudents.addAll(allStudents);
                studentAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            });
        }).start();
    }

    private void filterStudents(String query) {
        filteredStudents.clear();

        if (query.isEmpty()) {
            filteredStudents.addAll(allStudents);
        } else {
            for (Student student : allStudents) {
                if (student.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                        student.getUniversityId().toLowerCase().contains(query.toLowerCase()) ||
                        (student.getFaculty() != null && student.getFaculty().toLowerCase().contains(query.toLowerCase()))) {
                    filteredStudents.add(student);
                }
            }
        }

        studentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEditStudent(Student student) {
        Toast.makeText(getContext(), "Edit: " + student.getFullName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteStudent(Student student) {
        new Thread(() -> {
            dbHelper.deleteStudentWithPhotoCleanup(student);

            getActivity().runOnUiThread(() -> {
                allStudents.remove(student);
                filteredStudents.remove(student);
                studentAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Student deleted", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    @Override
    public void onStudentClick(Student student) {
        Toast.makeText(getContext(), "Clicked: " + student.getFullName(), Toast.LENGTH_SHORT).show();
    }
}