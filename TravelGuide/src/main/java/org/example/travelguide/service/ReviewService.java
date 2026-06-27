package org.example.travelguide.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.util.List;
import java.util.Scanner;
import org.example.travelguide.model.*;


@Service
public class ReviewService {

    private JdbcTemplate jdbcTemplate;
    private final UserService userService;  // ← Добавляем зависимость

    @Autowired
    public ReviewService(DataSource dataSource, UserService userService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.userService = userService;
    }

    public void addReview(int landmarkId, String user, int rating, String text) {

        int userId = userService.checkUsers(user);
        // Проверяем существование через EXISTS
        String checkSql = "SELECT EXISTS(SELECT 1 FROM reviews WHERE user_id = ? AND landmark_id = ?)";
        Boolean exists = jdbcTemplate.queryForObject(
                checkSql,
                new Object[]{userId, landmarkId},
                Boolean.class
        );

        if (exists != null && exists) {
            // Обновляем существующий отзыв
            String sql = "UPDATE reviews SET rating = ?, text = ?, created_at = NOW() " +
                    "WHERE user_id = ? AND landmark_id = ?";
            jdbcTemplate.update(sql, rating, text, userId, landmarkId);
            System.out.println("✅ Отзыв обновлен!");
        } else {
            // Вставляем новый отзыв
            String sql = "INSERT INTO reviews (landmark_id, user_id, rating, text, created_at) " +
                    "VALUES (?, ?, ?, ?, NOW())";
            jdbcTemplate.update(sql, landmarkId, userId, rating, text);
            System.out.println("✅ Отзыв добавлен!");
        }
    }

    public List<Review> getReviewsByLandmark(int landmarkId) {
        String sql = "SELECT r.landmark_id, r.user_id, r.rating, r.text, r.created_at, u.username " +
                "FROM reviews r " +
                "JOIN users u ON r.user_id = u.id " +
                "WHERE r.landmark_id = ? " +
                "ORDER BY r.created_at DESC";

        return jdbcTemplate.query(sql, new Object[]{landmarkId}, (rs, rowNum) ->
                new Review(

                        rs.getInt("landmark_id"),
                        rs.getInt("user_id"),
                        rs.getInt("rating"),
                        rs.getString("text"),
                        rs.getTimestamp("created_at") != null ?
                                rs.getTimestamp("created_at").toLocalDateTime() : null,
                        rs.getString("username")
                )
        );
    }

}
