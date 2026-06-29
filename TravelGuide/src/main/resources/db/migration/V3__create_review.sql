CREATE TABLE reviews (
                        landmark_id INTEGER NOT NULL,
                        user_id INTEGER NOT NULL,
                        rating INTEGER NOT NULL,
                        text TEXT,
                        created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);