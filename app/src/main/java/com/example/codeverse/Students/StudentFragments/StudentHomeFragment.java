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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Helper.EventHelper;
import com.example.codeverse.Staff.Models.Event;
import com.example.codeverse.EventAdapter;
import java.util.List;

public class StudentHomeFragment extends Fragment {

    private ImageView iv_notification, iv_profile, iv_close_notification;
    private TextView tv_attendance, tv_gpa, tv_credits;
    private TextView tv_class_name, tv_professor_name, tv_class_time, tv_class_location;
    private TextView tv_view_schedule, tv_view_all_assignments;
    private LinearLayout layout_assignments, layout_grades, layout_calendar, layout_resources;
    private MaterialCardView cv_notification, cv_profile;
    private CardView card_notification_banner;
    private MaterialButton btn_join_class;
    private RecyclerView rv_events;

    private EventHelper eventHelper;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

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
        setupRecyclerView();
        loadEvents();
    }

    private void initViews(View view) {
        iv_notification = view.findViewById(R.id.iv_notification);
        iv_profile = view.findViewById(R.id.iv_profile);
        iv_close_notification = view.findViewById(R.id.iv_close_notification);

        tv_attendance = view.findViewById(R.id.tv_attendance);
        tv_gpa = view.findViewById(R.id.tv_gpa);
        tv_credits = view.findViewById(R.id.tv_credits);

        tv_class_name = view.findViewById(R.id.tv_class_name);
        tv_professor_name = view.findViewById(R.id.tv_professor_name);
        tv_class_time = view.findViewById(R.id.tv_class_time);
        tv_class_location = view.findViewById(R.id.tv_class_location);

        tv_view_schedule = view.findViewById(R.id.tv_view_schedule);
        tv_view_all_assignments = view.findViewById(R.id.tv_view_all_assignments);

        layout_assignments = view.findViewById(R.id.layout_assignments);
        layout_grades = view.findViewById(R.id.layout_grades);
        layout_calendar = view.findViewById(R.id.layout_calendar);
        layout_resources = view.findViewById(R.id.layout_resources);

        cv_notification = view.findViewById(R.id.cv_notification);
        cv_profile = view.findViewById(R.id.cv_profile);
        card_notification_banner = view.findViewById(R.id.card_notification_banner);

        btn_join_class = view.findViewById(R.id.btn_join_class);
        rv_events = view.findViewById(R.id.rv_events);

        eventHelper = new EventHelper(getContext());
    }

    private void setupRecyclerView() {
        rv_events.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadEvents() {
        eventList = eventHelper.getAllEvents();
        eventAdapter = new EventAdapter(getContext(), eventList);
        rv_events.setAdapter(eventAdapter);
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

        tv_view_schedule.setOnClickListener(v -> openSchedule());

        tv_view_all_assignments.setOnClickListener(v -> openAssignments());

        btn_join_class.setOnClickListener(v -> joinVirtualClass());
    }

    private void loadData() {
        tv_attendance.setText("97%");
        tv_gpa.setText("3.85");
        tv_credits.setText("76");

        tv_class_name.setText("Advanced Data Structures");
        tv_professor_name.setText("Prof. Sarah Johnson");
        tv_class_time.setText("09:30 - 11:30 AM");
        tv_class_location.setText("Room CS-302");
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
        transaction.replace(R.id.framelayout,studentProfile);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openAssignments() {
        AssignmentUpload assignmentUpload = new AssignmentUpload();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout,assignmentUpload);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openGrades() {
        AssignmentUpload assignmentUpload = new AssignmentUpload();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout,assignmentUpload);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openCalendar() {
        StudentClass studentClass = new StudentClass();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout,studentClass);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openResources() {

    }

    private void openSchedule() {
        StudentExam studentExam = new StudentExam();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout,studentExam);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void joinVirtualClass() {

    }
}