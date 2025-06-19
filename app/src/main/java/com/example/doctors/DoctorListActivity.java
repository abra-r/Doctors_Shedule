package com.example.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class DoctorListActivity extends AppCompatActivity {

    private RecyclerView doctorRecyclerView;
    private DoctorAdapter doctorAdapter;
    private List<Doctor> doctorList = new ArrayList<>();


    private FirebaseFirestore db; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_list);

        doctorRecyclerView = findViewById(R.id.doctorRecyclerView);
        doctorRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        doctorList = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(doctorList, doctor -> {
            Intent intent = new Intent(DoctorListActivity.this, DoctorProfileActivity.class);
            intent.putExtra("name", doctor.getName());
            intent.putExtra("specialty", doctor.getSpecialty());
            intent.putExtra("degree", doctor.getDegree());
            intent.putExtra("uid", doctor.getUid());
            startActivity(intent);
        });


        doctorRecyclerView.setAdapter(doctorAdapter);

        loadDoctors();

        NavigationHelper.setupNavigation(this);
    }

    private void loadDoctors() {
        db.collection("users")
                .whereEqualTo("role", "doctor")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    doctorList.clear();

                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : querySnapshot) {
                        Doctor doctor = new Doctor(
                                document.getId(), // 🔥 this is the UID
                                document.getString("name"),
                                document.getString("specialization"),
                                document.getString("degree"),
                                R.drawable.doctor_image // fallback image
                        );
                        doctorList.add(doctor);
                    }


                    doctorAdapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load doctors.", Toast.LENGTH_SHORT).show();
                    Log.e("DoctorListActivity", "Error retrieving doctors.", e);
                });
    }
}
