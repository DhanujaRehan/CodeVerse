package com.example.codeverse.Students.StudentFragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.Students.Models.AssignmentModel;
import com.example.codeverse.Students.Helpers.AssignmentUploadHelper;
import com.example.codeverse.R;
import com.example.codeverse.Students.Adapters.UploadedFilesAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AssignmentUpload extends Fragment {

    private TextInputEditText etAssignmentTitle, etDescription;
    private AutoCompleteTextView dropdownSubject;
    private MaterialButton btnSelectFile, btnUpload, btnGoToDashboard;
    private TextView tvSelectedFile, tvUploadPercentage;
    private LinearProgressIndicator progressUpload;
    private LottieAnimationView animationUpload, animationSuccess;
    private FrameLayout loadingOverlay, successOverlay;
    private RecyclerView rvUploadedFiles;

    private AssignmentUploadHelper dbHelper;
    private UploadedFilesAdapter filesAdapter;
    private List<AssignmentModel> uploadedFiles;

    private Uri selectedFileUri;
    private String selectedFileName;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignment_upoad, container, false);

        initViews(view);
        setupFilePickerLauncher();
        setupDatabase();
        setupSubjectDropdown();
        setupClickListeners(view);
        loadUploadedFiles();

        return view;
    }

    private void initViews(View view) {
        etAssignmentTitle = view.findViewById(R.id.et_assignment_title);
        etDescription = view.findViewById(R.id.et_description);
        dropdownSubject = view.findViewById(R.id.dropdown_subject);
        btnSelectFile = view.findViewById(R.id.btn_select_file);
        btnUpload = view.findViewById(R.id.btn_upload);
        tvSelectedFile = view.findViewById(R.id.tv_selected_file);
        tvUploadPercentage = view.findViewById(R.id.tv_upload_percentage);
        progressUpload = view.findViewById(R.id.progress_upload);
        animationUpload = view.findViewById(R.id.animation_upload);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
        successOverlay = view.findViewById(R.id.success_overlay);
        btnGoToDashboard = view.findViewById(R.id.btn_go_to_dashboard);
        rvUploadedFiles = view.findViewById(R.id.rv_uploaded_files);
    }

    private void setupFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedFileUri = result.getData().getData();
                        selectedFileName = getFileName(selectedFileUri);
                        tvSelectedFile.setText(selectedFileName);
                        btnUpload.setEnabled(true);

                        hideOverlays();
                    }
                }
        );
    }

    private void setupDatabase() {
        dbHelper = new AssignmentUploadHelper(getContext());
        uploadedFiles = new ArrayList<>();
        filesAdapter = new UploadedFilesAdapter(uploadedFiles, this::onFileAction);
        rvUploadedFiles.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUploadedFiles.setAdapter(filesAdapter);
    }

    private void setupSubjectDropdown() {
        String[] subjects = {"Mathematics", "Computer Science", "Physics", "Chemistry", "Biology", "English", "History"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, subjects);
        dropdownSubject.setAdapter(adapter);
    }

    private void setupClickListeners(View view) {
        btnSelectFile.setOnClickListener(v -> openFilePicker());
        btnUpload.setOnClickListener(v -> uploadAssignment());
        view.findViewById(R.id.iv_back).setOnClickListener(v -> requireActivity().onBackPressed());

        if (btnGoToDashboard != null) {
            btnGoToDashboard.setOnClickListener(v -> {
                hideOverlays();
                requireActivity().onBackPressed();
            });
        }

        etAssignmentTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) hideOverlays();
        });

        etDescription.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) hideOverlays();
        });

        dropdownSubject.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) hideOverlays();
        });

        if (loadingOverlay != null) {
            loadingOverlay.setOnClickListener(v -> {
                // Prevent clicks from passing through
            });
        }

        if (successOverlay != null) {
            successOverlay.setOnClickListener(v -> {
                // Allow clicking to dismiss
                hideOverlays();
            });
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"});
        filePickerLauncher.launch(intent);
    }

    private void uploadAssignment() {
        if (!validateInputs()) return;

        String title = etAssignmentTitle.getText().toString().trim();
        String subject = dropdownSubject.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        showLoadingOverlay();
        simulateUploadProgress();

        new Handler().postDelayed(() -> {
            try {
                String filePath = saveFileToInternalStorage();
                File file = new File(filePath);

                AssignmentModel assignment = new AssignmentModel();
                assignment.setTitle(title);
                assignment.setSubject(subject);
                assignment.setDescription(description);
                assignment.setFileName(selectedFileName);
                assignment.setFilePath(filePath);
                assignment.setUploadDate(System.currentTimeMillis());
                assignment.setFileSize(file.exists() ? file.length() : 0);

                long id = dbHelper.insertAssignment(assignment);

                if (id > 0) {
                    hideLoadingOverlay();
                    clearForm();
                    showSuccessOverlay();
                    loadUploadedFiles();
                } else {
                    hideLoadingOverlay();
                    Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                hideLoadingOverlay();
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }

    private boolean validateInputs() {
        if (etAssignmentTitle.getText().toString().trim().isEmpty()) {
            etAssignmentTitle.setError("Title is required");
            return false;
        }

        if (dropdownSubject.getText().toString().trim().isEmpty()) {
            dropdownSubject.setError("Subject is required");
            return false;
        }

        if (selectedFileUri == null) {
            Toast.makeText(getContext(), "Please select a file", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void simulateUploadProgress() {
        progressUpload.setProgress(0);
        tvUploadPercentage.setText("0%");

        Handler handler = new Handler();
        int[] progress = {0};

        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                progress[0] += 10;
                progressUpload.setProgress(progress[0]);
                tvUploadPercentage.setText(progress[0] + "%");

                if (progress[0] < 100) {
                    handler.postDelayed(this, 300);
                }
            }
        };

        handler.post(progressRunnable);
    }

    private String saveFileToInternalStorage() throws Exception {
        InputStream inputStream = getContext().getContentResolver().openInputStream(selectedFileUri);
        File file = new File(getContext().getFilesDir(), selectedFileName);

        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        long totalSize = 0;

        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
            totalSize += length;
        }

        outputStream.close();
        inputStream.close();

        return file.getAbsolutePath();
    }

    private void showLoadingOverlay() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoadingOverlay() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    private void showSuccessOverlay() {
        if (successOverlay != null) {
            successOverlay.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> {
                if (successOverlay != null) {
                    successOverlay.setVisibility(View.GONE);
                }
            }, 5000);
        }
    }

    private void hideOverlays() {
        hideLoadingOverlay();
        if (successOverlay != null && successOverlay.getVisibility() == View.VISIBLE) {
            successOverlay.setVisibility(View.GONE);
        }
    }

    private void clearForm() {
        etAssignmentTitle.setText("");
        etDescription.setText("");
        dropdownSubject.setText("");
        tvSelectedFile.setText("No file selected");
        selectedFileUri = null;
        selectedFileName = null;
        btnUpload.setEnabled(false);
        progressUpload.setProgress(0);
        tvUploadPercentage.setText("0%");

        etAssignmentTitle.setError(null);
        dropdownSubject.setError(null);
    }

    private void loadUploadedFiles() {
        uploadedFiles.clear();
        uploadedFiles.addAll(dbHelper.getAllAssignments());
        filesAdapter.notifyDataSetChanged();

        View cardUploadedFiles = getView() != null ? getView().findViewById(R.id.card_uploaded_files) : null;
        if (cardUploadedFiles != null) {
            if (uploadedFiles.isEmpty()) {
                cardUploadedFiles.setVisibility(View.GONE);
            } else {
                cardUploadedFiles.setVisibility(View.VISIBLE);
            }
        }
    }

    private void onFileAction(AssignmentModel assignment, String action) {
        switch (action) {
            case "download":
                downloadFile(assignment);
                break;
            case "edit":
                editAssignment(assignment);
                break;
            case "delete":
                deleteAssignment(assignment);
                break;
        }
    }

    private void downloadFile(AssignmentModel assignment) {
        try {
            File file = new File(assignment.getFilePath());
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), getMimeType(assignment.getFileName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(getContext(), "Opening file", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error opening file", Toast.LENGTH_SHORT).show();
        }
    }

    private void editAssignment(AssignmentModel assignment) {
        etAssignmentTitle.setText(assignment.getTitle());
        dropdownSubject.setText(assignment.getSubject());
        etDescription.setText(assignment.getDescription());
        tvSelectedFile.setText(assignment.getFileName());

        btnUpload.setText("Update Assignment");
        btnUpload.setOnClickListener(v -> updateAssignment(assignment));
    }

    private void updateAssignment(AssignmentModel assignment) {
        if (!validateInputs()) return;

        assignment.setTitle(etAssignmentTitle.getText().toString().trim());
        assignment.setSubject(dropdownSubject.getText().toString().trim());
        assignment.setDescription(etDescription.getText().toString().trim());

        showLoadingOverlay();

        new Handler().postDelayed(() -> {
            if (selectedFileUri != null) {
                try {
                    String filePath = saveFileToInternalStorage();
                    File file = new File(filePath);
                    assignment.setFileName(selectedFileName);
                    assignment.setFilePath(filePath);
                    assignment.setFileSize(file.exists() ? file.length() : 0);
                } catch (Exception e) {
                    hideLoadingOverlay();
                    Toast.makeText(getContext(), "Error updating file", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            int result = dbHelper.updateAssignment(assignment);

            if (result > 0) {
                hideLoadingOverlay();
                clearForm();
                btnUpload.setText("Upload Assignment");
                btnUpload.setOnClickListener(v -> uploadAssignment());
                showSuccessOverlay();
                loadUploadedFiles();
                Toast.makeText(getContext(), "Assignment updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                hideLoadingOverlay();
                Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    private void deleteAssignment(AssignmentModel assignment) {
        showLoadingOverlay();

        new Handler().postDelayed(() -> {
            int result = dbHelper.deleteAssignment(assignment.getId());

            if (result > 0) {
                File file = new File(assignment.getFilePath());
                if (file.exists()) {
                    file.delete();
                }

                hideLoadingOverlay();
                Toast.makeText(getContext(), "Assignment deleted", Toast.LENGTH_SHORT).show();
                loadUploadedFiles();
            } else {
                hideLoadingOverlay();
                Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContext().getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(0);
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

    private String getMimeType(String fileName) {
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".doc")) {
            return "application/msword";
        } else if (fileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        return "*/*";
    }
}