package org.example.travelguide.controller;

import  org.example.travelguide.service.*;
import org.example.travelguide.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // ← Говорит Spring: это REST-контроллер
@RequestMapping("/api/landmarks")  // ← Все эндпоинты начинаются с /api/landmarks
public class LandmarkController {
    @Autowired
    private LandmarkService landmarkService;

    // GET /api/landmarks/top-rated?limit=10&category=Музей
    @GetMapping("/top-rated")
    public List<Landmark> getTopRated(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String category) {
        return landmarkService.getLandmarksSortedByRating(limit, category);
    }

    // GET /api/landmarks/nearby?lat=59.94&lon=30.31&radius=5&limit=10&category=Музей
    @GetMapping("/nearby")
    public List<Landmark> getNearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5") double radius,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String category) {
        return landmarkService.getLandmarksSortedByDistance(lat, lon, radius, limit, category);
    }

    // GET /api/landmarks/{id}
    @GetMapping("/{id}")
    public Landmark getById(@PathVariable int id) {
        return landmarkService.getLandmarkById(id);
    }
}
