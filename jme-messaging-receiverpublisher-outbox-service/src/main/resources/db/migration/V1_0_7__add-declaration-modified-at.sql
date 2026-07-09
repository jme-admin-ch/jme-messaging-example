ALTER TABLE declaration
    ADD COLUMN modified_at TIMESTAMP WITH TIME ZONE;

UPDATE declaration
    SET modified_at = created_at
    WHERE modified_at IS NULL;

ALTER TABLE declaration
    ALTER COLUMN modified_at SET NOT NULL;
