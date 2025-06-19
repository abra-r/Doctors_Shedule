package com.example.doctors;

public class Blog {
    private String title;
    private String content;
    private String author;
    private int authorImage;

    public Blog(String title, String content, String author, int authorImage) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.authorImage = authorImage;
    }
    public Blog() {

    }

    public Blog(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public int getAuthorImage() {
        return authorImage;
    }
}
