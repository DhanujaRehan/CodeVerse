package com.example.codeverse.Staff.Adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Staff.Models.ScheduleModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<ScheduleModel> schedules;
    private OnScheduleActionListener actionListener;

    // Interface for schedule actions callback
    public interface OnScheduleActionListener {
        void onAction(String action, ScheduleModel schedule);
    }

    public ScheduleAdapter(List<ScheduleModel> schedules, OnScheduleActionListener listener) {
        this.schedules = schedules;
        this.actionListener = listener;
    }

    /**
     * Update the entire dataset
     */
    public void updateData(List<ScheduleModel> newSchedules) {
        this.schedules = newSchedules;
        notifyDataSetChanged();
    }

    /**
     * Add a new schedule item to the top of the list
     */
    public void addItem(ScheduleModel schedule) {
        this.schedules.add(0, schedule);
        notifyItemInserted(0);
    }

    /**
     * Remove a schedule item from the list
     */
    public void removeItem(ScheduleModel schedule) {
        int position = schedules.indexOf(schedule);
        if (position != -1) {
            schedules.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * Get the total number of items in the adapter
     */
    @Override
    public int getItemCount() {
        return schedules.size();
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
        ScheduleModel schedule = schedules.get(position);

        // Set schedule details
        holder.tvSubjectName.setText(schedule.getSubjectName());
        holder.tvModuleNumber.setText(schedule.getModuleNumber());
        holder.tvLecturerName.setText(schedule.getLecturerName());
        holder.tvClassroom.setText(schedule.getClassroom());
        holder.tvTimeStart.setText(schedule.getStartTime());
        holder.tvTimeEnd.setText(schedule.getEndTime());
        holder.tvAmPm.setText(schedule.getAmPm());

        // Set schedule type badge
        if (schedule.isStudentSchedule()) {
            holder.tvScheduleBadge.setText("Student Schedule");
            holder.badgeScheduleType.setCardBackgroundColor(
                    ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.student_badge_bg)));
            holder.tvScheduleBadge.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.blue_primary));
        } else {
            holder.tvScheduleBadge.setText("Lecturer Schedule");
            holder.badgeScheduleType.setCardBackgroundColor(
                    ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.lecturer_badge_bg)));
            holder.tvScheduleBadge.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.lecturer_text));
        }

        // Set status badge
        if ("Active".equals(schedule.getStatus())) {
            holder.tvStatusBadge.setText("Active");
            holder.badgeStatus.setCardBackgroundColor(
                    ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.active_badge_bg)));
            holder.tvStatusBadge.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.active_text));
        } else {
            holder.tvStatusBadge.setText("Canceled");
            holder.badgeStatus.setCardBackgroundColor(
                    ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.canceled_badge_bg)));
            holder.tvStatusBadge.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.canceled_text));
        }

        // Set time section gradient background
        if (schedule.isStudentSchedule()) {
            holder.layoutTimeSection.setBackgroundResource(R.drawable.schedule_time_background);
        } else {
            holder.layoutTimeSection.setBackgroundResource(R.drawable.lecturer_schedule_time_background);
        }

        // Set action listeners
        holder.btnEdit.setOnClickListener(v ->
                actionListener.onAction("edit", schedule));

        holder.btnDelete.setOnClickListener(v ->
                actionListener.onAction("delete", schedule));

        holder.btnNotify.setOnClickListener(v ->
                actionListener.onAction("notify", schedule));

        holder.btnReschedule.setOnClickListener(v ->
                actionListener.onAction("reschedule", schedule));
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName;
        TextView tvModuleNumber;
        TextView tvLecturerName;
        TextView tvClassroom;
        TextView tvTimeStart;
        TextView tvTimeEnd;
        TextView tvAmPm;
        TextView tvScheduleBadge;
        TextView tvStatusBadge;
        MaterialCardView badgeScheduleType;
        MaterialCardView badgeStatus;
        LinearLayout layoutTimeSection;
        ImageView btnEdit;
        ImageView btnDelete;
        MaterialButton btnNotify;
        MaterialButton btnReschedule;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvModuleNumber = itemView.findViewById(R.id.tv_module_number);
            tvLecturerName = itemView.findViewById(R.id.tv_lecturer_name);
            tvClassroom = itemView.findViewById(R.id.tv_classroom);
            tvTimeStart = itemView.findViewById(R.id.tv_time_start);
            tvTimeEnd = itemView.findViewById(R.id.tv_time_end);
            tvAmPm = itemView.findViewById(R.id.tv_am_pm);
            tvScheduleBadge = itemView.findViewById(R.id.tv_schedule_badge);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            badgeScheduleType = itemView.findViewById(R.id.badge_schedule_type);
            badgeStatus = itemView.findViewById(R.id.badge_status);
            layoutTimeSection = itemView.findViewById(R.id.layout_time_section);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnNotify = itemView.findViewById(R.id.btn_notify);
            btnReschedule = itemView.findViewById(R.id.btn_reschedule);
        }
    }
}