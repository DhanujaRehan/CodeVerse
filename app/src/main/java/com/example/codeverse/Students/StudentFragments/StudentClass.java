package com.example.codeverse.Students.StudentFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Helper.ClassScheduleHelper;
import com.example.codeverse.Students.Adapters.StudentClassAdapter;
import com.example.codeverse.Staff.Models.StudentClassSchedule;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentClass extends Fragment implements StudentClassAdapter.OnClassActionListener {

    private RecyclerView rvClasses;
    private StudentClassAdapter adapter;
    private ClassScheduleHelper dbHelper;
    private TabLayout tabLayout;
    private TextView tvTotalClasses, tvActiveClasses, tvNextClassTime;
    private TextView tvClassesTitle, tvDate;
    private View layoutEmptyState;
    private FloatingActionButton fabAddClass;
    private ImageView ivBack, ivFilter;

    private List<StudentClassSchedule> allClasses = new ArrayList<>();
    private int selectedTabPosition = 0;

    public StudentClass() {
    }

    public static StudentClass newInstance() {
        return new StudentClass();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_class, container, false);
        dbHelper = new ClassScheduleHelper(getContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupTabs();
        setupClickListeners();
        loadClasses();
        updateDate();
    }

    private void initViews(View view) {
        rvClasses = view.findViewById(R.id.rv_classes);
        tabLayout = view.findViewById(R.id.tab_layout);
        tvTotalClasses = view.findViewById(R.id.tv_total_classes);
        tvActiveClasses = view.findViewById(R.id.tv_active_classes);
        tvNextClassTime = view.findViewById(R.id.tv_next_class_time);
        tvClassesTitle = view.findViewById(R.id.tv_classes_title);
        tvDate = view.findViewById(R.id.tv_date);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        fabAddClass = view.findViewById(R.id.fab_add_class);
        ivBack = view.findViewById(R.id.iv_back);
        ivFilter = view.findViewById(R.id.iv_filter);
    }

    private void setupRecyclerView() {
        adapter = new StudentClassAdapter(new ArrayList<>(), this);
        rvClasses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvClasses.setAdapter(adapter);
        rvClasses.setNestedScrollingEnabled(false);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTabPosition = tab.getPosition();
                filterClasses();
                updateTitle();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        ivFilter.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Filter options coming soon", Toast.LENGTH_SHORT).show();
        });

        fabAddClass.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Add new class reminder", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadClasses() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(new Date());
        allClasses = dbHelper.getStudentSchedulesByDate(today);
        filterClasses();
        updateStatistics();
    }

    private void filterClasses() {
        List<StudentClassSchedule> filteredClasses = new ArrayList<>();

        if (selectedTabPosition == 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = dateFormat.format(new Date());
            filteredClasses = dbHelper.getStudentSchedulesByDate(today);
        } else if (selectedTabPosition == 1) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            for (int i = 0; i < 7; i++) {
                String date = dateFormat.format(calendar.getTime());
                List<StudentClassSchedule> dayClasses = dbHelper.getStudentSchedulesByDate(date);
                filteredClasses.addAll(dayClasses);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            calendar.add(Calendar.MONTH, -1);
            for (int i = 0; i < 60; i++) {
                String date = dateFormat.format(calendar.getTime());
                List<StudentClassSchedule> dayClasses = dbHelper.getStudentSchedulesByDate(date);
                filteredClasses.addAll(dayClasses);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        adapter.updateData(filteredClasses);
        updateEmptyState(filteredClasses.isEmpty());
    }

    private void updateTitle() {
        String[] titles = {"Today's Classes", "This Week's Classes", "All Classes"};
        tvClassesTitle.setText(titles[selectedTabPosition]);
    }

    private void updateDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        tvDate.setText(dateFormat.format(new Date()));
    }

    private void updateStatistics() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(new Date());
        List<StudentClassSchedule> todayClasses = dbHelper.getStudentSchedulesByDate(today);

        int totalClasses = todayClasses.size();
        int activeClasses = 0;
        String nextClassTime = "--:--";

        for (StudentClassSchedule classSchedule : todayClasses) {
            if (classSchedule.getStatus().equals("Active")) {
                activeClasses++;
                if (nextClassTime.equals("--:--")) {
                    nextClassTime = classSchedule.getStartTime();
                }
            }
        }

        tvTotalClasses.setText(String.valueOf(totalClasses));
        tvActiveClasses.setText(String.valueOf(activeClasses));
        tvNextClassTime.setText(nextClassTime);
    }

    private void updateEmptyState(boolean isEmpty) {
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvClasses.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onJoinClass(StudentClassSchedule classSchedule) {
        Toast.makeText(getContext(), "Joining " + classSchedule.getSubjectName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewDetails(StudentClassSchedule classSchedule) {
        Toast.makeText(getContext(), "Viewing details for " + classSchedule.getSubjectName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOptionsClick(StudentClassSchedule classSchedule) {
        Toast.makeText(getContext(), "Options for " + classSchedule.getSubjectName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}