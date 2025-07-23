package com.example.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AppointmentActivity extends AppCompatActivity {

    private TextView doctorNameTextView;
    private EditText appointmentDescriptionEditText;
    private Button bookAppointmentBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String currentUserId;
    private String doctorId;
    private String clinicName;
    private String doctorName,clinicUid;
    private String scheduleId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        doctorNameTextView = findViewById(R.id.doctorName);
        appointmentDescriptionEditText = findViewById(R.id.appointmentDescription);
        bookAppointmentBtn = findViewById(R.id.bookAppointmentBtn);



        Intent intent = getIntent();
        if (intent != null) {
            doctorId = intent.getStringExtra("doctorUid");
            clinicName= intent.getStringExtra("clinicName");
            doctorName = intent.getStringExtra("doctorName");
            clinicUid = intent.getStringExtra("clinicUid");
            scheduleId = intent.getStringExtra("scheduleId");



            if (doctorName != null) {
                doctorNameTextView.setText(doctorName);
            }
        }

        if (doctorId == null || clinicName == null || currentUserId == null) {
            Toast.makeText(this, "Missing required information.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookAppointmentBtn.setOnClickListener(v -> bookAppointment());
    }

    private void bookAppointment() {
        String description = appointmentDescriptionEditText.getText().toString().trim();

        if (description.isEmpty()) {
            Toast.makeText(this, "Enter a brief description.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 1: Fetch current user info
        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        String username = userDoc.getString("username");
                        String email = userDoc.getString("email");


                        Map<String, Object> appointment = new HashMap<>();
                        appointment.put("doctorUid", doctorId);
                        appointment.put("clinicName", clinicName);
                        appointment.put("patientId", currentUserId);
                        appointment.put("description", description);
                        appointment.put("status", "pending");
                        appointment.put("clinicUid", clinicUid);
                        appointment.put("scheduleId", scheduleId);


                        appointment.put("patientName", username != null ? username : "Unknown");
                        appointment.put("patientEmail", email != null ? email : "");

                        db.collection("appointments")
                                .add(appointment)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "Appointment request sent successfully!", Toast.LENGTH_SHORT).show();
                                    // Redirect back to schedule detail to refresh status
                                    Intent intent = new Intent(this, ScheduleDetailActivity.class);
                                    intent.putExtra("clinicName", clinicName);
                                    intent.putExtra("doctorName", doctorName);
                                    intent.putExtra("clinicUid", clinicUid);
                                    intent.putExtra("scheduleId", scheduleId);
                                    intent.putExtra("clinicAddress", getIntent().getStringExtra("clinicAddress"));
                                    intent.putExtra("time", getIntent().getStringExtra("time"));
                                    intent.putExtra("doctorUid", doctorId);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to book appointment.", Toast.LENGTH_SHORT).show()
                                );
                    } else {
                        Toast.makeText(this, "User info not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load user info.", Toast.LENGTH_SHORT).show()
                );
    }




}
