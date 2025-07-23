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

import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    private ImageView listOfDoctorCard, healthBlogCard;
    private ImageView doctorNearMeCard;
    private ImageView doctorScheduleCard,chatIcon;
    private Button myAppointmentBtn,scheduleBtn;

    private LinearLayout requestContainer;

    private String currentDoctorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        listOfDoctorCard = findViewById(R.id.listOfDoctorCard);
        healthBlogCard = findViewById(R.id.healthBlogCard);
        doctorNearMeCard = findViewById(R.id.doctorNearMeCard);
        doctorScheduleCard = findViewById(R.id
                .doctorScheduleCard);
        chatIcon=findViewById(R.id.fab_chat);
        myAppointmentBtn =findViewById(R.id.btn_my_appointments);
        scheduleBtn=findViewById(R.id.btn_schedule);



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
        chatIcon.setOnClickListener(view->{
            Intent intent=new Intent(this,BotChatActivity.class);
            startActivity(intent);
        });

        NavigationHelper.setupNavigation(this);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = prefs.getString("role", null);

        if ("doctor".equals(role)) {
            chatIcon.setVisibility(View.GONE);
            currentDoctorName = prefs.getString("name", "");
            myAppointmentBtn.setText("Clinic Request");
            scheduleBtn.setVisibility(View.VISIBLE);


        }
        else if("clinic_manager".equals(role))
        {
            myAppointmentBtn.setText("Doctor Request");
            scheduleBtn.setVisibility(View.VISIBLE);
            scheduleBtn.setText("Clinic Schedule");
            Button appointmentRequestsBtn = findViewById(R.id.btn_appointment_requests);
            appointmentRequestsBtn.setVisibility(View.VISIBLE);

            appointmentRequestsBtn.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, AppointmentRequestListActivity.class);
                startActivity(intent);
            });


        }

        myAppointmentBtn.setOnClickListener((view->{
            Intent intent=new Intent(HomeActivity.this,ClinicRequestActivity.class);

            startActivity(intent);
        }));
        scheduleBtn.setOnClickListener((view->{
            Intent intent =new Intent(HomeActivity.this,MyScheduleActivity.class);
            startActivity(intent);
        }));

    }

}
