package com.example.codeverse;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.codeverse.Admin.Fragments.AdminSendNotification;
import com.example.codeverse.Admin.Fragments.CreateStudent;
import com.example.codeverse.Admin.Fragments.StaffPersonalInfo;
import com.example.codeverse.R;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class AdminHomeFragment extends Fragment {

    private LinearLayout layoutCreateStudent;
    private LinearLayout layoutCreateLecturer;
    private LinearLayout layoutSendNotification;
    private LinearLayout layoutAdminProfile;
    private MaterialButton btnSystemStatus;
    private TextView tvViewDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        layoutCreateStudent = view.findViewById(R.id.layout_create_student);
        layoutCreateLecturer = view.findViewById(R.id.layout_create_lecturer);
        layoutSendNotification = view.findViewById(R.id.layout_send_notification);
        layoutAdminProfile = view.findViewById(R.id.layout_admin_profile);
        btnSystemStatus = view.findViewById(R.id.btn_system_status);
        tvViewDetails = view.findViewById(R.id.tv_view_details);
    }

    private void setupClickListeners() {
        layoutCreateStudent.setOnClickListener(v -> {

            CreateStudent createStudent = new CreateStudent();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, createStudent);
            transaction.addToBackStack(null);
            transaction.commit();

        });

        layoutCreateLecturer.setOnClickListener(v -> {
            StaffPersonalInfo staffPersonalInfo = new StaffPersonalInfo();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, staffPersonalInfo);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        layoutSendNotification.setOnClickListener(v -> {
            AdminSendNotification adminSendNotification = new AdminSendNotification();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, adminSendNotification);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        layoutAdminProfile.setOnClickListener(v -> {

        });


        btnSystemStatus.setOnClickListener(v -> {
            showSystemStatus();
        });

        tvViewDetails.setOnClickListener(v -> {
            showSystemDetails();
        });
    }

    private void showSystemStatus() {
        Toast.makeText(getContext(), "System is running normally", Toast.LENGTH_SHORT).show();
    }

    private void showSystemDetails() {
        Toast.makeText(getContext(), "Viewing system details", Toast.LENGTH_SHORT).show();
    }
}