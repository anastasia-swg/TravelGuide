CREATE TABLE landmark (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL UNIQUE,
                          city VARCHAR(100) NOT NULL,
                          latitude DOUBLE PRECISION NOT NULL,
                          longitude DOUBLE PRECISION NOT NULL,
                          avg_rating DOUBLE PRECISION DEFAULT 0,
                          category VARCHAR(50) DEFAULT 'Памятник'
);
