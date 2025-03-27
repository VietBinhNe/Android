package com.example.passage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity2 extends AppCompatActivity {

    private EditText passage2EditText;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        passage2EditText = findViewById(R.id.passage2);
        button2 = findViewById(R.id.button2);

        // Get the passage from MainActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("passage1")) {
            String passage1 = intent.getStringExtra("passage1");
            passage2EditText.setText(passage1);
        }

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passage2 = passage2EditText.getText().toString();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("passage3", passage2);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}