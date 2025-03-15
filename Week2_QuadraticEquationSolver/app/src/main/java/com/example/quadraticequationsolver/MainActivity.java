package com.example.quadraticequationsolver;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khai báo các thành phần giao diện
        EditText editTextA = findViewById(R.id.editTextA);
        EditText editTextB = findViewById(R.id.editTextB);
        EditText editTextC = findViewById(R.id.editTextC);
        Button buttonSolve = findViewById(R.id.buttonSolve);

        // Xử lý sự kiện khi nhấn nút Solve
        buttonSolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy giá trị từ EditText
                String aStr = editTextA.getText().toString();
                String bStr = editTextB.getText().toString();
                String cStr = editTextC.getText().toString();

                // Kiểm tra nếu ô nhập trống
                if (aStr.isEmpty() || bStr.isEmpty() || cStr.isEmpty()) {
                    editTextA.setError("Vui lòng nhập đầy đủ!");
                    return;
                }

                // Chuyển sang kiểu double
                double a = Double.parseDouble(aStr);
                double b = Double.parseDouble(bStr);
                double c = Double.parseDouble(cStr);

                // Tạo Intent để chuyển sang ResultActivity
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);

                // Tạo Bundle để truyền dữ liệu
                Bundle bundle = new Bundle();
                bundle.putDouble("a", a);
                bundle.putDouble("b", b);
                bundle.putDouble("c", c);

                // Gắn Bundle vào Intent
                intent.putExtras(bundle);

                // Chuyển sang ResultActivity
                startActivity(intent);
            }
        });
    }
}