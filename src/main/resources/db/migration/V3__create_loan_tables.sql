CREATE TABLE IF NOT EXISTS loan (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    loan_number VARCHAR(36) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES customer(id),
    product_id BIGINT NOT NULL REFERENCES loan_product(id),
    principal_amount DECIMAL(15,2) NOT NULL,
    outstanding_balance DECIMAL(15,2) NOT NULL,
    total_fees_accrued DECIMAL(15,2) DEFAULT 0,
    total_repaid DECIMAL(15,2) DEFAULT 0,
    state VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    billing_cycle VARCHAR(20) NOT NULL DEFAULT 'INDIVIDUAL',
    origination_date DATE,
    due_date DATE,
    idempotency_key VARCHAR(36) UNIQUE
);

CREATE TABLE IF NOT EXISTS installment (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    loan_id BIGINT NOT NULL REFERENCES loan(id) ON DELETE CASCADE,
    installment_number INTEGER NOT NULL,
    amount_due DECIMAL(15,2) NOT NULL,
    amount_paid DECIMAL(15,2) DEFAULT 0,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE IF NOT EXISTS repayment (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    loan_id BIGINT NOT NULL REFERENCES loan(id) ON DELETE CASCADE,
    installment_id BIGINT REFERENCES installment(id),
    amount DECIMAL(15,2) NOT NULL,
    payment_method VARCHAR(50),
    transaction_reference VARCHAR(100) UNIQUE,
    payment_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS loan_event_history (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    loan_id BIGINT NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    description VARCHAR(500),
    amount DECIMAL(15,2),
    previous_state VARCHAR(20),
    new_state VARCHAR(20),
    occurred_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_loan_customer ON loan(customer_id);
CREATE INDEX idx_loan_product ON loan(product_id);
CREATE INDEX idx_loan_state ON loan(state);
CREATE INDEX idx_loan_due_date ON loan(due_date);
CREATE INDEX idx_loan_number ON loan(loan_number);
CREATE INDEX idx_installment_loan ON installment(loan_id);
CREATE INDEX idx_repayment_loan ON repayment(loan_id);
CREATE INDEX idx_loan_event_history_loan ON loan_event_history(loan_id);
