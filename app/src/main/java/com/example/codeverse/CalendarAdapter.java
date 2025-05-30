package com.example.codeverse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Models.CalendarDayModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<CalendarDayModel> days;
    private OnDateSelectedListener listener;

    public interface OnDateSelectedListener {
        void onDateSelected(Date date);
    }

    public CalendarAdapter(List<CalendarDayModel> days, OnDateSelectedListener listener) {
        this.days = days;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDayModel day = days.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("d", Locale.getDefault());
        holder.tvDay.setText(sdf.format(day.getDate()));

        if (day.isCurrentMonth()) {
            holder.tvDay.setTextColor(holder.itemView.getContext().getColor(R.color.text_primary));
            holder.tvDay.setAlpha(1.0f);
        } else {
            holder.tvDay.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary));
            holder.tvDay.setAlpha(0.5f);
        }

        if (day.isSelected()) {
            holder.itemView.setBackgroundResource(R.drawable.calendar_day_selected);
            holder.tvDay.setTextColor(holder.itemView.getContext().getColor(R.color.white));
        } else {
            holder.itemView.setBackgroundResource(android.R.color.transparent);
        }

        if (day.hasEvents() && day.isCurrentMonth()) {
            holder.eventIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.eventIndicator.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && day.isCurrentMonth()) {
                for (CalendarDayModel d : days) {
                    d.setSelected(false);
                }
                day.setSelected(true);
                notifyDataSetChanged();
                listener.onDateSelected(day.getDate());
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public void updateData(List<CalendarDayModel> newDays) {
        this.days.clear();
        this.days.addAll(newDays);
        notifyDataSetChanged();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        View eventIndicator;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            eventIndicator = itemView.findViewById(R.id.event_indicator);
        }
    }
}