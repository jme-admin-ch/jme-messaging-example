CREATE TABLE  idempotent_processing
(
    idempotence_id         varchar                  NOT NULL,
    idempotence_id_context varchar                  NOT NULL,
    created_at             timestamp with time zone NOT NULL,
    CONSTRAINT pk_idempotent_processing PRIMARY KEY (idempotence_id, idempotence_id_context)
)
