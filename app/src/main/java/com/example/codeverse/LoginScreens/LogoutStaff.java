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

        // Get user type from intent (to distinguish between staff and lecturer)
        String userType = getIntent().getStringExtra("userType");

        // Set goodbye message based on user type
        setGoodbyeMessage(userType);

        // Perform logout operations
        performLogout();

        // Navigate back to login after delay
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
            // Fallback message if name is not available
            if ("lecturer".equalsIgnoreCase(userType)) {
                tvGoodbyeStaff.setText("Goodbye, Lecturer!");
            } else {
                tvGoodbyeStaff.setText("Goodbye, Staff!");
            }
        }
    }

    private void performLogout() {
        // Clear StaffSessionManager
        staffSessionManager.logoutStaff();

        // Clear LecturerPrefs (for lecturer-specific data)
        SharedPreferences lecturerPrefs = getSharedPreferences("LecturerPrefs", MODE_PRIVATE);
        SharedPreferences.Editor lecturerEditor = lecturerPrefs.edit();
        lecturerEditor.clear();
        lecturerEditor.apply();

        // Clear StaffPrefs (for staff-specific data)
        SharedPreferences staffPrefs = getSharedPreferences("StaffPrefs", MODE_PRIVATE);
        SharedPreferences.Editor staffEditor = staffPrefs.edit();
        staffEditor.clear();
        staffEditor.apply();
    }

    /**
     * Static method to start logout process for Staff
     */
    public static void startStaffLogout(AppCompatActivity activity) {
        Intent intent = new Intent(activity, LogoutStaff.class);
        intent.putExtra("userType", "staff");
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * Static method to start logout process for Lecturer
     */
    public static void startLecturerLogout(AppCompatActivity activity) {
        Intent intent = new Intent(activity, LogoutStaff.class);
        intent.putExtra("userType", "lecturer");
        activity.startActivity(intent);
        activity.finish();
    }
}