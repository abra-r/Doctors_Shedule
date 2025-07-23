package com.example.doctors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointedPatientsAdapter extends RecyclerView.Adapter<AppointedPatientsAdapter.ViewHolder> {

    private List<PatientInfo> patients;

    public AppointedPatientsAdapter(List<PatientInfo> patients) {
        this.patients = patients;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText, descText;

        public ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.patientName);
            emailText = itemView.findViewById(R.id.patientEmail);
            descText = itemView.findViewById(R.id.patientDescription);
        }
    }

    @NonNull
    @Override
    public AppointedPatientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_patient_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointedPatientsAdapter.ViewHolder holder, int position) {
        PatientInfo p = patients.get(position);
        holder.nameText.setText("Name: " + p.getUsername());
        holder.emailText.setText("Email: " + p.getEmail());
        holder.descText.setText("Note: " + p.getDescription());
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }
}
