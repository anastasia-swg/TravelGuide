
import org.example.travelguide.service.UserService;
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
class testUserService {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private UserService userService;

    // ✅ Тест 1: Пользователь существует — возвращаем его ID
    @Test
    void checkUsers_UserExists_ShouldReturnExistingId() {
        String username = "alex";
        int expectedId = 5;

        // Настраиваем мок: при запросе ID возвращаем 5
        when(jdbcTemplate.queryForObject(
                eq("SELECT id FROM users WHERE username = ?"),
                any(Object[].class),
                eq(Integer.class)
        )).thenReturn(expectedId);

        // Вызываем метод
        int actualId = userService.checkUsers(username);

        // Проверяем
        assertEquals(expectedId, actualId);
        // Проверяем, что INSERT не вызывался
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    // ✅ Тест 2: Пользователь НЕ существует — создаем нового
    @Test
    void checkUsers_UserNotExists_ShouldCreateNewUser() {
        String username = "new_user";

        // 1. При поиске — выбрасываем исключение (пользователь не найден)
        when(jdbcTemplate.queryForObject(
                eq("SELECT id FROM users WHERE username = ?"),
                any(Object[].class),
                eq(Integer.class)
        )).thenThrow(new RuntimeException("Not found"));

        // 2. При подсчете MAX(id) — возвращаем 10
        when(jdbcTemplate.queryForObject(
                eq("SELECT COALESCE(MAX(id), 0) + 1 FROM users"),
                eq(Integer.class)
        )).thenReturn(10);

        // Вызываем метод
        int actualId = userService.checkUsers(username);

        // Проверяем
        assertEquals(10, actualId);
        // Проверяем, что INSERT вызван с правильными параметрами
        verify(jdbcTemplate).update(
                eq("INSERT INTO users (id, username) VALUES (?, ?)"),
                eq(10), eq(username)
        );
    }

    // ✅ Тест 3: Пользователь с пустым именем — создаем
    @Test
    void checkUsers_EmptyUsername_ShouldCreate() {
        String username = "";

        when(jdbcTemplate.queryForObject(
                eq("SELECT id FROM users WHERE username = ?"),
                any(Object[].class),
                eq(Integer.class)
        )).thenThrow(new RuntimeException("Not found"));

        when(jdbcTemplate.queryForObject(
                eq("SELECT COALESCE(MAX(id), 0) + 1 FROM users"),
                eq(Integer.class)
        )).thenReturn(1);

        int actualId = userService.checkUsers(username);

        assertEquals(1, actualId);
        verify(jdbcTemplate).update(anyString(), eq(1), eq(username));
    }
}