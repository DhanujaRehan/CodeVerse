package com.example.codeverse;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.codeverse.Admin.Helpers.StudentDatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentListFragment extends Fragment implements StudentAdapter.OnStudentActionListener {

    private RecyclerView rvStudents;
    private StudentAdapter studentAdapter;
    private StudentDatabaseHelper dbHelper;
    private SwipeRefreshLayout swipeRefresh;
    private TextInputEditText etSearch;
    private FloatingActionButton fabAddStudent;

    private List<StudentModel> allStudents;
    private List<StudentModel> filteredStudents;
    private ExecutorService executorService;

    public StudentListFragment() {
    }

    public static StudentListFragment newInstance() {
        return new StudentListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newFixedThreadPool(2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearchFilter();
        setupSwipeRefresh();
        setupFab();
        loadStudents();

        return view;
    }

    private void initViews(View view) {
        rvStudents = view.findViewById(R.id.rvStudents);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        etSearch = view.findViewById(R.id.etSearch);
        fabAddStudent = view.findViewById(R.id.fabAddStudent);

        allStudents = new ArrayList<>();
        filteredStudents = new ArrayList<>();

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
            swipeRefresh.setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light
            );
        }
    }

    private void setupFab() {
        if (fabAddStudent != null) {
            fabAddStudent.setOnClickListener(v -> {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Add Student feature coming soon!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadStudents() {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(true);
        }

        if (dbHelper == null && getContext() != null) {
            dbHelper = new StudentDatabaseHelper(getContext());
        }

        if (dbHelper != null && executorService != null) {
            executorService.execute(() -> {
                try {
                    List<StudentModel> students = convertToStudentModel(dbHelper.getAllStudent());

                    if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            updateStudentLists(students);
                            if (swipeRefresh != null) {
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            if (swipeRefresh != null) {
                                swipeRefresh.setRefreshing(false);
                            }
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Error loading students", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } else {
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
        }
    }

    private List<StudentModel> convertToStudentModel(List<com.example.codeverse.Students.Models.Student> students) {
        List<StudentModel> studentModels = new ArrayList<>();
        for (com.example.codeverse.Students.Models.Student student : students) {
            StudentModel model = new StudentModel();
            model.setId(student.getId());
            model.setFullName(student.getFullName());
            model.setUniversityId(student.getUniversityId());
            model.setFaculty(student.getFaculty());
            model.setBatch(student.getBatch());
            model.setMobileNumber(student.getMobileNumber());
            model.setEmail(student.getEmail());
            model.setPhotoUri(student.getPhotoUri());
            studentModels.add(model);
        }
        return studentModels;
    }

    private void updateStudentLists(List<StudentModel> students) {
        if (allStudents != null && filteredStudents != null) {
            allStudents.clear();
            if (students != null) {
                allStudents.addAll(students);
            }

            filteredStudents.clear();
            filteredStudents.addAll(allStudents);

            if (studentAdapter != null) {
                studentAdapter.notifyDataSetChanged();
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
            for (StudentModel student : allStudents) {
                if (student != null && matchesQuery(student, lowerQuery)) {
                    filteredStudents.add(student);
                }
            }
        }

        if (studentAdapter != null) {
            studentAdapter.notifyDataSetChanged();
        }
    }

    private boolean matchesQuery(StudentModel student, String query) {
        return student.getFullName().toLowerCase().contains(query) ||
                student.getUniversityId().toLowerCase().contains(query) ||
                student.getFaculty().toLowerCase().contains(query) ||
                student.getBatch().toLowerCase().contains(query) ||
                student.getMobileNumber().toLowerCase().contains(query) ||
                student.getEmail().toLowerCase().contains(query);
    }

    @Override
    public void onEditStudent(StudentModel student) {
        if (student != null && getContext() != null) {
            Toast.makeText(getContext(), "Edit: " + student.getFullName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteStudent(StudentModel student) {
        if (student == null || getContext() == null) {
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete " + student.getFullName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> performDeleteStudent(student))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performDeleteStudent(StudentModel student) {
        if (dbHelper != null && executorService != null) {
            executorService.execute(() -> {
                try {
                    dbHelper.deleteStudentWithPhotoCleanup(student);

                    if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            if (allStudents != null && filteredStudents != null) {
                                allStudents.remove(student);
                                filteredStudents.remove(student);

                                if (studentAdapter != null) {
                                    studentAdapter.notifyDataSetChanged();
                                }
                            }

                            if (getContext() != null) {
                                Toast.makeText(getContext(), student.getFullName() + " deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Error deleting student", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onStudentClick(StudentModel student) {
        if (student != null && getContext() != null) {
            Toast.makeText(getContext(), "Clicked: " + student.getFullName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
        studentAdapter = null;
        rvStudents = null;
        swipeRefresh = null;
        etSearch = null;
        fabAddStudent = null;
        dbHelper = null;
    }
}