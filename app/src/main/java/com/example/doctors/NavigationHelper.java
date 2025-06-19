package com.example.doctors;

import android.app.Activity;
import android.content.Intent;

import android.view.Gravity;
import android.widget.ImageView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class NavigationHelper {

    public static void setupNavigation(final Activity activity) {
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);
        NavigationView navigationView = activity.findViewById(R.id.navigation_view);
        ImageView menuIcon = activity.findViewById(R.id.menuIcon);
        ImageView profileIcon = activity.findViewById(R.id.profileIcon);


        if (menuIcon != null && drawerLayout != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null && currentUser.isEmailVerified()) {

                    Intent intent = new Intent(activity, UserProfileActivity.class);
                    activity.startActivity(intent);
                } else {

                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                }
            });
        }



        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);
                } else if (id == R.id.nav_doctor_list) {
                    Intent intent = new Intent(activity, DoctorListActivity.class);
                    activity.startActivity(intent);
                } else if (id == R.id.nav_health_blog) {
                    Intent intent = new Intent(activity, HealthBlogActivity.class);
                    activity.startActivity(intent);
                } else if (id == R.id.logout) {

                    FirebaseAuth.getInstance().signOut();


                    activity.getSharedPreferences("UserPrefs", activity.MODE_PRIVATE)
                            .edit()
                            .clear()
                            .apply();


                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                }

                assert drawerLayout != null;
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });

        }
    }
}
