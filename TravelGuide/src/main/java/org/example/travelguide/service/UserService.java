package org.example.travelguide.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.util.List;
import java.util.Scanner;
import org.example.travelguide.model.*;

@Service
public class UserService {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService (DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    //конструктор для тестов
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public int checkUsers(String username){
        String findUser= "SELECT id FROM users WHERE username = ?";
        try {
            Integer userId = jdbcTemplate.queryForObject(
                    findUser,
                    new Object[]{username},
                    Integer.class
            );
            // Если нашли — возвращаем ID
            if (userId != null) {
                return userId;
            }
        } catch (Exception e) {
            // Пользователь не найден — продолжаем
            System.out.println(" Пользователь '" + username + "' не найден, создаем нового...");
        }

        // 2. Если не нашли — создаем нового
        // Получаем следующий ID
        String countSql = "SELECT COALESCE(MAX(id), 0) + 1 FROM users";
        int newId = jdbcTemplate.queryForObject(countSql, Integer.class);

        // Вставляем нового пользователя
        String insertSql = "INSERT INTO users (id, username) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, newId, username);

        System.out.println("Создан новый пользователь: " + username + " (ID: " + newId + ")");
        return newId;
    }
    // Проверяет, существует ли пользователь, и возвращает его ID.
    // Если не существует — создает нового и возвращает новый ID.



}
