package com.example.codeverse.Staff.StaffFragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
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
import com.example.codeverse.Staff.Adapters.ScheduleAdapterNew;
import com.example.codeverse.Staff.Helper.ClassScheduleHelper;
import com.example.codeverse.Staff.Models.CalendarDayModel;
import com.example.codeverse.Staff.Models.StudentClassSchedule;
import com.example.codeverse.Staff.Models.LecturerClassSchedule;
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

public class StaffSchedule extends Fragment implements ScheduleAdapterNew.OnScheduleActionListener {

    public StaffSchedule() {
    }

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
    private ScrollView bottomSheet;
    private ImageView btnNextMonth, btnPrevMonth;
    private MaterialButton btnCreateSchedule;

    private CalendarAdapter calendarAdapter;
    private ScheduleAdapterNew scheduleAdapter;

    private Calendar currentCalendar = Calendar.getInstance();
    private Date selectedDate = new Date();
    private boolean isStudentSchedule = true;

    private BottomSheetBehavior<ScrollView> bottomSheetBehavior;

    private TextInputEditText etSubjectName;
    private TextInputEditText etModuleNumber;
    private TextInputEditText etLecturerName;
    private TextInputEditText etClassRoom;
    private TextInputEditText etTime;

    private View rootView;
    private ClassScheduleHelper dbHelper;
    private Object editingSchedule = null;

