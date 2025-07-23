package com.example.doctors;

public class DoctorSchedule {
    private String doctorUid;
    private String chamberName;
    private String location;
    private String time;
    private int patientCount;
    private String doctorName;
    private String doctorSpecialty;
    private String doctorDegree, clinicUid;


    public DoctorSchedule() {

    }


    public DoctorSchedule(String chamberName, String location, String time, int patientCount,
                          String doctorName, String doctorSpecialty, String doctorDegree, String doctorUid) {
        this.chamberName = chamberName;
        this.location = location;
        this.time = time;
        this.patientCount = patientCount;
        this.doctorName = doctorName;
        this.doctorSpecialty = doctorSpecialty;
        this.doctorDegree = doctorDegree;
        this.doctorUid = doctorUid;
    }


    public DoctorSchedule(String chamberName, String location, String time, int patientCount,
                          String doctorName, String doctorSpecialty, String doctorDegree) {
        this(chamberName, location, time, patientCount, doctorName, doctorSpecialty, doctorDegree, null);
    }



    public String getChamberName() {
        return chamberName;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public int getPatientCount() {
        return patientCount;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDoctorSpecialty() {
        return doctorSpecialty;
    }

    public String getDoctorDegree() {
        return doctorDegree;
    }

    public String getDoctorUid() {
        return doctorUid;
    }


    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }


    public void setPatientCount(int patientCount) {
        this.patientCount = patientCount;
    }
    public String getClinicUid() {
        return clinicUid;
    }

    public void setClinicUid(String clinicUid) {
        this.clinicUid = clinicUid;
    }
    private String scheduleId;

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }



}
