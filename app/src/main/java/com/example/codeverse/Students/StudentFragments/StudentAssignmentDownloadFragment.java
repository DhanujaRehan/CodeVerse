package com.example.codeverse.Students.StudentFragments;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.Assignment;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Helper.AssignmentHelper;
import com.example.codeverse.Students.Adapters.AssignmentAdapter;
import com.google.android.material.card.MaterialCardView;
import java.io.File;
import java.util.List;

public class StudentAssignmentDownloadFragment extends Fragment implements AssignmentAdapter.OnAssignmentClickListener {

    private RecyclerView rvAssignments;
    private TextView tvAssignmentCount, tvNoAssignments;
    private FrameLayout loadingOverlay;
    private MaterialCardView cvBack;

    private AssignmentHelper assignmentHelper;
    private AssignmentAdapter adapter;
    private List<Assignment> assignments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_assignment_download, container, false);

        initViews(view);
        setupRecyclerView();
        loadAssignments();

        return view;
    }

    private void initViews(View view) {
        rvAssignments = view.findViewById(R.id.rv_assignments);
        tvAssignmentCount = view.findViewById(R.id.tv_assignment_count);
        tvNoAssignments = view.findViewById(R.id.tv_no_assignments);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
        cvBack = view.findViewById(R.id.cv_back);

        assignmentHelper = new AssignmentHelper(getContext());

        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupRecyclerView() {
        rvAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AssignmentAdapter(getContext(), assignments);
        adapter.setOnAssignmentClickListener(this);
        rvAssignments.setAdapter(adapter);
    }

    private void loadAssignments() {
        showLoading(true);

        new Thread(() -> {
            assignments = assignmentHelper.getAllAssignments();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    showLoading(false);
                    updateUI();
                });
            }
        }).start();
    }

    private void updateUI() {
        if (assignments != null && !assignments.isEmpty()) {
            adapter.updateAssignments(assignments);
            tvAssignmentCount.setText(String.valueOf(assignments.size()));
            tvNoAssignments.setVisibility(View.GONE);
            rvAssignments.setVisibility(View.VISIBLE);
        } else {
            tvAssignmentCount.setText("0");
            tvNoAssignments.setVisibility(View.VISIBLE);
            rvAssignments.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDownloadClick(Assignment assignment) {
        if (assignment.getFilePath() != null && !assignment.getFilePath().isEmpty()) {
            downloadFile(assignment);
        } else {
            Toast.makeText(getContext(), "No file available for download", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile(Assignment assignment) {
        try {
            File file = new File(assignment.getFilePath());
            if (!file.exists()) {
                Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
                return;
            }

            DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.fromFile(file);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(assignment.getTitle());
            request.setDescription("Downloading assignment file");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file.getName());
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            downloadManager.enqueue(request);
            Toast.makeText(getContext(), "Download started", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Download failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (assignmentHelper != null) {
            assignmentHelper.close();
        }
    }
}