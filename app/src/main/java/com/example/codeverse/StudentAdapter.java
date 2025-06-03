package com.example.codeverse;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.Admin.Helpers.StudentDatabaseHelper;
import com.example.codeverse.R;
import com.example.codeverse.Students.Models.Student;

import java.util.List;


public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> students;
    private OnStudentActionListener listener;

    public StudentAdapter(List<Student> students, OnStudentActionListener listener) {
        this.students = students;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_card, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.bind(student, listener);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivStudentPhoto;
        private TextView tvFullName;
        private TextView tvUniversityId;
        private TextView tvFaculty;
        private TextView tvBatch;
        private TextView tvContactInfo;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            ivStudentPhoto = itemView.findViewById(R.id.ivStudentPhoto);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvUniversityId = itemView.findViewById(R.id.tvUniversityId);
            tvFaculty = itemView.findViewById(R.id.tvFaculty);
            tvBatch = itemView.findViewById(R.id.tvBatch);
            tvContactInfo = itemView.findViewById(R.id.tvContactInfo);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Student student, OnStudentActionListener listener) {
            tvFullName.setText(student.getFullName());
            tvUniversityId.setText("ID: " + student.getUniversityId());

            if (student.getFaculty() != null && !student.getFaculty().isEmpty()) {
                tvFaculty.setText(student.getFaculty());
                tvFaculty.setVisibility(View.VISIBLE);
            } else {
                tvFaculty.setVisibility(View.GONE);
            }

            if (student.getBatch() != null && !student.getBatch().isEmpty()) {
                tvBatch.setText(student.getBatch());
                tvBatch.setVisibility(View.VISIBLE);
            } else {
                tvBatch.setVisibility(View.GONE);
            }

            if (student.getMobileNumber() != null && !student.getMobileNumber().isEmpty()) {
                tvContactInfo.setText("📱 " + student.getMobileNumber());
                tvContactInfo.setVisibility(View.VISIBLE);
            } else if (student.getEmail() != null && !student.getEmail().isEmpty()) {
                tvContactInfo.setText("✉️ " + student.getEmail());
                tvContactInfo.setVisibility(View.VISIBLE);
            } else {
                tvContactInfo.setVisibility(View.GONE);
            }

            if (student.getPhotoUri() != null && !student.getPhotoUri().isEmpty()) {
                Bitmap photoBitmap = StudentDatabaseHelper.getStudentPhoto(student.getPhotoUri());
                if (photoBitmap != null) {
                    ivStudentPhoto.setImageBitmap(photoBitmap);
                } else {
                    ivStudentPhoto.setImageResource(R.drawable.ic_person);
                }
            } else {
                ivStudentPhoto.setImageResource(R.drawable.ic_person);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStudentClick(student);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditStudent(student);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteStudent(student);
                }
            });
        }
    }

    public interface OnStudentActionListener {
        void onEditStudent(Student student);
        void onDeleteStudent(Student student);
        void onStudentClick(Student student);
    }
}