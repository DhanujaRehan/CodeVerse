package com.example.codeverse.Admin.AdminActivities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codeverse.Admin.AdminFragments.AdminSendNotifications;
import com.example.codeverse.R;
import com.example.codeverse.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends AppCompatActivity {

    ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new AdminSendNotifications()).commit();
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemid = item.getItemId();
            if (itemid == R.id.navhome) {
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new AdminSendNotifications()).commit();
            }
            return true;
        });

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}