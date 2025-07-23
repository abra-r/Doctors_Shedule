package com.example.doctors;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

public class DoctorProfileActivity extends AppCompatActivity {

    private ImageView doctorProfileImage;
    private TextView doctorName, doctorSpecialty, doctorDegree, doctorDescription, doctorContact, doctorSchedule;
    private Button getAppointmentBtn, sendRequestBtn;

    private String doctorUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_profile);

        doctorProfileImage = findViewById(R.id.doctorImage);
        doctorName = findViewById(R.id.doctorName);
        doctorSpecialty = findViewById(R.id.doctorSpecialty);
        doctorDegree = findViewById(R.id.doctorDegree);
        doctorDescription = findViewById(R.id.doctorDescription);
        doctorContact = findViewById(R.id.doctorContact);
        doctorSchedule = findViewById(R.id.doctorSchedule);
        getAppointmentBtn = findViewById(R.id.getAppointmentBtn);
        sendRequestBtn = findViewById(R.id.sendRequestBtn);

        sendRequestBtn.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (intent != null) {
            doctorUid = intent.getStringExtra("doctorUid");

            if (doctorUid != null && !doctorUid.isEmpty()) {
                loadDoctorProfile(doctorUid);
            } else {
                doctorName.setText("Doctor UID not found");
            }
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String role = prefs.getString("role", null);

            if ("clinic_manager".equals(role)) {
                sendRequestBtn.setVisibility(View.VISIBLE);
            }
        }

        getAppointmentBtn.setOnClickListener(v -> {
            Intent appointmentIntent = new Intent(DoctorProfileActivity.this, DoctorScheduleListActivity.class);
            appointmentIntent.putExtra("doctorUid", doctorUid);
            startActivity(appointmentIntent);
        });

        sendRequestBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent(DoctorProfileActivity.this, SendRequestActivity.class);
            sendIntent.putExtra("doctorUid", doctorUid);
            sendIntent.putExtra("username", doctorName.getText().toString());
            startActivity(sendIntent);
        });

        NavigationHelper.setupNavigation(this);
    }

    private void loadDoctorProfile(String uid) {
        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("username");
                        String specialty = doc.getString("specialization");
                        String degree = doc.getString("degree");
                        String contact = doc.getString("phone");
                        String about = doc.getString("about");

                        doctorName.setText(name != null ? name : "N/A");
                        doctorSpecialty.setText(specialty != null ? specialty : "N/A");
                        doctorDegree.setText(degree != null ? degree : "N/A");
                        doctorContact.setText(contact != null ? contact : "Not available");
                        doctorDescription.setText(about != null ? about : "No description provided");


                        doctorSchedule.setText("Mon-Fri: 10AM - 2PM");
                    } else {
                        doctorName.setText("Doctor not found");
                    }
                })
                .addOnFailureListener(e -> doctorName.setText("Error loading profile"));
    }

}
