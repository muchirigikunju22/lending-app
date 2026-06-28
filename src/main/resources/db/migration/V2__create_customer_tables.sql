CREATE TABLE IF NOT EXISTS customer (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    national_id VARCHAR(50) UNIQUE,
    credit_score DECIMAL(5,2),
    date_of_birth DATE,
    address VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS loan_limit (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    customer_id BIGINT NOT NULL UNIQUE REFERENCES customer(id) ON DELETE CASCADE,
    max_single_loan DECIMAL(15,2) NOT NULL,
    max_total_outstanding DECIMAL(15,2) NOT NULL,
    max_active_loans INTEGER NOT NULL,
    last_reviewed TIMESTAMP
);

CREATE TABLE IF NOT EXISTS billing_profile (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    customer_id BIGINT NOT NULL UNIQUE REFERENCES customer(id) ON DELETE CASCADE,
    billing_day INTEGER NOT NULL DEFAULT 25
);

CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_customer_national_id ON customer(national_id);
CREATE INDEX idx_loan_limit_customer ON loan_limit(customer_id);
CREATE INDEX idx_billing_profile_customer ON billing_profile(customer_id);
