package com.example.codeverse.Staff.Adapters;

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

    private List<CalendarDayModel> calendarDays;
    private OnDateSelectedListener listener;

    public interface OnDateSelectedListener {
        void onDateSelected(Date date);
    }

    public CalendarAdapter(List<CalendarDayModel> calendarDays, OnDateSelectedListener listener) {
        this.calendarDays = calendarDays;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDayModel day = calendarDays.get(position);

        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
        holder.tvDay.setText(dayFormat.format(day.getDate()));

        if (day.isCurrentMonth()) {
            holder.tvDay.setTextColor(holder.itemView.getContext().getColor(R.color.black));
            holder.tvDay.setAlpha(1.0f);
        } else {
            holder.tvDay.setTextColor(holder.itemView.getContext().getColor(R.color.gray));
            holder.tvDay.setAlpha(0.5f);
        }

        if (day.isSelected()) {
            holder.itemView.setBackgroundResource(R.drawable.calendar_selected_background);
            holder.tvDay.setTextColor(holder.itemView.getContext().getColor(R.color.white));
        } else if (day.isHasEvents()) {
            holder.itemView.setBackgroundResource(R.drawable.calendar_event_background);
        } else {
            holder.itemView.setBackground(null);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDateSelected(day.getDate());
            }

            for (CalendarDayModel calendarDay : calendarDays) {
                calendarDay.setSelected(false);
            }
            day.setSelected(true);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return calendarDays.size();
    }

    public void updateData(List<CalendarDayModel> newCalendarDays) {
        this.calendarDays = newCalendarDays;
        notifyDataSetChanged();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
        }
    }
}