package com.example.codeverse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Models.Event;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.tvEventTitle.setText(event.getTitle());
        holder.tvEventDescription.setText(event.getDescription());
        holder.tvEventDate.setText(event.getDate());
        holder.tvEventTime.setText(event.getTime());
        holder.tvEventVenue.setText(event.getVenue());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventTitle, tvEventDescription, tvEventDate, tvEventTime, tvEventVenue;
        ImageView ivEventImage;

        public EventViewHolder(View itemView) {
            super(itemView);
            tvEventTitle = itemView.findViewById(R.id.tv_event_title);
            tvEventDescription = itemView.findViewById(R.id.tv_event_description);
            tvEventDate = itemView.findViewById(R.id.tv_event_date);
            tvEventTime = itemView.findViewById(R.id.tv_event_time);
            tvEventVenue = itemView.findViewById(R.id.tv_event_venue);
            ivEventImage = itemView.findViewById(R.id.iv_event_image);
        }
    }
}