package com.example.codeverse.Students.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Models.Event;
import com.example.codeverse.Students.StudentFragments.EventRegistration;
import com.google.android.material.button.MaterialButton;

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

        if (!event.getImage().isEmpty()) {
            try {
                byte[] decodedBytes = android.util.Base64.decode(event.getImage(), android.util.Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.ivEventImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("EventAdapter", "Error decoding image: " + e.getMessage());
                holder.ivEventImage.setImageResource(R.drawable.ccc);
            }
        } else {
            holder.ivEventImage.setImageResource(R.drawable.ccc);
        }

        holder.currentEvent = event;
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventTitle, tvEventDescription, tvEventDate, tvEventTime, tvEventVenue;
        ImageView ivEventImage;
        MaterialButton button_register;
        Event currentEvent;

        public EventViewHolder(View itemView) {
            super(itemView);
            button_register = itemView.findViewById(R.id.button_register);
            tvEventTitle = itemView.findViewById(R.id.tv_event_title);
            tvEventDescription = itemView.findViewById(R.id.tv_event_description);
            tvEventDate = itemView.findViewById(R.id.tv_event_date);
            tvEventTime = itemView.findViewById(R.id.tv_event_time);
            tvEventVenue = itemView.findViewById(R.id.tv_event_venue);
            ivEventImage = itemView.findViewById(R.id.iv_event_image);

            button_register.setOnClickListener(v -> {
                if (context instanceof FragmentActivity && currentEvent != null) {
                    EventRegistration eventRegisterFragment = new EventRegistration();

                    Bundle bundle = new Bundle();
                    bundle.putInt("event_id", currentEvent.getId());
                    eventRegisterFragment.setArguments(bundle);

                    FragmentTransaction transaction = ((FragmentActivity) context)
                            .getSupportFragmentManager()
                            .beginTransaction();
                    transaction.replace(R.id.framelayout, eventRegisterFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }
    }
}