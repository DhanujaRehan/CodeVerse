package com.example.codeverse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.R;
import com.example.codeverse.StudentClassSchedule;
import com.example.codeverse.LecturerClassSchedule;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ScheduleAdapterNew extends RecyclerView.Adapter<ScheduleAdapterNew.ScheduleViewHolder> {

    private List<Object> schedules;
    private boolean isStudentSchedule;
    private OnScheduleActionListener listener;

    public interface OnScheduleActionListener {
        void onEdit(Object schedule);
        void onDelete(Object schedule);
        void onNotify(Object schedule);
        void onReschedule(Object schedule);
    }

    public ScheduleAdapterNew(List<Object> schedules, boolean isStudentSchedule, OnScheduleActionListener listener) {
        this.schedules = schedules;
        this.isStudentSchedule = isStudentSchedule;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_card, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Object schedule = schedules.get(position);

        if (isStudentSchedule && schedule instanceof StudentClassSchedule) {
            StudentClassSchedule studentSchedule = (StudentClassSchedule) schedule;
            bindStudentSchedule(holder, studentSchedule);
        } else if (!isStudentSchedule && schedule instanceof LecturerClassSchedule) {
            LecturerClassSchedule lecturerSchedule = (LecturerClassSchedule) schedule;
            bindLecturerSchedule(holder, lecturerSchedule);
        }
    }

    private void bindStudentSchedule(ScheduleViewHolder holder, StudentClassSchedule schedule) {
        holder.tvTimeStart.setText(schedule.getStartTime());
        holder.tvTimeEnd.setText(schedule.getEndTime());
        holder.tvAmPm.setText(schedule.getAmPm());
        holder.tvSubjectName.setText(schedule.getSubjectName());
        holder.tvModuleNumber.setText(schedule.getModuleNumber());
        holder.tvLecturerName.setText(schedule.getLecturerName());
        holder.tvClassroom.setText(schedule.getClassroom());
        holder.tvScheduleBadge.setText("Student Schedule");
        holder.tvStatusBadge.setText(schedule.getStatus());

        setStatusBadgeColor(holder, schedule.getStatus());
        setClickListeners(holder, schedule);
    }

    private void bindLecturerSchedule(ScheduleViewHolder holder, LecturerClassSchedule schedule) {
        holder.tvTimeStart.setText(schedule.getStartTime());
        holder.tvTimeEnd.setText(schedule.getEndTime());
        holder.tvAmPm.setText(schedule.getAmPm());
        holder.tvSubjectName.setText(schedule.getSubjectName());
        holder.tvModuleNumber.setText(schedule.getModuleNumber());
        holder.tvLecturerName.setText(schedule.getLecturerName());
        holder.tvClassroom.setText(schedule.getClassroom());
        holder.tvScheduleBadge.setText("Lecturer Schedule");
        holder.tvStatusBadge.setText(schedule.getStatus());

        setStatusBadgeColor(holder, schedule.getStatus());
        setClickListeners(holder, schedule);
    }

    private void setStatusBadgeColor(ScheduleViewHolder holder, String status) {
        if (status.equals("Active")) {
            holder.badgeStatus.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.priority_low));
            holder.tvStatusBadge.setTextColor(holder.itemView.getContext().getColor(R.color.green));
        } else if (status.equals("Canceled")) {
            holder.badgeStatus.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.error_color));
            holder.tvStatusBadge.setTextColor(holder.itemView.getContext().getColor(R.color.red));
        }
    }

    private void setClickListeners(ScheduleViewHolder holder, Object schedule) {
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(schedule);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(schedule);
            }
        });

        holder.btnNotify.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotify(schedule);
            }
        });

        holder.btnReschedule.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReschedule(schedule);
            }
        });
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public void updateData(List<Object> newSchedules) {
        this.schedules = newSchedules;
        notifyDataSetChanged();
    }

    public void addItem(Object schedule) {
        schedules.add(schedule);
        notifyItemInserted(schedules.size() - 1);
    }

    public void removeItem(Object schedule) {
        int position = schedules.indexOf(schedule);
        if (position != -1) {
            schedules.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeStart, tvTimeEnd, tvAmPm, tvSubjectName, tvModuleNumber;
        TextView tvLecturerName, tvClassroom, tvScheduleBadge, tvStatusBadge;
        ImageView btnEdit, btnDelete;
        MaterialButton btnNotify, btnReschedule;
        MaterialCardView badgeStatus;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTimeStart = itemView.findViewById(R.id.tv_time_start);
            tvTimeEnd = itemView.findViewById(R.id.tv_time_end);
            tvAmPm = itemView.findViewById(R.id.tv_am_pm);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvModuleNumber = itemView.findViewById(R.id.tv_module_number);
            tvLecturerName = itemView.findViewById(R.id.tv_lecturer_name);
            tvClassroom = itemView.findViewById(R.id.tv_classroom);
            tvScheduleBadge = itemView.findViewById(R.id.tv_schedule_badge);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnNotify = itemView.findViewById(R.id.btn_notify);
            btnReschedule = itemView.findViewById(R.id.btn_reschedule);
            badgeStatus = itemView.findViewById(R.id.badge_status);
        }
    }
}