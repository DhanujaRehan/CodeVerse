package com.example.codeverse.LoginScreens;

import android.content.Intent;
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

        String staffName = staffSessionManager.getStaffName();
        if (staffName != null && !staffName.isEmpty()) {
            tvGoodbyeStaff.setText("Goodbye, " + staffName + "!");
        }

        staffSessionManager.logoutStaff();

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
}