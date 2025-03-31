package com.example.week3_1_personal_information;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CollectedInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collected_info);

        TextView tvCollectedInfo = findViewById(R.id.tvCollectedInfo);
        Button btnBack = findViewById(R.id.btnBack);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String fullName = intent.getStringExtra("FULL_NAME");
        String studentId = intent.getStringExtra("STUDENT_ID");
        String className = intent.getStringExtra("CLASS");
        String phone = intent.getStringExtra("PHONE");
        String year = intent.getStringExtra("YEAR");
        String majors = intent.getStringExtra("MAJORS");
        String developmentPlan = intent.getStringExtra("DEVELOPMENT_PLAN");

        // Hiển thị thông tin
        String info = "Họ và tên: " + fullName + "\n\n" +
                "MSSV: " + studentId + "\n\n" +
                "Lớp: " + className + "\n\n" +
                "SĐT: " + phone + "\n\n" +
                "Năm: " + year + "\n\n" +
                "Chuyên ngành: " + majors + "\n\n" +
                "Kế hoạch phát triển bản thân:\n" + developmentPlan;

        tvCollectedInfo.setText(info);

        btnBack.setOnClickListener(v -> finish());
    }
}