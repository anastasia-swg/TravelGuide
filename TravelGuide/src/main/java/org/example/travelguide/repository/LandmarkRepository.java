package org.example.travelguide.repository;

import org.example.travelguide.service.*;
import org.example.travelguide.model.*;
import java.util.List;
import java.util.Scanner;


public class LandmarkRepository {
    private LandmarkService landmarkService;
    private ReviewService reviewService;

    public LandmarkRepository(LandmarkService landmarkService, ReviewService reviewService) {
        this.landmarkService = landmarkService;
        this.reviewService = reviewService;
    }

    public void managerShow() {
        System.out.println("Выберите сортировку:\n 1. По рейтингу\n 2. По удаленности\n");
        Scanner in = new Scanner(System.in);
        int answ = in.nextInt();
        List<Landmark> distanceSortedLandmark;

        try {
            if (answ == 1) {
                System.out.print("Введите количество результатов: ");
                int limit = in.nextInt();
                distanceSortedLandmark = landmarkService.getLandmarksSortedByRating(limit);
            } else if (answ == 2) {
                System.out.print("Введите вашу широту: ");
                double lat = in.nextDouble();
                System.out.print("Введите вашу долготу: ");
                double lon = in.nextDouble();
                System.out.print("Введите радиус поиска (км): ");
                double radius = in.nextDouble();
                distanceSortedLandmark = landmarkService.getLandmarksSortedByDistance(lat, lon, radius);
            } else {
                System.out.println("Неверный выбор!");
                return;
            }

            System.out.println("\nРЕЗУЛЬТАТЫ :");
            System.out.println("========================================");

            if (distanceSortedLandmark.isEmpty()) {
                System.out.println("Достопримечательностей не найдено.");
            } else {
                for (Landmark lm : distanceSortedLandmark) {
                    lm.printAll();
                    System.out.println("----------------------------------------");
                }
                System.out.println("Всего найдено: " + distanceSortedLandmark.size());
            }

        } catch (Exception ex) {
            System.out.println("Ошибка сортировки: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void viewReviews() {
        Scanner in = new Scanner(System.in);
        System.out.print("\nВведите ID достопримечательности: ");
        int landmarkId = in.nextInt();
        in.nextLine(); // Чистим буфер

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("ОТЗЫВЫ О ДОСТОПРИМЕЧАТЕЛЬНОСТИ (ID: " + landmarkId + ")");
        System.out.println("═══════════════════════════════════════");

        try {
            // 1. Получаем информацию о достопримечательности
            Landmark landmark = landmarkService.getLandmarkById(landmarkId);

            if (landmark == null) {
                System.out.println("Достопримечательность " + landmarkId + " не найдена.");
                return;
            }

            System.out.println(landmark.getName() + " (" + landmark.getCity() + ")");
            System.out.println("Средний рейтинг: " + landmark.getRating() + "/5");
            System.out.println("----------------------------------------");

            // 2. Получаем отзывы
            List<Review> reviews = reviewService.getReviewsByLandmark(landmarkId);

            if (reviews.isEmpty()) {
                System.out.println("Отзывов об этой достопримечательности пока нет.");
            } else {
                System.out.println("Всего отзывов: " + reviews.size());

                int count = 1;
                for (Review review : reviews) {
                    System.out.println("\nОтзыв #" + count);
                    System.out.println("  Пользователь: " + review.getUsername());
                    System.out.println("  Оценка: " + review.getRating() + "/5");

                    String text = review.getText();
                    if (text != null && !text.trim().isEmpty()) {
                        System.out.println("  Текст: " + text);
                    } else {
                        System.out.println("  Без комментария");
                    }

                    System.out.println(" Дата: " + review.getCreatedAt());
                    System.out.println("----------------------------------------");
                    count++;
                }
            }

        } catch (Exception e) {
            System.out.println("Ошибка при получении отзывов: " + e.getMessage());
            e.printStackTrace();
        }
    }
}