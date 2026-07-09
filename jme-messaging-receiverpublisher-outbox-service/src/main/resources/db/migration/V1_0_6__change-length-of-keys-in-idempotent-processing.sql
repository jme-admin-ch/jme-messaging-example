-- Restrict the sizes of the idempotent_processing table's composite primary key fields.
-- This change is required because AWS Database Migration Service does not support unbounded data types as a primary key
-- for data validation. Additionally, AWS DMS requires the length of primary key VARCHAR columns to be less than 1024.
ALTER TABLE idempotent_processing
    ALTER COLUMN idempotence_id TYPE VARCHAR(200);

ALTER TABLE idempotent_processing
    ALTER COLUMN idempotence_id_context TYPE VARCHAR(200);
