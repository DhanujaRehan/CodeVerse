package com.example.codeverse.Staff.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codeverse.R;
import com.example.codeverse.Staff.StaffFragments.CreateEvent;
import com.example.codeverse.Staff.StaffFragments.StaffAddAssignment;
import com.example.codeverse.Staff.StaffFragments.StaffHome;
import com.example.codeverse.Staff.StaffFragments.StaffProfile;
import com.example.codeverse.Staff.StaffFragments.StaffSchedule;
import com.example.codeverse.databinding.ActivityStaffMainBinding;

public class StaffMainActivity extends AppCompatActivity {

    ActivityStaffMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityStaffMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, new StaffHome())
                    .commit();
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemid = item.getItemId();
            if (itemid == R.id.navhome){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new StaffHome())
                        .commit();
            }
            else if (itemid == R.id.navassignments){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new StaffAddAssignment())
                        .commit();
            }
            else if (itemid == R.id.navschedule){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new StaffSchedule())
                        .commit();
            }
            else if (itemid == R.id.navprofile){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new StaffProfile())
                        .commit();
            }
            return true;
        });
        EdgeToEdge.enable(this);

    }
}