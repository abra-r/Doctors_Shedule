package com.example.doctors;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SendRequestActivity extends AppCompatActivity {

    private EditText messageEditText;
    private TextView clinicNameText, clinicAddressText, selectedDateText, selectedTimeText;
    private Button sendBtn, selectDateBtn, selectTimeBtn;

    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    private String clinicName = "", clinicAddress = "";
    private String doctorUid = "", doctorName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);

        messageEditText = findViewById(R.id.messageEditText);
        clinicNameText = findViewById(R.id.clinicNameText);
        clinicAddressText = findViewById(R.id.clinicAddressText);
        selectedDateText = findViewById(R.id.selectedDateText);
        selectedTimeText = findViewById(R.id.selectedTimeText);
        sendBtn = findViewById(R.id.sendBtn);
        selectDateBtn = findViewById(R.id.selectDateBtn);
        selectTimeBtn = findViewById(R.id.selectTimeBtn);

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        doctorUid = getIntent().getStringExtra("doctorUid");
        doctorName = getIntent().getStringExtra("username");

        // Load clinic info
        if (currentUser != null) {
            firestore.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(doc -> {
                        String username = doc.getString("username");
                        clinicName = (username != null) ? username : "Unknown Clinic";
                        clinicAddress = doc.getString("clinicAddress");

                        clinicNameText.setText(clinicName);
                        clinicAddressText.setText(clinicAddress != null ? clinicAddress : "Not Set");
                    });
        }

        selectDateBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                String formattedDate = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(selected.getTime());
                selectedDateText.setText(formattedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();

        });

        selectTimeBtn.setOnClickListener(v -> {
            String selectedDate = selectedDateText.getText().toString().trim();

            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar now = Calendar.getInstance();
            int currentHour = now.get(Calendar.HOUR_OF_DAY);
            int currentMinute = now.get(Calendar.MINUTE);


            Calendar today = Calendar.getInstance();
            String todayStr = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(today.getTime());

            boolean isToday = selectedDate.equals(todayStr);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

                if (isToday && (hourOfDay < currentHour || (hourOfDay == currentHour && minute < currentMinute))) {
                    Toast.makeText(this, "Please select a future time", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar selected = Calendar.getInstance();
                selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selected.set(Calendar.MINUTE, minute);
                String formattedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(selected.getTime());
                selectedTimeText.setText(formattedTime);
            }, currentHour, currentMinute, false);

            timePickerDialog.show();
        });


        sendBtn.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            String appointmentDate = selectedDateText.getText().toString();
            String appointmentTime = selectedTimeText.getText().toString();

            if (message.isEmpty()) {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (appointmentDate.isEmpty() || appointmentTime.isEmpty()) {
                Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (doctorUid == null || currentUser == null) {
                Toast.makeText(this, "Error: doctor or user not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("message", message);
            requestMap.put("clinicUid", currentUser.getUid());
            requestMap.put("timestamp", System.currentTimeMillis());
            requestMap.put("status", "pending");
            requestMap.put("clinicName", clinicName);
            requestMap.put("clinicAddress", clinicAddress);
            requestMap.put("appointmentDate", appointmentDate);
            requestMap.put("appointmentTime", appointmentTime);
            requestMap.put("formattedDateTime", appointmentDate + " " + appointmentTime);

            firestore.collection("doctor_requests")
                    .document(doctorUid)
                    .collection("requests")
                    .add(requestMap)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Request sent to Dr. " + doctorName, Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });


    }
}
