package com.example.codeverse.Admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Admin.Models.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<Notification> notificationList;
    private OnNotificationClickListener clickListener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
        void onEditClick(Notification notification);
        void onDeleteClick(Notification notification);
        void onResendClick(Notification notification);
    }

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        holder.tvCategory.setText(notification.getCategory());
        holder.tvRecipients.setText(notification.getRecipients());
        holder.tvPriority.setText(notification.getPriority());
        holder.tvStatus.setText(notification.getStatusDisplayText());

        // Set priority indicator color
        try {
            holder.viewPriorityIndicator.setBackgroundColor(Color.parseColor(notification.getPriorityColor()));
        } catch (IllegalArgumentException e) {
            holder.viewPriorityIndicator.setBackgroundColor(Color.GRAY);
        }

        // Set status color
        setStatusColor(holder.tvStatus, notification.getStatus());

        // Format and set date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String dateText;

        if ("scheduled".equals(notification.getStatus())) {
            dateText = "Scheduled: " + dateFormat.format(new Date(notification.getScheduledTime()));
        } else {
            dateText = "Created: " + dateFormat.format(new Date(notification.getCreatedAt()));
        }
        holder.tvDate.setText(dateText);

        // Set delivery methods
        StringBuilder methods = new StringBuilder();
        if (notification.isPushEnabled()) methods.append("Push ");
        if (notification.isEmailEnabled()) methods.append("Email ");
        if (notification.isSmsEnabled()) methods.append("SMS ");

        holder.tvDeliveryMethods.setText(methods.toString().trim());

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onNotificationClick(notification);
            }
        });

        holder.ivEdit.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onEditClick(notification);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onDeleteClick(notification);
            }
        });

        holder.ivResend.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onResendClick(notification);
            }
        });

        // Show/hide action buttons based on status
        if ("draft".equals(notification.getStatus())) {
            holder.ivEdit.setVisibility(View.VISIBLE);
            holder.ivResend.setVisibility(View.GONE);
        } else {
            holder.ivEdit.setVisibility(View.GONE);
            holder.ivResend.setVisibility(View.VISIBLE);
        }
    }

    private void setStatusColor(TextView statusTextView, String status) {
        int color;
        switch (status.toLowerCase()) {
            case "sent":
                color = Color.parseColor("#4CAF50"); // Green
                break;
            case "scheduled":
                color = Color.parseColor("#2196F3"); // Blue
                break;
            case "draft":
                color = Color.parseColor("#FF9800"); // Orange
                break;
            case "failed":
                color = Color.parseColor("#F44336"); // Red
                break;
            default:
                color = Color.parseColor("#757575"); // Grey
                break;
        }
        statusTextView.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateNotificationList(List<Notification> newList) {
        this.notificationList = newList;
        notifyDataSetChanged();
    }

    public void removeNotification(int position) {
        notificationList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, notificationList.size());
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        View viewPriorityIndicator;
        TextView tvTitle, tvMessage, tvCategory, tvRecipients, tvPriority, tvStatus, tvDate, tvDeliveryMethods;
        ImageView ivEdit, ivDelete, ivResend;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardNotification);
            viewPriorityIndicator = itemView.findViewById(R.id.viewPriorityIndicator);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvRecipients = itemView.findViewById(R.id.tvRecipients);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDeliveryMethods = itemView.findViewById(R.id.tvDeliveryMethods);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivResend = itemView.findViewById(R.id.ivResend);
        }
    }
}