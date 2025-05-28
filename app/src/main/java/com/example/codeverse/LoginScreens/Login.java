package com.example.codeverse.LoginScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codeverse.Login;
import com.example.codeverse.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private String AdminEmail;
    private String AdminPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AdminEmail = "Admin1234";
        AdminPassword = "Admin1234";

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Login button listener
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputEmail = binding.etEmail.getText().toString().trim();
                String inputPassword = binding.etpassword.getText().toString().trim();

                // Check credentials
                if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                } else if (inputEmail.equals(AdminEmail) && inputPassword.equals(AdminPassword)) {
                    Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    // Proceed to the next activity (e.g., Dashboard)
                    Intent intent = new Intent(Login.this, MainActivity.class); // replace with your actual class
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Invalid credentials. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(Login.this, "Forgot Password feature is not implemented.", Toast.LENGTH_SHORT).show()
        );
    }
}
