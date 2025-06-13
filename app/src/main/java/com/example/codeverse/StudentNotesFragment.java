package com.example.codeverse;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.Lecturer.Helpers.LectureNotesHelper;
import com.example.codeverse.Lecturer.Models.LecturerNotes;
import com.example.codeverse.R;
import com.example.codeverse.StudentNotesAdapter;
import com.google.android.material.textfield.TextInputEditText;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudentNotesFragment extends Fragment implements StudentNotesAdapter.OnDownloadClickListener {

    private RecyclerView rvNotes;
    private StudentNotesAdapter adapter;
    private LectureNotesHelper dbHelper;
    private List<LecturerNotes> notesList;
    private List<LecturerNotes> filteredNotesList;
    private TextView tvNotesCount;
    private TextInputEditText etSearch;
    private LinearLayout llEmptyState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_notes, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearch();
        loadNotes();

        return view;
    }

    private void initViews(View view) {
        rvNotes = view.findViewById(R.id.rv_notes);
        tvNotesCount = view.findViewById(R.id.tv_notes_count);
        etSearch = view.findViewById(R.id.et_search);
        llEmptyState = view.findViewById(R.id.ll_empty_state);

        dbHelper = new LectureNotesHelper(getContext());
        notesList = new ArrayList<>();
        filteredNotesList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new StudentNotesAdapter(getContext(), filteredNotesList);
        adapter.setOnDownloadClickListener(this);
        rvNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotes.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadNotes() {
        notesList = dbHelper.getAllLecturerNotes();
        filteredNotesList.clear();
        filteredNotesList.addAll(notesList);

        updateUI();
    }

    private void filterNotes(String searchText) {
        if (searchText.isEmpty()) {
            filteredNotesList.clear();
            filteredNotesList.addAll(notesList);
        } else {
            filteredNotesList = dbHelper.searchLecturerNotes(searchText);
        }

        updateUI();
    }

    private void updateUI() {
        if (filteredNotesList.isEmpty()) {
            rvNotes.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
            tvNotesCount.setText("0 Notes");
        } else {
            rvNotes.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
            tvNotesCount.setText(filteredNotesList.size() + " Notes");
        }

        adapter.updateNotesList(filteredNotesList);
    }

    @Override
    public void onDownloadClick(LecturerNotes notes) {
        downloadFile(notes);
    }

    private void downloadFile(LecturerNotes notes) {
        try {
            String filePath = notes.getNotesFilePath();
            File file = new File(filePath);

            if (!file.exists()) {
                Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
                return;
            }

            DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

            DownloadManager.Request request = new DownloadManager.Request(Uri.fromFile(file));
            request.setTitle("Downloading " + notes.getLectureTitle());
            request.setDescription("Lecture notes by " + notes.getLecturerName());
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    "LectureNotes_" + notes.getLectureTitle() + "_" + System.currentTimeMillis() + getFileExtension(filePath));

            downloadManager.enqueue(request);
            Toast.makeText(getContext(), "Download started", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Download failed", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(String filePath) {
        if (filePath.contains(".")) {
            return filePath.substring(filePath.lastIndexOf("."));
        }
        return ".pdf";
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }
}