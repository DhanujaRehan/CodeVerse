package com.example.codeverse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.StudentNotification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentNotificationAdapter extends RecyclerView.Adapter<StudentNotificationAdapter.ViewHolder> {

    private Context context;
    private List<StudentNotification> notifications;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(StudentNotification notification);
    }

    public StudentNotificationAdapter(Context context, List<StudentNotification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentNotification notification = notifications.get(position);

        holder.tvNotificationTitle.setText(notification.getTitle());
        holder.tvNotificationMessage.setText(notification.getMessage());
        holder.tvCategory.setText(notification.getCategory());
        holder.tvRecipients.setText(notification.getRecipients());
        holder.tvPriority.setText(notification.getPriority());

        setPriorityColor(holder.tvPriority, notification.getPriority());

        String dateTime = formatDateTime(notification.getCreatedAt());
        holder.tvDateTime.setText(dateTime);

        if (notification.hasAttachment()) {
            holder.layoutAttachments.setVisibility(View.VISIBLE);
            int count = notification.getAttachmentCount();
            holder.tvAttachmentCount.setText(count + " attachment" + (count > 1 ? "s" : ""));
        } else {
            holder.layoutAttachments.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });
    }

    private void setPriorityColor(TextView tvPriority, String priority) {
        switch (priority.toLowerCase()) {
            case "urgent":
                tvPriority.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#FF5252")));
                break;
            case "high":
                tvPriority.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#FF9800")));
                break;
            case "medium":
                tvPriority.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#2196F3")));
                break;
            case "low":
                tvPriority.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#4CAF50")));
                break;
        }
    }

    private String formatDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateNotifications(List<StudentNotification> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotificationTitle, tvNotificationMessage, tvCategory, tvRecipients;
        TextView tvDateTime, tvPriority, tvAttachmentCount;
        LinearLayout layoutAttachments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvNotificationMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvRecipients = itemView.findViewById(R.id.tvRecipients);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvAttachmentCount = itemView.findViewById(R.id.tvAttachmentCount);
            layoutAttachments = itemView.findViewById(R.id.layoutAttachments);
        }
    }
}