package com.example.codeverse.LoginScreens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codeverse.R;
import com.example.codeverse.Staff.Utils.StaffSessionManager;

public class LogoutStaff extends AppCompatActivity {

    private StaffSessionManager staffSessionManager;
    private TextView tvGoodbyeStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_staff);

        staffSessionManager = new StaffSessionManager(this);
        tvGoodbyeStaff = findViewById(R.id.tv_goodbye_staff);

        String userType = getIntent().getStringExtra("userType");

        setGoodbyeMessage(userType);

        performLogout();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LogoutStaff.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, 3500);
    }

    private void setGoodbyeMessage(String userType) {
        String staffName = staffSessionManager.getStaffName();

        if (staffName != null && !staffName.isEmpty()) {
            if ("lecturer".equalsIgnoreCase(userType)) {
                tvGoodbyeStaff.setText("Goodbye, " + staffName + "!");
            } else {
                tvGoodbyeStaff.setText("Goodbye, " + staffName + "!");
            }
        } else {
            if ("lecturer".equalsIgnoreCase(userType)) {
                tvGoodbyeStaff.setText("Goodbye, Lecturer!");
            } else {
                tvGoodbyeStaff.setText("Goodbye, Staff!");
            }
        }
    }

    private void performLogout() {
        staffSessionManager.logoutStaff();

        SharedPreferences lecturerPrefs = getSharedPreferences("LecturerPrefs", MODE_PRIVATE);
        SharedPreferences.Editor lecturerEditor = lecturerPrefs.edit();
        lecturerEditor.clear();
        lecturerEditor.apply();

        SharedPreferences staffPrefs = getSharedPreferences("StaffPrefs", MODE_PRIVATE);
        SharedPreferences.Editor staffEditor = staffPrefs.edit();
        staffEditor.clear();
        staffEditor.apply();
    }


    public static void startStaffLogout(AppCompatActivity activity) {
        Intent intent = new Intent(activity, LogoutStaff.class);
        intent.putExtra("userType", "staff");
        activity.startActivity(intent);
        activity.finish();
    }


    public static void startLecturerLogout(AppCompatActivity activity) {
        Intent intent = new Intent(activity, LogoutStaff.class);
        intent.putExtra("userType", "lecturer");
        activity.startActivity(intent);
        activity.finish();
    }
}