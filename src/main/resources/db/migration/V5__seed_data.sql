-- Seed Loan Products
INSERT INTO loan_product (version, name, description, tenure_value, tenure_type, min_amount, max_amount, interest_rate, loan_type, active)
VALUES
    (0, '30-Day Payday Loan', 'Short-term payday loan for immediate cash needs', 30, 'DAYS', 100, 2000, 15.00, 'LUMP_SUM', TRUE),
    (0, '6-Month Installment Loan', 'Medium-term loan with monthly payments', 6, 'MONTHS', 500, 10000, 12.00, 'INSTALLMENT', TRUE),
    (0, '12-Month Personal Loan', 'Long-term personal loan for larger expenses', 12, 'MONTHS', 1000, 50000, 10.00, 'INSTALLMENT', TRUE);

-- Seed Fee Configurations
INSERT INTO fee_configuration (version, product_id, fee_type, calculation_method, amount, days_after_due, active)
VALUES
    (0, 1, 'SERVICE', 'FIXED', 25.00, NULL, TRUE),
    (0, 1, 'LATE', 'PERCENTAGE', 5.00, 1, TRUE),
    (0, 1, 'DAILY', 'PERCENTAGE', 0.50, NULL, TRUE),
    (0, 2, 'SERVICE', 'PERCENTAGE', 2.00, NULL, TRUE),
    (0, 2, 'LATE', 'PERCENTAGE', 3.00, 3, TRUE),
    (0, 3, 'SERVICE', 'PERCENTAGE', 1.50, NULL, TRUE),
    (0, 3, 'LATE', 'PERCENTAGE', 2.50, 5, TRUE),
    (0, 3, 'DAILY', 'PERCENTAGE', 0.25, NULL, TRUE);

-- Seed Customers
INSERT INTO customer (version, first_name, last_name, email, phone, national_id, credit_score, date_of_birth, address, active)
VALUES
    (0, 'James', 'Mwangi', 'james.mwangi@email.com', '0720334412', '41212234', 750.00, '1985-03-15', '123 Kimathi St, Nairobi', TRUE),
    (0, 'Victor', 'Odhiambo', 'victor.odhiambo@email.com', '0734212333', '39283746', 820.00, '1990-07-22', '456 Moi Ave, Mombasa', TRUE),
    (0, 'John', 'Kamau', 'john.kamau@email.com', '0712345678', '28475639', 680.00, '1988-11-05', '789 Oginga Odinga St, Kisumu', TRUE),
    (0, 'Paul', 'Ochieng', 'paul.ochieng@email.com', '0722987654', '56342189', 910.00, '1992-01-30', '321 Kenyatta Ave, Nakuru', TRUE),
    (0, 'Peter', 'Kiptoo', 'peter.kiptoo@email.com', '0701122334', '71829384', 590.00, '1978-09-12', '654 Uganda Rd, Eldoret', TRUE);

-- Seed Loan Limits
INSERT INTO loan_limit (version, customer_id, max_single_loan, max_total_outstanding, max_active_loans, last_reviewed)
VALUES
    (0, 1, 5000.00, 15000.00, 3, CURRENT_TIMESTAMP),
    (0, 2, 8000.00, 25000.00, 4, CURRENT_TIMESTAMP),
    (0, 3, 3000.00, 9000.00, 2, CURRENT_TIMESTAMP),
    (0, 4, 12000.00, 40000.00, 5, CURRENT_TIMESTAMP),
    (0, 5, 2000.00, 5000.00, 1, CURRENT_TIMESTAMP);

-- Seed Billing Profiles
INSERT INTO billing_profile (version, customer_id, billing_day)
VALUES
    (0, 1, 25),
    (0, 2, 15),
    (0, 3, 1),
    (0, 4, 20),
    (0, 5, 10);

-- Seed Notification Preferences
INSERT INTO notification_preference (version, customer_id, channel, enabled)
SELECT 0, id, 'EMAIL', TRUE FROM customer
UNION ALL
SELECT 0, id, 'SMS', TRUE FROM customer
UNION ALL
SELECT 0, id, 'PUSH', FALSE FROM customer;

-- Seed Notification Templates
INSERT INTO notification_template (version, event_type, channel, subject_template, body_template, active)
VALUES
    (0, 'LOAN_CREATED', 'EMAIL', 'Loan Application Received', 'Dear Customer, Your loan application for {{amount}} has been received. Loan ID: {{loanId}}', TRUE),
    (0, 'LOAN_CREATED', 'SMS', NULL, 'Your loan application for {{amount}} has been received. Loan ID: {{loanId}}', TRUE),
    (0, 'LOAN_DISBURSED', 'EMAIL', 'Loan Disbursed', 'Dear Customer, Your loan {{loanId}} for {{amount}} has been disbursed. Due date: {{dueDate}}', TRUE),
    (0, 'LOAN_DISBURSED', 'SMS', NULL, 'Loan {{loanId}} for {{amount}} disbursed. Due: {{dueDate}}', TRUE),
    (0, 'PAYMENT_RECEIVED', 'EMAIL', 'Payment Received', 'Thank you for your payment of {{amount}} on loan {{loanId}}. Remaining balance: {{remainingBalance}}', TRUE),
    (0, 'PAYMENT_RECEIVED', 'SMS', NULL, 'Payment of {{amount}} received for loan {{loanId}}. Balance: {{remainingBalance}}', TRUE),
    (0, 'LOAN_OVERDUE', 'EMAIL', 'Loan Payment Overdue', 'Your loan {{loanId}} is overdue. Outstanding balance: {{outstandingBalance}}. Days overdue: {{daysOverdue}}', TRUE),
    (0, 'LOAN_OVERDUE', 'SMS', NULL, 'URGENT: Loan {{loanId}} overdue by {{daysOverdue}} days. Balance: {{outstandingBalance}}', TRUE),
    (0, 'LOAN_CLOSED', 'EMAIL', 'Loan Fully Repaid', 'Congratulations! Loan {{loanId}} has been fully repaid. Total paid: {{totalPaid}}', TRUE),
    (0, 'LOAN_CLOSED', 'SMS', NULL, 'Loan {{loanId}} fully repaid. Total: {{totalPaid}}', TRUE),
    (0, 'LOAN_CANCELLED', 'EMAIL', 'Loan Cancelled', 'Loan {{loanId}} has been cancelled. Reason: {{reason}}', TRUE),
    (0, 'LOAN_CANCELLED', 'SMS', NULL, 'Loan {{loanId}} cancelled. Reason: {{reason}}', TRUE);
