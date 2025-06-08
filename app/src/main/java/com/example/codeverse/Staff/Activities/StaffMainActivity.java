package com.example.codeverse.Staff.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codeverse.R;
import com.example.codeverse.Staff.StaffFragments.StaffProfile;
import com.example.codeverse.databinding.ActivityAdminMainBinding;
import com.example.codeverse.databinding.ActivityStaffMainBinding;

public class StaffMainActivity extends AppCompatActivity {

    ActivityStaffMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStaffMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new StaffProfile()).commit();
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemid = item.getItemId();
            if (itemid == R.id.navhome){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new StaffProfile()).commit();
            }
            else if (itemid == R.id.navassignments){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new StaffProfile()).commit();
            }
            else if (itemid == R.id.navschedule){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new StaffProfile()).commit();
            }
            else if (itemid == R.id.navprofile){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new StaffProfile()).commit();
            }
            return true;
        });

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.staff_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}