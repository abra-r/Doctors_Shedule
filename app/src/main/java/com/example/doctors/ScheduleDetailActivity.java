package com.example.doctors;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ScheduleDetailActivity extends AppCompatActivity {

    private Button cancelRequestButton;
    private String appointmentId;

    private TextView clinicNameText, clinicAddressText, timeText;
    private TextView doctorNameText, doctorSpecialtyText, doctorDegreeText;
    private TextView statusText;
    private Button getAppointmentButton;
    private Button viewAppointmentsButton;


    private String doctorUid, scheduleId, clinicUid;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;

    private String clinicName, clinicAddress, time, doctorName;

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
        statusText = findViewById(R.id.statusTextView);
        cancelRequestButton = findViewById(R.id.cancelRequestButton);
        viewAppointmentsButton = findViewById(R.id.viewAppointmentsButton);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;


        Intent intent = getIntent();
        clinicName = intent.getStringExtra("clinicName");
        clinicAddress = intent.getStringExtra("clinicAddress");
        time = intent.getStringExtra("time");
        doctorName = intent.getStringExtra("doctorName");
        doctorUid = intent.getStringExtra("doctorUid");
        scheduleId = intent.getStringExtra("scheduleId");
        clinicUid = intent.getStringExtra("clinicUid");


        clinicNameText.setText("Clinic: " + clinicName);
        clinicAddressText.setText("Address: " + clinicAddress);
        timeText.setText("Time: " + time);
        doctorNameText.setText("Doctor: " + doctorName);

        doctorNameText.setOnClickListener(view -> {
            Intent intent2 = new Intent(this, DoctorProfileActivity.class);
            intent2.putExtra("doctorUid", doctorUid);
            intent2.putExtra("username", doctorName);
            startActivity(intent2);
        });

        loadDoctorInfo();
        viewAppointmentsButton.setOnClickListener(v -> {

            Intent intent1 = new Intent(this, AppointedPatientsActivity.class);
            intent1.putExtra("scheduleId", scheduleId);
            intent1.putExtra("doctorUid", doctorUid);

            startActivity(intent1);
        });

        getAppointmentButton.setOnClickListener(v -> {

            Intent apptIntent = new Intent(this, AppointmentActivity.class);
            apptIntent.putExtra("clinicName", clinicName);
            apptIntent.putExtra("doctorName", doctorName);
            apptIntent.putExtra("clinicAddress", clinicAddress);
            apptIntent.putExtra("time", time);
            apptIntent.putExtra("doctorUid", doctorUid);
            apptIntent.putExtra("clinicUid", clinicUid);
            apptIntent.putExtra("scheduleId", scheduleId);
            startActivity(apptIntent);
        });

        cancelRequestButton.setOnClickListener(v -> cancelPendingAppointment());
        checkIfUserIsOwner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserAppointment();
    }

    private void loadDoctorInfo() {
        if (doctorUid != null && !doctorUid.isEmpty()) {
            db.collection("users").document(doctorUid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            doctorSpecialtyText.setText("Specialization: " + doc.getString("specialization"));
                            doctorDegreeText.setText("Degree: " + doc.getString("degree"));
                        }
                    })
                    .addOnFailureListener(e -> {
                        doctorSpecialtyText.setText("Specialization: Not available");
                        doctorDegreeText.setText("Degree: Not available");
                    });
        }
    }

    private void checkUserAppointment() {
        if (currentUserId == null || scheduleId == null) return;

        db.collection("appointments")
                .whereEqualTo("scheduleId", scheduleId)
                .whereEqualTo("patientId", currentUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        appointmentId = doc.getId();
                        String status = doc.getString("status");

                        if ("pending".equals(status)) {
                            statusText.setText("Status: Pending");
                            statusText.setVisibility(View.VISIBLE);
                            getAppointmentButton.setVisibility(View.GONE);
                            cancelRequestButton.setVisibility(View.VISIBLE);
                        } else if ("accepted".equals(status)) {
                            fetchSerialNumber();
                        }
                    } else {
                        getAppointmentButton.setVisibility(View.VISIBLE);
                        statusText.setVisibility(View.GONE);
                        cancelRequestButton.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to check appointment", Toast.LENGTH_SHORT).show()
                );
    }

    private void fetchSerialNumber() {
        db.collection("doctor_schedules")
                .document(doctorUid)
                .collection("schedules")
                .document(scheduleId)
                .get()
                .addOnSuccessListener(doc -> {
                    Long count = doc.getLong("patientCount");
                    statusText.setText("Appointed | Serial No: " + (count != null ? count : "?"));
                    statusText.setVisibility(View.VISIBLE);
                    getAppointmentButton.setVisibility(View.GONE);
                    cancelRequestButton.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load serial number", Toast.LENGTH_SHORT).show());
    }

    private void cancelPendingAppointment() {
        if (appointmentId == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Cancel Appointment")
                .setMessage("Are you sure you want to cancel this appointment?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("appointments")
                            .document(appointmentId)
                            .delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Appointment request cancelled", Toast.LENGTH_SHORT).show();
                                appointmentId = null;
                                refreshUIAfterCancellation();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to cancel appointment", Toast.LENGTH_SHORT).show()
                            );
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void checkIfUserIsOwner() {
        if (currentUserId == null) return;

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String role = doc.getString("role");

                        if ((role != null && role.equals("doctor") && currentUserId.equals(doctorUid)) ||
                                (role != null && role.equals("clinic_manager") && currentUserId.equals(clinicUid))) {
                            viewAppointmentsButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }


    private void refreshUIAfterCancellation() {
        cancelRequestButton.setVisibility(View.GONE);
        getAppointmentButton.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.GONE);
    }
}
