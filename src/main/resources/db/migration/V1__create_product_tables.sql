CREATE TABLE IF NOT EXISTS loan_product (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    tenure_value INTEGER NOT NULL,
    tenure_type VARCHAR(10) NOT NULL,
    min_amount DECIMAL(15,2) NOT NULL,
    max_amount DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    loan_type VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS fee_configuration (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    product_id BIGINT NOT NULL REFERENCES loan_product(id) ON DELETE CASCADE,
    fee_type VARCHAR(20) NOT NULL,
    calculation_method VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    days_after_due INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_fee_config_product ON fee_configuration(product_id);
