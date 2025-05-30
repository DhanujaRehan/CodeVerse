package com.example.codeverse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.R;
import com.example.codeverse.ScheduleClassModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ScheduleClassAdapter extends RecyclerView.Adapter<ScheduleClassAdapter.ScheduleViewHolder> {

    private List<ScheduleClassModel> schedules;
    private OnScheduleActionListener listener;

    public interface OnScheduleActionListener {
        void onAction(String action, ScheduleClassModel schedule);
    }

    public ScheduleClassAdapter(List<ScheduleClassModel> schedules, OnScheduleActionListener listener) {
        this.schedules = schedules;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule_card, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleClassModel schedule = schedules.get(position);

        holder.tvTimeStart.setText(schedule.getStartTime());
        holder.tvTimeEnd.setText(schedule.getEndTime());
        holder.tvAmPm.setText(schedule.getAmPm());
        holder.tvSubjectName.setText(schedule.getSubjectName());
        holder.tvModuleNumber.setText(schedule.getModuleNumber());
        holder.tvLecturerName.setText(schedule.getLecturerName());
        holder.tvClassroom.setText(schedule.getClassroom());
        holder.tvScheduleBadge.setText(schedule.isStudentSchedule() ? "Student Schedule" : "Lecturer Schedule");
        holder.tvStatusBadge.setText(schedule.getStatus());

        if (schedule.getStatus().equals("Active")) {
            holder.badgeStatus.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.green_light));
            holder.tvStatusBadge.setTextColor(holder.itemView.getContext().getColor(R.color.green_primary));
        } else {
            holder.badgeStatus.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.red_light));
            holder.tvStatusBadge.setTextColor(holder.itemView.getContext().getColor(R.color.red_primary));
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAction("edit", schedule);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAction("delete", schedule);
            }
        });

        holder.btnNotify.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAction("notify", schedule);
            }
        });

        holder.btnReschedule.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAction("reschedule", schedule);
            }
        });
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public void updateData(List<ScheduleClassModel> newSchedules) {
        this.schedules.clear();
        this.schedules.addAll(newSchedules);
        notifyDataSetChanged();
    }

    public void addItem(ScheduleClassModel schedule) {
        schedules.add(schedule);
        notifyItemInserted(schedules.size() - 1);
    }

    public void removeItem(ScheduleClassModel schedule) {
        int position = schedules.indexOf(schedule);
        if (position != -1) {
            schedules.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeStart, tvTimeEnd, tvAmPm, tvSubjectName, tvModuleNumber,
                tvLecturerName, tvClassroom, tvScheduleBadge, tvStatusBadge;
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