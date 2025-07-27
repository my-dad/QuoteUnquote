package com.qwen.bookqoutecollectot.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "quotes")
public class Quote {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String text;

    @NonNull
    private String author;

    @NonNull
    private String bookTitle;

    private String category; // Optional

    private boolean isFavorite; // For bookmarking

    // Constructors
    public Quote(@NonNull String text, @NonNull String author, @NonNull String bookTitle, String category) {
        this.text = text;
        this.author = author;
        this.bookTitle = bookTitle;
        this.category = category;
        this.isFavorite = false; // Default not favorite
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull
    public String getText() { return text; }
    public void setText(@NonNull String text) { this.text = text; }

    @NonNull
    public String getAuthor() { return author; }
    public void setAuthor(@NonNull String author) { this.author = author; }

    @NonNull
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(@NonNull String bookTitle) { this.bookTitle = bookTitle; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}