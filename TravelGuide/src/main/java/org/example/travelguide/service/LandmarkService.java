package org.example.travelguide.service;

import org.example.travelguide.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.util.List;


@Service
public class LandmarkService {
    private JdbcTemplate jdbcTemplate;

    public List<Landmark> getLandmarksSortedByRating(int limit, String category) {
        String sql = "SELECT * FROM get_landmarks_sorted_by_rating(?, ?)";
        return jdbcTemplate.query(sql, new Object[]{limit, category}, (rs, rowNum) ->
                new Landmark(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getDouble("avg_rating"),
                        rs.getString("category")
                )
        );
    }
    // Сортировка по расстоянию с фильтром по категории
    public List<Landmark> getLandmarksSortedByDistance(double lat, double lon, double radius, int limit, String category) {
        String sql = "SELECT * FROM get_landmarks_sorted_by_distance(?, ?, ?, ?, ?)";
        return jdbcTemplate.query(sql, new Object[]{lat, lon, radius, limit, category}, (rs, rowNum) ->
                new Landmark(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getDouble("avg_rating"),
                        rs.getString("category")
                )
        );
    }

    public Landmark getLandmarkById(int id) {
        String sql = "SELECT id, name, city, latitude, longitude, avg_rating, category FROM landmark WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                    new Landmark(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("city"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getDouble("avg_rating"),
                            rs.getString("category")
                    )
            );
        } catch (Exception e) {
            return null; // Если не найдено
        }
    }
    public List<Landmark> sorted(String sql) {
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Landmark(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getDouble("avg_rating"),
                        rs.getString("category")
                )
        );
    }


    //конструктор
    @Autowired
    public LandmarkService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //конструктор для тестов
    public LandmarkService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
