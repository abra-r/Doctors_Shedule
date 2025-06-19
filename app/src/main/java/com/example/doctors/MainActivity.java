package com.example.doctors;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null && currentUser.isEmailVerified()) {
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String role = prefs.getString("role", null);

                if (role != null) {

                    Intent intent;
                    switch (role) {
                        case "general_user":
                            intent = new Intent(MainActivity.this, HomeActivity.class);
                            break;
                        case "doctor":
                            intent = new Intent(MainActivity.this, HomeActivity.class);
                            break;
                        case "clinic_manager":
                            intent = new Intent(MainActivity.this, HomeActivity.class);
                            break;
                        default:
                            intent = new Intent(MainActivity.this, LoginActivity.class);
                            break;
                    }
                    startActivity(intent);
                } else {

                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                }
            } else {

                startActivity(new Intent(MainActivity.this, HomeActivity .class));
            }

            finish();
        }, SPLASH_DELAY);
    }
}
