package com.example.codeverse.Staff.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Staff.Models.StaffCourse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.List;

public class StaffCourseAdapter extends RecyclerView.Adapter<StaffCourseAdapter.StaffCourseViewHolder> {

    private final List<StaffCourse> courseList;
    private final Context context;

    public StaffCourseAdapter(Context context, List<StaffCourse> courseList, OnCourseClickListener onCourseClickListener) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public StaffCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff_course, parent, false);
        return new StaffCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffCourseViewHolder holder, int position) {
        StaffCourse course = courseList.get(position);


        holder.tvCourseName.setText(course.getCourseName());
        holder.tvCourseId.setText(course.getCourseId());
        holder.tvSemester.setText(course.getSemester());
        holder.tvCourseDescription.setText(course.getCourseDescription());
        holder.chipStudentCount.setText(course.getStudentCount() + " Students");
        holder.ivCourseImage.setImageResource(course.getImageResourceId());


        holder.btnCourseDetails.setOnClickListener(v -> {
            Toast.makeText(context, "Viewing details for " + course.getCourseName(), Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public interface OnCourseClickListener {
        void onCourseClick(StaffCourse course);
    }

    public static class StaffCourseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCourseImage;
        TextView tvCourseName, tvCourseId, tvSemester, tvCourseDescription;
        Chip chipStudentCount;
        MaterialButton btnCourseDetails;

        public StaffCourseViewHolder(@NonNull View itemView) {
            super(itemView);


            ivCourseImage = itemView.findViewById(R.id.iv_course_image);
            tvCourseName = itemView.findViewById(R.id.tv_course_name);
            tvCourseId = itemView.findViewById(R.id.tv_course_id);
            tvSemester = itemView.findViewById(R.id.tv_semester);
            tvCourseDescription = itemView.findViewById(R.id.tv_course_description);
            chipStudentCount = itemView.findViewById(R.id.chip_student_count);
            btnCourseDetails = itemView.findViewById(R.id.btn_course_details);
        }
    }
}