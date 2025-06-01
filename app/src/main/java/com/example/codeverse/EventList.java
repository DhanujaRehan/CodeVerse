package com.example.codeverse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class EventList extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddEvent;
    private EventHelper eventHelper;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        initViews(view);
        eventHelper = new EventHelper(getContext());
        setupRecyclerView();
        loadEvents();

        fabAddEvent.setOnClickListener(v -> {
            CreateEvent fragment = new CreateEvent();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEvents();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_events);
        fabAddEvent = view.findViewById(R.id.fab_add_event);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadEvents() {
        eventList = eventHelper.getAllEvents();
        eventAdapter = new EventAdapter(eventList, this);
        recyclerView.setAdapter(eventAdapter);
    }

    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

        private List<Event> events;
        private Fragment parentFragment;

        public EventAdapter(List<Event> events, Fragment parentFragment) {
            this.events = events;
            this.parentFragment = parentFragment;
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            Event event = events.get(position);
            holder.bind(event);
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        class EventViewHolder extends RecyclerView.ViewHolder {

            private CardView cardView;
            private ImageView ivEventImage;
            private TextView tvEventTitle, tvEventDate, tvEventVenue;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.card_event);
                ivEventImage = itemView.findViewById(R.id.iv_event_image);
                tvEventTitle = itemView.findViewById(R.id.tv_event_title);
                tvEventDate = itemView.findViewById(R.id.tv_event_date);
                tvEventVenue = itemView.findViewById(R.id.tv_event_venue);
            }

            public void bind(Event event) {
                tvEventTitle.setText(event.getTitle());
                tvEventDate.setText(event.getDate());
                tvEventVenue.setText(event.getVenue());

                if (!event.getImage().isEmpty()) {
                    try {
                        byte[] decodedBytes = android.util.Base64.decode(event.getImage(), android.util.Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        ivEventImage.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                cardView.setOnClickListener(v -> {
                    EventRegistration fragment = new EventRegistration();
                    Bundle args = new Bundle();
                    args.putInt("event_id", event.getId());
                    fragment.setArguments(args);

                    parentFragment.getParentFragmentManager().beginTransaction()
                            .replace(R.id.framelayout, fragment)
                            .addToBackStack(null)
                            .commit();
                });
            }
        }
    }
}