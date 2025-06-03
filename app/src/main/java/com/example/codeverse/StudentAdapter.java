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

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<StudentModel> students;
    private OnStudentActionListener listener;

    public StudentAdapter(List<StudentModel> students, OnStudentActionListener listener) {
        this.students = students;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_list_card, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        if (students != null && position < students.size()) {
            StudentModel student = students.get(position);
            if (student != null) {
                holder.bind(student, listener);
            }
        }
    }

    @Override
    public int getItemCount() {
        return students != null ? students.size() : 0;
    }

    public void updateStudents(List<StudentModel> newStudents) {
        this.students = newStudents;
        notifyDataSetChanged();
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

        public void bind(StudentModel student, OnStudentActionListener listener) {
            if (student == null) return;

            if (tvFullName != null) {
                tvFullName.setText(student.getFullName());
            }

            if (tvUniversityId != null) {
                tvUniversityId.setText("ID: " + student.getUniversityId());
            }

            if (tvFaculty != null) {
                if (!student.getFaculty().trim().isEmpty()) {
                    tvFaculty.setText(student.getFaculty());
                    tvFaculty.setVisibility(View.VISIBLE);
                } else {
                    tvFaculty.setVisibility(View.GONE);
                }
            }

            if (tvBatch != null) {
                if (!student.getBatch().trim().isEmpty()) {
                    tvBatch.setText(student.getBatch());
                    tvBatch.setVisibility(View.VISIBLE);
                } else {
                    tvBatch.setVisibility(View.GONE);
                }
            }

            if (tvContactInfo != null) {
                if (!student.getMobileNumber().trim().isEmpty()) {
                    tvContactInfo.setText("📱 " + student.getMobileNumber());
                    tvContactInfo.setVisibility(View.VISIBLE);
                } else if (!student.getEmail().trim().isEmpty()) {
                    tvContactInfo.setText("✉️ " + student.getEmail());
                    tvContactInfo.setVisibility(View.VISIBLE);
                } else {
                    tvContactInfo.setVisibility(View.GONE);
                }
            }

            if (ivStudentPhoto != null) {
                if (!student.getPhotoUri().trim().isEmpty()) {
                    Bitmap photoBitmap = StudentDatabaseHelper.getStudentPhoto(student.getPhotoUri());
                    if (photoBitmap != null) {
                        ivStudentPhoto.setImageBitmap(photoBitmap);
                    } else {
                        ivStudentPhoto.setImageResource(R.drawable.ic_person);
                    }
                } else {
                    ivStudentPhoto.setImageResource(R.drawable.ic_person);
                }
            }

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onStudentClick(student));

                if (btnEdit != null) {
                    btnEdit.setOnClickListener(v -> listener.onEditStudent(student));
                }

                if (btnDelete != null) {
                    btnDelete.setOnClickListener(v -> listener.onDeleteStudent(student));
                }
            }
        }
    }

    public interface OnStudentActionListener {
        void onEditStudent(StudentModel student);
        void onDeleteStudent(StudentModel student);
        void onStudentClick(StudentModel student);
    }
}