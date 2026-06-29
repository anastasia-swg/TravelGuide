package org.example.travelguide;

import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class TravelGuideApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelGuideApplication.class, args);
    }

    @Bean
    public ApplicationRunner runner(DataSource dataSource) {
        return args -> {
            // Проверка, видит ли Flyway миграции
            try {
                var flyway = Flyway.configure()
                        .dataSource(dataSource)
                        .load();
                var info = flyway.info();
                System.out.println("FLYWAY INFO!");
                System.out.println("wait migrations: " + info.pending().length);
                for (var migration : info.pending()) {
                    System.out.println(" - " + migration.getVersion() + ": " + migration.getDescription());
                };
            } catch (Exception e) {
                System.err.println("Flyway error: " + e.getMessage());
            }
        };
    }
}
