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

        EditText editTextA = findViewById(R.id.editTextA);
        EditText editTextB = findViewById(R.id.editTextB);
        EditText editTextC = findViewById(R.id.editTextC);
        Button buttonSolve = findViewById(R.id.buttonSolve);

        buttonSolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aStr = editTextA.getText().toString();
                String bStr = editTextB.getText().toString();
                String cStr = editTextC.getText().toString();

                if (aStr.isEmpty() || bStr.isEmpty() || cStr.isEmpty()) {
                    editTextA.setError("Vui lòng nhập đầy đủ!");
                    return;
                }

                double a = Double.parseDouble(aStr);
                double b = Double.parseDouble(bStr);
                double c = Double.parseDouble(cStr);

                Intent intent = new Intent(MainActivity.this, ResultActivity.class);

                Bundle bundle = new Bundle();
                bundle.putDouble("a", a);
                bundle.putDouble("b", b);
                bundle.putDouble("c", c);

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }
}