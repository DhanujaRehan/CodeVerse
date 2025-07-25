package com.example.codeverse.Students.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.codeverse.Students.StudentFragments.StudentClass;
import com.example.codeverse.Students.StudentFragments.StudentNotesFragment;
import com.example.codeverse.Students.StudentFragments.StudentAssignmentDownloadFragment;
import com.example.codeverse.Students.StudentFragments.StudentHomeFragment;
import com.example.codeverse.Students.StudentFragments.PaymentScreenFragment;
import com.example.codeverse.R;
import com.example.codeverse.Students.StudentFragments.StudentProfile;
import com.example.codeverse.databinding.ActivityStudentMainBinding;

public class StudentMainActivity extends AppCompatActivity {

    private ActivityStudentMainBinding binding;

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

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemid = item.getItemId();
            if (itemid == R.id.navhome) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new StudentHomeFragment())
                        .commit();
            }
            else if (itemid == R.id.navassignments){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new StudentClass())
                        .commit();
            }
            else if (itemid == R.id.navschedule){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new PaymentScreenFragment())
                        .commit();
            }
            else if (itemid == R.id.navprofile){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new StudentProfile())
                        .commit();
            }
            return true;
        });

        EdgeToEdge.enable(this);

    }
}