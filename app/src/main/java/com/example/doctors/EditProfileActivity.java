package com.example.doctors;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView profilePictureImageView;
    private Button changePictureBtn, saveProfileBtn;

    private EditText userNameEditText, specializationEditText, degreeEditText,
            licenseEditText, clinicAddressEditText, aboutEditText;

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

        // UI initialization
        profilePictureImageView = findViewById(R.id.profilePictureImageView);
        changePictureBtn = findViewById(R.id.changePictureBtn);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);

        userNameEditText = findViewById(R.id.userNameEditText);
        specializationEditText = findViewById(R.id.specializationEditText);
        degreeEditText = findViewById(R.id.degreeEditText);
        licenseEditText = findViewById(R.id.licenseEditText);
        clinicAddressEditText = findViewById(R.id.clinicAddressEditText);
        aboutEditText = findViewById(R.id.aboutEditText);

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
        if (requestCode == RC_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            profilePictureImageView.setImageURI(imageUri);
        }
    }

    private void loadProfile() {
        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    role = doc.getString("role");
                    String username = doc.getString("username");
                    String specialization = doc.getString("specialization");
                    String degree = doc.getString("degree");
                    String license = doc.getString("licenseNumber");
                    String clinicAddress = doc.getString("clinicAddress");
                    String about = doc.getString("about");
                    String profileUrl = doc.getString("profilePictureURL");

                    userNameEditText.setText(username);
                    aboutEditText.setText(about);


                    if ("doctor".equals(role)) {
                        specializationEditText.setVisibility(View.VISIBLE);
                        degreeEditText.setVisibility(View.VISIBLE);
                        licenseEditText.setVisibility(View.GONE);
                        clinicAddressEditText.setVisibility(View.GONE);
                        specializationEditText.setText(specialization);
                        degreeEditText.setText(degree);
                    } else if ("clinic_manager".equals(role)) {
                        licenseEditText.setVisibility(View.VISIBLE);
                        clinicAddressEditText.setVisibility(View.VISIBLE);
                        specializationEditText.setVisibility(View.GONE);
                        degreeEditText.setVisibility(View.GONE);
                        licenseEditText.setText(license);
                        clinicAddressEditText.setText(clinicAddress);
                    } else {

                        specializationEditText.setVisibility(View.GONE);
                        degreeEditText.setVisibility(View.GONE);
                        licenseEditText.setVisibility(View.GONE);
                        clinicAddressEditText.setVisibility(View.GONE);
                    }

                    if (profileUrl != null && !profileUrl.isEmpty()) {
                        Glide.with(this).load(profileUrl).into(profilePictureImageView);
                    } else {
                        profilePictureImageView.setImageResource(R.drawable.account_icon);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show());
    }

    private void saveProfile() {
        if (currentUserId == null) return;

        Map<String, Object> profile = new HashMap<>();
        profile.put("username", userNameEditText.getText().toString().trim());
        profile.put("about", aboutEditText.getText().toString().trim());

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
                                updateUser(profile);
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Image URL failed", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show());
        } else {
            updateUser(profile);
        }
    }

    private void updateUser(Map<String, Object> profile) {
        db.collection("users").document(currentUserId)
                .set(profile, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(unused -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }
}
