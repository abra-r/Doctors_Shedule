package com.example.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorScheduleListActivity extends AppCompatActivity implements DoctorScheduleAdapter.OnItemClickListener {

    private RecyclerView scheduleRecyclerView;
    private DoctorScheduleAdapter adapter;
    private List<DoctorSchedule> scheduleList;

    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_schedule_list);

        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);
        scheduleList = new ArrayList<>();
        adapter = new DoctorScheduleAdapter(scheduleList, this);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleRecyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            loadAllDoctorSchedules();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    

    @Override
    public void onItemClick(DoctorSchedule schedule) {
        Intent intent = new Intent(this, ScheduleDetailActivity.class);
        intent.putExtra("clinicName", schedule.getChamberName());
        intent.putExtra("clinicAddress", schedule.getLocation());
        intent.putExtra("time", schedule.getTime());
        startActivity(intent);
    }
    private void loadAllDoctorSchedules() {
        firestore.collectionGroup("schedules")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    scheduleList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String clinicName = doc.getString("clinicName");
                        String location = doc.getString("clinicAddress");
                        String time = doc.getString("formattedDateTime");
                        int patientCount = 0;

                        DoctorSchedule schedule = new DoctorSchedule(clinicName, location, time, patientCount);
                        scheduleList.add(schedule);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load schedules", Toast.LENGTH_SHORT).show());

    }

}
