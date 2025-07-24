package com.example.doctors;

public class PersonalInfo {
    private String email;
    private String username;
    private String role;

    public PersonalInfo(String email, String username, String role) {
        this.email = email;
        this.username = username;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
