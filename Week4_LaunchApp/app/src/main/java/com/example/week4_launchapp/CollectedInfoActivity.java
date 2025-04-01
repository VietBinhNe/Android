package com.example.week4_launchapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

public class CollectedInfoActivity extends AppCompatActivity {

    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collected_info);

        TextView tvCollectedInfo = findViewById(R.id.tvCollectedInfo);
        Button btnCall = findViewById(R.id.btnCall);
        Button btnSms = findViewById(R.id.btnSms);
        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnBack = findViewById(R.id.btnBack);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String fullName = intent.getStringExtra("FULL_NAME");
        phoneNumber = intent.getStringExtra("PHONE");

        // Hiển thị thông tin
        String info = "Họ và tên: " + fullName + "\n\n" +
                "Số điện thoại: " + phoneNumber;
        tvCollectedInfo.setText(info);

        // Xử lý nút gọi điện
        btnCall.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        });

        // Xử lý nút gửi SMS
        btnSms.setOnClickListener(v -> {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setData(Uri.parse("sms:" + phoneNumber));
            startActivity(smsIntent);
        });

        // Xử lý nút chụp ảnh
        btnCamera.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(cameraIntent);
            } else {
                Toast.makeText(this, "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> finish());
    }
}