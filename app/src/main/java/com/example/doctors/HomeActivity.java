package com.example.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;



public class HomeActivity extends AppCompatActivity {

    private ImageView chatIcon;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        initializeUI();
        setupNavigation();
        setupClickListeners();
        handleUserRole();
    }

    private void initializeUI() {
        chatIcon = findViewById(R.id.fab_chat);
    }

    private void setupNavigation() {
        NavigationHelper.setupNavigation(this);
    }

    private void setupClickListeners() {
        navigateOnClick(R.id.listOfDoctorCard, DoctorListActivity.class);
        navigateOnClick(R.id.healthBlogCard, HealthBlogActivity.class);
        navigateOnClick(R.id.doctorNearMeCard, MapActivity.class);
        navigateOnClick(R.id.doctorScheduleCard, DoctorScheduleListActivity.class);
        navigateOnClick(R.id.fab_chat, BotChatActivity.class);
        navigateOnClick(R.id.btn_my_appointments, ClinicRequestActivity.class);
        navigateOnClick(R.id.btn_schedule, MyScheduleActivity.class);
    }

    private void navigateOnClick(int viewId, Class<?> targetActivity) {
        View view = findViewById(viewId);
        view.setOnClickListener(v -> startActivity(new Intent(this, targetActivity)));
    }

    private void handleUserRole() {
        String role = UserSessionManager.getUserRole(this);
        RoleHandler roleHandler = null;

        if ("doctor".equals(role)) {
            roleHandler = new DoctorRoleHandler();
        } else if ("clinic_manager".equals(role)) {
            roleHandler = new ClinicManagerRoleHandler();
        }

        if (roleHandler != null) {
            roleHandler.apply(this);
        }
    }

    
    public void setButtonText(int buttonId, String text) {
        Button btn = findViewById(buttonId);
        if (btn != null) {
            btn.setText(text);
        }
    }
}
