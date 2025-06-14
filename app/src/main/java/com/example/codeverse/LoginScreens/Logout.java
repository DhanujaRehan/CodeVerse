package com.example.codeverse.LoginScreens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codeverse.R;
import com.example.codeverse.Students.Utils.StudentSessionManager;

public class Logout extends AppCompatActivity {

    private StudentSessionManager sessionManager;
    private TextView tvGoodbyeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        sessionManager = new StudentSessionManager(this);
        tvGoodbyeUser = findViewById(R.id.tv_goodbye_user);

        String userName = sessionManager.getStudentName();
        if (userName != null && !userName.isEmpty()) {
            tvGoodbyeUser.setText("Goodbye, " + userName + "!");
        }

        sessionManager.logoutUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Logout.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}

