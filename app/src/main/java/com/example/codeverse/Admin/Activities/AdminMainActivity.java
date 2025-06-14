package com.example.codeverse.Admin.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codeverse.Admin.Fragments.StaffPersonalInfo;
import com.example.codeverse.Admin.Fragments.AdminHomeFragment;
import com.example.codeverse.Admin.Fragments.CreateStudent;
import com.example.codeverse.R;
import com.example.codeverse.Admin.Fragments.UserShowingFragment;
import com.example.codeverse.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends AppCompatActivity {

    ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, new AdminHomeFragment())
                    .commit();
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemid = item.getItemId();
            if (itemid == R.id.navhome) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout, new AdminHomeFragment())
                        .commit();
            }
            else if (itemid == R.id.navassignments){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new CreateStudent()).commit();
            }
            else if (itemid == R.id.navschedule){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new StaffPersonalInfo()).commit();
            }
            else if (itemid == R.id.navprofile){
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new UserShowingFragment()).commit();
            }
            return true;
        });

        EdgeToEdge.enable(this);
    }
}