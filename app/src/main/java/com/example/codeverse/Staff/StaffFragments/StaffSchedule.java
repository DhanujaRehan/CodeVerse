package com.example.codeverse.Staff.StaffFragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Adapters.CalendarAdapter;
import com.example.codeverse.Staff.Adapters.ScheduleAdapter;
import com.example.codeverse.Staff.Models.CalendarDayModel;
import com.example.codeverse.Staff.Models.ScheduleModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StaffSchedule extends Fragment {

    // Constants
    private static final String TAG = "StaffScheduleFragment";
    private static final int DAYS_IN_WEEK = 7;
    private static final int MAX_WEEKS_DISPLAY = 6;

    // UI Components
    private RecyclerView rvCalendar;
    private RecyclerView rvSchedules;
    private TextView tvMonthYear;
    private TextView tvScheduleDate;
    private TextView tvScheduleType;
    private MaterialButton btnStudentSchedule;
    private MaterialButton btnLecturerSchedule;
    private FloatingActionButton fabAddSchedule;
    private View bottomSheetContainer;
    private View successOverlay;
    private View layoutEmptyState;
    private MaterialCardView bottomSheet;
    private LottieAnimationView btnPrevMonth;
    private ImageView btnNextMonth;
    private MaterialButton btnCreateSchedule;

    // Adapters
    private CalendarAdapter calendarAdapter;
    private ScheduleAdapter scheduleAdapter;

    // Data
    private Calendar currentCalendar = Calendar.getInstance();
    private Date selectedDate = new Date();
    private boolean isStudentSchedule = true;

    // Bottom Sheet
    private BottomSheetBehavior<MaterialCardView> bottomSheetBehavior;

    // Form fields
    private TextInputEditText etSubjectName;
    private TextInputEditText etModuleNumber;
    private TextInputEditText etLecturerName;
    private TextInputEditText etClassRoom;
    private TextInputEditText etTime;

    // Root view
    private View rootView;

    public static StaffSchedule newInstance() {
        return new StaffSchedule();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_staff_schedule, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize all UI components
        initViews();

        // Setup calendar UI
        setupCalendar();

        // Setup schedules list
        setupSchedulesList();

        // Setup click listeners
        setupListeners();
    }

    /**
     * Initialize all UI components
     */
    private void initViews() {
        // RecyclerViews
        rvCalendar = rootView.findViewById(R.id.rv_calendar);
        rvSchedules = rootView.findViewById(R.id.rv_schedules);

        // TextViews
        tvMonthYear = rootView.findViewById(R.id.tv_month_year);
        tvScheduleDate = rootView.findViewById(R.id.tv_schedule_date);
        tvScheduleType = rootView.findViewById(R.id.tv_schedule_type);

        // Buttons
        btnStudentSchedule = rootView.findViewById(R.id.btn_student_schedule);
        btnLecturerSchedule = rootView.findViewById(R.id.btn_lecturer_schedule);
        fabAddSchedule = rootView.findViewById(R.id.fab_add_schedule);
        btnPrevMonth = rootView.findViewById(R.id.btn_prev_month);
        btnNextMonth = rootView.findViewById(R.id.btn_next_month);
        btnCreateSchedule = rootView.findViewById(R.id.btn_create_schedule);

        // Layout containers
        bottomSheetContainer = rootView.findViewById(R.id.bottom_sheet_container);
        bottomSheet = rootView.findViewById(R.id.bottom_sheet);
        successOverlay = rootView.findViewById(R.id.success_overlay);
        layoutEmptyState = rootView.findViewById(R.id.layout_empty_state);

        // Form fields
        etSubjectName = rootView.findViewById(R.id.et_subject_name);
        etModuleNumber = rootView.findViewById(R.id.et_module_number);
        etLecturerName = rootView.findViewById(R.id.et_lecturer_name);
        etClassRoom = rootView.findViewById(R.id.et_class_room);
        etTime = rootView.findViewById(R.id.et_time);

        // Setup Bottom Sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Set current date text
        updateDateDisplay();
    }

    /**
     * Setup the calendar grid view
     */
    private void setupCalendar() {
        // Create and set adapter
        List<CalendarDayModel> days = generateCalendarDays();
        calendarAdapter = new CalendarAdapter(days, this::onDateSelected);

        // Configure RecyclerView for calendar grid
        rvCalendar.setLayoutManager(new GridLayoutManager(getContext(), DAYS_IN_WEEK));
        rvCalendar.setAdapter(calendarAdapter);
        rvCalendar.setNestedScrollingEnabled(false);
    }

    /**
     * Setup the schedules list
     */
    private void setupSchedulesList() {
        // Get schedules for the selected date
        List<ScheduleModel> schedules = getSchedulesForDate(selectedDate, isStudentSchedule);

        // Create and set adapter
        scheduleAdapter = new ScheduleAdapter(schedules, this::onScheduleAction);

        // Configure RecyclerView for schedules
        rvSchedules.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSchedules.setAdapter(scheduleAdapter);
        rvSchedules.setNestedScrollingEnabled(false);

        // Show empty state if no schedules
        updateEmptyState(schedules.isEmpty());
    }

    /**
     * Setup all click listeners
     */
    private void setupListeners() {
        // Month navigation buttons
        btnPrevMonth.setOnClickListener(v -> changeMonth(-1));
        btnNextMonth.setOnClickListener(v -> changeMonth(1));

        // Schedule type toggle buttons
        btnStudentSchedule.setOnClickListener(v -> {
            if (!isStudentSchedule) {
                isStudentSchedule = true;
                updateScheduleTypeUI();
            }
        });

        btnLecturerSchedule.setOnClickListener(v -> {
            if (isStudentSchedule) {
                isStudentSchedule = false;
                updateScheduleTypeUI();
            }
        });

        // Add schedule buttons
        fabAddSchedule.setOnClickListener(v -> showAddScheduleBottomSheet());
        btnCreateSchedule.setOnClickListener(v -> showAddScheduleBottomSheet());

        // Back button - handle fragment navigation
        rootView.findViewById(R.id.cv_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Bottom sheet buttons
        rootView.findViewById(R.id.btn_cancel_schedule).setOnClickListener(v -> hideBottomSheet());
        rootView.findViewById(R.id.btn_save_schedule).setOnClickListener(v -> saveSchedule());

        // Bottom sheet behavior
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                // Optional: Add animation based on slide offset
                bottomSheetContainer.setAlpha(slideOffset);
            }
        });
    }

    /**
     * Update the month/year display and regenerate calendar days
     */
    private void updateDateDisplay() {
        // Update month/year text
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(sdf.format(currentCalendar.getTime()));

        // Update selected date text
        SimpleDateFormat dateSdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        tvScheduleDate.setText(dateSdf.format(selectedDate));
    }

    /**
     * Generate calendar days for the current month
     */
    private List<CalendarDayModel> generateCalendarDays() {
        List<CalendarDayModel> days = new ArrayList<>();

        // Clone the current calendar to avoid modifying it
        Calendar calendar = (Calendar) currentCalendar.clone();

        // Set to the first day of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Get the day of week for the first day of month (0 = Sunday, 1 = Monday, ..., 6 = Saturday)
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // Add empty days for previous month
        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek);
        for (int i = 0; i < firstDayOfWeek; i++) {
            CalendarDayModel day = new CalendarDayModel();
            day.setDate(calendar.getTime());
            day.setCurrentMonth(false);
            day.setHasEvents(false);
            days.add(day);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Reset to first day of current month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));

        // Get the number of days in the month
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add days of current month
        for (int i = 0; i < maxDays; i++) {
            CalendarDayModel day = new CalendarDayModel();
            day.setDate(calendar.getTime());
            day.setCurrentMonth(true);

            // Mark some days as having events (in a real app, this would be from a database)
            // For demo, mark every 3rd and 15th day as having events
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            day.setHasEvents(dayOfMonth % 3 == 0 || dayOfMonth == 15);

            // Check if this is the selected date
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);
            day.setSelected(calendar.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH));

            days.add(day);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Add days for next month to complete grid (42 days total for 6 rows)
        int remainingDays = (DAYS_IN_WEEK * MAX_WEEKS_DISPLAY) - days.size();
        for (int i = 0; i < remainingDays; i++) {
            CalendarDayModel day = new CalendarDayModel();
            day.setDate(calendar.getTime());
            day.setCurrentMonth(false);
            day.setHasEvents(false);
            days.add(day);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return days;
    }

    /**
     * Change the displayed month and update the calendar
     */
    private void changeMonth(int amount) {
        // Change month by the specified amount
        currentCalendar.add(Calendar.MONTH, amount);

        // Update display
        updateDateDisplay();

        // Regenerate calendar days
        calendarAdapter.updateData(generateCalendarDays());
    }

    /**
     * Handle date selection from the calendar
     */
    private void onDateSelected(Date date) {
        // Update selected date
        this.selectedDate = date;

        // Update date display
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        tvScheduleDate.setText(sdf.format(date));

        // Get schedules for the new date
        List<ScheduleModel> schedules = getSchedulesForDate(date, isStudentSchedule);
        scheduleAdapter.updateData(schedules);

        // Show/hide empty state
        updateEmptyState(schedules.isEmpty());
    }

    /**
     * Update UI based on selected schedule type (Student/Lecturer)
     */
    private void updateScheduleTypeUI() {
        if (getContext() == null) return;

        if (isStudentSchedule) {
            // Update button styles
            btnStudentSchedule.setBackgroundTintList(getContext().getColorStateList(R.color.blue_primary));
            btnStudentSchedule.setTextColor(getContext().getColorStateList(R.color.white));
            btnLecturerSchedule.setBackgroundTintList(getContext().getColorStateList(R.color.light_background));
            btnLecturerSchedule.setTextColor(getContext().getColorStateList(R.color.text_secondary));

            // Update schedule type text
            tvScheduleType.setText("Student Schedule");
        } else {
            // Update button styles
            btnLecturerSchedule.setBackgroundTintList(getContext().getColorStateList(R.color.blue_primary));
            btnLecturerSchedule.setTextColor(getContext().getColorStateList(R.color.white));
            btnStudentSchedule.setBackgroundTintList(getContext().getColorStateList(R.color.light_background));
            btnStudentSchedule.setTextColor(getContext().getColorStateList(R.color.text_secondary));

            // Update schedule type text
            tvScheduleType.setText("Lecturer Schedule");
        }

        // Update schedules list
        List<ScheduleModel> schedules = getSchedulesForDate(selectedDate, isStudentSchedule);
        scheduleAdapter.updateData(schedules);

        // Show/hide empty state
        updateEmptyState(schedules.isEmpty());
    }

    /**
     * Handle actions on schedule items (edit, delete, notify, reschedule)
     */
    private void onScheduleAction(String action, ScheduleModel schedule) {
        switch (action) {
            case "edit":
                // Open edit form
                showEditScheduleForm(schedule);
                break;

            case "delete":
                // Confirm and delete schedule
                deleteSchedule(schedule);
                break;

            case "notify":
                // Send notification to students/lecturer
                sendNotification(schedule);
                break;

            case "reschedule":
                // Open reschedule form
                showRescheduleForm(schedule);
                break;
        }
    }

    /**
     * Show the form to edit an existing schedule
     */
    private void showEditScheduleForm(ScheduleModel schedule) {
        // In a real app, populate the form with existing data and update when saved
        Toast.makeText(getContext(), "Editing schedule: " + schedule.getSubjectName(), Toast.LENGTH_SHORT).show();

        // Show bottom sheet with populated fields
        bottomSheetContainer.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // Populate fields
        etSubjectName.setText(schedule.getSubjectName());
        etModuleNumber.setText(schedule.getModuleNumber());
        etLecturerName.setText(schedule.getLecturerName());
        etClassRoom.setText(schedule.getClassroom());
        etTime.setText(schedule.getStartTime());

        // Change button text
        ((MaterialButton) rootView.findViewById(R.id.btn_save_schedule)).setText("Update");

        // Note: We can't change the title text because it's hardcoded in the XML
        // In a real app, you'd make the title a separate TextView with an ID
    }

    /**
     * Delete a schedule with confirmation
     */
    private void deleteSchedule(ScheduleModel schedule) {
        // In a real app, show a confirmation dialog and delete from database
        Toast.makeText(getContext(), "Deleted schedule: " + schedule.getSubjectName(), Toast.LENGTH_SHORT).show();

        // Remove from adapter
        scheduleAdapter.removeItem(schedule);

        // Show empty state if no schedules left
        updateEmptyState(scheduleAdapter.getItemCount() == 0);
    }

    /**
     * Send notification about a schedule
     */
    private void sendNotification(ScheduleModel schedule) {
        // In a real app, would send push notification or email
        String recipient = isStudentSchedule ? "students" : "lecturer";
        Toast.makeText(getContext(), "Notification sent to " + recipient + " about: " + schedule.getSubjectName(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Show form to reschedule a class
     */
    private void showRescheduleForm(ScheduleModel schedule) {
        // In a real app, would show a date/time picker
        Toast.makeText(getContext(), "Rescheduling: " + schedule.getSubjectName(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Show the bottom sheet to add a new schedule
     */
    private void showAddScheduleBottomSheet() {
        // Show bottom sheet
        bottomSheetContainer.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // Reset all fields
        etSubjectName.setText("");
        etModuleNumber.setText("");
        etLecturerName.setText("");
        etClassRoom.setText("");
        etTime.setText("");

        // Make sure button text is correct
        ((MaterialButton) rootView.findViewById(R.id.btn_save_schedule)).setText("Save");

        // Note: The title is hardcoded in the XML as "Add New Schedule"
        // No need to dynamically set it since it's already in the layout
    }

    /**
     * Hide the bottom sheet
     */
    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    /**
     * Save a new schedule from the form
     */
    private void saveSchedule() {
        // Get values from form
        String subjectName = etSubjectName.getText().toString().trim();
        String moduleNumber = etModuleNumber.getText().toString().trim();
        String lecturerName = etLecturerName.getText().toString().trim();
        String classroom = etClassRoom.getText().toString().trim();
        String time = etTime.getText().toString().trim();

        // Validate inputs
        if (subjectName.isEmpty() || moduleNumber.isEmpty() || lecturerName.isEmpty() ||
                classroom.isEmpty() || time.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine if this is an edit or new schedule
        boolean isEdit = ((MaterialButton) rootView.findViewById(R.id.btn_save_schedule)).getText().toString().equals("Update");

        if (isEdit) {
            // In a real app, would update the existing schedule in database
            Toast.makeText(getContext(), "Schedule updated", Toast.LENGTH_SHORT).show();
        } else {
            // Create new schedule
            ScheduleModel newSchedule = new ScheduleModel(
                    subjectName,
                    moduleNumber,
                    lecturerName,
                    classroom,
                    time,
                    calculateEndTime(time),
                    getAmPm(time),
                    isStudentSchedule,
                    "Active"
            );

            // Add to adapter
            scheduleAdapter.addItem(newSchedule);

            // Update empty state
            updateEmptyState(false);
        }

        // Hide bottom sheet
        hideBottomSheet();

        // Show success overlay
        showSuccessOverlay(isEdit ? "Schedule Updated!" : "Schedule Added!");
    }

    /**
     * Calculate end time based on start time
     */
    private String calculateEndTime(String startTime) {
        // Simple calculation: add 1.5 hours to start time
        try {
            String[] parts = startTime.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = 0;

            if (parts.length > 1) {
                minute = Integer.parseInt(parts[1]);
            }

            // Add 1 hour and 30 minutes
            minute += 30;
            if (minute >= 60) {
                minute -= 60;
                hour += 1;
            }
            hour += 1;

            // Keep in 12-hour format
            if (hour > 12) {
                hour -= 12;
            }

            return String.format(Locale.getDefault(), "%d:%02d", hour, minute);
        } catch (Exception e) {
            // Default to 1.5 hours later if parsing fails
            return "12:00";
        }
    }

    /**
     * Determine AM/PM based on time
     */
    private String getAmPm(String time) {
        try {
            int hour = Integer.parseInt(time.split(":")[0]);

            // If using 24-hour format
            if (hour >= 12) {
                return "PM";
            } else {
                return "AM";
            }
        } catch (Exception e) {
            // Default to AM if parsing fails
            return "AM";
        }
    }

    /**
     * Show the success overlay with animation
     */
    private void showSuccessOverlay(String message) {
        // Note: We can't set the success message since the TextView doesn't have an ID
        // The message is hardcoded in the XML as "Schedule Added!"

        // Show the overlay with fade-in animation
        successOverlay.setVisibility(View.VISIBLE);
        successOverlay.setAlpha(0f);
        successOverlay.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null);

        // Auto-hide after delay
        successOverlay.postDelayed(() -> {
            successOverlay.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            successOverlay.setVisibility(View.GONE);
                        }
                    });
        }, 2000);
    }

    /**
     * Show/hide empty state based on whether there are schedules
     */
    private void updateEmptyState(boolean isEmpty) {
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvSchedules.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    /**
     * Get schedules for the selected date (in a real app, this would query a database)
     */
    private List<ScheduleModel> getSchedulesForDate(Date date, boolean isStudentSchedule) {
        List<ScheduleModel> schedules = new ArrayList<>();

        // Extract day of month from the date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        // For demo purposes, only show schedules on days that have events (3rd, 15th, etc.)
        if (dayOfMonth % 3 == 0 || dayOfMonth == 15) {
            if (isStudentSchedule) {
                // Student schedules
                schedules.add(new ScheduleModel(
                        "Data Structures and Algorithms",
                        "CS3021",
                        "Dr. Sarah Wilson",
                        "Computer Science Building, Room 301",
                        "10:00",
                        "11:30",
                        "AM",
                        true,
                        "Active"
                ));

                schedules.add(new ScheduleModel(
                        "Advanced Database Systems",
                        "CS4015",
                        "Prof. James Rodriguez",
                        "Technology Center, Lab 204",
                        "1:00",
                        "2:30",
                        "PM",
                        true,
                        "Active"
                ));

                // Add a third class only on the 15th
                if (dayOfMonth == 15) {
                    schedules.add(new ScheduleModel(
                            "Machine Learning Fundamentals",
                            "CS4071",
                            "Dr. Maria Chen",
                            "AI Research Center, Room 105",
                            "3:00",
                            "5:00",
                            "PM",
                            true,
                            "Active"
                    ));
                }
            } else {
                // Lecturer schedules
                schedules.add(new ScheduleModel(
                        "Artificial Intelligence",
                        "CS4032",
                        "Dr. Sarah Wilson",
                        "Computer Science Building, Room 402",
                        "9:00",
                        "11:00",
                        "AM",
                        false,
                        "Active"
                ));

                schedules.add(new ScheduleModel(
                        "Computer Networks",
                        "CS3054",
                        "Dr. Michael Chen",
                        "Engineering Building, Room 105",
                        "2:30",
                        "4:00",
                        "PM",
                        false,
                        "Active"
                ));

                // Add a canceled class on the 15th
                if (dayOfMonth == 15) {
                    schedules.add(new ScheduleModel(
                            "Software Engineering",
                            "CS3013",
                            "Prof. Lisa Johnson",
                            "Computer Science Building, Room 201",
                            "10:00",
                            "12:00",
                            "PM",
                            false,
                            "Canceled"
                    ));
                }
            }
        }

        return schedules;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up resources if needed
        rootView = null;
    }
}