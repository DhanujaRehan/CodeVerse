import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class AccountDetails extends AppCompatActivity {

    // UI Components
    private MaterialCardView cvBack, cvHelp;
    private TextInputLayout tilEmail, tilUsername, tilPassword, tilConfirmPassword;
    private TextInputEditText etEmail, etUsername, etPassword, etConfirmPassword;
    private CheckBox checkboxTerms;
    private TextView tvTerms;
    private MaterialButton btnNextStep, btnCancel;
    private View loadingOverlay;

    // Password requirement TextViews
    private TextView tvPasswordRequirementLength, tvPasswordRequirementUppercase,
            tvPasswordRequirementNumber, tvPasswordRequirementSpecial, tvPasswordRequirementMatch;

    // Data
    private Student studentData;
    private StudentDatabaseHelper databaseHelper;

    // Password validation patterns
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        initializeViews();
        initializeDatabase();
        getStudentData();
        setupPasswordValidation();
        setupClickListeners();
        updateProgressIndicators();
    }

    private void initializeViews() {
        cvBack = findViewById(R.id.cv_back);
        cvHelp = findViewById(R.id.cv_help);
        tilEmail = findViewById(R.id.til_email);
        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        checkboxTerms = findViewById(R.id.checkbox_terms);
        tvTerms = findViewById(R.id.tv_terms);
        btnNextStep = findViewById(R.id.btn_next_step);
        btnCancel = findViewById(R.id.btn_cancel);
        loadingOverlay = findViewById(R.id.loading_overlay);

        // Password requirement TextViews
        tvPasswordRequirementLength = findViewById(R.id.tv_password_requirement_length);
        tvPasswordRequirementUppercase = findViewById(R.id.tv_password_requirement_uppercase);
        tvPasswordRequirementNumber = findViewById(R.id.tv_password_requirement_number);
        tvPasswordRequirementSpecial = findViewById(R.id.tv_password_requirement_special);
        tvPasswordRequirementMatch = findViewById(R.id.tv_password_requirement_match);
    }

    private void initializeDatabase() {
        databaseHelper = new StudentDatabaseHelper(this);
    }

    private void getStudentData() {
        studentData = (Student) getIntent().getSerializableExtra("student_data");
        if (studentData == null) {
            Toast.makeText(this, "Error: No student data found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupPasswordValidation() {
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswordRequirements(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswordMatch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Real-time username validation
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateUsernameRealTime(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Real-time email validation
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmailRealTime(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validatePasswordRequirements(String password) {
        int greenColor = ContextCompat.getColor(this, android.R.color.holo_green_dark);
        int redColor = ContextCompat.getColor(this, android.R.color.holo_red_dark);
        int grayColor = ContextCompat.getColor(this, android.R.color.darker_gray);

        // Length requirement
        if (password.length() >= 8) {
            tvPasswordRequirementLength.setTextColor(greenColor);
            tvPasswordRequirementLength.setText("✓ At least 8 characters");
        } else {
            tvPasswordRequirementLength.setTextColor(password.isEmpty() ? grayColor : redColor);
            tvPasswordRequirementLength.setText("• At least 8 characters");
        }

        // Uppercase requirement
        if (password.matches(".*[A-Z].*")) {
            tvPasswordRequirementUppercase.setTextColor(greenColor);
            tvPasswordRequirementUppercase.setText("✓ At least one uppercase letter");
        } else {
            tvPasswordRequirementUppercase.setTextColor(password.isEmpty() ? grayColor : redColor);
            tvPasswordRequirementUppercase.setText("• At least one uppercase letter");
        }

        // Number requirement
        if (password.matches(".*[0-9].*")) {
            tvPasswordRequirementNumber.setTextColor(greenColor);
            tvPasswordRequirementNumber.setText("✓ At least one number");
        } else {
            tvPasswordRequirementNumber.setTextColor(password.isEmpty() ? grayColor : redColor);
            tvPasswordRequirementNumber.setText("• At least one number");
        }

        // Special character requirement
        if (password.matches(".*[@#$%^&+=!].*")) {
            tvPasswordRequirementSpecial.setTextColor(greenColor);
            tvPasswordRequirementSpecial.setText("✓ At least one special character");
        } else {
            tvPasswordRequirementSpecial.setTextColor(password.isEmpty() ? grayColor : redColor);
            tvPasswordRequirementSpecial.setText("• At least one special character");
        }

        validatePasswordMatch();
    }

    private void validatePasswordMatch() {
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        int greenColor = ContextCompat.getColor(this, android.R.color.holo_green_dark);
        int redColor = ContextCompat.getColor(this, android.R.color.holo_red_dark);
        int grayColor = ContextCompat.getColor(this, android.R.color.darker_gray);

        if (!confirmPassword.isEmpty() && password.equals(confirmPassword) && !password.isEmpty()) {
            tvPasswordRequirementMatch.setTextColor(greenColor);
            tvPasswordRequirementMatch.setText("✓ Passwords match");
        } else if (!confirmPassword.isEmpty()) {
            tvPasswordRequirementMatch.setTextColor(redColor);
            tvPasswordRequirementMatch.setText("• Passwords match");
        } else {
            tvPasswordRequirementMatch.setTextColor(grayColor);
            tvPasswordRequirementMatch.setText("• Passwords match");
        }
    }

    private void validateUsernameRealTime(String username) {
        if (username.length() >= 3) {
            // Check if username exists in database
            new Thread(() -> {
                boolean exists = databaseHelper.isUsernameExists(username);
                runOnUiThread(() -> {
                    if (exists) {
                        tilUsername.setError("Username already exists");
                    } else if (!username.matches("^[a-zA-Z0-9._]+$")) {
                        tilUsername.setError("Username can only contain letters, numbers, dots and underscores");
                    } else {
                        tilUsername.setError(null);
                    }
                });
            }).start();
        }
    }

    private void validateEmailRealTime(String email) {
        if (!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Check if email exists in database
            new Thread(() -> {
                boolean exists = databaseHelper.isEmailExists(email);
                runOnUiThread(() -> {
                    if (exists) {
                        tilEmail.setError("Email already exists");
                    } else {
                        tilEmail.setError(null);
                    }
                });
            }).start();
        }
    }

    private void setupClickListeners() {
        cvBack.setOnClickListener(v -> onBackPressed());

        cvHelp.setOnClickListener(v -> showHelpDialog());

        tvTerms.setOnClickListener(v -> {
            showTermsAndConditions();
        });

        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                proceedToContactDetails();
            }
        });

        btnCancel.setOnClickListener(v -> {
            showCancelConfirmation();
        });
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Account Setup Help")
                .setMessage("Create a secure account:\n\n" +
                        "• Use a valid email address\n" +
                        "• Choose a unique username\n" +
                        "• Create a strong password following all requirements\n" +
                        "• Accept the terms to continue")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showTermsAndConditions() {
        new AlertDialog.Builder(this)
                .setTitle("Terms of Service & Privacy Policy")
                .setMessage("TERMS OF SERVICE\n\n" +
                        "1. Account Usage: You are responsible for maintaining the security of your account.\n\n" +
                        "2. Data Accuracy: You must provide accurate and complete information.\n\n" +
                        "3. Privacy: We respect your privacy and protect your personal information.\n\n" +
                        "PRIVACY POLICY\n\n" +
                        "1. Data Collection: We collect information necessary for academic management.\n\n" +
                        "2. Data Usage: Your information will be used solely for educational purposes.\n\n" +
                        "3. Data Security: We implement appropriate security measures to protect your data.")
                .setPositiveButton("Accept", (dialog, which) -> {
                    checkboxTerms.setChecked(true);
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void showCancelConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Registration")
                .setMessage("Are you sure you want to cancel the registration process? All entered data will be lost.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("Continue Registration", null)
                .show();
    }

    private void updateProgressIndicators() {
        // Update progress indicators to show current step
        // Steps 1 & 2 - Completed (Basic Info & Academic Details)
        // Step 3 (Account) - Current
        // Step 4 - Pending (Contact Details)
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Reset errors
        tilEmail.setError(null);
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Validate email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        } else if (databaseHelper.isEmailExists(email)) {
            tilEmail.setError("Email already exists");
            isValid = false;
        }

        // Validate username
        String username = etUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username is required");
            isValid = false;
        } else if (username.length() < 3) {
            tilUsername.setError("Username must be at least 3 characters");
            isValid = false;
        } else if (!username.matches("^[a-zA-Z0-9._]+$")) {
            tilUsername.setError("Username can only contain letters, numbers, dots and underscores");
            isValid = false;
        } else if (databaseHelper.isUsernameExists(username)) {
            tilUsername.setError("Username already exists");
            isValid = false;
        }

        // Validate password
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            tilPassword.setError("Password does not meet requirements");
            isValid = false;
        }

        // Validate confirm password
        String confirmPassword = etConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        // Validate terms acceptance
        if (!checkboxTerms.isChecked()) {
            Toast.makeText(this, "Please accept the Terms of Service and Privacy Policy", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Fallback to plain text (not recommended for production)
            return password;
        }
    }

    private void proceedToContactDetails() {
        showLoading(true);

        // Update student object with account details
        studentData.setEmail(etEmail.getText().toString().trim());
        studentData.setUsername(etUsername.getText().toString().trim());
        studentData.setPassword(hashPassword(etPassword.getText().toString()));

        // Pass data to Contact Details activity
        Intent intent = new Intent(this, ContactDetails.class);
        intent.putExtra("student_data", studentData);
        startActivity(intent);

        showLoading(false);
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        btnNextStep.setEnabled(!show);
    }

    @Override
    public void onBackPressed() {
        showCancelConfirmation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}