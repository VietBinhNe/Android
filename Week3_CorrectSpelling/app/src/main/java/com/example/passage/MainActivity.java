package com.example.passage;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText passage1EditText;
    private TextView passage3TextView;
    private Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passage1EditText = findViewById(R.id.passage1);
        passage3TextView = findViewById(R.id.passage3);
        button1 = findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passage1 = passage1EditText.getText().toString().trim();

                // Kiểm tra nội dung trống
                if (passage1.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập bài viết trước khi nộp!", Toast.LENGTH_SHORT).show();
                    passage1EditText.setError("Bắt buộc nhập nội dung");
                    return;
                }

                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("passage1", passage1);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String passage3 = data.getStringExtra("passage3");
                passage3TextView.setText(passage3);
            }
        }
    }
}