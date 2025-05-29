package com.example.codeverse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class StudentRegistrationActivity extends AppCompatActivity
        implements CreateStudent.OnStepCompleteListener,
        AcademicDetails.OnStepCompleteListener,
        AccountDetails.OnStepCompleteListener,
        ContactDetails.OnRegistrationCompleteListener {

    private static final String TAG = "StudentRegistrationActivity";
    private static final String STUDENT_DATA_KEY = "student_data";
    private static final String CURRENT_STEP_KEY = "current_step";

    private StudentDetails currentStudent;
    private int currentStep = 1;

    // Fragment instances
    private CreateStudent createStudentFragment;
    private AcademicDetails academicDetailsFragment;
    private AccountDetails accountDetailsFragment;
    private ContactDetails contactDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        if (savedInstanceState != null) {
            currentStudent = (StudentDetails) savedInstanceState.getSerializable(STUDENT_DATA_KEY);
            currentStep = savedInstanceState.getInt(CURRENT_STEP_KEY, 1);
        } else {
            currentStudent = new StudentDetails();
            currentStep = 1;
        }

        initializeFragments();
        showStep(currentStep);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STUDENT_DATA_KEY, currentStudent);
        outState.putInt(CURRENT_STEP_KEY, currentStep);
    }

    private void initializeFragments() {
        createStudentFragment = CreateStudent.newInstance();
        createStudentFragment.setOnStepCompleteListener(this);

        academicDetailsFragment = AcademicDetails.newInstance();
        academicDetailsFragment.setOnStepCompleteListener(this);

        accountDetailsFragment = AccountDetails.newInstance();
        accountDetailsFragment.setOnStepCompleteListener(this);

        contactDetailsFragment = ContactDetails.newInstance();
        contactDetailsFragment.setOnRegistrationCompleteListener(this);
    }

    private void showStep(int step) {
        currentStep = step;
        Fragment fragment = null;
        String tag = null;

        switch (step) {
            case 1:
                fragment = createStudentFragment;
                tag = "CreateStudentFragment";
                createStudentFragment.setStudentData(currentStudent);
                break;
            case 2:
                fragment = academicDetailsFragment;
                tag = "AcademicDetailsFragment";
                academicDetailsFragment.setStudentData(currentStudent);
                break;
            case 3:
                fragment = accountDetailsFragment;
                tag = "AccountDetailsFragment";
                accountDetailsFragment.setStudentData(currentStudent);
                break;
            case 4:
                fragment = contactDetailsFragment;
                tag = "ContactDetailsFragment";
                contactDetailsFragment.setStudentData(currentStudent);
                break;
            default:
                return;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Use replace to ensure only one fragment is shown at a time
            transaction.replace(R.id.fragment_container, fragment, tag);

            // Add to back stack only if not the first step
            if (step > 1) {
                transaction.addToBackStack(null);
            }

            transaction.commit();
        }
    }

    // CreateStudentFragment.OnStepCompleteListener implementation
    @Override
    public void onStepCompleted(StudentDetails student, int nextStep) {
        currentStudent = student;
        showStep(nextStep);
        Toast.makeText(this, "Step " + (nextStep - 1) + " completed successfully", Toast.LENGTH_SHORT).show();
    }

    // Common OnStepCompleteListener methods
    @Override
    public void onCancel() {
        showCancelDialog();
    }

    @Override
    public void onBack() {
        if (currentStep > 1) {
            showStep(currentStep - 1);
        } else {
            onBackPressed();
        }
    }

    // ContactDetailsFragment.OnRegistrationCompleteListener implementation
    @Override
    public void onRegistrationComplete(StudentDetails student) {
        currentStudent = student;
        // Registration completed successfully
        // The success overlay is already shown in the fragment
        Toast.makeText(this, "Registration completed successfully! Student ID: " + student.getId(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGoToDashboard() {
        // Navigate to dashboard or main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("student_id", currentStudent.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showCancelDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cancel Registration")
                .setMessage("Are you sure you want to cancel the registration? All entered data will be lost.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    // If we have a student ID, we might want to delete the partial record
                    if (currentStudent != null && currentStudent.getId() > 0) {
                        // Optionally delete the partial record from database
                        // For now, we'll just leave it as incomplete
                    }
                    finish(); // Close the activity
                })
                .setNegativeButton("Continue Registration", null)
                .show();
    }

//    @Override
//    public void onBackPressed() {
//        if (currentStep > 1) {
//            showStep(currentStep - 1);
//        } else {
//            showCancelDialog();
//        }
//    }

    // Helper method to get current student data (useful for debugging or external access)
    public StudentDetails getCurrentStudent() {
        return currentStudent;
    }

    // Helper method to get current step
    public int getCurrentStep() {
        return currentStep;
    }

    // Method to restart registration from beginning
    public void restartRegistration() {
        currentStudent = new StudentDetails();
        currentStep = 1;

        // Clear back stack
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Show first step
        showStep(1);
    }

    // Method to validate if we can proceed to step (useful for validation)
    private boolean canProceedToStep(int step) {
        switch (step) {
            case 2: // Academic Details
                return currentStudent.getId() > 0 &&
                        currentStudent.getFullName() != null &&
                        currentStudent.getUniversityId() != null &&
                        currentStudent.getNicNumber() != null;
            case 3: // Account Details
                return currentStudent.getId() > 0 &&
                        currentStudent.getFaculty() != null &&
                        currentStudent.getDepartment() != null;
            case 4: // Contact Details
                return currentStudent.getId() > 0 &&
                        currentStudent.getEmail() != null &&
                        currentStudent.getUsername() != null;
            default:
                return true;
        }
    }
}

// Note: You'll also need to create a simple DashboardActivity
// Here's a basic implementation:

/*
package com.example.studentregistration.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studentregistration.R;
import com.example.studentregistration.database.StudentDatabaseHelper;
import com.example.studentregistration.model.Student;

public class DashboardActivity extends AppCompatActivity {

    private StudentDatabaseHelper dbHelper;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = StudentDatabaseHelper.getInstance(this);
        studentId = getIntent().getIntExtra("student_id", -1);

        if (studentId != -1) {
            loadStudentData();
        } else {
            // Handle error - no student ID provided
            finish();
        }
    }

    private void loadStudentData() {
        Student student = dbHelper.getStudentById(studentId);
        if (student != null) {
            // Display student information
            TextView welcomeText = findViewById(R.id.tv_welcome);
            welcomeText.setText("Welcome, " + student.getFullName() + "!");

            TextView studentIdText = findViewById(R.id.tv_student_id);
            studentIdText.setText("Student ID: " + student.getUniversityId());
        }
    }
}
*/