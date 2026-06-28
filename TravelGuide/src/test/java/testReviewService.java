
import org.example.travelguide.service.UserService;
import org.example.travelguide.service.ReviewService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class testReviewService {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private UserService userService;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(jdbcTemplate, userService);
    }

    // Тест - добавление нового отзыва
    @Test
    void addReview_NewReview_ShouldInsert() {
        int landmarkId = 1;
        String username = "alex";
        int rating = 5;
        String text = "Отличное место!";
        int userId = 10;

        // пользователь существует (возвращает ID)
        when(userService.checkUsers(username)).thenReturn(userId);

        // отзыва нет (exists = false)
        when(jdbcTemplate.queryForObject(
                eq("SELECT EXISTS(SELECT 1 FROM reviews WHERE user_id = ? AND landmark_id = ?)"),
                any(Object[].class),
                eq(Boolean.class)
        )).thenReturn(false);

        reviewService.addReview(landmarkId, username, rating, text);

        // Проверяем INSERT вызван
        verify(jdbcTemplate).update(
                eq("INSERT INTO reviews (landmark_id, user_id, rating, text, created_at) VALUES (?, ?, ?, ?, NOW())"),
                eq(landmarkId), eq(userId), eq(rating), eq(text)
        );
        // Проверяем UPDATE НЕ вызывался
        verify(jdbcTemplate, never()).update(
                contains("UPDATE reviews"),
                anyInt(), anyString(), anyInt(), anyInt()
        );
    }

    //Тест - обновление существующего отзыва
    @Test
    void addReview_ExistingReview_ShouldUpdate() {
        int landmarkId = 1;
        String username = "alex";
        int rating = 4;
        String text = "Обновленный отзыв";
        int userId = 10;

        when(userService.checkUsers(username)).thenReturn(userId);
        when(jdbcTemplate.queryForObject(
                eq("SELECT EXISTS(SELECT 1 FROM reviews WHERE user_id = ? AND landmark_id = ?)"),
                any(Object[].class),
                eq(Boolean.class)
        )).thenReturn(true);

        reviewService.addReview(landmarkId, username, rating, text);

        verify(jdbcTemplate).update(
                eq("UPDATE reviews SET rating = ?, text = ?, created_at = NOW() WHERE user_id = ? AND landmark_id = ?"),
                eq(rating), eq(text), eq(userId), eq(landmarkId)
        );
    }

    //Тест - некорректная оценка
    @Test
    void addReview_RatingLessThan1_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.addReview(1, "alex", 0, "Invalid");
        });
        // JdbcTemplate НЕ вызывается
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    //Тест - некорректная оценка
    @Test
    void addReview_RatingGreaterThan5_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.addReview(1, "alex", 6, "Invalid");
        });
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    //Тест - отзыв без текста (должен сохраниться)
    @Test
    void addReview_EmptyText_ShouldSave() {
        int landmarkId = 1;
        String username = "alex";
        int rating = 5;
        String text = "";
        int userId = 10;

        when(userService.checkUsers(username)).thenReturn(userId);
        when(jdbcTemplate.queryForObject(
                eq("SELECT EXISTS(SELECT 1 FROM reviews WHERE user_id = ? AND landmark_id = ?)"),
                any(Object[].class),
                eq(Boolean.class)
        )).thenReturn(false);

        reviewService.addReview(landmarkId, username, rating, text);

        verify(jdbcTemplate).update(
                eq("INSERT INTO reviews (landmark_id, user_id, rating, text, created_at) VALUES (?, ?, ?, ?, NOW())"),
                eq(landmarkId), eq(userId), eq(rating), eq(text)
        );
    }

    //Тест - новый пользователь создается автоматически
    @Test
    void addReview_NewUser_ShouldCreateUserAndReview() {
        String username = "new_user";
        int newUserId = 99;
        int landmarkId = 1;
        int rating = 5;
        String text = "Круто!";

        when(userService.checkUsers(username)).thenReturn(newUserId);
        when(jdbcTemplate.queryForObject(
                eq("SELECT EXISTS(SELECT 1 FROM reviews WHERE user_id = ? AND landmark_id = ?)"),
                any(Object[].class),
                eq(Boolean.class)
        )).thenReturn(false);

        reviewService.addReview(landmarkId, username, rating, text);

        verify(jdbcTemplate).update(
                eq("INSERT INTO reviews (landmark_id, user_id, rating, text, created_at) VALUES (?, ?, ?, ?, NOW())"),
                eq(landmarkId), eq(newUserId), eq(rating), eq(text)
        );
    }
}

