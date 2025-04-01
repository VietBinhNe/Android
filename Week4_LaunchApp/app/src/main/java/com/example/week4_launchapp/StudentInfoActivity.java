package com.example.week4_launchapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class StudentInfoActivity extends AppCompatActivity {

    private EditText etFullName, etPhone;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString();
            String phone = etPhone.getText().toString();

            if (fullName.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(StudentInfoActivity.this, CollectedInfoActivity.class);
            intent.putExtra("FULL_NAME", fullName);
            intent.putExtra("PHONE", phone);
            startActivity(intent);
        });
    }
}