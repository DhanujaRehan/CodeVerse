package com.example.codeverse.Lecturer.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Staff.Adapters.ScheduleAdapter;
import com.example.codeverse.Staff.Helper.ClassScheduleHelper;
import com.example.codeverse.Staff.Models.LecturerClassSchedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LecturerHomeFragment extends Fragment {

    private RecyclerView recyclerViewSchedules;
    private TextView tvNoSchedules;
    private ScheduleAdapter scheduleAdapter;
    private ClassScheduleHelper classScheduleHelper;

    private LinearLayout layoutLecturerProfile;
    private LinearLayout layoutEditProfile;
    private LinearLayout layoutGradeSubmission;
    private LinearLayout layoutSendNotes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecturer_home, container, false);

        initViews(view);
        setupRecyclerView();
        loadTodaySchedules();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        recyclerViewSchedules = view.findViewById(R.id.recycler_view_schedules);
        tvNoSchedules = view.findViewById(R.id.tv_no_schedules);

        layoutLecturerProfile = view.findViewById(R.id.layout_lecturer_profile);
        layoutEditProfile = view.findViewById(R.id.layout_edit_profile);
        layoutGradeSubmission = view.findViewById(R.id.layout_grade_submission);
        layoutSendNotes = view.findViewById(R.id.layout_send_notes);

        classScheduleHelper = new ClassScheduleHelper(getContext());
    }

    private void setupRecyclerView() {
        recyclerViewSchedules.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSchedules.setNestedScrollingEnabled(false);
    }

    private void loadTodaySchedules() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        List<LecturerClassSchedule> schedules = classScheduleHelper.getLecturerSchedulesByDate(todayDate);

        if (schedules.isEmpty()) {
            recyclerViewSchedules.setVisibility(View.GONE);
            tvNoSchedules.setVisibility(View.VISIBLE);
        } else {
            recyclerViewSchedules.setVisibility(View.VISIBLE);
            tvNoSchedules.setVisibility(View.GONE);

            scheduleAdapter = new ScheduleAdapter(schedules);
            recyclerViewSchedules.setAdapter(scheduleAdapter);
        }
    }

    private void setupClickListeners() {
        layoutLecturerProfile.setOnClickListener(v -> openFragment(new LecturerProfile()));
        layoutEditProfile.setOnClickListener(v -> openFragment(new LecturerEditProfile()));
        layoutGradeSubmission.setOnClickListener(v -> openFragment(new GradeSubmission()));
        layoutSendNotes.setOnClickListener(v -> openFragment(new LecturerNotesFragment()));
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}