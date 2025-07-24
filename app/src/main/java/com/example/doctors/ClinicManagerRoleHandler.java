package com.example.doctors;

import android.view.View;
import android.content.Intent;

import com.example.doctors.AppointmentRequestListActivity;
import com.example.doctors.HomeActivity;
import com.example.doctors.R;

public class ClinicManagerRoleHandler implements RoleHandler {
    @Override
    public void apply(HomeActivity activity) {
        activity.setButtonText(R.id.btn_my_appointments, "Doctor Request");

        activity.findViewById(R.id.btn_schedule).setVisibility(View.VISIBLE);
        activity.setButtonText(R.id.btn_schedule, "Clinic Schedule");

        View appointmentRequestsBtn = activity.findViewById(R.id.btn_appointment_requests);
        appointmentRequestsBtn.setVisibility(View.VISIBLE);

        appointmentRequestsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(activity, AppointmentRequestListActivity.class);
            activity.startActivity(intent);
        });
    }
}
