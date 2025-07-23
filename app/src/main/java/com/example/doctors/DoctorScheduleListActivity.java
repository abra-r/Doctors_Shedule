package com.example.doctors;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DoctorScheduleListActivity extends AppCompatActivity implements DoctorScheduleAdapter.OnItemClickListener {

    private RecyclerView scheduleRecyclerView;
    private DoctorScheduleAdapter adapter;
    private List<DoctorSchedule> scheduleList;

    private EditText doctorNameFilter, dateFilter, specializationFilter;
    private Button clearFilterButton;
    private LinearLayout filterView;
    private ProgressBar loadingSpinner;

    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    private String specificDoctorUid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_schedule_list);


        doctorNameFilter = findViewById(R.id.doctorNameFilter);
        dateFilter = findViewById(R.id.dateFilter);
        specializationFilter = findViewById(R.id.specializationFilter);
        clearFilterButton = findViewById(R.id.clearFilterButton);
        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);
        filterView = findViewById(R.id.filterGroup);
        loadingSpinner = findViewById(R.id.loadingSpinner);

        specificDoctorUid = getIntent().getStringExtra("doctorUid");

        if (specificDoctorUid != null) {
            filterView.setVisibility(View.GONE);
        }

        scheduleList = new ArrayList<>();
        adapter = new DoctorScheduleAdapter(scheduleList, this);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleRecyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            loadSchedules(null, null, null);
        } else {
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

        // Clear filters
        clearFilterButton.setOnClickListener(v -> {
            doctorNameFilter.setText("");
            dateFilter.setText("");
            specializationFilter.setText("");
            loadSchedules(null, null, null);
        });

        // Date picker
        dateFilter.setKeyListener(null); // prevent manual typing
        dateFilter.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateFilter.setText(date);
                        applyFilters();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        // Auto filter while typing
        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                applyFilters();
            }
        };
        doctorNameFilter.addTextChangedListener(watcher);
        specializationFilter.addTextChangedListener(watcher);
    }

    private void applyFilters() {
        String name = doctorNameFilter.getText().toString().trim().toLowerCase();
        String date = dateFilter.getText().toString().trim();
        String specialty = specializationFilter.getText().toString().trim().toLowerCase();
        loadSchedules(name, date, specialty);
    }

    private void loadSchedules(String doctorName, String date, String specialization) {
        loadingSpinner.setVisibility(View.VISIBLE);

        firestore.collectionGroup("schedules")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    scheduleList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String docUid = doc.getString("doctorUid");
                        String docName = doc.getString("doctorName");
                        String docSpecialty = doc.getString("specialization");
                        String formattedDateTime = doc.getString("formattedDateTime");

                        if (specificDoctorUid != null && !specificDoctorUid.equals(docUid)) continue;

                        if (doctorName != null && (docName == null || !docName.toLowerCase().contains(doctorName)))
                            continue;
                        if (date != null && (formattedDateTime == null || !formattedDateTime.contains(date)))
                            continue;
                        if (specialization != null && (docSpecialty == null || !docSpecialty.toLowerCase().contains(specialization)))
                            continue;

                        DoctorSchedule schedule = new DoctorSchedule(
                                doc.getString("clinicName"),
                                doc.getString("clinicAddress"),
                                formattedDateTime,
                                0,
                                docName,
                                docSpecialty,
                                doc.getString("degree"),
                                docUid
                        );

                        schedule.setClinicUid(doc.getString("clinicUid"));
                        schedule.setDoctorUid(docUid);
                        schedule.setScheduleId(doc.getId());

                        scheduleList.add(schedule);
                    }
                    adapter.notifyDataSetChanged();
                    loadingSpinner.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    loadingSpinner.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load schedules", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onItemClick(DoctorSchedule schedule) {
        Intent intent = new Intent(this, ScheduleDetailActivity.class);
        intent.putExtra("clinicName", schedule.getChamberName());
        intent.putExtra("clinicAddress", schedule.getLocation());
        intent.putExtra("time", schedule.getTime());
        intent.putExtra("doctorName", schedule.getDoctorName());
        intent.putExtra("doctorSpecialty", schedule.getDoctorSpecialty());
        intent.putExtra("doctorDegree", schedule.getDoctorDegree());
        intent.putExtra("doctorUid", schedule.getDoctorUid());
        intent.putExtra("clinicUid", schedule.getClinicUid());
        intent.putExtra("scheduleId", schedule.getScheduleId());

        startActivity(intent);
    }
}
