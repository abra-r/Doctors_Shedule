package com.example.doctors;

public class User {

    private String email;
    private String role;
    private String specialization;
    private String collage;
    private String degree;
    private String clinicName;
    private String licenseNumber;
    private String clinicAddress;
    private String username;

    private double latitude;
    private double longitude;

    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User() {
    }


    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }


    public User(String email, String role, String specialization, String collage, String degree,
                String clinicName, String licenseNumber, String clinicAddress) {
        this.email = email;
        this.role = role;
        this.specialization = specialization;
        this.collage = collage;
        this.degree = degree;
        this.clinicName = clinicName;
        this.licenseNumber = licenseNumber;
        this.clinicAddress = clinicAddress;
    }


    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getCollage() {
        return collage;
    }

    public String getDegree() {
        return degree;
    }

    public String getClinicName() {
        return clinicName;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setCollage(String collage) {
        this.collage = collage;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }
}
