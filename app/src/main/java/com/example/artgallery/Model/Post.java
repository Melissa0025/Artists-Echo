package com.example.artgallery.Model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Post {

    // Fields for the Post class
    private String description;
    private String imageUrl;
    private long timestamp; // Unified name for timestamp field
    private User user;

    // No-argument constructor required for Firebase serialization
    public Post() {}

    // Constructor to initialize fields
    public Post(String description, String imageUrl, long timestamp, User user) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.user = user;
    }

    // Getters and setters for each field
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "Post{" +
                "description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", timestamp=" + timestamp +
                ", user=" + user +
                '}';
    }

    public long getCreationTimeMs() {
        return timestamp;
    }

}
