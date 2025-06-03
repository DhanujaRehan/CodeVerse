package com.example.codeverse.Admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Admin.Models.NotificationHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationHistoryAdapter extends RecyclerView.Adapter<NotificationHistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<NotificationHistory> historyList;
    private OnHistoryClickListener clickListener;

    public interface OnHistoryClickListener {
        void onHistoryClick(NotificationHistory history);
        void onViewDetailsClick(NotificationHistory history);
    }

    public NotificationHistoryAdapter(Context context, List<NotificationHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    public void setOnHistoryClickListener(OnHistoryClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        NotificationHistory history = historyList.get(position);

        holder.tvTitle.setText(history.getTitle());
        holder.tvMessage.setText(history.getMessage());
        holder.tvCategory.setText(history.getCategory());
        holder.tvRecipients.setText(history.getRecipients());
        holder.tvPriority.setText(history.getPriority());
        holder.tvStatus.setText(history.getStatusDisplayText());


        try {
            holder.viewPriorityIndicator.setBackgroundColor(Color.parseColor(history.getPriorityColor()));
        } catch (IllegalArgumentException e) {
            holder.viewPriorityIndicator.setBackgroundColor(Color.GRAY);
        }


        setStatusColor(holder.tvStatus, history.getStatus());


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        holder.tvSentDate.setText("Sent: " + dateFormat.format(new Date(history.getSentAt())));


        holder.tvDeliveryMethods.setText(history.getDeliveryMethodsText());


        holder.tvDeliveredCount.setText(String.valueOf(history.getDeliveredCount()));
        holder.tvReadCount.setText(String.valueOf(history.getReadCount()));


        double readPercentage = history.getReadPercentage();
        holder.progressReadRate.setProgress((int) readPercentage);
        holder.tvReadPercentage.setText(String.format(Locale.getDefault(), "%.1f%%", readPercentage));


        setProgressBarColor(holder.progressReadRate, readPercentage);


        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onHistoryClick(history);
            }
        });

        holder.tvViewDetails.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onViewDetailsClick(history);
            }
        });
    }

    private void setStatusColor(TextView statusTextView, String status) {
        int color;
        switch (status.toLowerCase()) {
            case "sent":
            case "delivered":
                color = Color.parseColor("#4CAF50");
                break;
            case "pending":
                color = Color.parseColor("#FF9800");
                break;
            case "failed":
                color = Color.parseColor("#F44336");
                break;
            default:
                color = Color.parseColor("#757575");
                break;
        }
        statusTextView.setTextColor(color);
    }

    private void setProgressBarColor(ProgressBar progressBar, double percentage) {
        int color;
        if (percentage >= 70) {
            color = Color.parseColor("#4CAF50");
        } else if (percentage >= 40) {
            color = Color.parseColor("#FF9800");
        } else {
            color = Color.parseColor("#F44336");
        }
        progressBar.getProgressDrawable().setTint(color);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateHistoryList(List<NotificationHistory> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }

    public void filterByCategory(String category) {

        notifyDataSetChanged();
    }

    public void filterByStatus(String status) {

        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        View viewPriorityIndicator;
        TextView tvTitle, tvMessage, tvCategory, tvRecipients, tvPriority, tvStatus,
                tvSentDate, tvDeliveryMethods, tvDeliveredCount, tvReadCount,
                tvReadPercentage, tvViewDetails;
        ProgressBar progressReadRate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardNotificationHistory);
            viewPriorityIndicator = itemView.findViewById(R.id.viewPriorityIndicator);
            tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvMessage = itemView.findViewById(R.id.tvHistoryMessage);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvRecipients = itemView.findViewById(R.id.tvRecipients);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvSentDate = itemView.findViewById(R.id.tvSentDate);
            tvDeliveryMethods = itemView.findViewById(R.id.tvDeliveryMethods);
            tvDeliveredCount = itemView.findViewById(R.id.tvDeliveredCount);
            tvReadCount = itemView.findViewById(R.id.tvReadCount);
            tvReadPercentage = itemView.findViewById(R.id.tvReadPercentage);
            tvViewDetails = itemView.findViewById(R.id.tvViewDetails);
            progressReadRate = itemView.findViewById(R.id.progressReadRate);
        }
    }
}