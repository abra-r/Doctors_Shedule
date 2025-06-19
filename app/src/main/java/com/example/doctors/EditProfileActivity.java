package com.example.doctors;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;


import java.util.HashMap;
import java.util.Map;
public class EditProfileActivity extends AppCompatActivity {

    private ImageView profilePictureImageView;
    private Button changePictureBtn, saveProfileBtn;

    private EditText userNameEditText, specializationEditText, degreeEditText,
            licenseEditText, clinicAddressEditText;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private StorageReference profilePictureRef;

    private static final int RC_PICK_IMAGE = 100;

    private Uri imageUri = null;

    private String currentUserId;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        profilePictureImageView = findViewById(R.id.profilePictureImageView);
        changePictureBtn = findViewById(R.id.changePictureBtn);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);

        userNameEditText = findViewById(R.id.userNameEditText);
        specializationEditText = findViewById(R.id.specializationEditText);
        degreeEditText = findViewById(R.id.degreeEditText);
        licenseEditText = findViewById(R.id.licenseEditText);
        clinicAddressEditText = findViewById(R.id.clinicAddressEditText);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        currentUserId = auth.getCurrentUser().getUid();

        profilePictureRef = storage.getReference().child("profiles/" + currentUserId + ".jpg");


        changePictureBtn.setOnClickListener(v -> selectPicture());


        saveProfileBtn.setOnClickListener(v -> saveProfile());

        loadProfile();
    }

    private void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, RC_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            profilePictureRef = storage.getReference().child("profiles/" + currentUserId + ".jpg");


            if (imageUri != null) {
                profilePictureImageView.setImageURI(imageUri);
            }
        }
    }

    private void saveProfile() {
        if (currentUserId == null) return;

        final HashMap<String, Object> profile = new HashMap<>();
        profile.put("username", userNameEditText.getText().toString().trim());

        if ("doctor".equals(role)) {
            profile.put("specialization", specializationEditText.getText().toString().trim());
            profile.put("degree", degreeEditText.getText().toString().trim());
        } else if ("clinic_manager".equals(role)) {
            profile.put("licenseNumber", licenseEditText.getText().toString().trim());
            profile.put("clinicAddress", clinicAddressEditText.getText().toString().trim());
        }

        if (imageUri != null) {
            profilePictureRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> profilePictureRef.getDownloadUrl()
                            .addOnSuccessListener(url -> {
                                profile.put("profilePictureURL", url.toString());


                                db.collection("users").document(currentUserId)
                                        .set(profile, com.google.firebase.firestore.SetOptions.merge()) // <- HERE
                                        .addOnSuccessListener(unused -> Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to update.", Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to retrieve picture.", Toast.LENGTH_SHORT).show()) )
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload.", Toast.LENGTH_SHORT).show());
        } else {
            db.collection("users").document(currentUserId)
                    .set(profile, com.google.firebase.firestore.SetOptions.merge()) // <- HERE
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update.", Toast.LENGTH_SHORT).show());
        }
    }

    private void loadProfile() {
        if (currentUserId == null) return;

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        role = documentSnapshot.getString("role");


                        userNameEditText.setText(documentSnapshot.getString("username"));

                        if ("doctor".equals(role)) {
                            specializationEditText.setVisibility(View.VISIBLE);
                            degreeEditText.setVisibility(View.VISIBLE);
                            licenseEditText.setVisibility(View.GONE);
                            clinicAddressEditText.setVisibility(View.GONE);
                        } else if ("clinic_manager".equals(role)) {
                            licenseEditText.setVisibility(View.VISIBLE);
                            clinicAddressEditText.setVisibility(View.VISIBLE);
                            specializationEditText.setVisibility(View.GONE);
                            degreeEditText.setVisibility(View.GONE);
                        } else {
                            licenseEditText.setVisibility(View.GONE);
                            clinicAddressEditText.setVisibility(View.GONE);
                            specializationEditText.setVisibility(View.GONE);
                            degreeEditText.setVisibility(View.GONE);
                        }


                        String profilePictureURL = documentSnapshot.getString("profilePictureURL");

                        if (profilePictureURL == null || profilePictureURL.isEmpty()) {
                            profilePictureImageView.setImageResource(R.drawable.account_icon);
                        } else {
                            Glide.with(this)
                                    .load(profilePictureURL)
                                    .into(profilePictureImageView);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load.", Toast.LENGTH_SHORT).show());
    }
}

