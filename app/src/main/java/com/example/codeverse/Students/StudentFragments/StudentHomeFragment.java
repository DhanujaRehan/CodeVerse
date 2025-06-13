package com.example.codeverse.Students.StudentFragments;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import com.google.android.material.card.MaterialCardView;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Helper.EventHelper;
import com.example.codeverse.Staff.Helper.ClassScheduleHelper;
import com.example.codeverse.Staff.Models.Event;
import com.example.codeverse.Staff.Models.StudentClassSchedule;
import com.example.codeverse.Students.Adapters.EventAdapter;
import com.example.codeverse.Students.Adapters.StudentScheduleAdapter;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentHomeFragment extends Fragment {

    private ImageView iv_notification, iv_profile, iv_close_notification;
    private TextView tv_attendance, tv_gpa, tv_credits;
    private TextView tv_view_all_schedules, tv_view_all_assignments;
    private LinearLayout layout_assignments, layout_grades, layout_calendar, layout_resources;
    private MaterialCardView cv_notification, cv_profile;
    private CardView card_notification_banner;
    private RecyclerView rv_events, rv_schedules;
    private FrameLayout layout_empty_schedules, layout_empty_events;

    private EventHelper eventHelper;
    private ClassScheduleHelper scheduleHelper;
    private EventAdapter eventAdapter;
    private StudentScheduleAdapter scheduleAdapter;
    private List<Event> eventList;
    private List<StudentClassSchedule> scheduleList;

    public StudentHomeFragment() {}

    public static StudentHomeFragment newInstance() {
        StudentHomeFragment fragment = new StudentHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupClickListeners();
        loadData();
        setupRecyclerViews();
        loadEvents();
        loadSchedules();
    }

    private void initViews(View view) {
        iv_notification = view.findViewById(R.id.iv_notification);
        iv_profile = view.findViewById(R.id.iv_profile);
        iv_close_notification = view.findViewById(R.id.iv_close_notification);

        tv_attendance = view.findViewById(R.id.tv_attendance);
        tv_gpa = view.findViewById(R.id.tv_gpa);
        tv_credits = view.findViewById(R.id.tv_credits);

        tv_view_all_schedules = view.findViewById(R.id.tv_view_all_schedules);
        tv_view_all_assignments = view.findViewById(R.id.tv_view_all_assignments);

        layout_assignments = view.findViewById(R.id.layout_assignments);
        layout_grades = view.findViewById(R.id.layout_grades);
        layout_calendar = view.findViewById(R.id.layout_calendar);
        layout_resources = view.findViewById(R.id.layout_resources);

        cv_notification = view.findViewById(R.id.cv_notification);
        cv_profile = view.findViewById(R.id.cv_profile);
        card_notification_banner = view.findViewById(R.id.card_notification_banner);

        rv_events = view.findViewById(R.id.rv_events);
        rv_schedules = view.findViewById(R.id.rv_schedules);
        layout_empty_schedules = view.findViewById(R.id.layout_empty_schedules);
        layout_empty_events = view.findViewById(R.id.layout_empty_events);

        eventHelper = new EventHelper(getContext());
        scheduleHelper = new ClassScheduleHelper(getContext());
    }

    private void setupRecyclerViews() {
        rv_events.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_schedules.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadEvents() {
        eventList = eventHelper.getAllEvents();
        if (eventList != null && !eventList.isEmpty()) {
            eventAdapter = new EventAdapter(getContext(), eventList);
            rv_events.setAdapter(eventAdapter);
            rv_events.setVisibility(View.VISIBLE);
            layout_empty_events.setVisibility(View.GONE);
        } else {
            rv_events.setVisibility(View.GONE);
            layout_empty_events.setVisibility(View.VISIBLE);
        }
    }

    private void loadSchedules() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        scheduleList = scheduleHelper.getStudentSchedulesByDate(currentDate);
        if (scheduleList != null && !scheduleList.isEmpty()) {
            scheduleAdapter = new StudentScheduleAdapter(getContext(), scheduleList);
            rv_schedules.setAdapter(scheduleAdapter);
            rv_schedules.setVisibility(View.VISIBLE);
            layout_empty_schedules.setVisibility(View.GONE);
        } else {
            rv_schedules.setVisibility(View.GONE);
            layout_empty_schedules.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        cv_notification.setOnClickListener(v -> openNotifications());

        cv_profile.setOnClickListener(v -> openProfile());

        iv_close_notification.setOnClickListener(v -> {
            card_notification_banner.setVisibility(View.GONE);
        });

        layout_assignments.setOnClickListener(v -> openAssignments());

        layout_grades.setOnClickListener(v -> openGrades());

        layout_calendar.setOnClickListener(v -> openCalendar());

        layout_resources.setOnClickListener(v -> openResources());

        tv_view_all_schedules.setOnClickListener(v -> openSchedule());

        tv_view_all_assignments.setOnClickListener(v -> openAssignments());
    }

    private void loadData() {
        tv_attendance.setText("97%");
        tv_gpa.setText("3.85");
        tv_credits.setText("76");
    }

    private void openNotifications() {
        StudentNotificationFragment fragment = new StudentNotificationFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openProfile() {
        StudentProfile studentProfile = new StudentProfile();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, studentProfile);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openAssignments() {
        AssignmentUpload assignmentUpload = new AssignmentUpload();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, assignmentUpload);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openGrades() {
        AssignmentUpload assignmentUpload = new AssignmentUpload();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, assignmentUpload);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openCalendar() {
        StudentClass studentClass = new StudentClass();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, studentClass);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openResources() {

    }

    private void openSchedule() {
        StudentExam studentExam = new StudentExam();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, studentExam);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}