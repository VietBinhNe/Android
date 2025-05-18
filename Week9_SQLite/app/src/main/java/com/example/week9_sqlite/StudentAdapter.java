package com.example.week9_sqlite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class StudentAdapter extends ArrayAdapter<Student> {
    private Context context;
    private List<Student> students;

    public StudentAdapter(Context context, List<Student> students) {
        super(context, R.layout.student_item, students);
        this.context = context;
        this.students = students;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);
        }

        Student student = students.get(position);

        ImageView avatar = convertView.findViewById(R.id.avatar);
        TextView name = convertView.findViewById(R.id.name);
        TextView mssv = convertView.findViewById(R.id.mssv);

        // Gán dữ liệu vào view
        name.setText(student.getName());
        mssv.setText(student.getMssv());

        // Xử lý avatar (giả sử ảnh lưu trong drawable)
        int resId = context.getResources().getIdentifier(
                student.getAvatar().replace(".png", ""), "drawable", context.getPackageName());
        if (resId != 0) {
            avatar.setImageResource(resId);
        } else {
            avatar.setImageResource(R.drawable.default_avatar); // Ảnh mặc định
        }

        return convertView;
    }
}