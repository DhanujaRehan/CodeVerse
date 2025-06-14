package com.example.codeverse.Students.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.R;
import com.example.codeverse.Students.Models.StudentClassSchedule;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class StudentScheduleAdapter extends RecyclerView.Adapter<StudentScheduleAdapter.ViewHolder> {

    private Context context;
    private List<StudentClassSchedule> scheduleList;

    public StudentScheduleAdapter(Context context, List<StudentClassSchedule> scheduleList) {
        this.context = context;
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentClassSchedule schedule = scheduleList.get(position);

        holder.tv_class_name.setText(schedule.getSubjectName() != null ? schedule.getSubjectName() : "");

        holder.tv_professor_name.setText(schedule.getLecturerName() != null ? schedule.getLecturerName() : "");

        String timeText = "";
        if (schedule.getStartTime() != null && schedule.getEndTime() != null) {
            timeText = schedule.getStartTime() + " - " + schedule.getEndTime();
            if (schedule.getAmPm() != null) {
                timeText += " " + schedule.getAmPm();
            }
        }
        holder.tv_class_time.setText(timeText);

        holder.tv_class_location.setText(schedule.getClassroom() != null ? schedule.getClassroom() : "");

        holder.btn_join_class.setOnClickListener(v -> {
            Toast.makeText(context, "Joining " + schedule.getSubjectName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList != null ? scheduleList.size() : 0;
    }

    public void updateSchedules(List<StudentClassSchedule> newSchedules) {
        this.scheduleList = newSchedules;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_class_name, tv_professor_name, tv_class_time, tv_class_location;
        MaterialButton btn_join_class;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_class_name = itemView.findViewById(R.id.tv_class_name);
            tv_professor_name = itemView.findViewById(R.id.tv_professor_name);
            tv_class_time = itemView.findViewById(R.id.tv_class_time);
            tv_class_location = itemView.findViewById(R.id.tv_class_location);
            btn_join_class = itemView.findViewById(R.id.btn_join_class);
        }
    }
}