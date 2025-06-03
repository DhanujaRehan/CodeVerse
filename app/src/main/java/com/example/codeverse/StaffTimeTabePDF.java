package com.example.codeverse;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.codeverse.R;
import com.example.codeverse.Students.Helpers.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StaffTimeTabePDF extends Fragment {

    private static final int PICK_PDF_REQUEST = 1001;

    private MaterialCardView cvBack, cardCurrentWeek, cardNextWeek;
    private TextInputEditText etWeekTitle, etStartDate, etEndDate;
    private AutoCompleteTextView dropdownStatus;
    private MaterialButton btnSelectPDF, btnSaveDraft, btnUpload;
    private TextView tvSelectedFile;
    private FrameLayout loadingOverlay;

    private DatabaseHelper dbHelper;
    private Uri selectedPdfUri;
    private String selectedWeekType = "";
    private Calendar startCalendar, endCalendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_timetabe_pdf, container, false);
        initViews(view);
        setupDatePickers();
        setupDropdown();
        setupClickListeners();
        setupWeekData();
        return view;
    }

    private void initViews(View view) {
        cvBack = view.findViewById(R.id.cv_back);
        cardCurrentWeek = view.findViewById(R.id.card_current_week);
        cardNextWeek = view.findViewById(R.id.card_next_week);
        etWeekTitle = view.findViewById(R.id.etWeekTitle);
        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        dropdownStatus = view.findViewById(R.id.dropdownStatus);
        btnSelectPDF = view.findViewById(R.id.btnSelectPDF);
        btnSaveDraft = view.findViewById(R.id.btnSaveDraft);
        btnUpload = view.findViewById(R.id.btnUpload);
        tvSelectedFile = view.findViewById(R.id.tv_selected_file);
        loadingOverlay = view.findViewById(R.id.loading_overlay);

        dbHelper = new DatabaseHelper(getContext());
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
    }

    private void setupDatePickers() {
        etStartDate.setOnClickListener(v -> showDatePicker(true));
        etEndDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void setupDropdown() {
        String[] statusOptions = {"Available", "Draft", "Unavailable"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, statusOptions);
        dropdownStatus.setAdapter(adapter);
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        cardCurrentWeek.setOnClickListener(v -> selectWeekType("current"));
        cardNextWeek.setOnClickListener(v -> selectWeekType("next"));

        btnSelectPDF.setOnClickListener(v -> openFilePicker());
        btnSaveDraft.setOnClickListener(v -> saveTimetable("Draft"));
        btnUpload.setOnClickListener(v -> saveTimetable("Available"));
    }

    private void setupWeekData() {
        Calendar current = Calendar.getInstance();
        current.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Calendar next = Calendar.getInstance();
        next.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        next.add(Calendar.WEEK_OF_YEAR, 1);
    }

    private void selectWeekType(String weekType) {
        selectedWeekType = weekType;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        if (weekType.equals("next")) {
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            etWeekTitle.setText("Next Week");
        } else {
            etWeekTitle.setText("Current Week");
        }

        startCalendar.setTime(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 6);
        endCalendar.setTime(cal.getTime());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etStartDate.setText(sdf.format(startCalendar.getTime()));
        etEndDate.setText(sdf.format(endCalendar.getTime()));
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String selectedDate = sdf.format(calendar.getTime());

                    if (isStartDate) {
                        etStartDate.setText(selectedDate);
                    } else {
                        etEndDate.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            selectedPdfUri = data.getData();
            if (selectedPdfUri != null) {
                String fileName = getFileName(selectedPdfUri);
                tvSelectedFile.setText(fileName);
                btnSelectPDF.setText("Change PDF");
            }
        }
    }

    private String getFileName(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            return path.substring(path.lastIndexOf('/') + 1);
        }
        return "Selected PDF";
    }

    private void saveTimetable(String status) {
        if (!validateInputs()) {
            return;
        }

        showLoading(true);

        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("week_title", etWeekTitle.getText().toString().trim());
            values.put("week_start_date", etStartDate.getText().toString().trim());
            values.put("week_end_date", etEndDate.getText().toString().trim());
            values.put("pdf_path", selectedPdfUri != null ? selectedPdfUri.toString() : "");
            values.put("file_size", calculateFileSize());
            values.put("status", status);
            values.put("upload_date", getCurrentDateTime());
            values.put("uploaded_by", "Staff");

            long result = db.insert("TimeTable", null, values);
            db.close();

            showLoading(false);

            if (result != -1) {
                String message = status.equals("Draft") ? "Saved as draft successfully!" : "Timetable uploaded successfully!";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(getContext(), "Failed to save timetable", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            showLoading(false);
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs() {
        if (etWeekTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Please enter week title", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etStartDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Please select start date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etEndDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Please select end date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedPdfUri == null) {
            Toast.makeText(getContext(), "Please select a PDF file", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String calculateFileSize() {
        if (selectedPdfUri != null) {
            try {
                return "Unknown";
            } catch (Exception e) {
                return "Unknown";
            }
        }
        return "0KB";
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void clearForm() {
        etWeekTitle.setText("");
        etStartDate.setText("");
        etEndDate.setText("");
        tvSelectedFile.setText("PDF only (Max 10MB)");
        btnSelectPDF.setText("Select PDF");
        dropdownStatus.setText("Available", false);
        selectedPdfUri = null;
        selectedWeekType = "";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}