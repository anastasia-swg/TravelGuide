package org.example.travelguide.model;

import java.time.LocalDateTime;

public class Review {
    private int id;
    private int landmarkId;
    private int userId;
    private int rating;
    private String text;
    private LocalDateTime createdAt;
    private String username; // Имя пользователя из JOIN

    public Review( int landmarkId, int userId, int rating, String text,
                   LocalDateTime createdAt, String username) {

        this.landmarkId = landmarkId;
        this.userId = userId;
        this.rating = rating;
        this.text = text;
        this.createdAt = createdAt;
        this.username = username;
    }

    public int getId() { return id; }
    public int getLandmarkId() { return landmarkId; }
    public int getUserId() { return userId; }
    public int getRating() { return rating; }
    public String getText() { return text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getUsername() { return username; }
}