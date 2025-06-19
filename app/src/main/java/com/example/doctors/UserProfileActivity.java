package com.example.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.bumptech.glide.Glide;


public class UserProfileActivity extends AppCompatActivity {

    private TextView emailTextView, roleTextView;
    private LinearLayout doctorLayout, clinicManagerLayout;
    private TextView specializationTextView, degreeTextView;
    private TextView licenseNumberTextView, clinicAddressTextView;
    private Button editProfileButton;

    private FirebaseFirestore db;
    private String currentUserId;
    private ImageView profilePictureImageView;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        emailTextView = findViewById(R.id.emailTextView);
        roleTextView = findViewById(R.id.roleTextView);
        editProfileButton = findViewById(R.id.editProfileButton);

        profilePictureImageView = findViewById(R.id.profilePictureImageView);
        userNameTextView = findViewById(R.id.userNameTextView);


        doctorLayout = findViewById(R.id.doctorLayout);
        specializationTextView = findViewById(R.id.specializationTextView);
        degreeTextView = findViewById(R.id.degreeTextView);


        clinicManagerLayout = findViewById(R.id.clinicManagerLayout);
        licenseNumberTextView = findViewById(R.id.licenseNumberTextView);
        clinicAddressTextView = findViewById(R.id.clinicAddressTextView);
        editProfileButton.setOnClickListener(v -> {
            String role = roleTextView.getText().toString();
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("role", role);
            startActivity(intent);
        });


        loadUserProfile();
    }

    private void loadUserProfile() {
        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");


                        String username = documentSnapshot.getString("username");

                        if (username == null || username.isEmpty()) {

                            if (email != null) {
                                username = email.split("@")[0];
                            } else {
                                username = "Unknown User";
                            }
                        }


                        emailTextView.setText(email);


                        String role = documentSnapshot.getString("role");

                        String displayedName = "";

                        if ("doctor".equals(role)) {
                            displayedName = "Doctor: " + username;
                        } else if ("clinic_manager".equals(role)) {
                            displayedName = "Clinic: " + username;
                        } else {
                            displayedName = "General User: " + username;
                        }

                        userNameTextView.setText(displayedName);


                        String profilePictureURL = documentSnapshot.getString("profilePictureURL");

                        if (profilePictureURL == null || profilePictureURL.isEmpty()) {
                            profilePictureImageView.setImageResource(R.drawable.account_icon);
                        } else {
                            Glide.with(this)
                                    .load(profilePictureURL)
                                    .placeholder(R.drawable.account_icon)
                                    .into(profilePictureImageView);
                        }


                        if ("doctor".equals(role)) {
                            doctorLayout.setVisibility(View.VISIBLE);
                            clinicManagerLayout.setVisibility(View.GONE);
                            specializationTextView.setText(documentSnapshot.getString("specialization"));
                            degreeTextView.setText(documentSnapshot.getString("degree"));
                        } else if ("clinic_manager".equals(role)) {
                            doctorLayout.setVisibility(View.GONE);
                            clinicManagerLayout.setVisibility(View.VISIBLE);
                            licenseNumberTextView.setText(documentSnapshot.getString("licenseNumber"));
                            clinicAddressTextView.setText(documentSnapshot.getString("clinicAddress"));
                        } else {
                            doctorLayout.setVisibility(View.GONE);
                            clinicManagerLayout.setVisibility(View.GONE);
                        }

                    } else {
                        Toast.makeText(this, "User profile not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load profile.", Toast.LENGTH_SHORT).show());
    }

}
