package com.example.codeverse.Students.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.R;
import com.example.codeverse.Students.Models.TimetableItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder> {

    private List<TimetableItem> timetableList;
    private OnDownloadClickListener downloadListener;

    public interface OnDownloadClickListener {
        void onDownloadClick(TimetableItem item);
    }

    public TimetableAdapter(List<TimetableItem> timetableList, OnDownloadClickListener listener) {
        this.timetableList = timetableList;
        this.downloadListener = listener;
    }

    @NonNull
    @Override
    public TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timetable, parent, false);
        return new TimetableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableViewHolder holder, int position) {
        TimetableItem item = timetableList.get(position);

        holder.tvWeekTitle.setText(item.weekTitle != null ? item.weekTitle : "Unknown Week");
        holder.tvDateRange.setText(formatDateRange(item.startDate, item.endDate));
        holder.chipSize.setText(item.fileSize != null ? item.fileSize : "Unknown");

        if ("Available".equals(item.status)) {
            holder.chipStatus.setText("Available");
            holder.chipStatus.setChipBackgroundColorResource(R.color.green_500);
            holder.btnDownload.setEnabled(true);
            holder.btnDownload.setAlpha(1.0f);
        } else {
            holder.chipStatus.setText("Unavailable");
            holder.chipStatus.setChipBackgroundColorResource(R.color.red_dark);
            holder.btnDownload.setEnabled(false);
            holder.btnDownload.setAlpha(0.5f);
        }

        holder.btnDownload.setOnClickListener(v -> {
            if (downloadListener != null && "Available".equals(item.status)) {
                downloadListener.onDownloadClick(item);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (downloadListener != null && "Available".equals(item.status)) {
                downloadListener.onDownloadClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timetableList != null ? timetableList.size() : 0;
    }

    private String formatDateRange(String startDate, String endDate) {
        if (startDate == null || endDate == null) {
            return "Date not available";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d", Locale.getDefault());

            Date start = inputFormat.parse(startDate);
            Date end = inputFormat.parse(endDate);

            if (start != null && end != null) {
                return outputFormat.format(start) + " - " + outputFormat.format(end) + ", " +
                        new SimpleDateFormat("yyyy", Locale.getDefault()).format(start);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate + " - " + endDate;
    }

    public void updateData(List<TimetableItem> newList) {
        this.timetableList = newList;
        notifyDataSetChanged();
    }

    static class TimetableViewHolder extends RecyclerView.ViewHolder {
        TextView tvWeekTitle, tvDateRange;
        Chip chipStatus, chipSize;
        MaterialButton btnDownload;

        public TimetableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeekTitle = itemView.findViewById(R.id.tv_week_title);
            tvDateRange = itemView.findViewById(R.id.tv_date_range);
            chipStatus = itemView.findViewById(R.id.chip_status);
            chipSize = itemView.findViewById(R.id.chip_size);
            btnDownload = itemView.findViewById(R.id.btn_download);
        }
    }
}