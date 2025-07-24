package com.example.doctors;

public class MedicalInfo {
    private String specialization;
    private String college;
    private String degree;

    public MedicalInfo(String specialization, String college, String degree) {
        this.specialization = specialization;
        this.college = college;
        this.degree = degree;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getCollege() {
        return college;
    }

    public String getDegree() {
        return degree;
    }
}
