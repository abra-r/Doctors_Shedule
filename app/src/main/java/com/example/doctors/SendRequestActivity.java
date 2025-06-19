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

        String doctorUid = getIntent().getStringExtra("doctorUid");
        String doctorName = getIntent().getStringExtra("doctorName");


        firestore.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    clinicName = doc.getString("clinicName");
                    clinicAddress = doc.getString("clinicAddress");
                    clinicNameText.setText(clinicName);
                    clinicAddressText.setText(clinicAddress);
                });


        selectDateBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                String formattedDate = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(selected.getTime());
                selectedDateText.setText(formattedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        selectTimeBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selected.set(Calendar.MINUTE, minute);
                String formattedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(selected.getTime());
                selectedTimeText.setText(formattedTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        });

        sendBtn.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (doctorUid == null || currentUser == null) {
                Toast.makeText(this, "Error: doctor or user not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("message", message);
            requestMap.put("fromUid", currentUser.getUid());
            requestMap.put("timestamp", System.currentTimeMillis());
            requestMap.put("status", "pending");
            requestMap.put("clinicName", clinicName);
            requestMap.put("clinicAddress", clinicAddress);
            requestMap.put("appointmentDate", selectedDateText.getText().toString());
            requestMap.put("appointmentTime", selectedTimeText.getText().toString());

            firestore.collection("doctor_requests")
                    .document(doctorUid)
                    .collection("requests")
                    .add(requestMap)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Request sent to Dr. " + doctorName, Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}
