package com.example.doctors;

public class ClinicInfo {
    private String clinicName;
    private String clinicAddress;
    private String licenseNumber;

    public ClinicInfo(String clinicName, String clinicAddress, String licenseNumber) {
        this.clinicName = clinicName;
        this.clinicAddress = clinicAddress;
        this.licenseNumber = licenseNumber;
    }

    public String getClinicName() {
        return clinicName;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }
}
