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

        // Initialize lists first
        allStudents = new ArrayList<>();
        filteredStudents = new ArrayList<>();

        // Initialize database helper
        if (getContext() != null) {
            dbHelper = new StudentDatabaseHelper(getContext());
        }
    }

    private void setupRecyclerView() {
        if (getContext() != null && rvStudents != null) {
            studentAdapter = new StudentAdapter(filteredStudents, this);
            rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));
            rvStudents.setAdapter(studentAdapter);
        }
    }

    private void setupSearchFilter() {
        if (etSearch != null) {
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
    }

    private void setupSwipeRefresh() {
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::loadStudents);
            swipeRefresh.setColorSchemeResources(R.color.primary_color);
        }
    }

    private void loadStudents() {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(true);
        }

        // Check if dbHelper is initialized and context is available
        if (dbHelper == null && getContext() != null) {
            dbHelper = new StudentDatabaseHelper(getContext());
        }

        if (dbHelper != null) {
            new Thread(() -> {
                try {
                    List<Student> students = dbHelper.getAllStudent();

                    // Check if fragment is still attached before updating UI
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        getActivity().runOnUiThread(() -> {
                            if (allStudents != null && filteredStudents != null) {
                                allStudents.clear();
                                allStudents.addAll(students != null ? students : new ArrayList<>());

                                filteredStudents.clear();
                                filteredStudents.addAll(allStudents);

                                if (studentAdapter != null) {
                                    studentAdapter.notifyDataSetChanged();
                                }
                            }

                            if (swipeRefresh != null) {
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle database error gracefully
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        getActivity().runOnUiThread(() -> {
                            if (swipeRefresh != null) {
                                swipeRefresh.setRefreshing(false);
                            }
                            Toast.makeText(getContext(), "Error loading students", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }).start();
        } else {
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
        }
    }

    private void filterStudents(String query) {
        if (filteredStudents == null || allStudents == null) {
            return;
        }

        filteredStudents.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredStudents.addAll(allStudents);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (Student student : allStudents) {
                if (student != null && (
                        (student.getFullName() != null && student.getFullName().toLowerCase().contains(lowerQuery)) ||
                                (student.getUniversityId() != null && student.getUniversityId().toLowerCase().contains(lowerQuery)) ||
                                (student.getFaculty() != null && student.getFaculty().toLowerCase().contains(lowerQuery)))) {
                    filteredStudents.add(student);
                }
            }
        }

        if (studentAdapter != null) {
            studentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEditStudent(Student student) {
        if (student != null && getContext() != null) {
            Toast.makeText(getContext(), "Edit: " + student.getFullName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteStudent(Student student) {
        if (student == null || dbHelper == null) {
            return;
        }

        new Thread(() -> {
            try {
                dbHelper.deleteStudentWithPhotoCleanup(student);

                // Check if fragment is still attached before updating UI
                if (getActivity() != null && !getActivity().isFinishing()) {
                    getActivity().runOnUiThread(() -> {
                        if (allStudents != null && filteredStudents != null) {
                            allStudents.remove(student);
                            filteredStudents.remove(student);

                            if (studentAdapter != null) {
                                studentAdapter.notifyDataSetChanged();
                            }
                        }

                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Student deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null && !getActivity().isFinishing()) {
                    getActivity().runOnUiThread(() -> {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error deleting student", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onStudentClick(Student student) {
        if (student != null && getContext() != null) {
            Toast.makeText(getContext(), "Clicked: " + student.getFullName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up references to prevent memory leaks
        studentAdapter = null;
        rvStudents = null;
        swipeRefresh = null;
        etSearch = null;
        fabAddStudent = null;
    }
}