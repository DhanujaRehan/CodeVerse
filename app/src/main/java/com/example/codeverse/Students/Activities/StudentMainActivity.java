package com.example.codeverse.Students.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codeverse.Admin.Fragments.AdminSendNotification;
import com.example.codeverse.Admin.Fragments.StaffPersonalInfo;
import com.example.codeverse.Lecturer.Fragments.GradeSubmission;
import com.example.codeverse.Students.StudentFragments.AssignmentUpload;
import com.example.codeverse.Students.StudentFragments.RecieptUpload;
import com.example.codeverse.Students.StudentFragments.StudentClass;
import com.example.codeverse.Admin.Fragments.CreateStudent;
import com.example.codeverse.R;
import com.example.codeverse.databinding.ActivityAdminMainBinding;
import com.example.codeverse.databinding.ActivityStudentMainBinding;

public class StudentMainActivity extends AppCompatActivity {

    ActivityStudentMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStudentMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, new RecieptUpload())
                    .commit();
        }

        binding.studentBottomNavBar.setOnItemSelectedListener(item -> {
            int itemid = item.getItemId();
            if (itemid == R.id.navhome) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new AssignmentUpload())
                        .commit();
            }
            else if (itemid == R.id.navassignments){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new AssignmentUpload()).commit();
            }
            else if (itemid == R.id.navschedule){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new GradeSubmission()).commit();
            }
            else if (itemid == R.id.navprofile){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new AssignmentUpload()).commit();
            }
            return true;
        });

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.student_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}