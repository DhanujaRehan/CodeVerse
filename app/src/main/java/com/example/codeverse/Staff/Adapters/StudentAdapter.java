package com.example.codeverse.Staff.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Staff.Models.StudentModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Adapter for displaying student cards in a RecyclerView
 */
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<StudentModel> students;
    private OnStudentActionListener actionListener;

    /**
     * Interface for student actions callback
     */
    public interface OnStudentActionListener {
        void onAction(String action, StudentModel student);
    }

    /**
     * Constructor
     */
    public StudentAdapter(List<StudentModel> students, OnStudentActionListener listener) {
        this.students = students;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_card, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentModel student = students.get(position);

        // Set student details
        holder.tvStudentName.setText(student.getName());
        holder.tvStudentId.setText(student.getStudentId());
        holder.tvDepartment.setText(student.getDepartment());
        holder.tvYear.setText(student.getYear());
        holder.tvBadge.setText(student.getBadge());
        holder.ivStudentImage.setImageResource(student.getImageResource());

        // Set gradient color based on badge
        switch (student.getBadge()) {
            case "Honor Roll":
                holder.studentImageBg.setBackgroundResource(R.drawable.student_image_gradient);
                break;
            case "Dean's List":
                holder.studentImageBg.setBackgroundResource(R.drawable.student_image_gradient_blue);
                break;
            case "President's List":
                holder.studentImageBg.setBackgroundResource(R.drawable.student_image_gradient_blue);
                break;
            case "Scholarship":
                holder.studentImageBg.setBackgroundResource(R.drawable.student_image_gradient_blue);
                break;
            case "Academic Warning":
                holder.studentImageBg.setBackgroundResource(R.drawable.student_image_gradient_blue);
                break;
            case "At Risk":
                holder.studentImageBg.setBackgroundResource(R.drawable.student_image_gradient_blue);
                break;
            default:
                holder.studentImageBg.setBackgroundResource(R.drawable.student_image_gradient);
                break;
        }

        // Set action listeners
        holder.btnViewProfile.setOnClickListener(v ->
                actionListener.onAction("profile", student));

        holder.btnViewGrades.setOnClickListener(v ->
                actionListener.onAction("grades", student));

        holder.btnMessage.setOnClickListener(v ->
                actionListener.onAction("message", student));

        holder.btnMoreOptions.setOnClickListener(v ->
                actionListener.onAction("more", student));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    /**
     * Update the adapter data
     */
    public void updateData(List<StudentModel> newStudents) {
        this.students = newStudents;
        notifyDataSetChanged();
    }

    /**
     * Add a new student to the list
     */
    public void addStudent(StudentModel student) {
        students.add(0, student);
        notifyItemInserted(0);
    }

    /**
     * Remove a student from the list
     */
    public void removeStudent(StudentModel student) {
        int position = students.indexOf(student);
        if (position != -1) {
            students.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * ViewHolder class for student items
     */
    static class StudentViewHolder extends RecyclerView.ViewHolder {
        View studentImageBg;
        ImageView ivStudentImage;
        TextView tvStudentName;
        TextView tvStudentId;
        TextView tvDepartment;
        TextView tvYear;
        TextView tvBadge;
        ImageView btnMoreOptions;
        MaterialButton btnViewProfile;
        MaterialButton btnViewGrades;
        MaterialButton btnMessage;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentImageBg = itemView.findViewById(R.id.student_image_bg);
            ivStudentImage = itemView.findViewById(R.id.iv_student_image);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentId = itemView.findViewById(R.id.tv_student_id);
            tvDepartment = itemView.findViewById(R.id.tv_department);
            tvYear = itemView.findViewById(R.id.tv_year);
            tvBadge = itemView.findViewById(R.id.tv_badge);
            btnMoreOptions = itemView.findViewById(R.id.btn_more_options);
            btnViewProfile = itemView.findViewById(R.id.btn_view_profile);
            btnViewGrades = itemView.findViewById(R.id.btn_view_grades);
            btnMessage = itemView.findViewById(R.id.btn_message);
        }
    }
}