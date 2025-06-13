package com.example.codeverse;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.Admin.Helpers.NotificationDatabaseHelper;
import com.example.codeverse.Admin.Adapters.NotificationHistoryAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.codeverse.R;
import com.example.codeverse.Admin.Models.NotificationHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationHistoryFragment extends Fragment implements
        NotificationHistoryAdapter.OnHistoryClickListener {

    private MaterialCardView cvBack;
    private LottieAnimationView ivBack;
    private TextView tvEmptyState;
    private RecyclerView recyclerViewHistory;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChipGroup chipGroupFilters;
    private Chip chipAll, chipDelivered, chipPending, chipFailed;

    private NotificationHistoryAdapter historyAdapter;
    private NotificationDatabaseHelper dbHelper;
    private List<NotificationHistory> historyList;
    private List<NotificationHistory> filteredList;
    private String currentFilter = "all";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_history, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        dbHelper = new NotificationDatabaseHelper(getContext());
        loadNotificationHistory();

        return view;
    }

    private void initViews(View view) {
        cvBack = view.findViewById(R.id.cv_back);
        ivBack = view.findViewById(R.id.ivBack);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        chipGroupFilters = view.findViewById(R.id.chipGroupFilters);
        chipAll = view.findViewById(R.id.chipAll);
        chipDelivered = view.findViewById(R.id.chipDelivered);
        chipPending = view.findViewById(R.id.chipPending);
        chipFailed = view.findViewById(R.id.chipFailed);
    }

    private void setupRecyclerView() {
        historyList = new ArrayList<>();
        filteredList = new ArrayList<>();

        historyAdapter = new NotificationHistoryAdapter(getContext(), filteredList);
        historyAdapter.setOnHistoryClickListener(this);

        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewHistory.setAdapter(historyAdapter);
    }

    private void setupListeners() {
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadNotificationHistory();
            swipeRefreshLayout.setRefreshing(false);
        });

        chipAll.setOnClickListener(v -> {
            currentFilter = "all";
            updateChipSelection();
            filterNotifications();
        });

        chipDelivered.setOnClickListener(v -> {
            currentFilter = "sent";
            updateChipSelection();
            filterNotifications();
        });

        chipPending.setOnClickListener(v -> {
            currentFilter = "pending";
            updateChipSelection();
            filterNotifications();
        });

        chipFailed.setOnClickListener(v -> {
            currentFilter = "failed";
            updateChipSelection();
            filterNotifications();
        });
    }

    private void updateChipSelection() {
        chipAll.setChecked(currentFilter.equals("all"));
        chipDelivered.setChecked(currentFilter.equals("sent"));
        chipPending.setChecked(currentFilter.equals("pending"));
        chipFailed.setChecked(currentFilter.equals("failed"));
    }

    private void loadNotificationHistory() {
        historyList.clear();
        historyList.addAll(dbHelper.getAllNotificationHistory());

        if (historyList.isEmpty()) {
            createSampleHistory();
        }

        filterNotifications();
    }

    private void createSampleHistory() {
        NotificationHistory history1 = new NotificationHistory();
        history1.setId(1);
        history1.setNotificationId(1);
        history1.setTitle("Examination Schedule Released");
        history1.setMessage("Final examination timetable for all faculties has been published on the student portal.");
        history1.setPriority("High");
        history1.setCategory("Examination");
        history1.setRecipients("All Students");
        history1.setStatus("sent");
        history1.setSentAt(System.currentTimeMillis() - 86400000);
        history1.setDeliveredCount(245);
        history1.setReadCount(180);
        history1.setPushSent(true);
        history1.setEmailSent(true);
        history1.setSmsSent(false);

        NotificationHistory history2 = new NotificationHistory();
        history2.setId(2);
        history2.setNotificationId(2);
        history2.setTitle("Library Maintenance Notice");
        history2.setMessage("Library will be closed for maintenance from 10 PM to 6 AM.");
        history2.setPriority("Medium");
        history2.setCategory("Library");
        history2.setRecipients("All Users");
        history2.setStatus("sent");
        history2.setSentAt(System.currentTimeMillis() - 172800000);
        history2.setDeliveredCount(320);
        history2.setReadCount(280);
        history2.setPushSent(true);
        history2.setEmailSent(false);
        history2.setSmsSent(false);

        NotificationHistory history3 = new NotificationHistory();
        history3.setId(3);
        history3.setNotificationId(3);
        history3.setTitle("Emergency Drill");
        history3.setMessage("Emergency evacuation drill will be conducted tomorrow at 2 PM.");
        history3.setPriority("Urgent");
        history3.setCategory("Emergency");
        history3.setRecipients("All Users");
        history3.setStatus("pending");
        history3.setSentAt(System.currentTimeMillis() - 3600000);
        history3.setDeliveredCount(0);
        history3.setReadCount(0);
        history3.setPushSent(true);
        history3.setEmailSent(true);
        history3.setSmsSent(true);

        historyList.add(history1);
        historyList.add(history2);
        historyList.add(history3);
    }

    private void filterNotifications() {
        filteredList.clear();

        if (currentFilter.equals("all")) {
            filteredList.addAll(historyList);
        } else {
            for (NotificationHistory history : historyList) {
                if (history.getStatus().equals(currentFilter)) {
                    filteredList.add(history);
                }
            }
        }

        historyAdapter.updateHistoryList(filteredList);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredList.isEmpty()) {
            recyclerViewHistory.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
            tvEmptyState.setText("No notification history found");
        } else {
            recyclerViewHistory.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onHistoryClick(NotificationHistory history) {
        Toast.makeText(getContext(), "Clicked: " + history.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewDetailsClick(NotificationHistory history) {
        showNotificationDetails(history);
    }

    private void showNotificationDetails(NotificationHistory history) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_notification_details, null);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvDialogMessage = dialogView.findViewById(R.id.tvDialogMessage);
        TextView tvDialogCategory = dialogView.findViewById(R.id.tvDialogCategory);
        TextView tvDialogPriority = dialogView.findViewById(R.id.tvDialogPriority);
        TextView tvDialogRecipients = dialogView.findViewById(R.id.tvDialogRecipients);
        TextView tvDialogSentDate = dialogView.findViewById(R.id.tvDialogSentDate);
        TextView tvDialogDeliveryMethods = dialogView.findViewById(R.id.tvDialogDeliveryMethods);
        TextView tvDialogDeliveredCount = dialogView.findViewById(R.id.tvDialogDeliveredCount);
        TextView tvDialogReadCount = dialogView.findViewById(R.id.tvDialogReadCount);
        TextView tvDialogReadPercentage = dialogView.findViewById(R.id.tvDialogReadPercentage);

        tvDialogTitle.setText(history.getTitle());
        tvDialogMessage.setText(history.getMessage());
        tvDialogCategory.setText(history.getCategory());
        tvDialogPriority.setText(history.getPriority());
        tvDialogRecipients.setText(history.getRecipients());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        tvDialogSentDate.setText(dateFormat.format(new Date(history.getSentAt())));

        tvDialogDeliveryMethods.setText(history.getDeliveryMethodsText());
        tvDialogDeliveredCount.setText(String.valueOf(history.getDeliveredCount()));
        tvDialogReadCount.setText(String.valueOf(history.getReadCount()));
        tvDialogReadPercentage.setText(String.format(Locale.getDefault(), "%.1f%%", history.getReadPercentage()));

        builder.setView(dialogView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}