    public static StaffSchedule newInstance() {
        return new StaffSchedule();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_staff_schedule, container, false);
        dbHelper = new ClassScheduleHelper(getContext());
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

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        updateDateDisplay();
    }

    private void setupCalendar() {
        List<CalendarDayModel> days = generateCalendarDays();
        calendarAdapter = new CalendarAdapter(days, this::onDateSelected);

        rvCalendar.setLayoutManager(new GridLayoutManager(getContext(), DAYS_IN_WEEK));
        rvCalendar.setAdapter(calendarAdapter);
        rvCalendar.setNestedScrollingEnabled(false);
    }

    private void setupSchedulesList() {
        List<Object> schedules = getSchedulesForDate(selectedDate, isStudentSchedule);

        scheduleAdapter = new ScheduleAdapterNew(schedules, isStudentSchedule, this);

        rvSchedules.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSchedules.setAdapter(scheduleAdapter);
        rvSchedules.setNestedScrollingEnabled(false);

        updateEmptyState(schedules.isEmpty());
    }

    private void setupListeners() {
        btnPrevMonth.setOnClickListener(v -> changeMonth(-1));
        btnNextMonth.setOnClickListener(v -> changeMonth(1));

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

        fabAddSchedule.setOnClickListener(v -> showAddScheduleBottomSheet());
        btnCreateSchedule.setOnClickListener(v -> showAddScheduleBottomSheet());

        rootView.findViewById(R.id.btn_cancel_schedule).setOnClickListener(v -> hideBottomSheet());
        rootView.findViewById(R.id.btn_save_schedule).setOnClickListener(v -> saveSchedule());

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                bottomSheetContainer.setAlpha(slideOffset);
            }
        });
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(sdf.format(currentCalendar.getTime()));

        SimpleDateFormat dateSdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        tvScheduleDate.setText(dateSdf.format(selectedDate));
    }

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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String dateString = dateFormat.format(calendar.getTime());

            boolean hasEvents = false;
            if (isStudentSchedule) {
                hasEvents = !dbHelper.getStudentSchedulesByDate(dateString).isEmpty();
            } else {
                hasEvents = !dbHelper.getLecturerSchedulesByDate(dateString).isEmpty();
            }
            day.setHasEvents(hasEvents);

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

        calendarAdapter.updateData(generateCalendarDays());
    }

    private void onDateSelected(Date date) {
        this.selectedDate = date;

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        tvScheduleDate.setText(sdf.format(date));

        List<Object> schedules = getSchedulesForDate(date, isStudentSchedule);
        scheduleAdapter.updateData(schedules);

        updateEmptyState(schedules.isEmpty());
    }

    private void updateScheduleTypeUI() {
        if (getContext() == null) return;

        if (isStudentSchedule) {
            btnStudentSchedule.setBackgroundTintList(getContext().getColorStateList(R.color.blue_primary));
            btnStudentSchedule.setTextColor(getContext().getColorStateList(R.color.white));
            btnLecturerSchedule.setBackgroundTintList(getContext().getColorStateList(R.color.light_background));
            btnLecturerSchedule.setTextColor(getContext().getColorStateList(R.color.text_secondary));

            tvScheduleType.setText("Student Schedule");
        } else {
            btnLecturerSchedule.setBackgroundTintList(getContext().getColorStateList(R.color.blue_primary));
            btnLecturerSchedule.setTextColor(getContext().getColorStateList(R.color.white));
            btnStudentSchedule.setBackgroundTintList(getContext().getColorStateList(R.color.light_background));
            btnStudentSchedule.setTextColor(getContext().getColorStateList(R.color.text_secondary));

            tvScheduleType.setText("Lecturer Schedule");
        }

        List<Object> schedules = getSchedulesForDate(selectedDate, isStudentSchedule);
        scheduleAdapter = new ScheduleAdapterNew(schedules, isStudentSchedule, this);
        rvSchedules.setAdapter(scheduleAdapter);

        updateEmptyState(schedules.isEmpty());

        calendarAdapter.updateData(generateCalendarDays());
    }

    @Override
    public void onEdit(Object schedule) {
        editingSchedule = schedule;

        bottomSheetContainer.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        if (schedule instanceof StudentClassSchedule) {
            StudentClassSchedule studentSchedule = (StudentClassSchedule) schedule;
            etSubjectName.setText(studentSchedule.getSubjectName());
            etModuleNumber.setText(studentSchedule.getModuleNumber());
            etLecturerName.setText(studentSchedule.getLecturerName());
            etClassRoom.setText(studentSchedule.getClassroom());
            etTime.setText(studentSchedule.getStartTime());
        } else if (schedule instanceof LecturerClassSchedule) {
            LecturerClassSchedule lecturerSchedule = (LecturerClassSchedule) schedule;
            etSubjectName.setText(lecturerSchedule.getSubjectName());
            etModuleNumber.setText(lecturerSchedule.getModuleNumber());
            etLecturerName.setText(lecturerSchedule.getLecturerName());
            etClassRoom.setText(lecturerSchedule.getClassroom());
            etTime.setText(lecturerSchedule.getStartTime());
        }

        ((MaterialButton) rootView.findViewById(R.id.btn_save_schedule)).setText("Update");
    }

    @Override
    public void onDelete(Object schedule) {
        boolean success = false;

        if (schedule instanceof StudentClassSchedule) {
            StudentClassSchedule studentSchedule = (StudentClassSchedule) schedule;
            success = dbHelper.deleteStudentSchedule(studentSchedule.getId());
        } else if (schedule instanceof LecturerClassSchedule) {
            LecturerClassSchedule lecturerSchedule = (LecturerClassSchedule) schedule;
            success = dbHelper.deleteLecturerSchedule(lecturerSchedule.getId());
        }

        if (success) {
            scheduleAdapter.removeItem(schedule);
            updateEmptyState(scheduleAdapter.getItemCount() == 0);
            calendarAdapter.updateData(generateCalendarDays());
            Toast.makeText(getContext(), "Schedule deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to delete schedule", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNotify(Object schedule) {
        String recipient = isStudentSchedule ? "students" : "lecturer";
        String subjectName = "";

        if (schedule instanceof StudentClassSchedule) {
            subjectName = ((StudentClassSchedule) schedule).getSubjectName();
        } else if (schedule instanceof LecturerClassSchedule) {
            subjectName = ((LecturerClassSchedule) schedule).getSubjectName();
        }

        Toast.makeText(getContext(), "Notification sent to " + recipient + " about: " + subjectName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReschedule(Object schedule) {
        String subjectName = "";

        if (schedule instanceof StudentClassSchedule) {
            subjectName = ((StudentClassSchedule) schedule).getSubjectName();
        } else if (schedule instanceof LecturerClassSchedule) {
            subjectName = ((LecturerClassSchedule) schedule).getSubjectName();
        }

        Toast.makeText(getContext(), "Rescheduling: " + subjectName, Toast.LENGTH_SHORT).show();
    }

    private void showAddScheduleBottomSheet() {
        editingSchedule = null;

        bottomSheetContainer.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        etSubjectName.setText("");
        etModuleNumber.setText("");
        etLecturerName.setText("");
        etClassRoom.setText("");
        etTime.setText("");

        ((MaterialButton) rootView.findViewById(R.id.btn_save_schedule)).setText("Save");
    }

    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void saveSchedule() {
        String subjectName = etSubjectName.getText().toString().trim();
        String moduleNumber = etModuleNumber.getText().toString().trim();
        String lecturerName = etLecturerName.getText().toString().trim();
        String classroom = etClassRoom.getText().toString().trim();
        String time = etTime.getText().toString().trim();

        if (subjectName.isEmpty() || moduleNumber.isEmpty() || lecturerName.isEmpty() ||
                classroom.isEmpty() || time.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String endTime = calculateEndTime(time);
        String amPm = getAmPm(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = dateFormat.format(selectedDate);

        boolean isEdit = editingSchedule != null;
        boolean success = false;

        if (isEdit) {
            if (isStudentSchedule && editingSchedule instanceof StudentClassSchedule) {
                StudentClassSchedule schedule = (StudentClassSchedule) editingSchedule;
                schedule.setSubjectName(subjectName);
                schedule.setModuleNumber(moduleNumber);
                schedule.setLecturerName(lecturerName);
                schedule.setClassroom(classroom);
                schedule.setStartTime(time);
                schedule.setEndTime(endTime);
                schedule.setAmPm(amPm);
                success = dbHelper.updateStudentSchedule(schedule);
            } else if (!isStudentSchedule && editingSchedule instanceof LecturerClassSchedule) {
                LecturerClassSchedule schedule = (LecturerClassSchedule) editingSchedule;
                schedule.setSubjectName(subjectName);
                schedule.setModuleNumber(moduleNumber);
                schedule.setLecturerName(lecturerName);
                schedule.setClassroom(classroom);
                schedule.setStartTime(time);
                schedule.setEndTime(endTime);
                schedule.setAmPm(amPm);
                success = dbHelper.updateLecturerSchedule(schedule);
            }
        } else {
            if (isStudentSchedule) {
                StudentClassSchedule newSchedule = new StudentClassSchedule(subjectName, moduleNumber, lecturerName,
                        classroom, time, endTime, amPm, "Active", dateString);
                long result = dbHelper.insertStudentSchedule(newSchedule);
                success = result != -1;
                if (success) {
                    newSchedule.setId((int) result);
                    scheduleAdapter.addItem(newSchedule);
                }
            } else {
                LecturerClassSchedule newSchedule = new LecturerClassSchedule(subjectName, moduleNumber, lecturerName,
                        classroom, time, endTime, amPm, "Active", dateString);
                long result = dbHelper.insertLecturerSchedule(newSchedule);
                success = result != -1;
                if (success) {
                    newSchedule.setId((int) result);
                    scheduleAdapter.addItem(newSchedule);
                }
            }
        }

        if (success) {
            if (isEdit) {
                List<Object> schedules = getSchedulesForDate(selectedDate, isStudentSchedule);
                scheduleAdapter.updateData(schedules);
            }

            updateEmptyState(scheduleAdapter.getItemCount() == 0);
            calendarAdapter.updateData(generateCalendarDays());
            hideBottomSheet();
            showSuccessOverlay(isEdit ? "Schedule Updated!" : "Schedule Added!");
        } else {
            Toast.makeText(getContext(), "Failed to save schedule", Toast.LENGTH_SHORT).show();
        }
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
            return "12:00";
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
                            successOverlay.setVisibility(View.GONE);
                        }
                    });
        }, 2000);
    }

    private void updateEmptyState(boolean isEmpty) {
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvSchedules.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private List<Object> getSchedulesForDate(Date date, boolean isStudentSchedule) {
        List<Object> schedules = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = dateFormat.format(date);

        if (isStudentSchedule) {
            List<StudentClassSchedule> studentSchedules = dbHelper.getStudentSchedulesByDate(dateString);
            schedules.addAll(studentSchedules);
        } else {
            List<LecturerClassSchedule> lecturerSchedules = dbHelper.getLecturerSchedulesByDate(dateString);
            schedules.addAll(lecturerSchedules);
        }

        return schedules;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
        rootView = null;
    }
}