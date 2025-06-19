package com.example.doctors;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BlogDetailActivity extends AppCompatActivity {

    private TextView blogTitle, blogContent, blogAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_details);

        blogTitle = findViewById(R.id.blogTitle);
        blogContent = findViewById(R.id.blogContent);
        blogAuthor = findViewById(R.id.authorName);


        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        String author = getIntent().getStringExtra("author");

        blogTitle.setText(title);
        blogContent.setText(content);
        blogAuthor.setText("By " + author);

        NavigationHelper.setupNavigation(this);
    }
}
