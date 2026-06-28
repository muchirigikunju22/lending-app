-- Backfill null version columns caused by seed data inserts and set a default for future rows.
-- Hibernate's @Version optimistic-locking column must be non-null.

ALTER TABLE loan_product ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE fee_configuration ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE customer ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE loan_limit ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE billing_profile ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE notification_template ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE notification_preference ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE loan ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE installment ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE repayment ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE loan_event_history ALTER COLUMN version SET DEFAULT 0;

UPDATE loan_product SET version = 0 WHERE version IS NULL;
UPDATE fee_configuration SET version = 0 WHERE version IS NULL;
UPDATE customer SET version = 0 WHERE version IS NULL;
UPDATE loan_limit SET version = 0 WHERE version IS NULL;
UPDATE billing_profile SET version = 0 WHERE version IS NULL;
UPDATE notification_template SET version = 0 WHERE version IS NULL;
UPDATE notification_preference SET version = 0 WHERE version IS NULL;
UPDATE loan SET version = 0 WHERE version IS NULL;
UPDATE installment SET version = 0 WHERE version IS NULL;
UPDATE repayment SET version = 0 WHERE version IS NULL;
UPDATE loan_event_history SET version = 0 WHERE version IS NULL;
