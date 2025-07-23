package com.example.doctors;

public class PatientInfo {
    private String username;
    private String email;
    private String description;

    public PatientInfo() {}

    public PatientInfo(String username, String email, String description) {
        this.username = username;
        this.email = email;
        this.description = description;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getDescription() { return description; }
}
