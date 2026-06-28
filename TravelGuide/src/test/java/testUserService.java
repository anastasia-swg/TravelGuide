
import org.example.travelguide.service.LandmarkService;
import org.example.travelguide.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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

    private  UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(jdbcTemplate);
    }
    //Тест - пользователь существует возвращаем его id
    @Test
    void checkUsers_UserExists_ShouldReturnExistingId() {
        String username = "alex";
        int expectedId = 5;

        when(jdbcTemplate.queryForObject(
                eq("SELECT id FROM users WHERE username = ?"),
                any(Object[].class),
                eq(Integer.class)
        )).thenReturn(expectedId);

        int actualId = userService.checkUsers(username);

        assertEquals(expectedId, actualId);
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    //Тест- пользователь не существует — создаем нового
    @Test
    void checkUsers_UserNotExists_ShouldCreateNewUser() {
        String username = "new_user";

        when(jdbcTemplate.queryForObject(
                eq("SELECT id FROM users WHERE username = ?"),
                any(Object[].class),
                eq(Integer.class)
        )).thenThrow(new RuntimeException("Not found"));

        when(jdbcTemplate.queryForObject(
                eq("SELECT COALESCE(MAX(id), 0) + 1 FROM users"),
                eq(Integer.class)
        )).thenReturn(10);

        int actualId = userService.checkUsers(username);

        assertEquals(10, actualId);
        verify(jdbcTemplate).update(
                eq("INSERT INTO users (id, username) VALUES (?, ?)"),
                eq(10), eq(username)
        );
    }

    // тест - пользователь с пустым именем — создаем
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