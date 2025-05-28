package com.example.codeverse.Admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.Admin.Models.NotificationHistory;
import com.example.codeverse.R;

import java.util.List;

public class NotificationHistoryAdapter extends RecyclerView.Adapter<NotificationHistoryAdapter.HistoryViewHolder> {
    private Context context;
    private List<NotificationHistory> historyList;

    public NotificationHistoryAdapter(Context context, List<NotificationHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
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
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateHistory(List<NotificationHistory> newHistory) {
        this.historyList = newHistory;
        notifyDataSetChanged();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvAction, tvDetails, tvTimestamp;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAction = itemView.findViewById(R.id.tvAction);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        public void bind(NotificationHistory history) {
            tvAction.setText(history.getAction());
            tvDetails.setText(history.getDetails());
            tvTimestamp.setText(history.getTimestamp());
        }
    }
}
