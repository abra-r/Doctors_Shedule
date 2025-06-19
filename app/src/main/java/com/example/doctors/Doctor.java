package com.example.doctors;

public class Doctor {
    private String name;
    private String specialty;
    private String degree;
    private int imageResId;
    private String uid;


    public Doctor(String name, String specialty, String degree, int imageResId) {
        this.name = name;
        this.specialty = specialty;
        this.degree = degree;
        this.imageResId = imageResId;
    }
    public Doctor(String uid, String name, String specialty, String degree, int imageResId) {
        this.uid = uid;
        this.name = name;
        this.specialty = specialty;
        this.degree = degree;
        this.imageResId = imageResId;
    }

    public Doctor(String name, String specialty) {
        this.name=name;
        this.specialty=specialty;
    }


    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getDegree() {
        return degree;
    }

    public int getImageResId() {
        return imageResId;
    }
    public String getUid() { return uid; }
}
