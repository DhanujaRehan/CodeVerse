package com.example.codeverse.Admin.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.codeverse.Admin.Helpers.NotificationDatabaseHelper;
import com.example.codeverse.Admin.Activities.NotificationHistoryActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.example.codeverse.R;
import com.example.codeverse.Admin.Models.Notification;

import java.io.InputStream;
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
    private LinearLayout layoutScheduleOptions, layoutAttachedFiles;
    private MaterialButton btnSaveAsDraft, btnSend, btnBrowse;
    private TextView tvPreviewTitle, tvPreviewMessage;
    private ImageView ivBack, ivNotificationHistory;

    private NotificationDatabaseHelper dbHelper;
    private Calendar selectedDateTime;

    // File browser variables
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ArrayList<Uri> selectedFiles = new ArrayList<>();
    private ArrayList<String> selectedFileNames = new ArrayList<>();
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_send_notification, container, false);

        initViews(view);
        setupDropdowns();
        setupListeners();
        setupFilePickerLauncher();

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
        layoutAttachedFiles = view.findViewById(R.id.layoutAttachedFiles);

        // Buttons
        btnSaveAsDraft = view.findViewById(R.id.btnSaveAsDraft);
        btnSend = view.findViewById(R.id.btnSend);
        btnBrowse = view.findViewById(R.id.btnBrowse);

        // Preview
        tvPreviewTitle = view.findViewById(R.id.tvPreviewTitle);
        tvPreviewMessage = view.findViewById(R.id.tvPreviewMessage);

        // Navigation
        ivBack = view.findViewById(R.id.iv_back);
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

    private void setupFilePickerLauncher() {
        // File picker launcher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                handleSelectedFiles(data);
                            }
                        }
                    }
                }
        );

        // Permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        showFileSelectionDialog();
                    } else {
                        Toast.makeText(getContext(), "Permission denied. Cannot access files.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void browseFiles() {
        if (checkStoragePermission()) {
            showFileSelectionDialog();
        } else {
            requestStoragePermission();
        }
    }

    private boolean checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ doesn't need storage permission for file picker
            return true;
        } else {
            return ContextCompat.checkSelfPermission(
                    getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // No permission needed for Android 13+
            showFileSelectionDialog();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void showFileSelectionDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_file_options, null);

        LinearLayout layoutGallery = bottomSheetView.findViewById(R.id.layoutGallery);
        LinearLayout layoutDocuments = bottomSheetView.findViewById(R.id.layoutDocuments);
        LinearLayout layoutAllFiles = bottomSheetView.findViewById(R.id.layoutAllFiles);


        layoutGallery.setOnClickListener(v -> {
            openGallery();
            bottomSheetDialog.dismiss();
        });

        layoutDocuments.setOnClickListener(v -> {
            openDocuments();
            bottomSheetDialog.dismiss();
        });

        layoutAllFiles.setOnClickListener(v -> {
            openAllFiles();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

        filePickerLauncher.launch(Intent.createChooser(galleryIntent, "Select Images"));
    }

    private void openDocuments() {
        Intent documentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        documentIntent.setType("*/*");
        String[] mimeTypes = {
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "text/plain"
        };
        documentIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        documentIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        documentIntent.addCategory(Intent.CATEGORY_OPENABLE);

        filePickerLauncher.launch(Intent.createChooser(documentIntent, "Select Documents"));
    }

    private void openAllFiles() {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
        fileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        filePickerLauncher.launch(Intent.createChooser(fileIntent, "Select Files"));
    }

    private void handleSelectedFiles(Intent data) {
        if (data.getClipData() != null) {
            // Multiple files selected
            ClipData clipData = data.getClipData();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                Uri fileUri = clipData.getItemAt(i).getUri();
                if (validateFile(fileUri)) {
                    addSelectedFile(fileUri);
                }
            }
        } else if (data.getData() != null) {
            // Single file selected
            Uri fileUri = data.getData();
            if (validateFile(fileUri)) {
                addSelectedFile(fileUri);
            }
        }

        updateAttachedFilesUI();
    }

    private boolean validateFile(Uri fileUri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                long sizeInBytes = inputStream.available();
                long sizeInMB = sizeInBytes / (1024 * 1024);
                inputStream.close();

                if (sizeInMB > 10) {
                    Toast.makeText(getContext(), "File too large. Maximum size is 10MB.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            Log.e("FileValidation", "Error validating file: " + e.getMessage());
            Toast.makeText(getContext(), "Invalid file selected", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    private void addSelectedFile(Uri fileUri) {
        if (!selectedFiles.contains(fileUri)) {
            selectedFiles.add(fileUri);
            String fileName = getFileName(fileUri);
            selectedFileNames.add(fileName);
            Log.d("FileSelection", "Added file: " + fileName);
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "Unknown";
        try {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex);
                }
                cursor.close();
            }

            if (fileName == null || fileName.equals("Unknown")) {
                fileName = "File_" + System.currentTimeMillis();
            }
        } catch (Exception e) {
            Log.e("FileName", "Error getting file name: " + e.getMessage());
            fileName = "File_" + System.currentTimeMillis();
        }
        return fileName;
    }

    private void updateAttachedFilesUI() {
        if (layoutAttachedFiles != null) {
            layoutAttachedFiles.removeAllViews();

            for (int i = 0; i < selectedFileNames.size(); i++) {
                View fileItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_attached_file, null);

                TextView tvFileName = fileItemView.findViewById(R.id.tvFileName);
                TextView tvFileSize = fileItemView.findViewById(R.id.tvFileSize);
                ImageView ivRemoveFile = fileItemView.findViewById(R.id.ivRemoveFile);
                ImageView ivFileIcon = fileItemView.findViewById(R.id.ivFileIcon);

                String fileName = selectedFileNames.get(i);
                tvFileName.setText(fileName);

                // Set file size
                try {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(selectedFiles.get(i));
                    if (inputStream != null) {
                        long sizeInBytes = inputStream.available();
                        tvFileSize.setText(formatFileSize(sizeInBytes));
                        inputStream.close();
                    }
                } catch (Exception e) {
                    tvFileSize.setText("Unknown size");
                }

                // Set file icon based on type
                setFileIcon(ivFileIcon, fileName);

                // Remove file listener
                final int fileIndex = i;
                ivRemoveFile.setOnClickListener(v -> {
                    selectedFiles.remove(fileIndex);
                    selectedFileNames.remove(fileIndex);
                    updateAttachedFilesUI();
                    Toast.makeText(getContext(), "File removed", Toast.LENGTH_SHORT).show();
                });

                layoutAttachedFiles.addView(fileItemView);
            }

            // Show/hide the files container
            if (layoutAttachedFiles != null) {
                layoutAttachedFiles.setVisibility(selectedFiles.isEmpty() ? View.GONE : View.VISIBLE);
            }
        }
    }

    private String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        } else if (sizeInBytes < 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.1f KB", sizeInBytes / 1024.0);
        } else {
            return String.format(Locale.getDefault(), "%.1f MB", sizeInBytes / (1024.0 * 1024.0));
        }
    }

    private void setFileIcon(ImageView ivFileIcon, String fileName) {
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = fileName.substring(dotIndex + 1).toLowerCase();
        }

        switch (extension) {
            case "pdf":
                ivFileIcon.setImageResource(R.drawable.pdf);
                break;
            case "doc":
            case "docx":
                ivFileIcon.setImageResource(R.drawable.word);
                break;
            case "xls":
            case "xlsx":
                ivFileIcon.setImageResource(R.drawable.xls);
                break;
            case "ppt":
            case "pptx":
                ivFileIcon.setImageResource(R.drawable.ppt);
                break;
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                ivFileIcon.setImageResource(R.drawable.image);
                break;
            case "txt":
                ivFileIcon.setImageResource(R.drawable.txt);
                break;
            default:
                ivFileIcon.setImageResource(R.drawable.greyfile);
                break;
        }
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

        // Add attachment info if any
        if (!selectedFiles.isEmpty()) {
            details.append(" with ").append(selectedFiles.size()).append(" attachment(s)");
        }

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

        // Add attachment paths (store as comma-separated string)
        if (!selectedFiles.isEmpty()) {
            StringBuilder attachmentPaths = new StringBuilder();
            for (int i = 0; i < selectedFiles.size(); i++) {
                if (i > 0) attachmentPaths.append(",");
                attachmentPaths.append(selectedFiles.get(i).toString());
            }
            notification.setAttachmentPath(attachmentPaths.toString());
        }

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

        // Clear attachments
        selectedFiles.clear();
        selectedFileNames.clear();
        updateAttachedFilesUI();

        // Reset selected date time
        selectedDateTime = Calendar.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}