package com.example.week3_1_personal_information;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

public class StudentInfoActivity extends AppCompatActivity {

    private EditText etFullName, etStudentId, etClass, etPhone, etDevelopmentPlan;
    private RadioGroup rgYear;
    private CheckBox cbElectronics, cbComputerSystems, cbTelecom;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        // Ánh xạ view
        etFullName = findViewById(R.id.etFullName);
        etStudentId = findViewById(R.id.etStudentId);
        etClass = findViewById(R.id.etClass);
        etPhone = findViewById(R.id.etPhone);
        etDevelopmentPlan = findViewById(R.id.etDevelopmentPlan);
        rgYear = findViewById(R.id.rgYear);
        cbElectronics = findViewById(R.id.cbElectronics);
        cbComputerSystems = findViewById(R.id.cbComputerSystems);
        cbTelecom = findViewById(R.id.cbTelecom);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            // Lấy thông tin từ các view
            String fullName = etFullName.getText().toString();
            String studentId = etStudentId.getText().toString();
            String className = etClass.getText().toString();
            String phone = etPhone.getText().toString();
            String developmentPlan = etDevelopmentPlan.getText().toString();

            // Lấy năm học
            int selectedYearId = rgYear.getCheckedRadioButtonId();
            RadioButton selectedYear = findViewById(selectedYearId);
            String year = selectedYear != null ? selectedYear.getText().toString() : "";

            // Lấy chuyên ngành
            StringBuilder majors = new StringBuilder();
            if (cbElectronics.isChecked()) majors.append("Điện tử, ");
            if (cbComputerSystems.isChecked()) majors.append("Máy tính - Hệ thống Nhúng, ");
            if (cbTelecom.isChecked()) majors.append("Viễn thông - Mạng, ");
            if (majors.length() > 0) majors.setLength(majors.length() - 2);

            // Tạo Intent để chuyển sang Activity 2
            Intent intent = new Intent(StudentInfoActivity.this, CollectedInfoActivity.class);
            intent.putExtra("FULL_NAME", fullName);
            intent.putExtra("STUDENT_ID", studentId);
            intent.putExtra("CLASS", className);
            intent.putExtra("PHONE", phone);
            intent.putExtra("YEAR", year);
            intent.putExtra("MAJORS", majors.toString());
            intent.putExtra("DEVELOPMENT_PLAN", developmentPlan);

            startActivity(intent);
        });
    }
}