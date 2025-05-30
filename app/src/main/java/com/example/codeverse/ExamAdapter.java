package com.example.codeverse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {
    private Context context;
    private List<Exam> examList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Exam exam);
    }

    public ExamAdapter(Context context, List<Exam> examList) {
        this.context = context;
        this.examList = examList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exam_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exam exam = examList.get(position);

        holder.tvSubjectName.setText(exam.getSubjectName());
        holder.tvCourseCode.setText(exam.getCourseCode());
        holder.tvSemester.setText(exam.getSemester());
        holder.tvExamType.setText(exam.getExamType());
        holder.tvStatus.setText(exam.getStatus());
        holder.tvExamDate.setText(exam.getExamDate());
        holder.tvExamTime.setText(exam.getStartTime() + " - " + exam.getEndTime());
        holder.tvRoom.setText(exam.getRoom());
        holder.tvInstructor.setText(exam.getInstructor());
        holder.tvStudentCount.setText(exam.getStudentCount() + " Students");
        holder.tvDuration.setText("3 Hours");

        if ("Pending".equals(exam.getStatus())) {
            holder.layoutPendingInfo.setVisibility(View.VISIBLE);
        } else {
            holder.layoutPendingInfo.setVisibility(View.GONE);
        }

        holder.btnEditSchedule.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(exam);
            }
        });
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    public void updateList(List<Exam> newList) {
        this.examList = newList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvCourseCode, tvSemester, tvExamType, tvStatus;
        TextView tvExamDate, tvExamTime, tvDuration, tvRoom, tvInstructor, tvStudentCount;
        Button btnEditSchedule;
        LinearLayout layoutPendingInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvCourseCode = itemView.findViewById(R.id.tv_course_code);
            tvSemester = itemView.findViewById(R.id.tv_semester);
            tvExamType = itemView.findViewById(R.id.tv_exam_type);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvExamDate = itemView.findViewById(R.id.tv_exam_date);
            tvExamTime = itemView.findViewById(R.id.tv_exam_time);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvRoom = itemView.findViewById(R.id.tv_room);
            tvInstructor = itemView.findViewById(R.id.tv_instructor);
            tvStudentCount = itemView.findViewById(R.id.tv_student_count);
            btnEditSchedule = itemView.findViewById(R.id.btn_edit_schedule);
            layoutPendingInfo = itemView.findViewById(R.id.layout_pending_info);
        }
    }
}