package com.example.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class HealthBlogActivity extends AppCompatActivity {

    private RecyclerView blogRecyclerView;
    private List<Blog> blogList;
    private BlogAdapter blogAdapter;
    private FloatingActionButton fabAddBlog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_list);

        blogRecyclerView = findViewById(R.id.blogRecyclerView);
        blogRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddBlog = findViewById(R.id.fab_add_blog);
        fabAddBlog.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {
            String uid = currentUser.getUid();
            firestore.collection("users").document(uid).get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String role = snapshot.getString("role");
                            if ("doctor".equalsIgnoreCase(role)) {
                                fabAddBlog.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }

        fabAddBlog.setOnClickListener(v -> {
            startActivity(new Intent(this, BlogWritingActivity.class));
        });

        blogList = new ArrayList<>();
        blogAdapter = new BlogAdapter(this, blogList);
        blogRecyclerView.setAdapter(blogAdapter);

        loadBlogsFromFirestore();
    }

    private void loadBlogsFromFirestore() {
        firestore.collection("blogs")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Failed to load blogs.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    blogList.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value) {
                            Blog blog = doc.toObject(Blog.class);
                            blogList.add(blog);
                        }
                        blogAdapter.notifyDataSetChanged();
                    }
                });
    }
}
