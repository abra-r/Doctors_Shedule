package com.example.doctors;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AppointedPatientsActivity extends AppCompatActivity {

    private RecyclerView patientsRecyclerView;
    private TextView emptyText;

    private AppointedPatientsAdapter adapter;
    private List<PatientInfo> patientList = new ArrayList<>();

    private String scheduleId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointed_patients);

        scheduleId = getIntent().getStringExtra("scheduleId");
        if (scheduleId == null) {
            Toast.makeText(this, "Schedule ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        patientsRecyclerView = findViewById(R.id.patientsRecyclerView);
        emptyText = findViewById(R.id.emptyText);

        adapter = new AppointedPatientsAdapter(patientList);
        patientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        patientsRecyclerView.setAdapter(adapter);

        loadAcceptedAppointments();
    }

    private void loadAcceptedAppointments() {
        db.collection("appointments")
                .whereEqualTo("scheduleId", scheduleId)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        emptyText.setVisibility(View.VISIBLE);
                        return;
                    }

                    for (DocumentSnapshot doc : querySnapshot) {
                        String patientId = doc.getString("patientId");
                        String description = doc.getString("description");

                        db.collection("users").document(patientId)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String username = userDoc.getString("username");
                                    String email = userDoc.getString("email");

                                    PatientInfo info = new PatientInfo(username, email, description);
                                    patientList.add(info);
                                    adapter.notifyDataSetChanged();
                                });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load appointments", Toast.LENGTH_SHORT).show());
    }
}
