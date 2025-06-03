package com.example.codeverse.Staff.StaffFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.LoginScreens.LogoutStaff;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Models.StaffCourse;
import com.example.codeverse.Staff.Adapters.StaffCourseAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class StaffProfile extends Fragment {

    private RecyclerView rvTeachingCourses;
    private StaffCourseAdapter staffCourseAdapter;
    private List<StaffCourse> courseList;

    private MaterialCardView cvBack;
    private MaterialCardView cvDepartmentSelector;
    private FloatingActionButton fabSchedule;
    private LottieAnimationView ivBack;
    private TextView tvViewAllCourses;
    private MaterialButton btnStaffLogout;

    private View rootView;

    public interface OnStaffFragmentListener {
        void onBackPressed();
        void onDepartmentSelectorClicked();
        void onScheduleAppointmentClicked();
        void onViewAllCoursesClicked();
        void onCourseClicked(StaffCourse course);
    }

    private OnStaffFragmentListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_staff_profile, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initializeViews() {
        rvTeachingCourses = rootView.findViewById(R.id.rv_teaching_courses);
        cvBack = rootView.findViewById(R.id.cv_back);
        cvDepartmentSelector = rootView.findViewById(R.id.cv_department_selector);
        fabSchedule = rootView.findViewById(R.id.fab_schedule);
        ivBack = rootView.findViewById(R.id.iv_back);
        tvViewAllCourses = rootView.findViewById(R.id.tv_view_all_courses);
        btnStaffLogout = rootView.findViewById(R.id.btn_staff_logout);
    }

    private void setupRecyclerView() {
        courseList = createHardcodedCourseList();

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvTeachingCourses.setLayoutManager(layoutManager);

        staffCourseAdapter = new StaffCourseAdapter(getContext(), courseList, new StaffCourseAdapter.OnCourseClickListener() {
            @Override
            public void onCourseClick(StaffCourse course) {
                if (listener != null) {
                    listener.onCourseClicked(course);
                } else {
                    showToast("Course clicked: " + course.getCourseName());
                }
            }
        });
        rvTeachingCourses.setAdapter(staffCourseAdapter);

        if (getResources() != null) {
            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
            rvTeachingCourses.addItemDecoration(new HorizontalSpaceItemDecoration(spacingInPixels));
        }
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBackPressed();
            } else {
                navigateBack();
            }
        });

        cvDepartmentSelector.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDepartmentSelectorClicked();
            } else {
                showToast("Department settings clicked");
            }
        });

        fabSchedule.setOnClickListener(v -> {
            if (listener != null) {
                listener.onScheduleAppointmentClicked();
            } else {
                showToast("Schedule appointment clicked");
            }
        });

        tvViewAllCourses.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewAllCoursesClicked();
            } else {
                showToast("View all courses clicked");
            }
        });

        btnStaffLogout.setOnClickListener(v -> {
            logoutStaff();
        });
    }

    private List<StaffCourse> createHardcodedCourseList() {
        List<StaffCourse> courses = new ArrayList<>();

        courses.add(new StaffCourse(
                "CS401",
                "Advanced Machine Learning",
                "This course covers advanced topics in machine learning including deep learning, reinforcement learning, and generative models.",
                "Spring 2024",
                45,
                R.drawable.ccc
        ));

        courses.add(new StaffCourse(
                "CS305",
                "Artificial Intelligence",
                "An introduction to the fundamental concepts and techniques of artificial intelligence, including search, knowledge representation, and learning.",
                "Spring 2024",
                65,
                R.drawable.ccc
        ));

        courses.add(new StaffCourse(
                "CS210",
                "Data Structures & Algorithms",
                "A comprehensive exploration of efficient data structures and algorithms for solving computational problems.",
                "Fall 2023",
                78,
                R.drawable.ccc
        ));

        courses.add(new StaffCourse(
                "CS150",
                "Introduction to Programming",
                "Fundamentals of programming using modern programming languages and development environments.",
                "Fall 2023",
                92,
                R.drawable.ccc
        ));

        courses.add(new StaffCourse(
                "CS320",
                "Database Systems",
                "Design and implementation of database systems, including relational databases, SQL, and NoSQL databases.",
                "Spring 2024",
                38,
                R.drawable.ccc
        ));

        return courses;
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateBack() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void logoutStaff() {
        Intent intent = new Intent(getActivity(), LogoutStaff.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void updateCourseList(List<StaffCourse> newCourseList) {
        if (newCourseList != null && staffCourseAdapter != null) {
            courseList.clear();
            courseList.addAll(newCourseList);
            staffCourseAdapter.notifyDataSetChanged();
        }
    }

    public void addCourse(StaffCourse course) {
        if (course != null && courseList != null && staffCourseAdapter != null) {
            courseList.add(course);
            staffCourseAdapter.notifyItemInserted(courseList.size() - 1);
        }
    }

    public void removeCourse(int position) {
        if (position >= 0 && position < courseList.size() && staffCourseAdapter != null) {
            courseList.remove(position);
            staffCourseAdapter.notifyItemRemoved(position);
        }
    }

    public List<StaffCourse> getCourseList() {
        return new ArrayList<>(courseList);
    }

    public static class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int horizontalSpaceWidth;

        public HorizontalSpaceItemDecoration(int horizontalSpaceWidth) {
            this.horizontalSpaceWidth = horizontalSpaceWidth;
        }

        @Override
        public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                outRect.right = horizontalSpaceWidth;
            }
        }
    }

    public void setOnStaffFragmentListener(OnStaffFragmentListener listener) {
        this.listener = listener;
    }

    public static StaffProfile newInstance() {
        return new StaffProfile();
    }

    public static StaffProfile newInstance(String staffId, String department) {
        StaffProfile fragment = new StaffProfile();
        Bundle args = new Bundle();
        args.putString("staff_id", staffId);
        args.putString("department", department);
        fragment.setArguments(args);
        return fragment;
    }

    private String getStaffIdFromArguments() {
        if (getArguments() != null) {
            return getArguments().getString("staff_id");
        }
        return null;
    }

    private String getDepartmentFromArguments() {
        if (getArguments() != null) {
            return getArguments().getString("department");
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        rootView = null;
        listener = null;
        staffCourseAdapter = null;
        courseList = null;
    }
}