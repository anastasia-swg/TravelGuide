
import org.example.travelguide.service.UserService;
import org.example.travelguide.service.ReviewService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class testReviewService {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewService reviewService;

    // ✅ Тест 1: Добавление нового отзыва
    @Test
    void addReview_NewReview_ShouldInsert() {
        int landmarkId = 1;
        String username = "alex";
        int rating = 5;
        String text = "Отличное место!";
        int userId = 10;

        // 1. Пользователь существует (UserService возвращает ID)
        when(userService.checkUsers(username)).thenReturn(userId);

        // 2. Отзыва нет (exists = false)
        when(jdbcTemplate.queryForObject(
                eq("SELECT EXISTS(SELECT 1 FROM reviews WHERE user_id = ? AND landmark_id = ?)"),
                any(Object[].class),
                eq(Boolean.class)
        )).thenReturn(false);

        // 3. Вызываем метод
        reviewService.addReview(landmarkId, username, rating, text);

        // 4. Проверяем, что INSERT вызван
        verify(jdbcTemplate).update(
                eq("INSERT INTO reviews (landmark_id, user_id, rating, text, created_at) VALUES (?, ?, ?, ?, NOW())"),
                eq(landmarkId), eq(userId), eq(rating), eq(text)
        );
        // Проверяем, что UPDATE НЕ вызывался
        verify(jdbcTemplate, never()).update(
                contains("UPDATE reviews"),
                anyInt(), anyString(), anyInt(), anyInt()
        );
    }

    // ✅ Тест 2: Обновление существующего отзыва
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

    // ✅ Тест 3: Некорректная оценка (меньше 1)
    @Test
    void addReview_RatingLessThan1_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.addReview(1, "alex", 0, "Invalid");
        });
        // JdbcTemplate НЕ вызывается
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    // ✅ Тест 4: Некорректная оценка (больше 5)
    @Test
    void addReview_RatingGreaterThan5_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.addReview(1, "alex", 6, "Invalid");
        });
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    // ✅ Тест 5: Отзыв без текста (должен сохраниться)
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

    // ✅ Тест 6: Новый пользователь создается автоматически
    @Test
    void addReview_NewUser_ShouldCreateUserAndReview() {
        String username = "new_user";
        int newUserId = 99;
        int landmarkId = 1;
        int rating = 5;
        String text = "Круто!";

        // UserService создает нового пользователя
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

