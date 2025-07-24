package com.example.doctors;

import android.view.View;
import com.example.doctors.HomeActivity;

public class DoctorRoleHandler implements RoleHandler {
    @Override
    public void apply(HomeActivity activity) {
        activity.findViewById(R.id.fab_chat).setVisibility(View.GONE);
        activity.findViewById(R.id.btn_schedule).setVisibility(View.VISIBLE);
        activity.setButtonText(R.id.btn_my_appointments, "Clinic Request");
    }
}
