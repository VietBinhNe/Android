package com.example.week9_sqlite;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private DatabaseHelper dbHelper;
    private long lastClickTime = 0;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        dbHelper = new DatabaseHelper(this);

        // Lấy danh sách sinh viên từ database
        List<Student> students = dbHelper.getAllStudents();

        // Tạo adapter và gán vào ListView
        StudentAdapter adapter = new StudentAdapter(this, students);
        listView.setAdapter(adapter);

        // Xử lý sự kiện click vào item
        listView.setOnItemClickListener((parent, view, position, id) -> {
            long clickTime = SystemClock.elapsedRealtime();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                // Double-click detected
                Student student = students.get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("student_id", student.getId());
                startActivity(intent);
            }
            lastClickTime = clickTime;
        });
    }
}