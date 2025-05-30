package com.example.codeverse;

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

    private static final String TAG = "StaffScheduleFragment";
    private static final int DAYS_IN_WEEK = 7;
    private static final int MAX_WEEKS_DISPLAY = 6;

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

    private CalendarAdapter calendarAdapter;
    private ScheduleAdapter scheduleAdapter;

    private Calendar currentCalendar = Calendar.getInstance();
    private Date selectedDate = new Date();
    private boolean isStudentSchedule = true;

    private BottomSheetBehavior<MaterialCardView> bottomSheetBehavior;

    private TextInputEditText etSubjectName;
    private TextInputEditText etModuleNumber;
    private TextInputEditText etLecturerName;
    private TextInputEditText etClassRoom;
    private TextInputEditText etTime;

    private View rootView;

    public StaffSchedule() {
    }

    public static StaffSchedule newInstance() {
        return new StaffSchedule();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_staff_schedule, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();

        setupCalendar();

        setupSchedulesList();

        setupListeners();
    }

    /**
     * Initialize all UI components
     */
    private void initViews() {
        rvCalendar = rootView.findViewById(R.id.rv_calendar);
        rvSchedules = rootView.findViewById(R.id.rv_schedules);

        tvMonthYear = rootView.findViewById(R.id.tv_month_year);
        tvScheduleDate = rootView.findViewById(R.id.tv_schedule_date);
        tvScheduleType = rootView.findViewById(R.id.tv_schedule_type);

        btnStudentSchedule = rootView.findViewById(R.id.btn_student_schedule);
        btnLecturerSchedule = rootView.findViewById(R.id.btn_lecturer_schedule);
        fabAddSchedule = rootView.findViewById(R.id.fab_add_schedule);
        btnPrevMonth = rootView.findViewById(R.id.btn_prev_month);
        btnNextMonth = rootView.findViewById(R.id.btn_next_month);
        btnCreateSchedule = rootView.findViewById(R.id.btn_create_schedule);

        bottomSheetContainer = rootView.findViewById(R.id.bottom_sheet_container);
        bottomSheet = rootView.findViewById(R.id.bottom_sheet);
        successOverlay = rootView.findViewById(R.id.success_overlay);
        layoutEmptyState = rootView.findViewById(R.id.layout_empty_state);

        etSubjectName = rootView.findViewById(R.id.et_subject_name);
        etModuleNumber = rootView.findViewById(R.id.et_module_number);
        etLecturerName = rootView.findViewById(R.id.et_lecturer_name);
        etClassRoom = rootView.findViewById(R.id.et_class_room);
        etTime = rootView.findViewById(R.id.et_time);

        if (bottomSheet != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        updateDateDisplay();
    }

    private void setupCalendar() {
        List<CalendarDayModel> days = generateCalendarDays();
        calendarAdapter = new CalendarAdapter(days, this::onDateSelected);

         rvCalendar.setLayoutManager(new GridLayoutManager(getContext(), DAYS_IN_WEEK));
        rvCalendar.setAdapter(calendarAdapter);
        rvCalendar.setNestedScrollingEnabled(false);
    }

    /**
     * Setup the schedules list
     */
    private void setupSchedulesList() {
         List<ScheduleModel> schedules = getSchedulesForDate(selectedDate, isStudentSchedule);

         scheduleAdapter = new ScheduleAdapter(schedules, this::onScheduleAction);

         rvSchedules.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSchedules.setAdapter(scheduleAdapter);
        rvSchedules.setNestedScrollingEnabled(false);

         updateEmptyState(schedules.isEmpty());
    }

    private void setupListeners() {
         if (btnPrevMonth != null) {
            btnPrevMonth.setOnClickListener(v -> changeMonth(-1));
        }
        if (btnNextMonth != null) {
            btnNextMonth.setOnClickListener(v -> changeMonth(1));
        }

         if (btnStudentSchedule != null) {
            btnStudentSchedule.setOnClickListener(v -> {
                if (!isStudentSchedule) {
                    isStudentSchedule = true;
                    updateScheduleTypeUI();
                }
            });
        }

        if (btnLecturerSchedule != null) {
            btnLecturerSchedule.setOnClickListener(v -> {
                if (isStudentSchedule) {
                    isStudentSchedule = false;
                    updateScheduleTypeUI();
                }
            });
        }

         if (fabAddSchedule != null) {
            fabAddSchedule.setOnClickListener(v -> showAddScheduleBottomSheet());
        }
        if (btnCreateSchedule != null) {
            btnCreateSchedule.setOnClickListener(v -> showAddScheduleBottomSheet());
        }

         View backButton = rootView.findViewById(R.id.cv_back);
        if (backButton != null) {
            backButton.setOnClickListener(v -> navigateBack());
        }

         View btnCancel = rootView.findViewById(R.id.btn_cancel_schedule);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> hideBottomSheet());
        }

        View btnSave = rootView.findViewById(R.id.btn_save_schedule);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveSchedule());
        }

         if (bottomSheetBehavior != null) {
            bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN && bottomSheetContainer != null) {
                        bottomSheetContainer.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onSlide(View bottomSheet, float slideOffset) {
                     if (bottomSheetContainer != null) {
                        bottomSheetContainer.setAlpha(slideOffset);
                    }
                }
            });
        }
    }

    private void navigateBack() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    private void updateDateDisplay() {
         SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        if (tvMonthYear != null) {
            tvMonthYear.setText(sdf.format(currentCalendar.getTime()));
        }

         SimpleDateFormat dateSdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        if (tvScheduleDate != null) {
            tvScheduleDate.setText(dateSdf.format(selectedDate));
        }
    }

    /**
     * Generate calendar days for the current month
     */
    private List<CalendarDayModel> generateCalendarDays() {
        List<CalendarDayModel> days = new ArrayList<>();

         Calendar calendar = (Calendar) currentCalendar.clone();

         calendar.set(Calendar.DAY_OF_MONTH, 1);

         int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

         calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek);
        for (int i = 0; i < firstDayOfWeek; i++) {
            CalendarDayModel day = new CalendarDayModel();
            day.setDate(calendar.getTime());
            day.setCurrentMonth(false);
            day.setHasEvents(false);
            days.add(day);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

         calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));

         int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

         for (int i = 0; i < maxDays; i++) {
            CalendarDayModel day = new CalendarDayModel();
            day.setDate(calendar.getTime());
            day.setCurrentMonth(true);


            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            day.setHasEvents(dayOfMonth % 3 == 0 || dayOfMonth == 15);

             Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);
            day.setSelected(calendar.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH));

            days.add(day);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

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


    private void changeMonth(int amount) {
         currentCalendar.add(Calendar.MONTH, amount);

         updateDateDisplay();

         if (calendarAdapter != null) {
            calendarAdapter.updateData(generateCalendarDays());
        }
    }


    private void onDateSelected(Date date) {
         this.selectedDate = date;

         SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        if (tvScheduleDate != null) {
            tvScheduleDate.setText(sdf.format(date));
        }

         List<ScheduleModel> schedules = getSchedulesForDate(date, isStudentSchedule);
        if (scheduleAdapter != null) {
            scheduleAdapter.updateData(schedules);
        }

         updateEmptyState(schedules.isEmpty());
    }


    private void updateScheduleTypeUI() {
        if (getContext() == null) return;

        if (isStudentSchedule) {
             if (btnStudentSchedule != null) {
                btnStudentSchedule.setBackgroundTintList(getContext().getColorStateList(R.color.blue_primary));
                btnStudentSchedule.setTextColor(getContext().getColorStateList(R.color.white));
            }
            if (btnLecturerSchedule != null) {
                btnLecturerSchedule.setBackgroundTintList(getContext().getColorStateList(R.color.light_background));
                btnLecturerSchedule.setTextColor(getContext().getColorStateList(R.color.text_secondary));
            }

             if (tvScheduleType != null) {
                tvScheduleType.setText("Student Schedule");
            }
        } else {
             if (btnLecturerSchedule != null) {
                btnLecturerSchedule.setBackgroundTintList(getContext().getColorStateList(R.color.blue_primary));
                btnLecturerSchedule.setTextColor(getContext().getColorStateList(R.color.white));
            }
            if (btnStudentSchedule != null) {
                btnStudentSchedule.setBackgroundTintList(getContext().getColorStateList(R.color.light_background));
                btnStudentSchedule.setTextColor(getContext().getColorStateList(R.color.text_secondary));
            }

             if (tvScheduleType != null) {
                tvScheduleType.setText("Lecturer Schedule");
            }
        }

         List<ScheduleModel> schedules = getSchedulesForDate(selectedDate, isStudentSchedule);
        if (scheduleAdapter != null) {
            scheduleAdapter.updateData(schedules);
        }

         updateEmptyState(schedules.isEmpty());
    }


    private void onScheduleAction(String action, ScheduleModel schedule) {
        switch (action) {
            case "edit":
                 showEditScheduleForm(schedule);
                break;

            case "delete":
                 deleteSchedule(schedule);
                break;

            case "notify":
                 sendNotification(schedule);
                break;

            case "reschedule":
                 showRescheduleForm(schedule);
                break;
        }
    }


    private void showEditScheduleForm(ScheduleModel schedule) {
         Toast.makeText(getContext(), "Editing schedule: " + schedule.getSubjectName(), Toast.LENGTH_SHORT).show();

         if (bottomSheetContainer != null) {
            bottomSheetContainer.setVisibility(View.VISIBLE);
        }
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

         if (etSubjectName != null) etSubjectName.setText(schedule.getSubjectName());
        if (etModuleNumber != null) etModuleNumber.setText(schedule.getModuleNumber());
        if (etLecturerName != null) etLecturerName.setText(schedule.getLecturerName());
        if (etClassRoom != null) etClassRoom.setText(schedule.getClassroom());
        if (etTime != null) etTime.setText(schedule.getStartTime());

         MaterialButton saveButton = rootView.findViewById(R.id.btn_save_schedule);
        if (saveButton != null) {
            saveButton.setText("Update");
        }
    }


    private void deleteSchedule(ScheduleModel schedule) {
         Toast.makeText(getContext(), "Deleted schedule: " + schedule.getSubjectName(), Toast.LENGTH_SHORT).show();

         if (scheduleAdapter != null) {
            scheduleAdapter.removeItem(schedule);

             updateEmptyState(scheduleAdapter.getItemCount() == 0);
        }
    }


    private void sendNotification(ScheduleModel schedule) {
         String recipient = isStudentSchedule ? "students" : "lecturer";
        Toast.makeText(getContext(), "Notification sent to " + recipient + " about: " + schedule.getSubjectName(), Toast.LENGTH_SHORT).show();
    }


    private void showRescheduleForm(ScheduleModel schedule) {
         Toast.makeText(getContext(), "Rescheduling: " + schedule.getSubjectName(), Toast.LENGTH_SHORT).show();
    }


    private void showAddScheduleBottomSheet() {
         if (bottomSheetContainer != null) {
            bottomSheetContainer.setVisibility(View.VISIBLE);
        }
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

         if (etSubjectName != null) etSubjectName.setText("");
        if (etModuleNumber != null) etModuleNumber.setText("");
        if (etLecturerName != null) etLecturerName.setText("");
        if (etClassRoom != null) etClassRoom.setText("");
        if (etTime != null) etTime.setText("");

        // Make sure button text is correct
        MaterialButton saveButton = rootView.findViewById(R.id.btn_save_schedule);
        if (saveButton != null) {
            saveButton.setText("Save");
        }
    }


    private void hideBottomSheet() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }


    private void saveSchedule() {
         String subjectName = etSubjectName != null ? etSubjectName.getText().toString().trim() : "";
        String moduleNumber = etModuleNumber != null ? etModuleNumber.getText().toString().trim() : "";
        String lecturerName = etLecturerName != null ? etLecturerName.getText().toString().trim() : "";
        String classroom = etClassRoom != null ? etClassRoom.getText().toString().trim() : "";
        String time = etTime != null ? etTime.getText().toString().trim() : "";

         if (subjectName.isEmpty() || moduleNumber.isEmpty() || lecturerName.isEmpty() ||
                classroom.isEmpty() || time.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

         MaterialButton saveButton = rootView.findViewById(R.id.btn_save_schedule);
        boolean isEdit = saveButton != null && saveButton.getText().toString().equals("Update");

        if (isEdit) {
             Toast.makeText(getContext(), "Schedule updated", Toast.LENGTH_SHORT).show();
        } else {
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

             if (scheduleAdapter != null) {
                scheduleAdapter.addItem(newSchedule);

                 updateEmptyState(false);
            }
        }

         hideBottomSheet();

         showSuccessOverlay(isEdit ? "Schedule Updated!" : "Schedule Added!");
    }


    private String calculateEndTime(String startTime) {
         try {
            String[] parts = startTime.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = 0;

            if (parts.length > 1) {
                minute = Integer.parseInt(parts[1]);
            }

             minute += 30;
            if (minute >= 60) {
                minute -= 60;
                hour += 1;
            }
            hour += 1;

             if (hour > 12) {
                hour -= 12;
            }

            return String.format(Locale.getDefault(), "%d:%02d", hour, minute);
        } catch (Exception e) {
             return "11:30";
        }
    }


    private String getAmPm(String time) {
        try {
            int hour = Integer.parseInt(time.split(":")[0]);

             if (hour >= 12) {
                return "PM";
            } else {
                return "AM";
            }
        } catch (Exception e) {
             return "AM";
        }
    }


    private void showSuccessOverlay(String message) {
        if (successOverlay == null) return;

         successOverlay.setVisibility(View.VISIBLE);
        successOverlay.setAlpha(0f);
        successOverlay.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null);

         successOverlay.postDelayed(() -> {
            successOverlay.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (successOverlay != null) {
                                successOverlay.setVisibility(View.GONE);
                            }
                        }
                    });
        }, 2000);
    }


    private void updateEmptyState(boolean isEmpty) {
        if (layoutEmptyState != null) {
            layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (rvSchedules != null) {
            rvSchedules.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }


    private List<ScheduleModel> getSchedulesForDate(Date date, boolean isStudentSchedule) {
        List<ScheduleModel> schedules = new ArrayList<>();

         Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

         if (dayOfMonth % 3 == 0 || dayOfMonth == 15) {
            if (isStudentSchedule) {
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
    public void onDestroyView() {
        super.onDestroyView();

         rootView = null;
        calendarAdapter = null;
        scheduleAdapter = null;
        bottomSheetBehavior = null;

         rvCalendar = null;
        rvSchedules = null;
        tvMonthYear = null;
        tvScheduleDate = null;
        tvScheduleType = null;
        btnStudentSchedule = null;
        btnLecturerSchedule = null;
        fabAddSchedule = null;
        bottomSheetContainer = null;
        successOverlay = null;
        layoutEmptyState = null;
        bottomSheet = null;
        btnPrevMonth = null;
        btnNextMonth = null;
        btnCreateSchedule = null;

         etSubjectName = null;
        etModuleNumber = null;
        etLecturerName = null;
        etClassRoom = null;
        etTime = null;
    }
}