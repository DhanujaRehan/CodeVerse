package com.example.codeverse.Staff.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeverse.Staff.Models.CalendarDayModel;
import com.example.codeverse.R;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<CalendarDayModel> days;
    private OnDateClickListener onDateClickListener;


    public interface OnDateClickListener {
        void onDateClicked(Date date);
    }

    public CalendarAdapter(List<CalendarDayModel> days, OnDateClickListener listener) {
        this.days = days;
        this.onDateClickListener = listener;
    }

    public void updateData(List<CalendarDayModel> newDays) {
        this.days = newDays;
        notifyDataSetChanged();
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


        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
        holder.tvDay.setText(dayFormat.format(day.getDate()));


        if (day.isCurrentMonth()) {
            holder.tvDay.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
        } else {
            holder.tvDay.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_disabled));
        }


        if (day.hasEvents() && day.isCurrentMonth()) {
            holder.viewIndicator.setVisibility(View.VISIBLE);
            holder.viewIndicator.setBackgroundResource(R.drawable.circle_indicator);
            holder.viewIndicator.getBackground().setTint(
                    holder.itemView.getContext().getResources().getColor(R.color.event_indicator));
        } else {
            holder.viewIndicator.setVisibility(View.GONE);
        }


        if (day.isSelected()) {
            holder.cardView.setCardBackgroundColor(
                    holder.itemView.getContext().getResources().getColor(R.color.blue_primary));
            holder.tvDay.setTextColor(
                    holder.itemView.getContext().getResources().getColor(R.color.white));
            if (day.hasEvents()) {
                holder.viewIndicator.getBackground().setTint(
                        holder.itemView.getContext().getResources().getColor(R.color.white));
            }
        } else {
            holder.cardView.setCardBackgroundColor(
                    holder.itemView.getContext().getResources().getColor(R.color.light_background));
        }


        Calendar today = Calendar.getInstance();
        Calendar dayCalendar = Calendar.getInstance();
        dayCalendar.setTime(day.getDate());

        if (today.get(Calendar.YEAR) == dayCalendar.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == dayCalendar.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == dayCalendar.get(Calendar.DAY_OF_MONTH)) {

            holder.cardView.setStrokeWidth(2);
            holder.cardView.setStrokeColor(
                    holder.itemView.getContext().getResources().getColor(R.color.blue_primary));
        } else {
            holder.cardView.setStrokeWidth(0);
        }


        holder.itemView.setOnClickListener(v -> {
            if (day.isCurrentMonth()) {

                for (CalendarDayModel d : days) {
                    d.setSelected(false);
                }
                day.setSelected(true);
                notifyDataSetChanged();


                onDateClickListener.onDateClicked(day.getDate());
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        View viewIndicator;
        MaterialCardView cardView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            viewIndicator = itemView.findViewById(R.id.view_indicator);
            cardView = itemView.findViewById(R.id.cv_day);
        }
    }
}