package com.example.doctors;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class BlogWritingActivity extends AppCompatActivity {

    private EditText editTitle, editContent;
    private Button btnSubmit;

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_writing);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        btnSubmit = findViewById(R.id.btnSubmit);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnSubmit.setOnClickListener(v -> submitBlog());
    }

    private void submitBlog() {
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();


        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String author = documentSnapshot.getString("username");
                    if (author == null) author = mAuth.getCurrentUser().getEmail();

                    Map<String, Object> blogData = new HashMap<>();
                    blogData.put("title", title);
                    blogData.put("content", content);
                    blogData.put("author", author);
                    blogData.put("timestamp", FieldValue.serverTimestamp());

                    firestore.collection("blogs")
                            .add(blogData)
                            .addOnSuccessListener(docRef -> {
                                Toast.makeText(this, "Blog posted!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to post blog", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "User info fetch failed", Toast.LENGTH_SHORT).show();
                });
    }
}
