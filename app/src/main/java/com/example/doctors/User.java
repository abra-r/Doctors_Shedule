package com.example.doctors;

public class User {

    private PersonalInfo personalInfo;
    private MedicalInfo medicalInfo;
    private ClinicInfo clinicInfo;
    private GeoLocation geoLocation;

    private User(Builder builder) {
        this.personalInfo = builder.personalInfo;
        this.medicalInfo = builder.medicalInfo;
        this.clinicInfo = builder.clinicInfo;
        this.geoLocation = builder.geoLocation;
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public MedicalInfo getMedicalInfo() {
        return medicalInfo;
    }

    public ClinicInfo getClinicInfo() {
        return clinicInfo;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    
    public static class Builder {
        private PersonalInfo personalInfo;
        private MedicalInfo medicalInfo;
        private ClinicInfo clinicInfo;
        private GeoLocation geoLocation;

        public Builder setPersonalInfo(PersonalInfo personalInfo) {
            this.personalInfo = personalInfo;
            return this;
        }

        public Builder setMedicalInfo(MedicalInfo medicalInfo) {
            this.medicalInfo = medicalInfo;
            return this;
        }

        public Builder setClinicInfo(ClinicInfo clinicInfo) {
            this.clinicInfo = clinicInfo;
            return this;
        }

        public Builder setGeoLocation(GeoLocation geoLocation) {
            this.geoLocation = geoLocation;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
