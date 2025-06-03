package com.example.codeverse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.yourpackage.models.TimeTableModel;
import java.util.List;

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.ViewHolder> {

    private List<TimeTableModel> timeTableList;
    private OnDownloadClickListener downloadClickListener;

    public interface OnDownloadClickListener {
        void onDownloadClick(TimeTableModel model);
    }

    public TimeTableAdapter(List<TimeTableModel> timeTableList, OnDownloadClickListener listener) {
        this.timeTableList = timeTableList;
        this.downloadClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_class_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeTableModel model = timeTableList.get(position);

        holder.tvSubjectName.setText(model.getSubjectName());
        holder.tvTeacherName.setText(model.getTeacherName());
        holder.tvClassroom.setText(model.getClassroom());
        holder.tvTime.setText(model.getStartTime() + " - " + model.getEndTime());
        holder.tvDate.setText(model.getDate());

        // Set status indicator
        if ("active".equalsIgnoreCase(model.getStatus())) {
            holder.viewStatusIndicator.setBackgroundColor(0xFF4CAF50); // Green
            holder.tvStatus.setText("Active");
            holder.tvStatus.setTextColor(0xFF4CAF50);
        } else if ("scheduled".equalsIgnoreCase(model.getStatus())) {
            holder.viewStatusIndicator.setBackgroundColor(0xFFFF9800); // Orange
            holder.tvStatus.setText("Scheduled");
            holder.tvStatus.setTextColor(0xFFFF9800);
        } else {
            holder.viewStatusIndicator.setBackgroundColor(0xFF757575); // Gray
            holder.tvStatus.setText("Completed");
            holder.tvStatus.setTextColor(0xFF757575);
        }

        // Handle download button click
        holder.btnDownload.setOnClickListener(v -> {
            if (downloadClickListener != null) {
                downloadClickListener.onDownloadClick(model);
            }
        });

        // Handle card click
        holder.cardView.setOnClickListener(v -> {
            // You can add navigation to class details here
        });
    }

    @Override
    public int getItemCount() {
        return timeTableList.size();
    }

    public void updateData(List<TimeTableModel> newList) {
        this.timeTableList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvSubjectName, tvTeacherName, tvClassroom, tvTime, tvDate, tvStatus;
        ImageView btnDownload;
        View viewStatusIndicator;
        LinearLayout layoutClassInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_class);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvTeacherName = itemView.findViewById(R.id.tv_teacher_name);
            tvClassroom = itemView.findViewById(R.id.tv_classroom);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnDownload = itemView.findViewById(R.id.btn_download);
            viewStatusIndicator = itemView.findViewById(R.id.view_status_indicator);
            layoutClassInfo = itemView.findViewById(R.id.layout_class_info);
        }
    }
}