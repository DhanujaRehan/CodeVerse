package com.example.codeverse.Admin.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codeverse.Admin.Fragments.AdminSendNotification;
import com.example.codeverse.Staff.StaffFragments.StaffSchedule;
import com.example.codeverse.Students.StudentFragments.CreateStudent;
import com.example.codeverse.R;
import com.example.codeverse.databinding.ActivityMainBinding;

public class AdminMainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new StaffSchedule() {
            }).commit();
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemid = item.getItemId();
            if (itemid == R.id.navhome) {
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new StaffSchedule() {
                }).commit();
            }
            else if (itemid == R.id.navassignments){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new AdminSendNotification()).commit();
            }
            else if (itemid == R.id.navschedule){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new CreateStudent()).commit();
            }
            else if (itemid == R.id.navprofile){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new AdminSendNotification()).commit();
            }
            return true;
        });

        EdgeToEdge.enable(this);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}