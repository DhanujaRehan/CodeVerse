package com.example.codeverse.LoginScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codeverse.Admin.Activities.AdminMainActivity;
import com.example.codeverse.MainActivity;
import com.example.codeverse.Students.Activities.StudentMainActivity;
import com.example.codeverse.Admin.Helpers.StudentDatabaseHelper;
import com.example.codeverse.Students.Models.Student;
import com.example.codeverse.Utils.StudentSessionManager;
import com.example.codeverse.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private String AdminEmail;
    private String AdminPassword;
    private StudentDatabaseHelper studentDbHelper;
    private StudentSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AdminEmail = "Admin1234";
        AdminPassword = "Admin1234";

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize database helper and session manager
        studentDbHelper = new StudentDatabaseHelper(this);
        sessionManager = new StudentSessionManager(this);

        // Check if student is already logged in
        if (sessionManager.isLoggedIn()) {
            // Redirect to student main activity
            Intent intent = new Intent(Login.this, StudentMainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        binding.tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(Login.this, "Forgot Password feature is not implemented.", Toast.LENGTH_SHORT).show()
        );
    }

    private void handleLogin() {
        String inputEmail = binding.etEmail.getText().toString().trim();
        String inputPassword = binding.etpassword.getText().toString().trim();

        if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(Login.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if it's admin login
        if (inputEmail.equals(AdminEmail) && inputPassword.equals(AdminPassword)) {
            Toast.makeText(Login.this, "Admin login successful!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Login.this, AdminMainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Check for student login
        authenticateStudent(inputEmail, inputPassword);
    }

    private void authenticateStudent(String emailOrUsername, String password) {
        Student student = findStudentByEmailOrUsername(emailOrUsername);

        if (student != null) {
            // Check if the password matches
            if (student.getPassword() != null && student.getPassword().equals(password)) {
                // Login successful
                Toast.makeText(this, "Welcome, " + student.getFullName() + "!", Toast.LENGTH_SHORT).show();

                // Create login session using session manager
                sessionManager.createLoginSession(student);

                // Redirect to Student Main Activity
                Intent intent = new Intent(Login.this, StudentMainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid password. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No account found with this email/username.", Toast.LENGTH_SHORT).show();
        }
    }

    private Student findStudentByEmailOrUsername(String emailOrUsername) {
        // First try to find by email
        Student student = findStudentByEmail(emailOrUsername);

        // If not found by email, try by username
        if (student == null) {
            student = findStudentByUsername(emailOrUsername);
        }

        return student;
    }

    private Student findStudentByEmail(String email) {
        // Get all students and check email
        java.util.List<Student> allStudents = studentDbHelper.getAllStudent();

        for (Student student : allStudents) {
            if (student.getEmail() != null && student.getEmail().equalsIgnoreCase(email)) {
                return student;
            }
        }

        return null;
    }

    private Student findStudentByUsername(String username) {
        // Get all students and check username
        java.util.List<Student> allStudents = studentDbHelper.getAllStudent();

        for (Student student : allStudents) {
            if (student.getUsername() != null && student.getUsername().equalsIgnoreCase(username)) {
                return student;
            }
        }

        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (studentDbHelper != null) {
            studentDbHelper.close();
        }
    }
}