package com.example.apartmentmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.apartmentmanager.R;
import com.example.apartmentmanager.models.User;
import com.example.apartmentmanager.network.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseService firebaseService;
    private EditText nameEditText, emailEditText, passwordEditText, phoneEditText, idNumberEditText;
    private Button registerButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        firebaseService = new FirebaseService();
        nameEditText = findViewById(R.id.name_input);
        emailEditText = findViewById(R.id.email_input);
        passwordEditText = findViewById(R.id.password_input);
        phoneEditText = findViewById(R.id.phone_input);
        idNumberEditText = findViewById(R.id.id_number_input);
        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.loginButton);

        registerButton.setOnClickListener(v -> registerUser());
        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String idNumber = idNumberEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || idNumber.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        User user = new User(userId, email, name, null, phone, idNumber, 0.0, null, "Chưa thuê");
                        firebaseService.addUser(user, success -> {
                            if (success) {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Lưu thông tin người dùng thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}