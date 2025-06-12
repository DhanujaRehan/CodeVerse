package com.example.codeverse.Lecturer.Activities;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codeverse.Lecturer.Fragments.LecturerProfile;
import com.example.codeverse.Lecturer.Fragments.LecturerHomeFragment;
import com.example.codeverse.Lecturer.Fragments.LecturerNotesFragment;
import com.example.codeverse.R;
import com.example.codeverse.Staff.StaffFragments.GradeSubmissions;
import com.example.codeverse.databinding.ActivityLecturerMainBinding;

public class LecturerMainActivity extends AppCompatActivity {

    ActivityLecturerMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding = ActivityLecturerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, new LecturerHomeFragment())
                    .commit();
        }

        binding.bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            int itemid = menuItem.getItemId();
            if (itemid == R.id.navhome){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new LecturerHomeFragment())
                        .commit();
            }
            else if (itemid == R.id.navgradesubmissions){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new GradeSubmissions())
                        .commit();
            }
            else if (itemid == R.id.navschedule){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new LecturerNotesFragment())
                        .commit();
            }
            else if (itemid == R.id.navprofile){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new LecturerProfile())
                        .commit();
            }
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.lecMain, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}