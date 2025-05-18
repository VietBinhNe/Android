package com.example.week9_sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudentDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_STUDENTS = "students";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_MSSV = "mssv";
    private static final String KEY_AVATAR = "avatar";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_MSSV + " TEXT,"
                + KEY_AVATAR + " TEXT" + ")";
        db.execSQL(CREATE_STUDENTS_TABLE);

        // Thêm dữ liệu mẫu
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, "Nguyen Van A");
        values.put(KEY_MSSV, "123456");
        values.put(KEY_AVATAR, "avatar1.png");
        db.insert(TABLE_STUDENTS, null, values);

        values.clear();
        values.put(KEY_NAME, "Tran Thi B");
        values.put(KEY_MSSV, "789012");
        values.put(KEY_AVATAR, "avatar2.png");
        db.insert(TABLE_STUDENTS, null, values);
    }

    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_STUDENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Student student = new Student(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_MSSV)),
                        cursor.getString(cursor.getColumnIndex(KEY_AVATAR))
                );
                studentList.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return studentList;
    }

    public Student getStudentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS, new String[]{KEY_ID, KEY_NAME, KEY_MSSV, KEY_AVATAR},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Student student = new Student(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_MSSV)),
                    cursor.getString(cursor.getColumnIndex(KEY_AVATAR))
            );
            cursor.close();
            return student;
        }
        return null;
    }
}