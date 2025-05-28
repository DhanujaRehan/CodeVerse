package com.example.codeverse.Admin.AdminFragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.Admin.helpers.NotificationDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Import your custom classes
import com.example.codeverse.R;
import com.example.codeverse.Admin.models.Notification;
import com.example.codeverse.Admin.models.NotificationHistory;
import com.example.codeverse.Admin.helpers.NotificationDatabaseHelper;
import com.example.codeverse.Admin.adapters.NotificationAdapter;
import com.example.codeverse.Admin.adapters.NotificationHistoryAdapter;

public class AdminSendNotifications extends Fragment {

    // UI Components
    private AutoCompleteTextView dropdownPriority, dropdownRecipients, dropdownCategory;
    private TextInputEditText etNotificationTitle, etMessage, etScheduleDate, etScheduleTime;
    private SwitchMaterial switchScheduleNotification, switchPushNotification,
            switchEmailNotification, switchSMSNotification;
    private LinearLayout layoutScheduleOptions;
    private MaterialButton btnBrowse, btnSaveAsDraft, btnSend;
    private TextView tvPreviewTitle, tvPreviewMessage;
    private ImageView ivBack, ivNotificationHistory;
    private RecyclerView rvNotifications, rvHistory;

    // Data
    private NotificationDatabaseHelper dbHelper;
    private NotificationAdapter notificationAdapter;
    private NotificationHistoryAdapter historyAdapter;
    private List<Notification> notificationList;
    private List<NotificationHistory> historyList;
    private String selectedAttachmentPath = "";
    private String selectedAttachmentName = "";

    // Constants
    private static final int PICK_FILE_REQUEST = 1001;

