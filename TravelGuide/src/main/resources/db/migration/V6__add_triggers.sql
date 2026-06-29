CREATE OR REPLACE FUNCTION update_avg_rating()
RETURNS TRIGGER AS $$
BEGIN
UPDATE landmark
SET avg_rating = (SELECT COALESCE(AVG(rating), 0) FROM reviews WHERE landmark_id = NEW.landmark_id)
WHERE id = NEW.landmark_id;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER recalc_avg_rating
    AFTER INSERT OR UPDATE OR DELETE ON reviews
    FOR EACH ROW EXECUTE FUNCTION update_avg_rating();