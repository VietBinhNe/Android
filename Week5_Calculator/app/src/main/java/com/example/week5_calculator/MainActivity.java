package com.example.week5_calculator;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private double firstNumber = 0.0;
    private double secondNumber = 0.0;
    private String operator = null;
    private boolean isNewOperation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Adjust status bar color programmatically for compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0xFF2196F3); // Màu xanh dương #2196F3
        }

        // Initialize the display
        display = findViewById(R.id.display);

        // Initialize all buttons
        Button btn0 = findViewById(R.id.btn0);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        Button btn9 = findViewById(R.id.btn9);
        Button btnPlus = findViewById(R.id.btnPlus);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnMultiply = findViewById(R.id.btnMultiply);
        Button btnDivide = findViewById(R.id.btnDivide);
        Button btnMod = findViewById(R.id.btnMod);
        Button btnEqual = findViewById(R.id.btnEqual);
        Button btnDel = findViewById(R.id.btnDel);

        // Set click listeners for number buttons
        View.OnClickListener numberClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Calculator", "Number button clicked: " + ((Button) v).getText());
                Button button = (Button) v;
                String number = button.getText().toString();
                if (isNewOperation) {
                    display.setText(number);
                    isNewOperation = false;
                } else {
                    display.setText(display.getText() + number);
                }
            }
        };

        btn0.setOnClickListener(numberClickListener);
        btn1.setOnClickListener(numberClickListener);
        btn2.setOnClickListener(numberClickListener);
        btn3.setOnClickListener(numberClickListener);
        btn4.setOnClickListener(numberClickListener);
        btn5.setOnClickListener(numberClickListener);
        btn6.setOnClickListener(numberClickListener);
        btn7.setOnClickListener(numberClickListener);
        btn8.setOnClickListener(numberClickListener);
        btn9.setOnClickListener(numberClickListener);

        // Set click listeners for operator buttons
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Calculator", "Operator + clicked");
                setOperator("+");
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Calculator", "Operator - clicked");
                setOperator("-");
            }
        });

        btnMultiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Calculator", "Operator x clicked");
                setOperator("x");
            }
        });

        btnDivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Calculator", "Operator / clicked");
                setOperator("/");
            }
        });

        btnMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Calculator", "Operator % clicked");
                setOperator("%");
            }
        });

        // Equal button
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Calculator", "Equal button clicked");
                if (operator != null) {
                    try {
                        secondNumber = Double.parseDouble(display.getText().toString());
                    } catch (NumberFormatException e) {
                        secondNumber = 0.0;
                    }
                    double result = calculate();
                    display.setText(String.valueOf(result));
                    isNewOperation = true;
                    operator = null;
                }
            }
        });

        // Delete button
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Calculator", "Delete button clicked");
                display.setText("0");
                firstNumber = 0.0;
                secondNumber = 0.0;
                operator = null;
                isNewOperation = true;
            }
        });
    }

    private void setOperator(String op) {
        try {
            firstNumber = Double.parseDouble(display.getText().toString());
        } catch (NumberFormatException e) {
            firstNumber = 0.0;
        }
        operator = op;
        isNewOperation = true;
    }

    private double calculate() {
        if (operator == null) return 0.0;

        switch (operator) {
            case "+":
                return firstNumber + secondNumber;
            case "-":
                return firstNumber - secondNumber;
            case "x":
                return firstNumber * secondNumber;
            case "/":
                if (secondNumber == 0.0) {
                    Toast.makeText(this, "Không thể chia cho 0", Toast.LENGTH_SHORT).show();
                    return 0.0;
                }
                return firstNumber / secondNumber;
            case "%":
                if (secondNumber == 0.0) {
                    Toast.makeText(this, "Không thể chia cho 0", Toast.LENGTH_SHORT).show();
                    return 0.0;
                }
                return firstNumber % secondNumber;
            default:
                return 0.0;
        }
    }
}