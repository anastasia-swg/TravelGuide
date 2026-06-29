CREATE OR REPLACE FUNCTION get_landmarks_sorted_by_rating(
    p_limit INT DEFAULT 10,
    p_category VARCHAR DEFAULT NULL
)
RETURNS TABLE (
    id INT,
    name VARCHAR,
    city VARCHAR,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    avg_rating DOUBLE PRECISION,
    category VARCHAR
) AS $$
BEGIN
RETURN QUERY
SELECT
    l.id,
    l.name,
    l.city,
    l.latitude,
    l.longitude,
    l.avg_rating,
    l.category
FROM landmark l
WHERE
    (p_category IS NULL OR l.category = p_category)
ORDER BY l.avg_rating DESC
    LIMIT p_limit;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_landmarks_sorted_by_distance(
    p_user_lat DOUBLE PRECISION,
    p_user_lon DOUBLE PRECISION,
    p_radius_km DOUBLE PRECISION DEFAULT 1000,
    p_limit INT DEFAULT 10,
    p_category VARCHAR DEFAULT NULL
)
RETURNS TABLE (
    id INT,
    name VARCHAR,
    city VARCHAR,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    avg_rating DOUBLE PRECISION,
    category VARCHAR,
    distance_km DOUBLE PRECISION
) AS $$
BEGIN
RETURN QUERY
SELECT
    l.id,
    l.name,
    l.city,
    l.latitude,
    l.longitude,
    l.avg_rating,
    l.category,
    6371 * 2 * ASIN(
            SQRT(
                    POWER(SIN(RADIANS(l.latitude - p_user_lat) / 2), 2) +
                    COS(RADIANS(p_user_lat)) * COS(RADIANS(l.latitude)) *
                    POWER(SIN(RADIANS(l.longitude - p_user_lon) / 2), 2)
            )
               ) AS distance_km
FROM landmark l
WHERE
    (p_category IS NULL OR l.category = p_category)
  AND 6371 * 2 * ASIN(
        SQRT(
                POWER(SIN(RADIANS(l.latitude - p_user_lat) / 2), 2) +
                COS(RADIANS(p_user_lat)) * COS(RADIANS(l.latitude)) *
                POWER(SIN(RADIANS(l.longitude - p_user_lon) / 2), 2)
        )
                 ) <= p_radius_km
ORDER BY distance_km ASC
    LIMIT p_limit;
END;
$$ LANGUAGE plpgsql;