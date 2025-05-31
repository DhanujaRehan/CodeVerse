package com.example.codeverse.Students.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Models.StudentClassSchedule;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class StudentClassAdapter extends RecyclerView.Adapter<StudentClassAdapter.ClassViewHolder> {

    private List<StudentClassSchedule> classes;
    private OnClassActionListener listener;

    public interface OnClassActionListener {
        void onJoinClass(StudentClassSchedule classSchedule);
        void onViewDetails(StudentClassSchedule classSchedule);
        void onOptionsClick(StudentClassSchedule classSchedule);
    }

    public StudentClassAdapter(List<StudentClassSchedule> classes, OnClassActionListener listener) {
        this.classes = classes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_class_card, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        StudentClassSchedule classSchedule = classes.get(position);

        holder.tvStartTime.setText(classSchedule.getStartTime());
        holder.tvEndTime.setText(classSchedule.getEndTime());
        holder.tvAmPm.setText(classSchedule.getAmPm());
        holder.tvSubjectName.setText(classSchedule.getSubjectName());
        holder.tvModuleNumber.setText(classSchedule.getModuleNumber());
        holder.tvLecturerName.setText(classSchedule.getLecturerName());
        holder.tvClassroom.setText(classSchedule.getClassroom());
        holder.tvStatus.setText(classSchedule.getStatus());

        setStatusColor(holder, classSchedule.getStatus());
        setClickListeners(holder, classSchedule);
    }

    private void setStatusColor(ClassViewHolder holder, String status) {
        if (status.equals("Active")) {
            holder.tvStatus.setBackgroundResource(R.drawable.status_background_green);
            holder.cardTimeSection.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.green));
        } else if (status.equals("Canceled")) {
            holder.tvStatus.setBackgroundResource(R.drawable.status_background_red);
            holder.cardTimeSection.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.red));
        } else if (status.equals("Completed")) {
            holder.tvStatus.setBackgroundResource(R.drawable.status_background_gray);
            holder.cardTimeSection.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.gray));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.status_background_blue);
            holder.cardTimeSection.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.blue_primary));
        }
    }

    private void setClickListeners(ClassViewHolder holder, StudentClassSchedule classSchedule) {
        holder.btnJoinClass.setOnClickListener(v -> {
            if (listener != null) {
                listener.onJoinClass(classSchedule);
            }
        });

        holder.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetails(classSchedule);
            }
        });

        holder.ivOptions.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOptionsClick(classSchedule);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public void updateData(List<StudentClassSchedule> newClasses) {
        this.classes = newClasses;
        notifyDataSetChanged();
    }

    public void addItem(StudentClassSchedule classSchedule) {
        classes.add(classSchedule);
        notifyItemInserted(classes.size() - 1);
    }

    public void removeItem(StudentClassSchedule classSchedule) {
        int position = classes.indexOf(classSchedule);
        if (position != -1) {
            classes.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvStartTime, tvEndTime, tvAmPm, tvSubjectName, tvModuleNumber;
        TextView tvLecturerName, tvClassroom, tvStatus;
        MaterialButton btnJoinClass, btnViewDetails;
        ImageView ivOptions;
        MaterialCardView cardTimeSection;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStartTime = itemView.findViewById(R.id.tv_start_time);
            tvEndTime = itemView.findViewById(R.id.tv_end_time);
            tvAmPm = itemView.findViewById(R.id.tv_am_pm);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvModuleNumber = itemView.findViewById(R.id.tv_module_number);
            tvLecturerName = itemView.findViewById(R.id.tv_lecturer_name);
            tvClassroom = itemView.findViewById(R.id.tv_classroom);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnJoinClass = itemView.findViewById(R.id.btn_join_class);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
            ivOptions = itemView.findViewById(R.id.iv_options);
            cardTimeSection = itemView.findViewById(R.id.card_time_section);
        }
    }
}