CREATE TABLE tb_failed_event (
    failed_event_id VARCHAR(50) PRIMARY KEY,
    topic VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL
);