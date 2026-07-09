CREATE SEQUENCE message_header_sequence START WITH 1 INCREMENT 50 CYCLE;

CREATE TABLE message_header
(
    id                   bigint not null
        constraint message_header_pkey primary key,
    buffered_message_id  bigint not null references buffered_message,
    header_name          text   not null,
    header_value         bytea  not null
);

CREATE INDEX message_header_buffered_message_id ON message_header (buffered_message_id);