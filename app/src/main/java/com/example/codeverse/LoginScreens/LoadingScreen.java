package com.example.codeverse.LoginScreens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.codeverse.Admin.Activities.AdminMainActivity;
import com.example.codeverse.Lecturer.Activities.LecturerMainActivity;
import com.example.codeverse.MainActivity;
import com.example.codeverse.R;
import com.example.codeverse.Staff.Activities.StaffMainActivity;
import com.example.codeverse.Students.Activities.StudentMainActivity;
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
            "Education is the most powerful weapon which you can use to change the world. — Nelson Mandela",
            "The future belongs to those who believe in the beauty of their dreams. — Eleanor Roosevelt",
            "Learning never exhausts the mind. — Leonardo da Vinci"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading_screen);

        String username = getIntent().getStringExtra("username");
        String nextActivity = getIntent().getStringExtra("nextActivity");
        String position = getIntent().getStringExtra("position");

        if (username == null || username.isEmpty()) {
            username = "User";
        }

        initViews();

        // Customize welcome message based on position
        String welcomeMessage = getWelcomeMessage(username, position);
        tvWelcomeUser.setText(welcomeMessage);
        tvFunFact.setText(getRandomQuote());

        setupLoadingSequence();
        simulateLoading(nextActivity, position);
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

    private String getWelcomeMessage(String username, String position) {
        if (position != null) {
            switch (position.trim()) {
                case "Lecturer":
                    return "Welcome, " + username + " (Lecturer)!";
                case "Program Coordinator":
                    return "Welcome, " + username + " (Program Coordinator)!";
                default:
                    return "Welcome, " + username + "!";
            }
        }
        return "Welcome, " + username + "!";
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

    private void simulateLoading(String nextActivity, String position) {
        // Customize loading messages based on user type
        String[] loadingMessages = getLoadingMessages(nextActivity, position);

        new Handler().postDelayed(() -> {
            updateStatus(loadingMessages[0], chipCourses);

            new Handler().postDelayed(() -> {
                updateStatus(loadingMessages[1], chipSchedule);

                new Handler().postDelayed(() -> {
                    updateStatus(loadingMessages[2], chipAssignments);

                    new Handler().postDelayed(() -> finishLoading(nextActivity), 1500);

                }, 1500);

            }, 1500);

        }, 1000);
    }

    private String[] getLoadingMessages(String nextActivity, String position) {
        if ("LecturerMainActivity".equals(nextActivity) || "Lecturer".equals(position)) {
            return new String[]{
                    "Loading your teaching schedule...",
                    "Preparing student assignments...",
                    "Setting up lecturer dashboard..."
            };
        } else if ("StaffMainActivity".equals(nextActivity) || "Program Coordinator".equals(position)) {
            return new String[]{
                    "Loading program data...",
                    "Preparing coordination tools...",
                    "Setting up staff dashboard..."
            };
        } else if ("AdminMainActivity".equals(nextActivity)) {
            return new String[]{
                    "Loading system data...",
                    "Preparing admin controls...",
                    "Setting up admin dashboard..."
            };
        } else if ("StudentMainActivity".equals(nextActivity)) {
            return new String[]{
                    "Loading your courses...",
                    "Preparing your schedule...",
                    "Fetching assignments..."
            };
        } else {
            return new String[]{
                    "Loading your profile...",
                    "Preparing dashboard...",
                    "Finalizing setup..."
            };
        }
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

    private void finishLoading(String nextActivity) {
        tvLoadingMessage.setText("Ready to go!");

        loadingAnimation.setAnimation(R.raw.successfull);
        loadingAnimation.playAnimation();

        progressIndicator.setIndeterminate(false);
        progressIndicator.setProgress(100);

        new Handler().postDelayed(() -> {
            try {
                Intent intent;
                switch (nextActivity) {
                    case "AdminMainActivity":
                        intent = new Intent(LoadingScreen.this, AdminMainActivity.class);
                        break;
                    case "StudentMainActivity":
                        intent = new Intent(LoadingScreen.this, StudentMainActivity.class);
                        break;
                    case "StaffMainActivity":
                        intent = new Intent(LoadingScreen.this, StaffMainActivity.class);
                        break;
                    case "LecturerMainActivity":
                        intent = new Intent(LoadingScreen.this, LecturerMainActivity.class);
                        break;
                    default:
                        intent = new Intent(LoadingScreen.this, MainActivity.class);
                        break;
                }

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();

            } catch (Exception e) {
                Intent fallback = new Intent(LoadingScreen.this, Login.class);
                Toast.makeText(this, "Error logging in: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                startActivity(fallback);
                finish();
            }
        }, 1500);
    }
}