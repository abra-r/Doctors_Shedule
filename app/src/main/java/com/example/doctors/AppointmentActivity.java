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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        doctorNameTextView = findViewById(R.id.doctorName);
        appointmentDescriptionEditText = findViewById(R.id.appointmentDescription);
        bookAppointmentBtn = findViewById(R.id.bookAppointmentBtn);

        Intent intent = getIntent();
        if (intent != null) {
            doctorId = intent.getStringExtra("doctorId");


        }

        bookAppointmentBtn.setOnClickListener(v -> bookAppointment());
    }

    private void bookAppointment(){
        String description = appointmentDescriptionEditText.getText().toString().trim();

        if (description.isEmpty()) {
            Toast.makeText(this, "Enter a brief description.", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> appointment = new HashMap<>();
        appointment.put("doctorId", doctorId);
        appointment.put("patientId", currentUserId);
        appointment.put("description", description);
        appointment.put("status", "pending");

        db.collection("appointments")
                .add(appointment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Appointment successfully booked.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to book.", Toast.LENGTH_SHORT).show());
    }
}
