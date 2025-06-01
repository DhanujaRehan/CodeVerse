package com.example.codeverse.Staff.StaffFragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import com.example.codeverse.Staff.Models.Event;
import com.example.codeverse.Staff.Helper.EventHelper;
import com.example.codeverse.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateEvent extends Fragment {

    private TextInputEditText etEventTitle, etEventDescription, etEventDate, etEventTime, etEventVenue;
    private ImageView ivEventImage, ivBack;
    private CardView cardImagePicker;
    private LinearLayout layoutImagePlaceholder;
    private MaterialButton btnCreateEvent;
    private EventHelper eventHelper;
    private String encodedImage = "";

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        initViews(view);
        eventHelper = new EventHelper(getContext());
        setupImagePicker();
        setupClickListeners();

        return view;
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                            ivEventImage.setImageBitmap(bitmap);
                            ivEventImage.setVisibility(View.VISIBLE);
                            layoutImagePlaceholder.setVisibility(View.GONE);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            byte[] imageBytes = baos.toByteArray();
                            encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initViews(View view) {
        etEventTitle = view.findViewById(R.id.et_event_title);
        etEventDescription = view.findViewById(R.id.et_event_description);
        etEventDate = view.findViewById(R.id.et_event_date);
        etEventTime = view.findViewById(R.id.et_event_time);
        etEventVenue = view.findViewById(R.id.et_event_venue);
        ivEventImage = view.findViewById(R.id.iv_event_image);
        ivBack = view.findViewById(R.id.iv_back);
        cardImagePicker = view.findViewById(R.id.card_image_picker);
        layoutImagePlaceholder = view.findViewById(R.id.layout_image_placeholder);
        btnCreateEvent = view.findViewById(R.id.btn_create_event);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        etEventDate.setOnClickListener(v -> showDatePicker());

        etEventTime.setOnClickListener(v -> showTimePicker());

        cardImagePicker.setOnClickListener(v -> openImagePicker());

        btnCreateEvent.setOnClickListener(v -> createEvent());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    etEventDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    etEventTime.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void createEvent() {
        String title = etEventTitle.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();
        String date = etEventDate.getText().toString().trim();
        String time = etEventTime.getText().toString().trim();
        String venue = etEventVenue.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty() || venue.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Event event = new Event(title, description, date, time, venue, encodedImage);

        if (eventHelper.insertEvent(event)) {
            Toast.makeText(getContext(), "Event created successfully", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } else {
            Toast.makeText(getContext(), "Failed to create event", Toast.LENGTH_SHORT).show();
        }
    }
}