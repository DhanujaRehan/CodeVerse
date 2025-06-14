package com.example.codeverse.Students.StudentFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.codeverse.Staff.Models.Event;
import com.example.codeverse.Staff.Helper.EventHelper;
import com.example.codeverse.Students.Models.EventRegister;
import com.example.codeverse.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

public class EventRegistration extends Fragment {

    private TextView tvEventTitle, tvEventDescription, tvEventDate, tvEventLocation;
    private ImageView ivEventBanner, ivBack;
    private TextInputEditText etStudentId, etFullName, etEmail, etPhone, etDescription;
    private AutoCompleteTextView dropdownDepartment;
    private MaterialButton btnRegister;
    private MaterialCheckBox cbTerms;
    private LinearLayout layoutRegistrationSuccess;
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

        setupDepartmentDropdown();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        tvEventTitle = view.findViewById(R.id.tv_event_title);
        tvEventDescription = view.findViewById(R.id.tv_event_description);
        tvEventDate = view.findViewById(R.id.tv_event_date);
        tvEventLocation = view.findViewById(R.id.tv_event_venue);
        ivEventBanner = view.findViewById(R.id.iv_event_image);
        ivBack = view.findViewById(R.id.iv_back);

        etStudentId = view.findViewById(R.id.et_student_id);
        etFullName = view.findViewById(R.id.et_full_name);
        etEmail = view.findViewById(R.id.et_email);
        etPhone = view.findViewById(R.id.et_phone);
        etDescription = view.findViewById(R.id.et_description);
        dropdownDepartment = view.findViewById(R.id.dropdown_department);
        btnRegister = view.findViewById(R.id.btn_register);
        cbTerms = view.findViewById(R.id.cb_terms);
        layoutRegistrationSuccess = view.findViewById(R.id.layout_registration_success);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnRegister.setOnClickListener(v -> registerForEvent());
    }

    private void setupDepartmentDropdown() {
        String[] departments = {
                "Computer Science",
                "Information Technology",
                "Software Engineering",
                "Electrical Engineering",
                "Mechanical Engineering",
                "Civil Engineering",
                "Business Administration",
                "Marketing",
                "Accounting",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, departments);
        dropdownDepartment.setAdapter(adapter);
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

    private void registerForEvent() {
        String studentId = etStudentId.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String department = dropdownDepartment.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (studentId.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || department.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(getContext(), "Please accept terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        EventRegister eventRegister = new EventRegister(eventId, studentId, fullName, email, phone, department, description);

        if (eventHelper.insertEventRegister(eventRegister)) {
            showSuccessAnimation();
            Toast.makeText(getContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSuccessAnimation() {
        layoutRegistrationSuccess.setVisibility(View.VISIBLE);

        etStudentId.setText("");
        etFullName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        dropdownDepartment.setText("");
        etDescription.setText("");
        cbTerms.setChecked(false);
    }
}