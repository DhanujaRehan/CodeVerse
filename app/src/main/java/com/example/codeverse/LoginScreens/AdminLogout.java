package com.example.codeverse.LoginScreens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codeverse.R;
import com.example.codeverse.Admin.Utils.AdminSessionManager;

public class AdminLogout extends AppCompatActivity {

    private AdminSessionManager adminSessionManager;
    private TextView tvGoodbyeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_logout);

        adminSessionManager = new AdminSessionManager(this);
        tvGoodbyeUser = findViewById(R.id.tv_goodbye_user);

        String userName = adminSessionManager.getAdminName();
        if (userName != null && !userName.isEmpty()) {
            tvGoodbyeUser.setText("Goodbye, " + userName + "!");
        } else {
            tvGoodbyeUser.setText("Goodbye, Administrator!");
        }

        adminSessionManager.logoutUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AdminLogout.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}