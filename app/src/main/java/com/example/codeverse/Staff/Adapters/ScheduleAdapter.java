package com.example.codeverse.Staff.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Models.LecturerClassSchedule;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<LecturerClassSchedule> schedules;

    public ScheduleAdapter(List<LecturerClassSchedule> schedules) {
        this.schedules = schedules;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_card, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        LecturerClassSchedule schedule = schedules.get(position);

        holder.tvSubjectName.setText(schedule.getSubjectName());
        holder.tvModuleNumber.setText(schedule.getModuleNumber());
        holder.tvClassroom.setText(schedule.getClassroom());

        holder.tvTimeStart.setText(schedule.getStartTime());
        holder.tvTimeEnd.setText(schedule.getEndTime());
        holder.tvAmPm.setText(schedule.getAmPm());

        holder.tvStatusBadge.setText(schedule.getStatus());
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public void updateSchedules(List<LecturerClassSchedule> newSchedules) {
        this.schedules = newSchedules;
        notifyDataSetChanged();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvModuleNumber, tvClassroom, tvTimeStart, tvTimeEnd, tvAmPm, tvStatusBadge;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvModuleNumber = itemView.findViewById(R.id.tv_module_number);
            tvClassroom = itemView.findViewById(R.id.tv_classroom);
            tvTimeStart = itemView.findViewById(R.id.tv_time_start);
            tvTimeEnd = itemView.findViewById(R.id.tv_time_end);
            tvAmPm = itemView.findViewById(R.id.tv_am_pm);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
        }
    }
}