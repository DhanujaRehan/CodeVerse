package com.example.codeverse.Admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.example.codeverse.R;
import com.example.codeverse.Admin.NotificationDatabaseHelper;
import com.example.codeverse.Admin.Notification;
import com.example.codeverse.Admin.NotificationHistoryActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminSendNotification extends Fragment {

    private AutoCompleteTextView dropdownPriority, dropdownRecipients, dropdownCategory;
    private TextInputEditText etNotificationTitle, etMessage, etScheduleDate, etScheduleTime;
    private SwitchMaterial switchScheduleNotification, switchPushNotification,
            switchEmailNotification, switchSMSNotification;
    private LinearLayout layoutScheduleOptions;
    private MaterialButton btnSaveAsDraft, btnSend, btnBrowse;
    private TextView tvPreviewTitle, tvPreviewMessage;
    private ImageView ivBack, ivNotificationHistory;

    private NotificationDatabaseHelper dbHelper;
    private Calendar selectedDateTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_send_notification, container, false);

        initViews(view);
        setupDropdowns();
        setupListeners();

        dbHelper = new NotificationDatabaseHelper(getContext());
        selectedDateTime = Calendar.getInstance();

        return view;
    }

    private void initViews(View view) {
        // Dropdowns
        dropdownPriority = view.findViewById(R.id.dropdownPriority);
        dropdownRecipients = view.findViewById(R.id.dropdownRecipients);
        dropdownCategory = view.findViewById(R.id.dropdownCategory);

        // Text inputs
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
        btnSaveAsDraft = view.findViewById(R.id.btnSaveAsDraft);
        btnSend = view.findViewById(R.id.btnSend);
        btnBrowse = view.findViewById(R.id.btnBrowse);

        // Preview
        tvPreviewTitle = view.findViewById(R.id.tvPreviewTitle);
        tvPreviewMessage = view.findViewById(R.id.tvPreviewMessage);

        // Navigation
        ivBack = view.findViewById(R.id.ivBack);
        ivNotificationHistory = view.findViewById(R.id.iv_notification_history);
    }

    private void setupDropdowns() {
        // Priority dropdown
        String[] priorities = {"Low", "Medium", "High", "Urgent"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, priorities);
        dropdownPriority.setAdapter(priorityAdapter);

        // Recipients dropdown
        String[] recipients = {"All Students", "All Staff", "All Users", "First Year Students",
                "Second Year Students", "Third Year Students", "Fourth Year Students",
                "Academic Staff", "Administrative Staff"};
        ArrayAdapter<String> recipientsAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, recipients);
        dropdownRecipients.setAdapter(recipientsAdapter);

        // Category dropdown
        String[] categories = {"Academic", "Administrative", "Emergency", "Event",
                "Examination", "General", "Library", "Sports", "Hostel"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, categories);
        dropdownCategory.setAdapter(categoryAdapter);
    }

    private void setupListeners() {
        // Schedule switch listener
        switchScheduleNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutScheduleOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Date picker
        etScheduleDate.setOnClickListener(v -> showDatePicker());

        // Time picker
        etScheduleTime.setOnClickListener(v -> showTimePicker());

        // Preview listeners
        etNotificationTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvPreviewTitle.setText(s.length() > 0 ? s.toString() : "Notification title will appear here");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvPreviewMessage.setText(s.length() > 0 ? s.toString() : "Message content will appear here");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Button listeners
        btnSaveAsDraft.setOnClickListener(v -> saveAsDraft());
        btnSend.setOnClickListener(v -> sendNotification());
        btnBrowse.setOnClickListener(v -> browseFiles());

        // Navigation listeners
        ivBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        ivNotificationHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotificationHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    etScheduleDate.setText(dateFormat.format(selectedDateTime.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    etScheduleTime.setText(timeFormat.format(selectedDateTime.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        timePickerDialog.show();
    }

    private void saveAsDraft() {
        if (validateInput()) {
            Notification notification = createNotificationObject();
            notification.setStatus("draft");

            try {
                saveNotification(notification);
                Toast.makeText(getContext(), "Notification saved as draft", Toast.LENGTH_SHORT).show();
                clearForm();
            } catch (Exception e) {
                Log.e("SaveDraft", "Error saving draft: " + e.getMessage());
                Toast.makeText(getContext(), "Failed to save draft", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendNotification() {
        if (validateInput()) {
            Notification notification = createNotificationObject();
            notification.setStatus(switchScheduleNotification.isChecked() ? "scheduled" : "sent");

            try {
                saveNotification(notification);

                String message = switchScheduleNotification.isChecked() ?
                        "Notification scheduled successfully" : "Notification sent successfully";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                clearForm();

            } catch (Exception e) {
                Log.e("SendNotification", "Error sending notification: " + e.getMessage());
                Toast.makeText(getContext(), "Failed to send notification", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveNotification(Notification notification) {
        try {
            // Insert the notification
            long notificationId = dbHelper.insertNotification(notification);

            if (notificationId > 0) {
                notification.setId((int) notificationId);

                // Insert history record with the correct method signature
                String action = notification.getStatus().toUpperCase(); // "DRAFT", "SENT", "SCHEDULED"
                String details = createNotificationDetails(notification);

                dbHelper.insertHistory((int) notificationId, action, details);

                Log.d("NotificationSaved", "Notification saved with ID: " + notificationId);
            } else {
                Log.e("NotificationError", "Failed to save notification");
            }
        } catch (Exception e) {
            Log.e("NotificationError", "Error saving notification: " + e.getMessage());
        }
    }

    private String createNotificationDetails(Notification notification) {
        StringBuilder details = new StringBuilder();
        details.append("Notification ").append(notification.getStatus());
        details.append(" (").append(notification.getTitle()).append(")");

        // Add delivery method information
        List<String> methods = new ArrayList<>();
        if (notification.isPushEnabled()) methods.add("Push");
        if (notification.isEmailEnabled()) methods.add("Email");
        if (notification.isSmsEnabled()) methods.add("SMS");

        if (!methods.isEmpty()) {
            details.append(" via ").append(String.join(", ", methods));
        }

        // Add recipient information
        details.append(" to ").append(notification.getRecipients());

        return details.toString();
    }

    private boolean validateInput() {
        if (dropdownPriority.getText().toString().trim().isEmpty() ||
                dropdownPriority.getText().toString().equals("Select Priority")) {
            Toast.makeText(getContext(), "Please select priority level", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dropdownRecipients.getText().toString().trim().isEmpty() ||
                dropdownRecipients.getText().toString().equals("Select Recipients")) {
            Toast.makeText(getContext(), "Please select recipients", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dropdownCategory.getText().toString().trim().isEmpty() ||
                dropdownCategory.getText().toString().equals("Select Category")) {
            Toast.makeText(getContext(), "Please select category", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etNotificationTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Please enter notification title", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etMessage.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Please enter message", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (switchScheduleNotification.isChecked()) {
            if (etScheduleDate.getText().toString().trim().isEmpty() ||
                    etScheduleTime.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "Please select date and time for scheduling", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Validate that scheduled time is in the future
            if (selectedDateTime.getTimeInMillis() <= System.currentTimeMillis()) {
                Toast.makeText(getContext(), "Scheduled time must be in the future", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private Notification createNotificationObject() {
        Notification notification = new Notification();
        notification.setTitle(etNotificationTitle.getText().toString().trim());
        notification.setMessage(etMessage.getText().toString().trim());
        notification.setPriority(dropdownPriority.getText().toString());
        notification.setCategory(dropdownCategory.getText().toString());
        notification.setRecipients(dropdownRecipients.getText().toString());
        notification.setPushEnabled(switchPushNotification.isChecked());
        notification.setEmailEnabled(switchEmailNotification.isChecked());
        notification.setSmsEnabled(switchSMSNotification.isChecked());

        if (switchScheduleNotification.isChecked()) {
            notification.setScheduledTime(selectedDateTime.getTimeInMillis());
        } else {
            notification.setScheduledTime(System.currentTimeMillis());
        }

        notification.setCreatedAt(System.currentTimeMillis());

        return notification;
    }

    private void clearForm() {
        etNotificationTitle.setText("");
        etMessage.setText("");
        etScheduleDate.setText("");
        etScheduleTime.setText("");
        dropdownPriority.setText("Select Priority", false);
        dropdownRecipients.setText("Select Recipients", false);
        dropdownCategory.setText("Select Category", false);
        switchScheduleNotification.setChecked(false);
        switchPushNotification.setChecked(true);
        switchEmailNotification.setChecked(false);
        switchSMSNotification.setChecked(false);
        layoutScheduleOptions.setVisibility(View.GONE);
        tvPreviewTitle.setText("Notification title will appear here");
        tvPreviewMessage.setText("Message content will appear here");

        // Reset selected date time
        selectedDateTime = Calendar.getInstance();
    }

    private void browseFiles() {
        // File browsing functionality - you can implement this based on your requirements
        Toast.makeText(getContext(), "File browsing feature to be implemented", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}