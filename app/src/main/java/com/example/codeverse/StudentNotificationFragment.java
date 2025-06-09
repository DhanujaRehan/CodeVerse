package com.example.codeverse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.Admin.Helpers.NotificationDatabaseHelper;
import com.example.codeverse.R;
import com.example.codeverse.StudentNotificationAdapter;
import com.example.codeverse.StudentNotification;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class StudentNotificationFragment extends Fragment {

    private RecyclerView recyclerViewNotifications;
    private StudentNotificationAdapter adapter;
    private LinearLayout layoutEmptyState;
    private FrameLayout loadingOverlay;
    private ImageView ivBack;
    private MaterialButton btnAllNotifications, btnUrgentNotifications, btnAcademicNotifications;

    private NotificationDatabaseHelper dbHelper;
    private List<StudentNotification> allNotifications;
    private String currentFilter = "all";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_notification, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        dbHelper = new NotificationDatabaseHelper(getContext());
        loadNotifications();

        return view;
    }

    private void initViews(View view) {
        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
        ivBack = view.findViewById(R.id.iv_back);
        btnAllNotifications = view.findViewById(R.id.btnAllNotifications);
        btnUrgentNotifications = view.findViewById(R.id.btnUrgentNotifications);
        btnAcademicNotifications = view.findViewById(R.id.btnAcademicNotifications);
    }

    private void setupRecyclerView() {
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StudentNotificationAdapter(getContext(), new ArrayList<>());
        recyclerViewNotifications.setAdapter(adapter);

        adapter.setOnNotificationClickListener(notification -> {
            Toast.makeText(getContext(), "Clicked: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });


        btnAllNotifications.setOnClickListener(v -> {
            currentFilter = "all";
            updateFilterButtons();
            filterNotifications();
        });

        btnUrgentNotifications.setOnClickListener(v -> {
            currentFilter = "urgent";
            updateFilterButtons();
            filterNotifications();
        });

        btnAcademicNotifications.setOnClickListener(v -> {
            currentFilter = "academic";
            updateFilterButtons();
            filterNotifications();
        });
    }

    private void updateFilterButtons() {
        resetButtonStyles();

        switch (currentFilter) {
            case "all":
                setActiveButton(btnAllNotifications);
                break;
            case "urgent":
                setActiveButton(btnUrgentNotifications);
                break;
            case "academic":
                setActiveButton(btnAcademicNotifications);
                break;
        }
    }

    private void resetButtonStyles() {
        setInactiveButton(btnAllNotifications);
        setInactiveButton(btnUrgentNotifications);
        setInactiveButton(btnAcademicNotifications);
    }

    private void setActiveButton(MaterialButton button) {
        button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                getResources().getColor(R.color.DarkBlue)));
        button.setTextColor(getResources().getColor(R.color.white));
    }

    private void setInactiveButton(MaterialButton button) {
        button.setBackgroundTintList(null);
        button.setTextColor(getResources().getColor(R.color.DarkBlue));
    }

    private void loadNotifications() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<com.example.codeverse.Admin.Models.Notification> adminNotifications = dbHelper.getAllNotifications();
                allNotifications = new ArrayList<>();

                for (com.example.codeverse.Admin.Models.Notification adminNotif : adminNotifications) {
                    if ("sent".equals(adminNotif.getStatus()) || "scheduled".equals(adminNotif.getStatus())) {
                        StudentNotification studentNotif = new StudentNotification();
                        studentNotif.setId(adminNotif.getId());
                        studentNotif.setTitle(adminNotif.getTitle());
                        studentNotif.setMessage(adminNotif.getMessage());
                        studentNotif.setPriority(adminNotif.getPriority());
                        studentNotif.setCategory(adminNotif.getCategory());
                        studentNotif.setRecipients(adminNotif.getRecipients());
                        studentNotif.setStatus(adminNotif.getStatus());
                        studentNotif.setScheduledTime(adminNotif.getScheduledTime());
                        studentNotif.setCreatedAt(adminNotif.getCreatedAt());
                        studentNotif.setAttachmentPath(adminNotif.getAttachmentPath());
                        allNotifications.add(studentNotif);
                    }
                }

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        filterNotifications();
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(getContext(), "Error loading notifications", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void filterNotifications() {
        List<StudentNotification> filteredList = new ArrayList<>();

        for (StudentNotification notification : allNotifications) {
            switch (currentFilter) {
                case "all":
                    filteredList.add(notification);
                    break;
                case "urgent":
                    if ("Urgent".equalsIgnoreCase(notification.getPriority()) ||
                            "High".equalsIgnoreCase(notification.getPriority())) {
                        filteredList.add(notification);
                    }
                    break;
                case "academic":
                    if ("Academic".equalsIgnoreCase(notification.getCategory())) {
                        filteredList.add(notification);
                    }
                    break;
            }
        }

        adapter.updateNotifications(filteredList);

        if (filteredList.isEmpty()) {
            recyclerViewNotifications.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerViewNotifications.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}