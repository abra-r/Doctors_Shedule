package com.example.doctors;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AppointmentRequestListActivity extends AppCompatActivity {

    private LinearLayout requestContainer;
    private FirebaseFirestore db;
    private String clinicUid,scheduleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_request_list);

        requestContainer = findViewById(R.id.requestContainer);
        db = FirebaseFirestore.getInstance();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        clinicUid = prefs.getString("clinicUid", null);

        if (clinicUid == null) {
            Toast.makeText(this, "Clinic UID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPendingAppointments();
    }

    private void loadPendingAppointments() {
        db.collection("appointments")
                .whereEqualTo("clinicUid", clinicUid)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    requestContainer.removeAllViews();

                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this, "No pending requests", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        View view = LayoutInflater.from(this).inflate(R.layout.item_appointment_request, null);

                        String appointmentId = doc.getId();
                        String doctorUid = doc.getString("doctorUid");
                        String patientId = doc.getString("patientId");
                        String description = doc.getString("description");


                        TextView descText = view.findViewById(R.id.appointmentDescription);
                        TextView patientText = view.findViewById(R.id.patientIdText);
                        Button acceptBtn = view.findViewById(R.id.acceptBtn);
                        Button declineBtn = view.findViewById(R.id.declineBtn);

                        descText.setText("Description: " + description);
                        patientText.setText("Patient ID: " + patientId);

                        acceptBtn.setOnClickListener(v -> updateStatus(appointmentId, "accepted"));
                        declineBtn.setOnClickListener(v -> updateStatus(appointmentId, "declined"));

                        requestContainer.addView(view);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load appointments", Toast.LENGTH_SHORT).show()
                );
    }


    private void updateStatus(String appointmentId, String status) {
        db.collection("appointments")
                .document(appointmentId)
                .get()
                .addOnSuccessListener(docSnapshot -> {
                    if (!docSnapshot.exists()) {
                        Toast.makeText(this, "Appointment not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String doctorUid = docSnapshot.getString("doctorUid");
                    String scheduleId = docSnapshot.getString("scheduleId");


                    db.collection("appointments")
                            .document(appointmentId)
                            .update("status", status)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Appointment " + status, Toast.LENGTH_SHORT).show();
                                loadPendingAppointments();


                                if ("accepted".equals(status) && doctorUid != null && scheduleId != null) {
                                    incrementSchedulePatientCount(doctorUid, scheduleId);
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show());
                });


    }
    private void incrementSchedulePatientCount(String doctorUid, String scheduleId) {
        db.collection("doctor_schedules")
                .document(doctorUid)
                .collection("schedules")
                .document(scheduleId)
                .update("patientCount", FieldValue.increment(1))
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Patient count incremented", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to increment patient count", Toast.LENGTH_SHORT).show());
    }


}
