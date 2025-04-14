package com.example.week6_loginui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckBox;
    private TextView forgetPasswordTextView, signUpTextView;
    private Button loginButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        forgetPasswordTextView = findViewById(R.id.forgetPasswordTextView);
        signUpTextView = findViewById(R.id.signUpTextView);
        loginButton = findViewById(R.id.loginButton);

        // Load saved credentials if "Remember Me" was checked
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            emailEditText.setText(sharedPreferences.getString("email", ""));
            passwordEditText.setText(sharedPreferences.getString("password", ""));
            rememberMeCheckBox.setChecked(true);
        }

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check credentials against SharedPreferences (simulating a database)
                String savedEmail = sharedPreferences.getString("user_" + email + "_email", null);
                String savedPassword = sharedPreferences.getString("user_" + email + "_password", null);

                if (savedEmail != null && savedPassword != null && savedEmail.equals(email) && savedPassword.equals(password)) {
                    // Save "Remember Me" preferences
                    if (rememberMeCheckBox.isChecked()) {
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.putBoolean("rememberMe", true);
                    } else {
                        editor.clear();
                    }
                    editor.apply();

                    // Navigate to success screen
                    Intent intent = new Intent(MainActivity.this, SuccessActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Tài khoản hoặc mật khẩu bị sai", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Forget Password click listener
        forgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your email first", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the user exists
                String savedEmail = sharedPreferences.getString("user_" + email + "_email", null);
                if (savedEmail == null) {
                    Toast.makeText(MainActivity.this, "Account does not exist", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Navigate to forget password screen
                Intent intent = new Intent(MainActivity.this, ForgetPasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        // Sign Up click listener
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}