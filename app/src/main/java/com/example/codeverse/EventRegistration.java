package com.example.codeverse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class EventRegistration extends Fragment {

    private TextView tvEventTitle, tvEventDescription, tvEventDate, tvEventLocation;
    private ImageView ivEventBanner, ivBack;
    private EventHelper eventHelper;
    private int eventId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_registration, container, false);

        initViews(view);
        eventHelper = new EventHelper(getContext());

        if (getArguments() != null) {
            eventId = getArguments().getInt("event_id", -1);
        }

        if (eventId != -1) {
            loadEventData();
        }

        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        tvEventTitle = view.findViewById(R.id.tv_event_title);
        tvEventDescription = view.findViewById(R.id.tv_event_description);
        tvEventDate = view.findViewById(R.id.tv_event_date);
        tvEventLocation = view.findViewById(R.id.tv_event_location);
        ivEventBanner = view.findViewById(R.id.iv_event_banner);
        ivBack = view.findViewById(R.id.iv_back);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void loadEventData() {
        Event event = eventHelper.getEventById(eventId);

        if (event != null) {
            tvEventTitle.setText(event.getTitle());
            tvEventDescription.setText(event.getDescription());
            tvEventDate.setText(event.getDate());
            tvEventLocation.setText(event.getVenue());

            if (!event.getImage().isEmpty()) {
                try {
                    byte[] decodedBytes = android.util.Base64.decode(event.getImage(), android.util.Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    ivEventBanner.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}