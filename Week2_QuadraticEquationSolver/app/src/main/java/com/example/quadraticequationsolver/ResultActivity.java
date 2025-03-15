package com.example.quadraticequationsolver;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Khai báo các thành phần giao diện
        TextView textViewResult1 = findViewById(R.id.textViewResult1);
        TextView textViewResult2 = findViewById(R.id.textViewResult2);
        Button buttonBack = findViewById(R.id.buttonBack);

        // Lấy dữ liệu từ Bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            double a = bundle.getDouble("a");
            double b = bundle.getDouble("b");
            double c = bundle.getDouble("c");

            // Giải phương trình bậc 2
            solveQuadratic(a, b, c, textViewResult1, textViewResult2);
        }

        // Xử lý sự kiện nút Back
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại MainActivity
            }
        });
    }

    // Hàm giải phương trình bậc 2
    private void solveQuadratic(double a, double b, double c, TextView result1, TextView result2) {
        if (a == 0) {
            if (b == 0) {
                if (c == 0) {
                    result1.setText("Phương trình vô số nghiệm");
                    result2.setText("");
                } else {
                    result1.setText("Phương trình vô nghiệm");
                    result2.setText("");
                }
            } else {
                double x = -c / b;
                result1.setText("Phương trình có 1 nghiệm: x = " + x);
                result2.setText("");
            }
        } else {
            // Tính delta
            double delta = b * b - 4 * a * c;

            if (delta < 0) {
                result1.setText("Phương trình vô nghiệm");
                result2.setText("");
            } else if (delta == 0) {
                double x = -b / (2 * a);
                result1.setText("Phương trình có nghiệm kép: x = " + x);
                result2.setText("");
            } else {
                double x1 = (-b + Math.sqrt(delta)) / (2 * a);
                double x2 = (-b - Math.sqrt(delta)) / (2 * a);
                result1.setText("Nghiệm 1: x1 = " + x1);
                result2.setText("Nghiệm 2: x2 = " + x2);
            }
        }
    }
}