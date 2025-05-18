package com.example.week9_sqlite;

public class Student {
    private int id;
    private String name;
    private String mssv;
    private String avatar; // Lưu tên file hoặc đường dẫn ảnh

    public Student(int id, String name, String mssv, String avatar) {
        this.id = id;
        this.name = name;
        this.mssv = mssv;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMssv() {
        return mssv;
    }

    public String getAvatar() {
        return avatar;
    }
}