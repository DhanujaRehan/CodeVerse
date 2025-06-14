package com.example.codeverse.Admin.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.codeverse.LoginScreens.AdminLogout;
import com.example.codeverse.LoginScreens.Login;
import com.example.codeverse.LoginScreens.LogoutStaff;
import com.example.codeverse.R;

import com.google.android.material.button.MaterialButton;

public class AdminHomeFragment extends Fragment {

    private LinearLayout layoutCreateStudent;
    private LinearLayout layoutCreateLecturer;
    private LinearLayout layoutSendNotification;
    private LinearLayout layoutAllUsers;
    private MaterialButton btnSystemStatus;
    private ImageView ivLogout;
    private SharedPreferences sharedPreferences;
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
        layoutAllUsers = view.findViewById(R.id.layout_all_users);
        btnSystemStatus = view.findViewById(R.id.btn_system_status);
        tvViewDetails = view.findViewById(R.id.tv_view_details);
        ivLogout = view.findViewById(R.id.iv_logout);
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

        layoutAllUsers.setOnClickListener(v -> {
            UserShowingFragment userShowingFragment = new UserShowingFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, userShowingFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        btnSystemStatus.setOnClickListener(v -> {
            showSystemStatus();
        });

        ivLogout.setOnClickListener(v -> {
            logoutAdmin();
        });

        tvViewDetails.setOnClickListener(v -> {
            showSystemDetails();
        });
    }

    private void logoutAdmin() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

        Intent intent = new Intent(getActivity(), AdminLogout.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void showSystemStatus() {
        Toast.makeText(getContext(), "System is running normally", Toast.LENGTH_SHORT).show();
    }

    private void showSystemDetails() {
        Toast.makeText(getContext(), "Viewing system details", Toast.LENGTH_SHORT).show();
    }
}