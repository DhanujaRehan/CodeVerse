package com.example.codeverse.Students.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codeverse.Lecturer.Fragments.GradeSubmission;
import com.example.codeverse.Staff.StaffFragments.StaffTimeTabePDF;
import com.example.codeverse.R;
import com.example.codeverse.StudentHomeFragment;
import com.example.codeverse.Students.StudentFragments.PaymentScreenFragment;
import com.example.codeverse.Students.StudentFragments.StudentClass;
import com.example.codeverse.Students.StudentFragments.StudentProfile;
import com.example.codeverse.Students.StudentFragments.TimetableDownloadFragment;
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
                    .replace(R.id.framelayout, new StudentHomeFragment())
                    .commit();
        }

        binding.studentBottomNavBar.setOnItemSelectedListener(item -> {
            int itemid = item.getItemId();
            if (itemid == R.id.navhome) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new StudentHomeFragment())
                        .commit();
            }
            else if (itemid == R.id.navassignments){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new StudentProfile()).commit();
            }
            else if (itemid == R.id.navschedule){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new StudentClass()).commit();
            }
            else if (itemid == R.id.navprofile){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new PaymentScreenFragment()).commit();
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