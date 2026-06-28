
import org.example.travelguide.model.Landmark;
import org.example.travelguide.service.LandmarkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LandmarkServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private LandmarkService landmarkService;

    // ✅ Тест 1: Поиск по ID — место существует
    @Test
    void getLandmarkById_Exists_ShouldReturnLandmark() {
        int id = 1;
        Landmark expected = new Landmark(id, "Эрмитаж", "СПб", 59.94, 30.31, 4.5);

        when(jdbcTemplate.queryForObject(
                eq("SELECT id, name, city, latitude, longitude, avg_rating FROM landmark WHERE id = ?"),
                any(Object[].class),
                any(RowMapper.class)
        )).thenReturn(expected);

        Landmark actual = landmarkService.getLandmarkById(id);

        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    // ✅ Тест 2: Поиск по ID — место НЕ существует
    @Test
    void getLandmarkById_NotExists_ShouldReturnNull() {
        int id = 999;

        when(jdbcTemplate.queryForObject(
                eq("SELECT id, name, city, latitude, longitude, avg_rating FROM landmark WHERE id = ?"),
                any(Object[].class),
                any(RowMapper.class)
        )).thenThrow(new RuntimeException("Not found"));

        Landmark actual = landmarkService.getLandmarkById(id);

        assertNull(actual);
    }

    // ✅ Тест 3: Получение топ-5 по рейтингу
    @Test
    void getLandmarksSortedByRating_ShouldReturnList() {
        int limit = 5;
        List<Landmark> expectedList = new ArrayList<>();
        expectedList.add(new Landmark(1, "Эрмитаж", "СПб", 59.94, 30.31, 4.8));
        expectedList.add(new Landmark(2, "Петропавловка", "СПб", 59.95, 30.32, 4.5));

        when(jdbcTemplate.query(
                eq("SELECT * FROM get_landmarks_sorted_by_rating(" + limit + ")"),
                any(RowMapper.class)
        )).thenReturn(expectedList);

        List<Landmark> actual = landmarkService.getLandmarksSortedByRating(limit);

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals("Эрмитаж", actual.get(0).getName());
    }
}
