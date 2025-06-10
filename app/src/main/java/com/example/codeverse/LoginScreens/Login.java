package com.example.codeverse.LoginScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codeverse.Admin.Activities.AdminMainActivity;
import com.example.codeverse.Staff.Activities.StaffMainActivity;
import com.example.codeverse.Students.Activities.StudentMainActivity;
import com.example.codeverse.Admin.Helpers.StudentDatabaseHelper;
import com.example.codeverse.Admin.Helpers.StaffDatabaseHelper;
import com.example.codeverse.Students.Models.Student;
import com.example.codeverse.Admin.Models.Staff;
import com.example.codeverse.Students.Utils.StudentSessionManager;
import com.example.codeverse.Staff.Utils.StaffSessionManager;
import com.example.codeverse.Admin.Utils.AdminSessionManager;
import com.example.codeverse.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private String AdminEmail;
    private String AdminPassword;
    private StudentDatabaseHelper studentDbHelper;
    private StaffDatabaseHelper staffDbHelper;
    private StudentSessionManager sessionManager;
    private StaffSessionManager staffSessionManager;
    private AdminSessionManager adminSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AdminEmail = "Admin1234";
        AdminPassword = "Admin1234";

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        studentDbHelper = new StudentDatabaseHelper(this);
        staffDbHelper = new StaffDatabaseHelper(this);
        sessionManager = new StudentSessionManager(this);
        staffSessionManager = new StaffSessionManager(this);
        adminSessionManager = new AdminSessionManager(this);

        if (adminSessionManager.isLoggedIn()) {
            Intent intent = new Intent(Login.this, AdminMainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(Login.this, StudentMainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (staffSessionManager.isLoggedIn()) {
            Intent intent = new Intent(Login.this, StaffMainActivity.class);
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

        authenticateAdmin(inputEmail, inputPassword);
    }

    private void authenticateAdmin(String email, String password) {
        if (email.equals(AdminEmail) && password.equals(AdminPassword)) {
            Toast.makeText(Login.this, "Admin login successful!", Toast.LENGTH_SHORT).show();

            adminSessionManager.createLoginSession("admin_001", "Administrator", AdminEmail);

            Intent intent = new Intent(Login.this, LoadingScreen.class);
            intent.putExtra("nextActivity", "AdminMainActivity");
            intent.putExtra("username", "Administrator");
            startActivity(intent);
            finish();
        } else {
            authenticateStaff(email, password);
        }
    }

    private void authenticateStaff(String email, String password) {
        Staff staff = findStaffByEmail(email);

        if (staff != null) {
            if (staff.getPassword() != null && staff.getPassword().equals(password)) {
                Toast.makeText(this, "Welcome, " + staff.getFullName() + "!", Toast.LENGTH_SHORT).show();

                staffSessionManager.createLoginSession(
                        String.valueOf(staff.getId()),
                        staff.getFullName(),
                        staff.getEmail(),
                        staff.getDepartment()
                );

                Intent intent = new Intent(Login.this, LoadingScreen.class);

                String position = staff.getPosition();
                if ("Lecturer".equals(position)) {
                    intent.putExtra("nextActivity", "LecturerMainActivity");
                } else if ("Program Coordinator".equals(position)) {
                    intent.putExtra("nextActivity", "StaffMainActivity");
                } else {
                    intent.putExtra("nextActivity", "StaffMainActivity");
                }

                intent.putExtra("username", staff.getFullName());
                startActivity(intent);
                finish();
            } else {
                authenticateStudent(email, password);
            }
        } else {
            authenticateStudent(email, password);
        }
    }

    private Staff findStaffByEmail(String email) {
        java.util.List<Staff> allStaff = staffDbHelper.getAllStaff();

        for (Staff staff : allStaff) {
            if (staff.getEmail() != null && staff.getEmail().equalsIgnoreCase(email)) {
                return staff;
            }
        }

        return null;
    }

    private void authenticateStudent(String emailOrUsername, String password) {
        Student student = findStudentByEmailOrUsername(emailOrUsername);

        if (student != null) {
            if (student.getPassword() != null && student.getPassword().equals(password)) {
                Toast.makeText(this, "Welcome, " + student.getFullName() + "!", Toast.LENGTH_SHORT).show();

                sessionManager.createLoginSession(student);

                Intent intent = new Intent(Login.this, LoadingScreen.class);
                intent.putExtra("nextActivity", "StudentMainActivity");
                intent.putExtra("username", student.getFullName());
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
        Student student = findStudentByEmail(emailOrUsername);

        if (student == null) {
            student = findStudentByUsername(emailOrUsername);
        }

        return student;
    }

    private Student findStudentByEmail(String email) {
        java.util.List<Student> allStudents = studentDbHelper.getAllStudent();

        for (Student student : allStudents) {
            if (student.getEmail() != null && student.getEmail().equalsIgnoreCase(email)) {
                return student;
            }
        }

        return null;
    }

    private Student findStudentByUsername(String username) {
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
        if (staffDbHelper != null) {
            staffDbHelper.close();
        }
    }
}