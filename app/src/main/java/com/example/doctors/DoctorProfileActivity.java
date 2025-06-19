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
import com.google.firebase.database.*;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;

public class DoctorProfileActivity extends AppCompatActivity {

    private ImageView doctorProfileImage;
    private TextView doctorName, doctorSpecialty, doctorDegree, doctorDescription, doctorContact, doctorSchedule;
    private Button getAppointmentBtn, sendRequestBtn;

    private boolean isClinic = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_profile);

        // Bind views
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
        String doctorUid;

        if (intent != null) {
            String name = intent.getStringExtra("name");
            doctorUid = intent.getStringExtra("uid"); //

            doctorName.setText(name);
            doctorSpecialty.setText(intent.getStringExtra("specialty"));
            doctorDegree.setText(intent.getStringExtra("degree"));
            doctorDescription.setText("Dr. " + name + " is a well-experienced specialist.");
            doctorContact.setText("+1234567890");
            doctorSchedule.setText("Mon-Fri: 10AM - 2PM");
        } else {
            doctorUid = null;
        }


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String role = prefs.getString("role", null);

                        if ("clinic_manager".equals(role)) {
                            sendRequestBtn.setVisibility(View.VISIBLE);

                        }
                        else
                        {
                            sendRequestBtn.setVisibility(View.GONE);

                        }
        }

        getAppointmentBtn.setOnClickListener(v -> {
            Intent appointmentIntent = new Intent(DoctorProfileActivity.this, AppointmentActivity.class);
            appointmentIntent.putExtra("doctorName", doctorName.getText().toString());
            startActivity(appointmentIntent);
        });

        sendRequestBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent(DoctorProfileActivity.this, SendRequestActivity.class);
            sendIntent.putExtra("doctorName", doctorName.getText().toString());
            sendIntent.putExtra("doctorUid", doctorUid);

            startActivity(sendIntent);
        });
    }

}
