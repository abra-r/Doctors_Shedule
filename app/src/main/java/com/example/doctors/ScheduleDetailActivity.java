package com.example.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScheduleDetailActivity extends AppCompatActivity {

    private TextView clinicNameText, clinicAddressText, timeText;
    private TextView doctorNameText, doctorSpecialtyText, doctorDegreeText;
    private Button getAppointmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        clinicNameText = findViewById(R.id.clinicNameText);
        clinicAddressText = findViewById(R.id.clinicAddressText);
        timeText = findViewById(R.id.timeText);
        doctorNameText = findViewById(R.id.doctorNameText);
        doctorSpecialtyText = findViewById(R.id.doctorSpecialtyText);
        doctorDegreeText = findViewById(R.id.doctorDegreeText);
        getAppointmentButton = findViewById(R.id.getAppointmentButton);


        Intent intent = getIntent();
        String clinicName = intent.getStringExtra("clinicName");
        String clinicAddress = intent.getStringExtra("clinicAddress");
        String time = intent.getStringExtra("time");
        String doctorName = intent.getStringExtra("doctorName");
        String doctorSpecialty = intent.getStringExtra("doctorSpecialty");
        String doctorDegree = intent.getStringExtra("doctorDegree");

        clinicNameText.setText("Clinic: " + clinicName);
        clinicAddressText.setText("Address: " + clinicAddress);
        timeText.setText("Time: " + time);
        doctorNameText.setText("Doctor: " + doctorName);
        doctorSpecialtyText.setText("Specialty: " + doctorSpecialty);
        doctorDegreeText.setText("Degree: " + doctorDegree);

        getAppointmentButton.setOnClickListener(v -> {
            Intent apptIntent = new Intent(this, AppointmentActivity.class);
            apptIntent.putExtra("clinicName", clinicName);
            apptIntent.putExtra("doctorName", doctorName);
            apptIntent.putExtra("doctorSpecialty", doctorSpecialty);
            apptIntent.putExtra("doctorDegree", doctorDegree);
            apptIntent.putExtra("clinicAddress", clinicAddress);
            apptIntent.putExtra("time", time);
            startActivity(apptIntent);
        });
    }
}
