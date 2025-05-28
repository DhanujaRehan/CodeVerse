package com.example.codeverse.LoginScreens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.MainActivity;
import com.example.codeverse.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Random;

public class LoadingScreen extends AppCompatActivity {

    private LottieAnimationView loadingAnimation;
    private TextView tvWelcomeUser, tvLoadingMessage, tvFunFact;
    private LinearProgressIndicator progressIndicator;
    private ChipGroup chipGroupStatus;
    private Chip chipCourses, chipSchedule, chipAssignments;


    private final String[] quotes = {
            "The best way to predict your future is to create it. — Abraham Lincoln",
            "Success is not final, failure is not fatal: It is the courage to continue that counts. — Winston Churchill",

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading_screen);


        String username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            username = "Student";
        }


        initViews();


        tvWelcomeUser.setText("Welcome, " + username + "!");
        tvFunFact.setText(getRandomQuote());


        setupLoadingSequence();
        simulateLoading();
    }

    private void initViews() {
        loadingAnimation = findViewById(R.id.loading_animation);
        tvWelcomeUser = findViewById(R.id.tv_welcome_user);
        tvLoadingMessage = findViewById(R.id.tv_loading_message);
        tvFunFact = findViewById(R.id.tv_fun_fact);
        progressIndicator = findViewById(R.id.progress_indicator);
        chipGroupStatus = findViewById(R.id.chip_group_status);
        chipCourses = findViewById(R.id.chip_courses);
        chipSchedule = findViewById(R.id.chip_schedule);
        chipAssignments = findViewById(R.id.chip_assignments);


        chipCourses.setChecked(false);
        chipSchedule.setChecked(false);
        chipAssignments.setChecked(false);
    }

    private String getRandomQuote() {

        int randomIndex = new Random().nextInt(quotes.length);
        return quotes[randomIndex];
    }

    private void setupLoadingSequence() {
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);

        tvWelcomeUser.startAnimation(fadeIn);

        Animation fadeInDelayed = new AlphaAnimation(0.0f, 1.0f);
        fadeInDelayed.setDuration(1000);
        fadeInDelayed.setStartOffset(300);

        tvLoadingMessage.startAnimation(fadeInDelayed);

        progressIndicator.setVisibility(LinearProgressIndicator.VISIBLE);

        Animation fadeInOut = new AlphaAnimation(0.5f, 1.0f);
        fadeInOut.setDuration(1500);
        fadeInOut.setRepeatMode(Animation.REVERSE);
        fadeInOut.setRepeatCount(Animation.INFINITE);

        tvFunFact.startAnimation(fadeInOut);
    }

    private void simulateLoading() {

        new Handler().postDelayed(() -> {
            updateStatus("Loading your courses...", chipCourses);


            new Handler().postDelayed(() -> {
                updateStatus("Preparing your schedule...", chipSchedule);


                new Handler().postDelayed(() -> {
                    updateStatus("Fetching assignments...", chipAssignments);


                    new Handler().postDelayed(this::finishLoading, 1500);

                }, 1500);

            }, 1500);

        }, 1000);
    }

    private void updateStatus(String message, Chip chipToCheck) {

        tvLoadingMessage.setText(message);

        chipToCheck.setChecked(true);

        chipToCheck.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(200)
                .withEndAction(() ->
                        chipToCheck.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(200)
                                .start()
                )
                .start();
    }

    private void finishLoading() {

        tvLoadingMessage.setText("Ready to go!");


        loadingAnimation.setAnimation(R.raw.successfull);
        loadingAnimation.playAnimation();


        progressIndicator.setIndeterminate(false);
        progressIndicator.setProgress(100);


        new Handler().postDelayed(() -> {

            Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
            startActivity(intent);


            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


            finish();
        }, 1500);
    }
}