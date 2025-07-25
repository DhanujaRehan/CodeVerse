package com.example.codeverse.Students.StudentFragments;

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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Students.Adapters.TimetableAdapter;
import com.example.codeverse.Students.Helpers.DatabaseHelper;
import com.example.codeverse.Students.Models.TimetableItem;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimetableDownloadFragment extends Fragment implements TimetableAdapter.OnDownloadClickListener {

    private RecyclerView rvTimetables;
    private TimetableAdapter adapter;
    private List<TimetableItem> timetableList;
    private List<TimetableItem> filteredList;
    private CircularProgressIndicator progressLoading;
    private DatabaseHelper dbHelper;
    private TextView tvTotalDownloads, tvThisWeek, tvLastDownload;
    private MaterialCardView cardCurrentWeek, cardNextWeek;
    private ImageView ivBack, ivRefresh;
    private TextView tvDownloadAll;

    private boolean isCurrentWeekSelected = false;
    private boolean isNextWeekSelected = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetabledownloa, container, false);
        initViews(view);
        setupClickListeners();
        loadTimetables();
        updateStats();
        return view;
    }

    private void initViews(View view) {
        rvTimetables = view.findViewById(R.id.rv_timetables);
        progressLoading = view.findViewById(R.id.progress_loading);
        tvTotalDownloads = view.findViewById(R.id.tv_total_downloads);
        tvThisWeek = view.findViewById(R.id.tv_this_week);
        tvLastDownload = view.findViewById(R.id.tv_last_download);
        cardCurrentWeek = view.findViewById(R.id.card_current_week);
        cardNextWeek = view.findViewById(R.id.card_next_week);
        ivBack = view.findViewById(R.id.iv_back);
        ivRefresh = view.findViewById(R.id.iv_refresh);
        tvDownloadAll = view.findViewById(R.id.tv_download_all);

        dbHelper = new DatabaseHelper(getContext());
        timetableList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new TimetableAdapter(filteredList, this);
        rvTimetables.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTimetables.setAdapter(adapter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        ivRefresh.setOnClickListener(v -> {
            loadTimetables();
            updateStats();
            resetFilters();
        });

        cardCurrentWeek.setOnClickListener(v -> toggleCurrentWeekFilter());

        cardNextWeek.setOnClickListener(v -> toggleNextWeekFilter());

        tvDownloadAll.setOnClickListener(v -> downloadAllTimetables());
    }

    private void toggleCurrentWeekFilter() {
        isCurrentWeekSelected = !isCurrentWeekSelected;
        if (isCurrentWeekSelected) {
            isNextWeekSelected = false;
        }
        updateCardStates();
        applyFilter();
    }

    private void toggleNextWeekFilter() {
        isNextWeekSelected = !isNextWeekSelected;
        if (isNextWeekSelected) {
            isCurrentWeekSelected = false;
        }
        updateCardStates();
        applyFilter();
    }

    private void updateCardStates() {
        if (isCurrentWeekSelected) {
            cardCurrentWeek.setCardElevation(8);
            cardCurrentWeek.setStrokeWidth(2);
            cardCurrentWeek.setStrokeColor(ContextCompat.getColor(getContext(), R.color.green_primary));
        } else {
            cardCurrentWeek.setCardElevation(4);
            cardCurrentWeek.setStrokeWidth(0);
        }

        if (isNextWeekSelected) {
            cardNextWeek.setCardElevation(8);
            cardNextWeek.setStrokeWidth(2);
            cardNextWeek.setStrokeColor(ContextCompat.getColor(getContext(), R.color.blue_primary));
        } else {
            cardNextWeek.setCardElevation(4);
            cardNextWeek.setStrokeWidth(0);
        }
    }

    private void applyFilter() {
        filteredList.clear();

        if (!isCurrentWeekSelected && !isNextWeekSelected) {
            filteredList.addAll(timetableList);
        } else if (isCurrentWeekSelected) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            String currentWeekStart = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

            for (TimetableItem item : timetableList) {
                if (item.startDate != null && item.startDate.equals(currentWeekStart)) {
                    filteredList.add(item);
                }
            }
        } else if (isNextWeekSelected) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            String nextWeekStart = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

            for (TimetableItem item : timetableList) {
                if (item.startDate != null && item.startDate.equals(nextWeekStart)) {
                    filteredList.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();

        if (filteredList.isEmpty()) {
            String message = isCurrentWeekSelected ? "No current week timetable found" :
                    isNextWeekSelected ? "No next week timetable found" : "";
            if (!message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetFilters() {
        isCurrentWeekSelected = false;
        isNextWeekSelected = false;
        updateCardStates();
        applyFilter();
    }

    private void loadTimetables() {
        progressLoading.setVisibility(View.VISIBLE);
        timetableList.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM TimeTable ORDER BY week_start_date DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                TimetableItem item = new TimetableItem();
                item.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                item.weekTitle = cursor.getString(cursor.getColumnIndexOrThrow("week_title"));
                item.startDate = cursor.getString(cursor.getColumnIndexOrThrow("week_start_date"));
                item.endDate = cursor.getString(cursor.getColumnIndexOrThrow("week_end_date"));
                item.pdfPath = cursor.getString(cursor.getColumnIndexOrThrow("pdf_path"));
                item.fileSize = cursor.getString(cursor.getColumnIndexOrThrow("file_size"));
                item.status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                timetableList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        applyFilter();
        progressLoading.setVisibility(View.GONE);
    }

    private void updateStats() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor totalCursor = db.rawQuery("SELECT COUNT(*) FROM downloads WHERE type = 'timetable'", null);
        int totalDownloads = 0;
        if (totalCursor.moveToFirst()) {
            totalDownloads = totalCursor.getInt(0);
        }
        totalCursor.close();

        Calendar weekStart = Calendar.getInstance();
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        weekStart.set(Calendar.HOUR_OF_DAY, 0);
        weekStart.set(Calendar.MINUTE, 0);
        weekStart.set(Calendar.SECOND, 0);

        String weekStartStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(weekStart.getTime());
        Cursor weekCursor = db.rawQuery("SELECT COUNT(*) FROM downloads WHERE type = 'timetable' AND download_date >= ?", new String[]{weekStartStr});
        int weekDownloads = 0;
        if (weekCursor.moveToFirst()) {
            weekDownloads = weekCursor.getInt(0);
        }
        weekCursor.close();

        Cursor lastCursor = db.rawQuery("SELECT download_date FROM downloads WHERE type = 'timetable' ORDER BY download_date DESC LIMIT 1", null);
        String lastDownload = "Never";
        if (lastCursor.moveToFirst()) {
            String lastDate = lastCursor.getString(0);
            lastDownload = getTimeAgo(lastDate);
        }
        lastCursor.close();
        db.close();

        tvTotalDownloads.setText(String.valueOf(totalDownloads));
        tvThisWeek.setText(String.valueOf(weekDownloads));
        tvLastDownload.setText(lastDownload);
    }

    private String getTimeAgo(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            if (date != null) {
                long diff = System.currentTimeMillis() - date.getTime();
                long days = diff / (24 * 60 * 60 * 1000);
                if (days == 0) return "Today";
                if (days == 1) return "1d";
                return days + "d";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private void downloadAllTimetables() {
        requestStoragePermission();

        int downloadCount = 0;
        List<TimetableItem> itemsToDownload = filteredList.isEmpty() ? timetableList : filteredList;

        for (TimetableItem item : itemsToDownload) {
            if ("Available".equals(item.status)) {
                startDownload(item);
                downloadCount++;
            }
        }

        if (downloadCount > 0) {
            Toast.makeText(getContext(), "Downloading " + downloadCount + " timetables", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No timetables available for download", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDownloadClick(TimetableItem item) {
        requestStoragePermission();
        startDownload(item);
    }

    private void startDownload(TimetableItem item) {
        if (item == null || item.pdfPath == null || item.pdfPath.isEmpty()) {
            Toast.makeText(getContext(), "Invalid timetable data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!"Available".equals(item.status)) {
            Toast.makeText(getContext(), "Timetable is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

            Uri uri = Uri.parse(item.pdfPath);
            DownloadManager.Request request = new DownloadManager.Request(uri);

            String fileName = "Timetable_" + item.weekTitle.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setTitle("Timetable - " + item.weekTitle);
            request.setDescription("Downloading timetable PDF");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setMimeType("application/pdf");

            downloadManager.enqueue(request);
            saveDownloadRecord(item.id, fileName);

            Toast.makeText(getContext(), "Download started: " + item.weekTitle, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Download failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveDownloadRecord(int timetableId, String fileName) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String query = "INSERT INTO downloads (timetable_id, file_name, download_date, type) VALUES (?, ?, ?, 'timetable')";
            db.execSQL(query, new Object[]{timetableId, fileName, currentTime});
            db.close();
            updateStats();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to save download record", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            return;
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Storage permission required", Toast.LENGTH_SHORT).show();
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