ALTER TABLE deferred_message
    ADD COLUMN trace_id_string varchar;

ALTER TABLE deferred_message
    ADD COLUMN trace_id_high bigint;
