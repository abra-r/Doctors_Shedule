package com.example.doctors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoctorScheduleAdapter extends RecyclerView.Adapter<DoctorScheduleAdapter.ScheduleViewHolder> {

    private final List<DoctorSchedule> scheduleList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DoctorSchedule schedule);
    }

    public DoctorScheduleAdapter(List<DoctorSchedule> scheduleList, OnItemClickListener listener) {
        this.scheduleList = scheduleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        DoctorSchedule schedule = scheduleList.get(position);
        holder.chamberName.setText("Chamber: " + schedule.getChamberName());
        holder.chamberLocation.setText("Location: " + schedule.getLocation());
        holder.scheduleTime.setText("Time: " + schedule.getTime());
        holder.patientCount.setText(String.valueOf(schedule.getPatientCount()));

        holder.itemView.setOnClickListener(v -> listener.onItemClick(schedule));
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView chamberName, chamberLocation, scheduleTime, patientCount;
        ImageView personIcon;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            chamberName = itemView.findViewById(R.id.chamberName);
            chamberLocation = itemView.findViewById(R.id.chamberLocation);
            scheduleTime = itemView.findViewById(R.id.scheduleTime);
            patientCount = itemView.findViewById(R.id.patientCount);
            personIcon = itemView.findViewById(R.id.personIcon);
        }
    }
}
