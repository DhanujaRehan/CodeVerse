package com.example.codeverse.LoginScreens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.R;
import com.google.android.material.button.MaterialButton;

public class WelcomeScreen extends AppCompatActivity {

    private LottieAnimationView appLogo;
    private TextView appName, tagline;
    private LinearLayout featurePoints;
    private MaterialButton btnGetStarted;
    private TextView tvExistingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_screen);

        initViews();
        setAnimations();
        setupClickListeners();
    }

    private void initViews() {
        appLogo = findViewById(R.id.app_logo);
        appName = findViewById(R.id.tv_app_name);
        tagline = findViewById(R.id.tv_tagline);
        featurePoints = findViewById(R.id.feature_points);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tvExistingUser = findViewById(R.id.tv_existing_user);
    }

    private void setAnimations() {


        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1200);
        fadeIn.setStartOffset(500);

        Animation fadeInSlower = new AlphaAnimation(0.0f, 1.0f);
        fadeInSlower.setDuration(1200);
        fadeInSlower.setStartOffset(800);


        appName.setAnimation(fadeIn);
        tagline.setAnimation(fadeInSlower);

        featurePoints.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.INVISIBLE);
        tvExistingUser.setVisibility(View.INVISIBLE);


        new Handler().postDelayed(() -> {
            featurePoints.setVisibility(View.VISIBLE);


            for (int i = 0; i < featurePoints.getChildCount(); i++) {
                View child = featurePoints.getChildAt(i);
                child.setAlpha(0f);

                child.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .setStartDelay(i * 150)
                        .start();
            }
        }, 1500);


        new Handler().postDelayed(() -> {
            btnGetStarted.setVisibility(View.VISIBLE);
            btnGetStarted.setScaleX(0.7f);
            btnGetStarted.setScaleY(0.7f);

            btnGetStarted.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(500)
                    .start();


            new Handler().postDelayed(() -> {
                tvExistingUser.setVisibility(View.VISIBLE);
                tvExistingUser.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .start();
            }, 300);
        }, 2500);
    }

    private void setupClickListeners() {

        btnGetStarted.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }

            return false;
        });


        btnGetStarted.setOnClickListener(v -> {


            startActivity(new Intent(WelcomeScreen.this, Login.class));
        });


        tvExistingUser.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeScreen.this, Login.class));
        });
    }
}