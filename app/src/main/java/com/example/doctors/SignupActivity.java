package com.example.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private RadioGroup roleRadioGroup;
    private Button signUpButton,pickLocationBtn;
    private LinearLayout doctorFields, clinicFields;

    private double selectedLat = 0.0, selectedLng = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        emailEditText = findViewById(R.id.emailSignUp);
        passwordEditText = findViewById(R.id.passwordSignUp);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordSignUp);
        signUpButton = findViewById(R.id.signUpButton);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        doctorFields = findViewById(R.id.doctorFields);
        clinicFields = findViewById(R.id.clinicFields);
        pickLocationBtn = findViewById(R.id.pickLocationBtn);
        EditText clinicAddress = findViewById(R.id.clinicAddress);

        pickLocationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, MapPickerActivity.class);
            startActivityForResult(intent, 101);
        });


        roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.generalUserRadio) {
                doctorFields.setVisibility(View.GONE);
                clinicFields.setVisibility(View.GONE);
            } else if (checkedId == R.id.doctorRadio) {
                doctorFields.setVisibility(View.VISIBLE);
                clinicFields.setVisibility(View.GONE);
            } else if (checkedId == R.id.clinicManagerRadio) {
                doctorFields.setVisibility(View.GONE);
                clinicFields.setVisibility(View.VISIBLE);
            }
        });


        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }


            int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
            String role = "";
            if (selectedRoleId == R.id.generalUserRadio) {
                role = "general_user";
            } else if (selectedRoleId == R.id.doctorRadio) {
                role = "doctor";
            } else if (selectedRoleId == R.id.clinicManagerRadio) {
                role = "clinic_manager";
            }


            signUpUser(email, password, role);
        });
    }

    private void signUpUser(String email, String password, String role) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        saveUserData(role);
                        mAuth.getCurrentUser().sendEmailVerification()
                                .addOnCompleteListener(verifyTask -> {
                                    if (verifyTask.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this,
                                                "Verification email sent. Please verify before logging in.",
                                                Toast.LENGTH_LONG).show();

                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(SignupActivity.this,
                                                "Failed to send verification email.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(SignupActivity.this,
                                "Sign up failed: " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void saveUserData(String role) {
        String email = emailEditText.getText().toString().trim();
        String username = email.split("@")[0];


        PersonalInfo personalInfo = new PersonalInfo(email, username, role);
        MedicalInfo medicalInfo = null;
        ClinicInfo clinicInfo = null;
        GeoLocation geoLocation = null;

        if (role.equals("doctor")) {
            String specialization = ((EditText) findViewById(R.id.specialization)).getText().toString().trim();
            String degree = ((EditText) findViewById(R.id.degree)).getText().toString().trim();
            String college = ((EditText) findViewById(R.id.collage)).getText().toString().trim();

            medicalInfo = new MedicalInfo(specialization, college, degree);
        }

        if (role.equals("clinic_manager")) {
            String licenseNumber = ((EditText) findViewById(R.id.licenseNumber)).getText().toString().trim();
            String clinicAddress = ((EditText) findViewById(R.id.clinicAddress)).getText().toString().trim();
            String clinicName = ((EditText) findViewById(R.id.clinicName)).getText().toString().trim();

            clinicInfo = new ClinicInfo(clinicName, clinicAddress, licenseNumber);
            geoLocation = new GeoLocation(selectedLat, selectedLng);
        }

        User user = new User.Builder()
                .setPersonalInfo(personalInfo)
                .setMedicalInfo(medicalInfo)
                .setClinicInfo(clinicInfo)
                .setGeoLocation(geoLocation)
                .build();

        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignupActivity.this, "User data saved successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignupActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String address = data.getStringExtra("address");
            selectedLat = data.getDoubleExtra("lat", 0.0);
            selectedLng = data.getDoubleExtra("lng", 0.0);

            EditText clinicAddress = findViewById(R.id.clinicAddress);
            clinicAddress.setText(address);
        }
    }



}
