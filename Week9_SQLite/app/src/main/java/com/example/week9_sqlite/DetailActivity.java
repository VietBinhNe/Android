package com.example.week9_sqlite;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView avatar = findViewById(R.id.detail_avatar);
        TextView name = findViewById(R.id.detail_name);
        TextView mssv = findViewById(R.id.detail_mssv);

        // Lấy student_id từ Intent
        int studentId = getIntent().getIntExtra("student_id", -1);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Student student = dbHelper.getStudentById(studentId);

        if (student != null) {
            // Hiển thị thông tin
            name.setText(student.getName());
            mssv.setText(student.getMssv());

            // Xử lý avatar
            int resId = getResources().getIdentifier(
                    student.getAvatar().replace(".png", ""), "drawable", getPackageName());
            if (resId != 0) {
                avatar.setImageResource(resId);
            } else {
                avatar.setImageResource(R.drawable.default_avatar);
            }
        }
    }
}