    public AdminSendNotifications() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_send_notifications, container, false);

        initViews(view);
        initDatabase();
        setupDropdowns();
        setupListeners();
        setupRecyclerViews();
        loadNotifications();

        return view;
    }

    private void initViews(View view) {
        // Dropdowns
        dropdownPriority = view.findViewById(R.id.dropdownPriority);
        dropdownRecipients = view.findViewById(R.id.dropdownRecipients);
        dropdownCategory = view.findViewById(R.id.dropdownCategory);

        // Edit texts
        etNotificationTitle = view.findViewById(R.id.etNotificationTitle);
        etMessage = view.findViewById(R.id.etMessage);
        etScheduleDate = view.findViewById(R.id.etScheduleDate);
        etScheduleTime = view.findViewById(R.id.etScheduleTime);

        // Switches
        switchScheduleNotification = view.findViewById(R.id.switchScheduleNotification);
        switchPushNotification = view.findViewById(R.id.switchPushNotification);
        switchEmailNotification = view.findViewById(R.id.switchEmailNotification);
        switchSMSNotification = view.findViewById(R.id.switchSMSNotification);

        // Layouts
        layoutScheduleOptions = view.findViewById(R.id.layoutScheduleOptions);

        // Buttons
        btnBrowse = view.findViewById(R.id.btnBrowse);
        btnSaveAsDraft = view.findViewById(R.id.btnSaveAsDraft);
        btnSend = view.findViewById(R.id.btnSend);

        // Preview
        tvPreviewTitle = view.findViewById(R.id.tvPreviewTitle);
        tvPreviewMessage = view.findViewById(R.id.tvPreviewMessage);

        // Navigation
        ivBack = view.findViewById(R.id.ivBack);
        ivNotificationHistory = view.findViewById(R.id.iv_notification_history);
    }

    private void initDatabase() {
        dbHelper = new NotificationDatabaseHelper(getContext());
        notificationList = new ArrayList<>();
        historyList = new ArrayList<>();
    }

    private void setupDropdowns() {
        // Priority options
        String[] priorityOptions = {"High", "Medium", "Low"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, priorityOptions);
        dropdownPriority.setAdapter(priorityAdapter);

        // Recipients options
        String[] recipientOptions = {"All Students", "All Staff", "All Faculty",
                "First Year Students", "Final Year Students", "Department Heads", "Administrators"};
        ArrayAdapter<String> recipientAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, recipientOptions);
        dropdownRecipients.setAdapter(recipientAdapter);

        // Category options
        String[] categoryOptions = {"Academic", "Administrative", "Emergency", "Event",
                "Examination", "Fee Payment", "General", "Holiday", "Maintenance"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categoryOptions);
        dropdownCategory.setAdapter(categoryAdapter);
    }

    private void setupListeners() {
        // Back button
        ivBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // History button
        ivNotificationHistory.setOnClickListener(v -> showNotificationHistory());

        // Schedule switch
        switchScheduleNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutScheduleOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Date picker
        etScheduleDate.setOnClickListener(v -> showDatePicker());

        // Time picker
        etScheduleTime.setOnClickListener(v -> showTimePicker());

        // File browser
        btnBrowse.setOnClickListener(v -> openFilePicker());

        // Preview updates
        setupPreviewListeners();

        // Action buttons
        btnSaveAsDraft.setOnClickListener(v -> saveNotification("DRAFT"));
        btnSend.setOnClickListener(v -> sendNotification());
    }

    private void setupPreviewListeners() {
        etNotificationTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String title = s.toString().trim();
                tvPreviewTitle.setText(title.isEmpty() ? "Notification title will appear here" : title);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String message = s.toString().trim();
                tvPreviewMessage.setText(message.isEmpty() ? "Message content will appear here" : message);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerViews() {
        // Setup notifications RecyclerView (you might want to add this to your layout)
        notificationAdapter = new NotificationAdapter(requireContext(), notificationList);
        notificationAdapter.setOnNotificationClickListener(new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onNotificationClick(Notification notification) {
                // Handle notification click - maybe edit or view details
                loadNotificationForEditing(notification);
            }

            @Override
            public void onNotificationLongClick(Notification notification) {
                // Handle long click - maybe delete or show options
                showNotificationOptions(notification);
            }
        });

        // Setup history RecyclerView
        historyAdapter = new NotificationHistoryAdapter(requireContext(), historyList);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etScheduleDate.setText(sdf.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    etScheduleTime.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);

        timePickerDialog.show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "image/jpeg",
                "image/png"
        });
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == requireActivity().RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                selectedAttachmentPath = selectedFileUri.toString();
                selectedAttachmentName = getFileName(selectedFileUri);
                Toast.makeText(requireContext(), "File attached: " + selectedAttachmentName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex >= 0) {
                        result = cursor.getString(displayNameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result != null ? result : "Unknown file";
    }

    private void saveNotification(String status) {
        if (!validateForm()) {
            return;
        }

        Notification notification = createNotificationFromForm(status);
        long id = dbHelper.insertNotification(notification);

        if (id > 0) {
            // Add to history
            NotificationHistory history = new NotificationHistory((int) id,
                    status.equals("DRAFT") ? "CREATED_DRAFT" : "SENT",
                    "Notification " + status.toLowerCase());
            dbHelper.insertHistory(history);

            Toast.makeText(requireContext(),
                    status.equals("DRAFT") ? "Draft saved successfully" : "Notification sent successfully",
                    Toast.LENGTH_SHORT).show();

            clearForm();
            loadNotifications();
        } else {
            Toast.makeText(requireContext(), "Failed to save notification", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification() {
        saveNotification("SENT");
    }

    private boolean validateForm() {
        if (etNotificationTitle.getText() == null || etNotificationTitle.getText().toString().trim().isEmpty()) {
            etNotificationTitle.setError("Title is required");
            return false;
        }

        if (etMessage.getText() == null || etMessage.getText().toString().trim().isEmpty()) {
            etMessage.setError("Message is required");
            return false;
        }

        if (dropdownPriority.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Please select priority", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dropdownRecipients.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Please select recipients", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dropdownCategory.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Please select category", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (switchScheduleNotification.isChecked()) {
            if ((etScheduleDate.getText() == null || etScheduleDate.getText().toString().trim().isEmpty()) ||
                    (etScheduleTime.getText() == null || etScheduleTime.getText().toString().trim().isEmpty())) {
                Toast.makeText(requireContext(), "Please select schedule date and time", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private Notification createNotificationFromForm(String status) {
        Notification notification = new Notification();
        notification.setTitle(etNotificationTitle.getText() != null ? etNotificationTitle.getText().toString().trim() : "");
        notification.setMessage(etMessage.getText() != null ? etMessage.getText().toString().trim() : "");
        notification.setPriority(dropdownPriority.getText().toString());
        notification.setCategory(dropdownCategory.getText().toString());
        notification.setRecipients(dropdownRecipients.getText().toString());
        notification.setStatus(status);

        notification.setScheduled(switchScheduleNotification.isChecked());
        if (switchScheduleNotification.isChecked()) {
            notification.setScheduledDate(etScheduleDate.getText() != null ? etScheduleDate.getText().toString().trim() : "");
            notification.setScheduledTime(etScheduleTime.getText() != null ? etScheduleTime.getText().toString().trim() : "");
        }

        notification.setPushEnabled(switchPushNotification.isChecked());
        notification.setEmailEnabled(switchEmailNotification.isChecked());
        notification.setSMSEnabled(switchSMSNotification.isChecked());

        notification.setAttachmentPath(selectedAttachmentPath);
        notification.setAttachmentName(selectedAttachmentName);

        return notification;
    }

    private void clearForm() {
        if (etNotificationTitle != null) etNotificationTitle.setText("");
        if (etMessage != null) etMessage.setText("");
        if (etScheduleDate != null) etScheduleDate.setText("");
        if (etScheduleTime != null) etScheduleTime.setText("");

        dropdownPriority.setText("Select Priority", false);
        dropdownRecipients.setText("Select Recipients", false);
        dropdownCategory.setText("Select Category", false);

        switchScheduleNotification.setChecked(false);
        switchPushNotification.setChecked(true);
        switchEmailNotification.setChecked(false);
        switchSMSNotification.setChecked(false);

        layoutScheduleOptions.setVisibility(View.GONE);

        selectedAttachmentPath = "";
        selectedAttachmentName = "";

        tvPreviewTitle.setText("Notification title will appear here");
        tvPreviewMessage.setText("Message content will appear here");
    }

    private void loadNotifications() {
        notificationList.clear();
        notificationList.addAll(dbHelper.getAllNotifications());
        if (notificationAdapter != null) {
            notificationAdapter.updateNotifications(notificationList);
        }
    }

    private void loadNotificationForEditing(Notification notification) {
        etNotificationTitle.setText(notification.getTitle());
        etMessage.setText(notification.getMessage());
        dropdownPriority.setText(notification.getPriority(), false);
        dropdownRecipients.setText(notification.getRecipients(), false);
        dropdownCategory.setText(notification.getCategory(), false);

        switchScheduleNotification.setChecked(notification.isScheduled());
        switchPushNotification.setChecked(notification.isPushEnabled());
        switchEmailNotification.setChecked(notification.isEmailEnabled());
        switchSMSNotification.setChecked(notification.isSMSEnabled());

        if (notification.isScheduled()) {
            etScheduleDate.setText(notification.getScheduledDate());
            etScheduleTime.setText(notification.getScheduledTime());
            layoutScheduleOptions.setVisibility(View.VISIBLE);
        }

        selectedAttachmentPath = notification.getAttachmentPath() != null ? notification.getAttachmentPath() : "";
        selectedAttachmentName = notification.getAttachmentName() != null ? notification.getAttachmentName() : "";
    }

    private void showNotificationOptions(Notification notification) {
        // Create and show bottom sheet or dialog with options like Edit, Delete, View History
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Notification Options")
                .setItems(new String[]{"Edit", "Delete", "View History"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // Edit
                            loadNotificationForEditing(notification);
                            break;
                        case 1: // Delete
                            deleteNotification(notification);
                            break;
                        case 2: // View History
                            showNotificationHistoryForId(notification.getId());
                            break;
                    }
                })
                .show();
    }

    private void deleteNotification(Notification notification) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Notification")
                .setMessage("Are you sure you want to delete this notification?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteNotification(notification.getId());
                    loadNotifications();
                    Toast.makeText(requireContext(), "Notification deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showNotificationHistory() {
        // Create and show dialog with all notification history
        historyList.clear();
        historyList.addAll(dbHelper.getAllHistory());

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());

        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new NotificationHistoryAdapter(requireContext(), historyList));

        builder.setTitle("All Notification History")
                .setView(recyclerView)
                .setPositiveButton("Close", null)
                .show();
    }

    private void showNotificationHistoryForId(int notificationId) {
        historyList.clear();
        historyList.addAll(dbHelper.getHistoryByNotificationId(notificationId));

        // Create and show dialog with history RecyclerView
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());

        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new NotificationHistoryAdapter(requireContext(), historyList));

        builder.setTitle("Notification History")
                .setView(recyclerView)
                .setPositiveButton("Close", null)
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