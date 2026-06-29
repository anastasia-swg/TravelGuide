package org.example.travelguide.controller;

import  org.example.travelguide.service.*;
import org.example.travelguide.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // POST /api/reviews
    @PostMapping
    public String addReview(@RequestBody Map<String, Object> request) {
        int landmarkId = (int) request.get("landmarkId");
        String username = (String) request.get("username");
        int rating = (int) request.get("rating");
        String text = (String) request.get("text");

        reviewService.addReview(landmarkId, username, rating, text);
        return "отзыв успешно добавлен";
    }

    // GET /api/reviews/landmark/1
    @GetMapping("/landmark/{landmarkId}")
    public List<Review> getReviews(@PathVariable int landmarkId) {
        return reviewService.getReviewsByLandmark(landmarkId);
    }
}

