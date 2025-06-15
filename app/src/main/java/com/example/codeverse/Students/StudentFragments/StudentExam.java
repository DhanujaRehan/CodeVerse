package com.example.codeverse.Students.StudentFragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.Students.Activities.AdmitCardDialog;
import com.example.codeverse.Students.Models.Exam;
import com.example.codeverse.Students.Helpers.ExamSchedulingHelper;
import com.example.codeverse.R;
import com.example.codeverse.Students.Adapters.StudentExamAdapter;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class StudentExam extends Fragment implements StudentExamAdapter.OnExamActionListener {

    private static final int REQUEST_CODE_DOCUMENT_PICKER = 1001;
    private static final String GOOGLE_DRIVE_PACKAGE = "com.google.android.apps.docs";

    private RecyclerView rvExamResults;
    private StudentExamAdapter examAdapter;
    private List<Exam> examList;
    private ExamSchedulingHelper dbHelper;

    private MaterialCardView cvBack;
    private ImageView ivBack, ivFilter;
    private TextView tvSelectedSemester, tvViewAllResults;
    private MaterialCardView cardAdmission, cardSubmissions;

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_student_exam, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        setupClickListeners();
        setupRecyclerView();
        loadExamData();
    }

    private void initViews() {
        dbHelper = new ExamSchedulingHelper(getContext());
        examList = new ArrayList<>();

        cvBack = rootView.findViewById(R.id.cv_back);
        ivBack = rootView.findViewById(R.id.iv_back);
        ivFilter = rootView.findViewById(R.id.iv_filter);
        tvSelectedSemester = rootView.findViewById(R.id.tv_selected_semester);
        tvViewAllResults = rootView.findViewById(R.id.tv_view_all_results);
        cardAdmission = rootView.findViewById(R.id.card_admission);
        cardSubmissions = rootView.findViewById(R.id.card_submissions);
        rvExamResults = rootView.findViewById(R.id.rv_exam_results);
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        ivFilter.setOnClickListener(v -> {
            showFilterDialog();
        });

        tvSelectedSemester.setOnClickListener(v -> {
            showSemesterDialog();
        });

        tvViewAllResults.setOnClickListener(v -> {
            loadExamData();
            Toast.makeText(getContext(), "Showing all results", Toast.LENGTH_SHORT).show();
        });

        cardAdmission.setOnClickListener(v -> {
            navigateToExamAdmissions();
        });

        cardSubmissions.setOnClickListener(v -> {
            showDocumentSourceDialog();
        });
    }

    private void setupRecyclerView() {
        examAdapter = new StudentExamAdapter(getContext(), examList);
        examAdapter.setOnExamActionListener(this);
        rvExamResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExamResults.setAdapter(examAdapter);
        rvExamResults.setNestedScrollingEnabled(false);
    }

    private void loadExamData() {
        examList.clear();
        List<Exam> allExams = dbHelper.getAllExams();
        examList.addAll(allExams);
        examAdapter.updateExams(examList);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Filter Exams");

        String[] options = {"All Exams", "Scheduled", "Pending", "Completed", "Cancelled"};

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    loadExamData();
                    break;
                case 1:
                    filterExamsByStatus("Scheduled");
                    break;
                case 2:
                    filterExamsByStatus("Pending");
                    break;
                case 3:
                    filterExamsByStatus("Completed");
                    break;
                case 4:
                    filterExamsByStatus("Cancelled");
                    break;
            }
        });

        builder.show();
    }

    private void showSemesterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Semester");

        String[] semesters = {"All Semesters", "Spring 2025", "Fall 2024", "Summer 2024"};

        builder.setItems(semesters, (dialog, which) -> {
            switch (which) {
                case 0:
                    loadExamData();
                    tvSelectedSemester.setText("All Semesters");
                    break;
                case 1:
                    filterExamsBySemester("Spring 2025");
                    tvSelectedSemester.setText("Spring 2025");
                    break;
                case 2:
                    filterExamsBySemester("Fall 2024");
                    tvSelectedSemester.setText("Fall 2024");
                    break;
                case 3:
                    filterExamsBySemester("Summer 2024");
                    tvSelectedSemester.setText("Summer 2024");
                    break;
            }
        });

        builder.show();
    }

    private void filterExamsByStatus(String status) {
        examList.clear();
        List<Exam> allExams = dbHelper.getAllExams();
        for (Exam exam : allExams) {
            if (status.equals(exam.getStatus())) {
                examList.add(exam);
            }
        }
        examAdapter.updateExams(examList);
        Toast.makeText(getContext(), "Showing " + status.toLowerCase() + " exams", Toast.LENGTH_SHORT).show();
    }

    private void filterExamsBySemester(String semester) {
        examList.clear();
        List<Exam> allExams = dbHelper.getAllExams();
        for (Exam exam : allExams) {
            if (semester.equals(exam.getSemester())) {
                examList.add(exam);
            }
        }
        examAdapter.updateExams(examList);
        Toast.makeText(getContext(), "Showing " + semester + " exams", Toast.LENGTH_SHORT).show();
    }

    private void navigateToExamAdmissions() {
        AdmissionDownload examAdmissionsFragment = new AdmissionDownload();
        if (getParentFragmentManager() != null) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, examAdmissionsFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void showDocumentSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Document Source");

        String[] options = {"Google Drive", "Device Documents", "Cancel"};

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    openGoogleDrive();
                    break;
                case 1:
                    openDeviceDocuments();
                    break;
                case 2:
                    dialog.dismiss();
                    break;
            }
        });

        builder.show();
    }

    private void openGoogleDrive() {
        try {
            if (isAppInstalled(GOOGLE_DRIVE_PACKAGE)) {
                Intent intent = new Intent();
                intent.setPackage(GOOGLE_DRIVE_PACKAGE);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://drive.google.com"));
                startActivity(intent);
                Toast.makeText(getContext(), "Google Drive app not found. Opening web version.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unable to open Google Drive", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDeviceDocuments() {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");

            String[] mimeTypes = {
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/vnd.ms-powerpoint",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "text/plain",
                    "image/*"
            };
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

            startActivityForResult(intent, REQUEST_CODE_DOCUMENT_PICKER);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unable to open document picker", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAppInstalled(String packageName) {
        try {
            getContext().getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DOCUMENT_PICKER && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    handleSelectedDocument(uri);
                }
            }
        }
    }

    private void handleSelectedDocument(Uri uri) {
        try {
            String fileName = getFileName(uri);
            Toast.makeText(getContext(), "Selected: " + fileName, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "No app found to open this document", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error handling document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onDownloadAdmitCard(Exam exam) {
        if ("Scheduled".equals(exam.getStatus())) {
            AdmitCardDialog dialog = new AdmitCardDialog(getContext(), exam);
            dialog.show();
        } else {
            Toast.makeText(getContext(), "Admit card not available for this exam", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
        examAdapter = null;
        examList = null;
    }
}