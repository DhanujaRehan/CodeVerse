package com.example.codeverse.Admin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.Admin.Models.Notification;
import com.example.codeverse.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private Context context;
    private List<Notification> notifications;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
        void onNotificationLongClick(Notification notification);
    }

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateNotifications(List<Notification> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvPriority, tvStatus, tvCategory, tvRecipients, tvDateTime;
        ImageView ivAttachment;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvRecipients = itemView.findViewById(R.id.tvRecipients);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            ivAttachment = itemView.findViewById(R.id.ivAttachment);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationClick(notifications.get(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationLongClick(notifications.get(getAdapterPosition()));
                }
                return true;
            });
        }

        public void bind(Notification notification) {
            tvTitle.setText(notification.getTitle());
            tvMessage.setText(notification.getMessage());
            tvPriority.setText(notification.getPriority());
            tvStatus.setText(notification.getStatus());
            tvCategory.setText(notification.getCategory());
            tvRecipients.setText(notification.getRecipients());
            tvDateTime.setText(notification.getCreatedDate() + " " + notification.getCreatedTime());

            // Set priority background color
            setPriorityColor(tvPriority, notification.getPriority());

            // Set status background color
            setStatusColor(tvStatus, notification.getStatus());

            // Show attachment icon if attachment exists
            if (notification.getAttachmentPath() != null && !notification.getAttachmentPath().isEmpty()) {
                ivAttachment.setVisibility(View.VISIBLE);
            } else {
                ivAttachment.setVisibility(View.GONE);
            }
        }

        private void setPriorityColor(TextView textView, String priority) {
            int colorRes;
            switch (priority.toUpperCase()) {
                case "HIGH":
                    colorRes = R.color.priority_high;
                    break;
                case "MEDIUM":
                    colorRes = R.color.priority_medium;
                    break;
                case "LOW":
                    colorRes = R.color.priority_low;
                    break;
                default:
                    colorRes = R.color.text_secondary;
                    break;
            }
            textView.setBackgroundTintList(context.getResources().getColorStateList(colorRes));
        }

        private void setStatusColor(TextView textView, String status) {
            int colorRes;
            switch (status.toUpperCase()) {
                case "SENT":
                    colorRes = R.color.status_sent;
                    break;
                case "SCHEDULED":
                    colorRes = R.color.status_scheduled;
                    break;
                case "DRAFT":
                    colorRes = R.color.status_draft;
                    break;
                default:
                    colorRes = R.color.text_secondary;
                    break;
            }
            textView.setBackgroundTintList(context.getResources().getColorStateList(colorRes));
        }
    }
}
