package com.example.doctors;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;



import android.content.SharedPreferences;

import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    private ImageView listOfDoctorCard, healthBlogCard;
    private ImageView doctorNearMeCard;
    private ImageView doctorScheduleCard;

    private LinearLayout requestContainer;

    private String currentDoctorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        listOfDoctorCard = findViewById(R.id.listOfDoctorCard);
        healthBlogCard = findViewById(R.id.healthBlogCard);
        doctorNearMeCard = findViewById(R.id.doctorNearMeCard);  // new line
        requestContainer = findViewById(R.id.requestContainer);
        doctorScheduleCard = findViewById(R.id.doctorScheduleCard);


        listOfDoctorCard.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, DoctorListActivity.class);
            startActivity(intent);
        });

        healthBlogCard.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, HealthBlogActivity.class);
            startActivity(intent);
        });

        doctorNearMeCard.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, MapActivity.class);
            startActivity(intent);
        });

        doctorScheduleCard.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, DoctorScheduleListActivity.class);
            startActivity(intent);
        });

        NavigationHelper.setupNavigation(this);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = prefs.getString("role", null);

        if ("doctor".equals(role)) {
            currentDoctorName = prefs.getString("name", "");
            loadClinicRequests();
        }
    }

    private void loadClinicRequests() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String doctorUid = prefs.getString("uid", null);  // Make sure you save doctor UID in SharedPreferences during login

        if (doctorUid == null) {
            Toast.makeText(this, "Doctor UID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("doctor_requests")
                .document(doctorUid)
                .collection("requests")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    requestContainer.removeAllViews(); // Clear previous requests

                    boolean hasRequests = false;

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String message = doc.getString("message");
                        String status = doc.getString("status");

                        if ("pending".equals(status)) {
                            hasRequests = true;

                            View requestView = getLayoutInflater().inflate(R.layout.item_request, null);

                            TextView msgText = requestView.findViewById(R.id.requestMessage);
                            TextView clinicNameText = requestView.findViewById(R.id.clinicNameText);
                            TextView clinicAddressText = requestView.findViewById(R.id.clinicAddressText);
                            TextView timestampText = requestView.findViewById(R.id.timestampText);

                            Button acceptBtn = requestView.findViewById(R.id.acceptBtn);
                            Button declineBtn = requestView.findViewById(R.id.declineBtn);


                            String clinicName = doc.getString("clinicName");
                            String clinicAddress = doc.getString("clinicAddress");
                            String formattedTime = doc.getString("formattedDateTime");

                            msgText.setText("Message: " + message);
                            clinicNameText.setText("Clinic: " + (clinicName != null ? clinicName : "Unknown"));
                            clinicAddressText.setText("Address: " + (clinicAddress != null ? clinicAddress : "Unknown"));
                            timestampText.setText("Requested Time: " + (formattedTime != null ? formattedTime : "N/A"));

                            acceptBtn.setOnClickListener(v -> updateRequestStatus(firestore, doctorUid, doc.getId(), "accepted"));
                            declineBtn.setOnClickListener(v -> updateRequestStatus(firestore, doctorUid, doc.getId(), "declined"));

                            requestContainer.addView(requestView);
                        }

                    }

                    if (!hasRequests) {
                        Toast.makeText(HomeActivity.this, "No new requests", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(HomeActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show());
    }


    private void updateRequestStatus(FirebaseFirestore firestore, String doctorUid, String requestId, String status) {
        firestore.collection("doctor_requests")
                .document(doctorUid)
                .collection("requests")
                .document(requestId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Request " + status, Toast.LENGTH_SHORT).show();


                    if ("accepted".equals(status)) {
                        firestore.collection("doctor_requests")
                                .document(doctorUid)
                                .collection("requests")
                                .document(requestId)
                                .get()
                                .addOnSuccessListener(doc -> {
                                    if (doc.exists()) {
                                        Map<String, Object> scheduleEntry = new HashMap<>();
                                        scheduleEntry.put("clinicName", doc.getString("clinicName"));
                                        scheduleEntry.put("clinicAddress", doc.getString("clinicAddress"));
                                        scheduleEntry.put("appointmentDate", doc.getString("appointmentDate"));
                                        scheduleEntry.put("appointmentTime", doc.getString("appointmentTime"));
                                        scheduleEntry.put("formattedDateTime", doc.getString("formattedDateTime"));
                                        scheduleEntry.put("doctorUid", doctorUid);

                                        firestore.collection("doctor_schedules")
                                                .document(doctorUid)
                                                .collection("schedules")
                                                .add(scheduleEntry);
                                    }
                                });
                    }

                    loadClinicRequests();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update request", Toast.LENGTH_SHORT).show());
    }

}
