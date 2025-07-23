package com.example.doctors;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ClinicRequestActivity extends AppCompatActivity {

    private LinearLayout requestContainer;
    private FirebaseFirestore firestore;
    private String doctorUid, clinicUid;
    private String doctorName;
    private TextView noReqFindTxt;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_requests);

        requestContainer = findViewById(R.id.requestContainer);
        noReqFindTxt = findViewById(R.id.txt_noreq_found);
        scrollView = findViewById(R.id.scrollView2);
        firestore = FirebaseFirestore.getInstance();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        doctorUid = prefs.getString("uid", null);
        doctorName = prefs.getString("name", "");

        if (doctorUid == null) {
            Toast.makeText(this, "Doctor UID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        loadClinicRequests();
    }

    private void loadClinicRequests() {
        firestore.collection("doctor_requests")
                .document(doctorUid)
                .collection("requests")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    requestContainer.removeAllViews();
                    boolean hasRequests = false;

                    for (QueryDocumentSnapshot doc : querySnapshot) {
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


                            String requestId = doc.getId();
                            String message = doc.getString("message");
                            String clinicName = doc.getString("clinicName");
                            String clinicAddress = doc.getString("clinicAddress");
                            String appointmentDate = doc.getString("appointmentDate");
                            String appointmentTime = doc.getString("appointmentTime");
                            String formattedDateTime = doc.getString("formattedDateTime");
                            clinicUid= doc.getString("clinicUid");

                            msgText.setText("Message: " + message);
                            clinicNameText.setText("Clinic: " + clinicName);
                            clinicAddressText.setText("Address: " + clinicAddress);
                            timestampText.setText("Requested Time: " + formattedDateTime);

                            acceptBtn.setOnClickListener(v ->
                                    updateRequestStatus(requestId, "accepted", clinicName, clinicAddress, appointmentDate, appointmentTime, formattedDateTime)
                            );

                            declineBtn.setOnClickListener(v ->
                                    updateRequestStatus(requestId, "declined", null, null, null, null, null)
                            );

                            requestContainer.addView(requestView);
                        }
                    }

                    scrollView.setVisibility(hasRequests ? View.VISIBLE : View.GONE);
                    noReqFindTxt.setVisibility(hasRequests ? View.GONE : View.VISIBLE);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load requests", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateRequestStatus(String requestId, String status,
                                     String clinicName, String clinicAddress,
                                     String appointmentDate, String appointmentTime,
                                     String formattedDateTime) {

        firestore.collection("doctor_requests")
                .document(doctorUid)
                .collection("requests")
                .document(requestId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Request " + status, Toast.LENGTH_SHORT).show();

                    if ("accepted".equals(status)) {
                        Map<String, Object> scheduleEntry = new HashMap<>();
                        scheduleEntry.put("clinicName", clinicName);
                        scheduleEntry.put("clinicAddress", clinicAddress);
                        scheduleEntry.put("appointmentDate", appointmentDate);
                        scheduleEntry.put("appointmentTime", appointmentTime);
                        scheduleEntry.put("formattedDateTime", formattedDateTime);
                        scheduleEntry.put("doctorUid", doctorUid);
                        scheduleEntry.put("doctorName", doctorName);
                        scheduleEntry.put("clinicUid", clinicUid);
                        scheduleEntry.put("patientCount", 0);
                        scheduleEntry.put("scheduleId",requestId);

                        firestore.collection("doctor_schedules")
                                .document(doctorUid)
                                .collection("schedules")
                                .document(requestId)
                                .set(scheduleEntry);
                    }



                    loadClinicRequests();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update request", Toast.LENGTH_SHORT).show()
                );
    }
}
