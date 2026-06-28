CREATE TABLE IF NOT EXISTS notification (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    customer_id BIGINT NOT NULL,
    loan_id BIGINT,
    channel VARCHAR(20) NOT NULL,
    subject VARCHAR(200),
    body VARCHAR(2000),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    event_type VARCHAR(50),
    sent_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notification_template (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    event_type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    subject_template VARCHAR(200),
    body_template VARCHAR(2000) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS notification_preference (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    customer_id BIGINT NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    channel VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_notification_customer ON notification(customer_id);
CREATE UNIQUE INDEX idx_notification_template_event_channel 
    ON notification_template(event_type, channel) WHERE active = TRUE;
CREATE INDEX idx_notification_preference_customer ON notification_preference(customer_id);
