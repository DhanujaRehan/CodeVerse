package com.example.codeverse.Students.StudentFragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.codeverse.Admin.Helpers.StudentDatabaseHelper;
import com.example.codeverse.R;
import com.example.codeverse.Students.Models.StudentModel;
import com.example.codeverse.Students.Adapters.StudentAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentListFragment extends Fragment implements StudentAdapter.OnStudentActionListener {

    private static final String ALL_FACULTIES = "All Faculties";
    private static final String ALL_BATCHES = "All Batches";

    private RecyclerView rvStudents;
    private StudentAdapter studentAdapter;
    private StudentDatabaseHelper dbHelper;
    private SwipeRefreshLayout swipeRefresh;
    private TextInputEditText etSearch;
    private FloatingActionButton fabAddStudent;
    private FrameLayout layoutEmptyStudents;
    private Spinner spinnerFaculty;
    private Spinner spinnerBatch;
    private MaterialCardView cvBack;

    private List<StudentModel> allStudents;
    private List<StudentModel> filteredStudents;
    private ExecutorService executorService;

    private String selectedFaculty = ALL_FACULTIES;
    private String selectedBatch = ALL_BATCHES;

    public StudentListFragment() {
        // Required empty public constructor
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearchFilter();
        setupSpinners();
        setupSwipeRefresh();
        setupFab();
        setupBackButton();
        loadStudents();

        return view;
    }

    private void initViews(View view) {
        cvBack = view.findViewById(R.id.cv_back);
        rvStudents = view.findViewById(R.id.rvStudents);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        etSearch = view.findViewById(R.id.etSearch);
        fabAddStudent = view.findViewById(R.id.fabAddStudent);
        layoutEmptyStudents = view.findViewById(R.id.layout_empty_students);
        spinnerFaculty = view.findViewById(R.id.spinnerFaculty);
        spinnerBatch = view.findViewById(R.id.spinnerBatch);

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
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Implementation not needed
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterStudents();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Implementation not needed
                }
            });
        }
    }

    private void setupSpinners() {
        if (getContext() != null) {
            setupFacultySpinner();
            setupBatchSpinner();
        }
    }

    private void setupFacultySpinner() {
        List<String> facultyList = new ArrayList<>();
        facultyList.add(ALL_FACULTIES);
        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, facultyList);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFaculty.setAdapter(facultyAdapter);

        spinnerFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFaculty = (String) parent.getItemAtPosition(position);
                filterStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Implementation not needed
            }
        });
    }

    private void setupBatchSpinner() {
        List<String> batchList = new ArrayList<>();
        batchList.add(ALL_BATCHES);
        ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, batchList);
        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBatch.setAdapter(batchAdapter);

        spinnerBatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBatch = (String) parent.getItemAtPosition(position);
                filterStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Implementation not needed
            }
        });
    }

    private void updateSpinners() {
        if (getContext() == null || allStudents == null) return;

        Set<String> faculties = new HashSet<>();
        Set<String> batches = new HashSet<>();

        for (StudentModel student : allStudents) {
            if (student.getFaculty() != null && !student.getFaculty().trim().isEmpty()) {
                faculties.add(student.getFaculty());
            }
            if (student.getBatch() != null && !student.getBatch().trim().isEmpty()) {
                batches.add(student.getBatch());
            }
        }

        updateFacultySpinner(faculties);
        updateBatchSpinner(batches);
    }

    private void updateFacultySpinner(Set<String> faculties) {
        List<String> facultyList = new ArrayList<>();
        facultyList.add(ALL_FACULTIES);
        facultyList.addAll(faculties);

        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, facultyList);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFaculty.setAdapter(facultyAdapter);
    }

    private void updateBatchSpinner(Set<String> batches) {
        List<String> batchList = new ArrayList<>();
        batchList.add(ALL_BATCHES);
        batchList.addAll(batches);

        ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, batchList);
        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBatch.setAdapter(batchAdapter);
    }

    private void setupSwipeRefresh() {
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::loadStudents);
            swipeRefresh.setColorSchemeResources(
                    R.color.DarkBlue,
                    R.color.MiddleBlue,
                    R.color.MiddleBlue2
            );
        }
    }

    private void setupFab() {
        if (fabAddStudent != null) {
            fabAddStudent.setOnClickListener(v -> showAddStudentMessage());
        }
    }

    private void setupBackButton() {
        if (cvBack != null) {
            cvBack.setOnClickListener(v -> handleBackPress());
        }
    }

    private void showAddStudentMessage() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Add Student feature coming soon!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleBackPress() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else if (getActivity() != null) {
            getActivity().finish();
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
                            updateSpinners();
                            updateEmptyState();
                            if (swipeRefresh != null) {
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                } catch (Exception e) {
                    handleLoadError();
                }
            });
        } else {
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
        }
    }

    private void handleLoadError() {
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

    private void updateEmptyState() {
        if (layoutEmptyStudents != null && rvStudents != null) {
            if (filteredStudents.isEmpty()) {
                layoutEmptyStudents.setVisibility(View.VISIBLE);
                rvStudents.setVisibility(View.GONE);
            } else {
                layoutEmptyStudents.setVisibility(View.GONE);
                rvStudents.setVisibility(View.VISIBLE);
            }
        }
    }

    private void filterStudents() {
        if (filteredStudents == null || allStudents == null) {
            return;
        }

        filteredStudents.clear();

        String searchQuery = "";
        if (etSearch != null && etSearch.getText() != null) {
            searchQuery = etSearch.getText().toString().toLowerCase().trim();
        }

        for (StudentModel student : allStudents) {
            if (student != null && matchesFilters(student, searchQuery)) {
                filteredStudents.add(student);
            }
        }

        if (studentAdapter != null) {
            studentAdapter.notifyDataSetChanged();
        }
        updateEmptyState();
    }

    private boolean matchesFilters(StudentModel student, String searchQuery) {
        boolean matchesSearch = searchQuery.isEmpty() ||
                student.getFullName().toLowerCase().contains(searchQuery) ||
                student.getUniversityId().toLowerCase().contains(searchQuery) ||
                student.getFaculty().toLowerCase().contains(searchQuery) ||
                student.getBatch().toLowerCase().contains(searchQuery) ||
                student.getMobileNumber().toLowerCase().contains(searchQuery) ||
                student.getEmail().toLowerCase().contains(searchQuery);

        boolean matchesFaculty = ALL_FACULTIES.equals(selectedFaculty) ||
                student.getFaculty().equals(selectedFaculty);

        boolean matchesBatch = ALL_BATCHES.equals(selectedBatch) ||
                student.getBatch().equals(selectedBatch);

        return matchesSearch && matchesFaculty && matchesBatch;
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
                                updateSpinners();
                                updateEmptyState();
                            }

                            if (getContext() != null) {
                                Toast.makeText(getContext(), student.getFullName() + " deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    handleDeleteError();
                }
            });
        }
    }

    private void handleDeleteError() {
        if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
            getActivity().runOnUiThread(() -> {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error deleting student", Toast.LENGTH_SHORT).show();
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
        layoutEmptyStudents = null;
        spinnerFaculty = null;
        spinnerBatch = null;
        cvBack = null;
    }
}