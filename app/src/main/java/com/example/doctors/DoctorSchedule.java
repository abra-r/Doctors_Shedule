package com.example.doctors;

public class DoctorSchedule {
    private String chamberName;
    private String location;
    private String time;
    private int patientCount;

    public DoctorSchedule() {

    }

    public DoctorSchedule(String chamberName, String location, String time, int patientCount) {
        this.chamberName = chamberName;
        this.location = location;
        this.time = time;
        this.patientCount = patientCount;
    }

    public String getChamberName() { return chamberName; }
    public String getLocation() { return location; }
    public String getTime() { return time; }
    public int getPatientCount() { return patientCount; }
}
