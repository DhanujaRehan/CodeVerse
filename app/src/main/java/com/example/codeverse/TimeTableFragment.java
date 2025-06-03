package com.example.codeverse;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeTableFragment extends Fragment {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;

    private RecyclerView rvClasses;
    private TextView tvTotalClasses, tvActiveClasses, tvNextClassTime;
    private TextView tvClassesTitle, tvDate, tvDownloadCount;
    private View layoutEmptyState;
    private CardView cardDownloadAll;
    private TabLayout tabLayout;
    private FloatingActionButton fabAddClass;
    private ImageView ivFilter;

    private TimeTableAdapter adapter;
    private List<TimeTableModel> timeTableList;
    private TimeTableDBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_table, container, false);

        initViews(view);
        setupDatabase();
        setupRecyclerView();
        setupTabLayout();
        setupClickListeners();
        loadTimeTableData();
        updateUI();

        return view;
    }

    private void initViews(View view) {
        rvClasses = view.findViewById(R.id.rv_classes);
        tvTotalClasses = view.findViewById(R.id.tv_total_classes);
        tvActiveClasses = view.findViewById(R.id.tv_active_classes);
        tvNextClassTime = view.findViewById(R.id.tv_next_class_time);
        tvClassesTitle = view.findViewById(R.id.tv_classes_title);
        tvDate = view.findViewById(R.id.tv_date);
        tvDownloadCount = view.findViewById(R.id.tv_download_count);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        cardDownloadAll = view.findViewById(R.id.card_download_all);
        tabLayout = view.findViewById(R.id.tab_layout);
        fabAddClass = view.findViewById(R.id.fab_add_class);
        ivFilter = view.findViewById(R.id.iv_filter);

        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        tvDate.setText(dateFormat.format(new Date()));
    }

    private void setupDatabase() {
        dbHelper = new TimeTableDBHelper(getContext());
    }

    private void setupRecyclerView() {
        timeTableList = new ArrayList<>();
        adapter = new TimeTableAdapter(timeTableList, this::downloadPDF);
        rvClasses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvClasses.setAdapter(adapter);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Today
                        loadTodaysClasses();
                        tvClassesTitle.setText("Today's Classes");
                        break;
                    case 1: // This Week
                        loadThisWeekClasses();
                        tvClassesTitle.setText("This Week's Classes");
                        break;
                    case 2: // All Classes
                        loadAllClasses();
                        tvClassesTitle.setText("All Classes");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupClickListeners() {
        fabAddClass.setOnClickListener(v -> {
            // Navigate to add class screen
            Toast.makeText(getContext(), "Add new class", Toast.LENGTH_SHORT).show();
        });

        ivFilter.setOnClickListener(v -> {
            // Show filter options
            Toast.makeText(getContext(), "Filter classes", Toast.LENGTH_SHORT).show();
        });

        cardDownloadAll.setOnClickListener(v -> {
            downloadAllPDFs();
        });
    }

    private void loadTimeTableData() {
        loadTodaysClasses();
    }

    private void loadTodaysClasses() {
        timeTableList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get today's date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(new Date());

        String query = "SELECT * FROM TimeTable WHERE date = ? ORDER BY start_time ASC";
        Cursor cursor = db.rawQuery(query, new String[]{today});

        loadDataFromCursor(cursor);
        cursor.close();
        db.close();
    }

    private void loadThisWeekClasses() {
        timeTableList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM TimeTable WHERE date >= date('now', 'weekday 0', '-6 days') " +
                "AND date <= date('now', 'weekday 0') ORDER BY date ASC, start_time ASC";
        Cursor cursor = db.rawQuery(query, null);

        loadDataFromCursor(cursor);
        cursor.close();
        db.close();
    }

    private void loadAllClasses() {
        timeTableList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM TimeTable ORDER BY date ASC, start_time ASC";
        Cursor cursor = db.rawQuery(query, null);

        loadDataFromCursor(cursor);
        cursor.close();
        db.close();
    }

    private void loadDataFromCursor(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            do {
                TimeTableModel model = new TimeTableModel();
                model.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                model.setSubjectName(cursor.getString(cursor.getColumnIndexOrThrow("subject_name")));
                model.setTeacherName(cursor.getString(cursor.getColumnIndexOrThrow("teacher_name")));
                model.setClassroom(cursor.getString(cursor.getColumnIndexOrThrow("classroom")));
                model.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                model.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow("start_time")));
                model.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow("end_time")));
                model.setPdfUrl(cursor.getString(cursor.getColumnIndexOrThrow("pdf_url")));
                model.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));

                timeTableList.add(model);
            } while (cursor.moveToNext());
        }

        adapter.notifyDataSetChanged();
        updateUI();
    }

    private void updateUI() {
        if (timeTableList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvClasses.setVisibility(View.GONE);
            cardDownloadAll.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvClasses.setVisibility(View.VISIBLE);

            // Show download all button only if there are PDFs available
            int availablePDFs = 0;
            for (TimeTableModel model : timeTableList) {
                if (model.getPdfUrl() != null && !model.getPdfUrl().isEmpty()) {
                    availablePDFs++;
                }
            }

            if (availablePDFs > 0) {
                cardDownloadAll.setVisibility(View.VISIBLE);
                tvDownloadCount.setText(String.valueOf(availablePDFs));
            } else {
                cardDownloadAll.setVisibility(View.GONE);
            }
        }

        // Update stats
        int totalClasses = timeTableList.size();
        int activeClasses = 0;
        String nextClassTime = "--:--";

        for (TimeTableModel model : timeTableList) {
            if ("active".equalsIgnoreCase(model.getStatus())) {
                activeClasses++;
            }
        }

        // Find next class time
        if (!timeTableList.isEmpty()) {
            nextClassTime = timeTableList.get(0).getStartTime();
        }

        tvTotalClasses.setText(String.valueOf(totalClasses));
        tvActiveClasses.setText(String.valueOf(activeClasses));
        tvNextClassTime.setText(nextClassTime);
    }

    private void downloadPDF(TimeTableModel model) {
        if (model.getPdfUrl() == null || model.getPdfUrl().isEmpty()) {
            Toast.makeText(getContext(), "PDF not available for this class", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check storage permission
        if (!hasStoragePermission()) {
            requestStoragePermission();
            return;
        }

        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(model.getPdfUrl()));
            request.setDescription("Downloading " + model.getSubjectName() + " timetable");
            request.setTitle(model.getSubjectName() + "_TimeTable.pdf");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    model.getSubjectName() + "_TimeTable_" + System.currentTimeMillis() + ".pdf");

            DownloadManager manager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
                Toast.makeText(getContext(), "Download started...", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadAllPDFs() {
        if (!hasStoragePermission()) {
            requestStoragePermission();
            return;
        }

        int downloadCount = 0;
        for (TimeTableModel model : timeTableList) {
            if (model.getPdfUrl() != null && !model.getPdfUrl().isEmpty()) {
                downloadPDFSilent(model);
                downloadCount++;
            }
        }

        if (downloadCount > 0) {
            Toast.makeText(getContext(), "Downloading " + downloadCount + " PDF files...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No PDF files available to download", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadPDFSilent(TimeTableModel model) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(model.getPdfUrl()));
            request.setDescription("Downloading " + model.getSubjectName() + " timetable");
            request.setTitle(model.getSubjectName() + "_TimeTable.pdf");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    model.getSubjectName() + "_TimeTable_" + System.currentTimeMillis() + ".pdf");

            DownloadManager manager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
            }
        } catch (Exception e) {
            // Silent download - don't show individual errors
        }
    }

    private boolean hasStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Android 10 and above doesn't need WRITE_EXTERNAL_STORAGE for downloads
            return true;
        } else {
            return ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission granted. Please try downloading again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Storage permission is required to download files.", Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}