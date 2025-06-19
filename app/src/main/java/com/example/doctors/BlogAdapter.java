package com.example.doctors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    private Context context;
    private List<Blog> blogList;

    public BlogAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blog_list_item, parent, false);
        return new BlogViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        Blog blog = blogList.get(position);
        holder.blogTitle.setText(blog.getTitle());

        // Show only a short part of the blog
        String fullContent = blog.getContent();
        String preview = fullContent.length() > 100 ? fullContent.substring(0, 100) + "..." : fullContent;
        holder.blogContent.setText(preview);

        holder.blogAuthor.setText("By " + blog.getAuthor());

        if (blog.getAuthorImage() != 0) {
            holder.authorImage.setImageResource(blog.getAuthorImage());
        } else {
            holder.authorImage.setImageResource(R.drawable.doctor_flaticon); // fallback image
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BlogDetailActivity.class);
            intent.putExtra("title", blog.getTitle());
            intent.putExtra("content", blog.getContent());
            intent.putExtra("author", blog.getAuthor());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        ImageView authorImage;
        TextView blogTitle, blogContent, blogAuthor;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            authorImage = itemView.findViewById(R.id.authorImage);
            blogTitle = itemView.findViewById(R.id.blogTitle);
            blogContent = itemView.findViewById(R.id.blogContent);
            blogAuthor = itemView.findViewById(R.id.blogAuthor);
        }
    }
}
