package com.example.codeverse.Students.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.Exam;
import com.example.codeverse.R;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class StudentExamAdapter extends RecyclerView.Adapter<StudentExamAdapter.ViewHolder> {
    private Context context;
    private List<Exam> examList;
    private OnExamActionListener listener;

    public interface OnExamActionListener {
        void onDownloadAdmitCard(Exam exam);
    }

    public StudentExamAdapter(Context context, List<Exam> examList) {
        this.context = context;
        this.examList = examList;
    }

    public void setOnExamActionListener(OnExamActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exam_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exam exam = examList.get(position);

        holder.tvExamSubject.setText(exam.getSubjectName());
        holder.tvExamDetails.setText(exam.getExamType() + " • " + exam.getExamDate());
        holder.tvCourseCode.setText(exam.getCourseCode());
        holder.tvExamStatus.setText(exam.getStatus());
        holder.tvExamDatetime.setText(exam.getExamDate() + " • " + exam.getStartTime());
        holder.tvExamRoom.setText(exam.getRoom().isEmpty() ? "TBA" : exam.getRoom());
        holder.tvExamInstructor.setText(exam.getInstructor());

        setStatusAppearance(holder, exam.getStatus());

        holder.btnDownloadAdmit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDownloadAdmitCard(exam);
            }
        });
    }

    private void setStatusAppearance(ViewHolder holder, String status) {
        switch (status) {
            case "Scheduled":
                holder.tvExamStatus.setBackgroundResource(R.drawable.chip_success_background);
                holder.tvExamStatus.setTextColor(context.getResources().getColor(R.color.green_700));
                holder.cvExamIcon.setCardBackgroundColor(context.getResources().getColor(R.color.green_50));
                holder.btnDownloadAdmit.setEnabled(true);
                holder.btnDownloadAdmit.setText("Download Admit Card");
                break;
            case "Pending":
                holder.tvExamStatus.setBackgroundResource(R.drawable.chip_warning_background);
                holder.tvExamStatus.setTextColor(context.getResources().getColor(R.color.orange_700));
                holder.cvExamIcon.setCardBackgroundColor(context.getResources().getColor(R.color.orange_50));
                holder.btnDownloadAdmit.setEnabled(false);
                holder.btnDownloadAdmit.setText("Not Available Yet");
                break;
            case "Cancelled":
                holder.tvExamStatus.setBackgroundResource(R.drawable.chip_error_background);
                holder.tvExamStatus.setTextColor(context.getResources().getColor(R.color.red_700));
                holder.cvExamIcon.setCardBackgroundColor(context.getResources().getColor(R.color.red_50));
                holder.btnDownloadAdmit.setEnabled(false);
                holder.btnDownloadAdmit.setText("Cancelled");
                break;
            default:
                holder.tvExamStatus.setBackgroundResource(R.drawable.chip_default_background);
                holder.tvExamStatus.setTextColor(context.getResources().getColor(R.color.gray_700));
                holder.cvExamIcon.setCardBackgroundColor(context.getResources().getColor(R.color.gray_50));
                holder.btnDownloadAdmit.setEnabled(false);
                holder.btnDownloadAdmit.setText("Unknown Status");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    public void updateExams(List<Exam> newExams) {
        this.examList = newExams;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExamSubject, tvExamDetails, tvCourseCode, tvExamStatus;
        TextView tvExamDatetime, tvExamRoom, tvExamInstructor;
        Button btnDownloadAdmit;
        MaterialCardView cvExamIcon;
        LottieAnimationView animationExamStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExamSubject = itemView.findViewById(R.id.tv_exam_subject);
            tvExamDetails = itemView.findViewById(R.id.tv_exam_details);
            tvCourseCode = itemView.findViewById(R.id.tv_course_code);
            tvExamStatus = itemView.findViewById(R.id.tv_exam_status);
            tvExamDatetime = itemView.findViewById(R.id.tv_exam_datetime);
            tvExamRoom = itemView.findViewById(R.id.tv_exam_room);
            tvExamInstructor = itemView.findViewById(R.id.tv_exam_instructor);
            btnDownloadAdmit = itemView.findViewById(R.id.btn_download_admit);
            cvExamIcon = itemView.findViewById(R.id.cv_exam_icon);
            animationExamStatus = itemView.findViewById(R.id.animation_exam_status);
        }
    }
}