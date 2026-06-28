-- Seed Loans in various states
INSERT INTO loan (version, loan_number, customer_id, product_id, principal_amount, outstanding_balance, total_fees_accrued, total_repaid, state, billing_cycle, origination_date, due_date, idempotency_key)
VALUES
    (0, 'L-OPEN-001', 1, 1, 1500.00, 1500.00, 25.00, 0, 'OPEN', 'INDIVIDUAL', CURRENT_DATE - 5, CURRENT_DATE + 25, 'idemp-001'),
    (0, 'L-OPEN-002', 2, 2, 5000.00, 3500.00, 100.00, 1500.00, 'OPEN', 'CONSOLIDATED', CURRENT_DATE - 60, CURRENT_DATE + 120, 'idemp-002'),
    (0, 'L-CLOSED-001', 3, 1, 1000.00, 0, 25.00, 1025.00, 'CLOSED', 'INDIVIDUAL', CURRENT_DATE - 45, CURRENT_DATE - 15, 'idemp-003'),
    (0, 'L-OVERDUE-001', 4, 3, 10000.00, 10500.00, 500.00, 0, 'OVERDUE', 'INDIVIDUAL', CURRENT_DATE - 15, CURRENT_DATE - 5, 'idemp-004'),
    (0, 'L-CANCELLED-001', 5, 1, 500.00, 0, 0, 0, 'CANCELLED', 'INDIVIDUAL', CURRENT_DATE - 10, CURRENT_DATE + 20, 'idemp-005'),
    (0, 'L-OPEN-003', 1, 3, 8000.00, 8000.00, 120.00, 0, 'OPEN', 'CONSOLIDATED', CURRENT_DATE - 2, CURRENT_DATE + 25, 'idemp-006'),
    (0, 'L-OVERDUE-002', 2, 1, 2000.00, 2150.00, 150.00, 0, 'OVERDUE', 'INDIVIDUAL', CURRENT_DATE - 40, CURRENT_DATE - 10, 'idemp-007'),
    (0, 'L-CLOSED-002', 3, 2, 3000.00, 0, 60.00, 3060.00, 'CLOSED', 'INDIVIDUAL', CURRENT_DATE - 200, CURRENT_DATE - 20, 'idemp-008');

-- Seed Installments for installment-based loans (product_id 2 and 3)
-- Loan 2 (INSTALLMENT, 6 months)
INSERT INTO installment (version, loan_id, installment_number, amount_due, amount_paid, due_date, status)
VALUES
    (0, 2, 1, 833.33, 833.33, CURRENT_DATE - 60, 'PAID'),
    (0, 2, 2, 833.33, 833.33, CURRENT_DATE - 30, 'PAID'),
    (0, 2, 3, 833.34, 0, CURRENT_DATE, 'PENDING'),
    (0, 2, 4, 833.33, 0, CURRENT_DATE + 30, 'PENDING'),
    (0, 2, 5, 833.33, 0, CURRENT_DATE + 60, 'PENDING'),
    (0, 2, 6, 833.34, 0, CURRENT_DATE + 90, 'PENDING');

-- Loan 8 (INSTALLMENT, 6 months, CLOSED)
INSERT INTO installment (version, loan_id, installment_number, amount_due, amount_paid, due_date, status)
VALUES
    (0, 8, 1, 500.00, 500.00, CURRENT_DATE - 200, 'PAID'),
    (0, 8, 2, 500.00, 500.00, CURRENT_DATE - 170, 'PAID'),
    (0, 8, 3, 500.00, 500.00, CURRENT_DATE - 140, 'PAID'),
    (0, 8, 4, 500.00, 500.00, CURRENT_DATE - 110, 'PAID'),
    (0, 8, 5, 500.00, 500.00, CURRENT_DATE - 80, 'PAID'),
    (0, 8, 6, 560.00, 560.00, CURRENT_DATE - 50, 'PAID');

-- Loan 4 (INSTALLMENT, 12 months, OVERDUE)
INSERT INTO installment (version, loan_id, installment_number, amount_due, amount_paid, due_date, status)
VALUES
    (0, 4, 1, 833.33, 0, CURRENT_DATE - 15, 'OVERDUE'),
    (0, 4, 2, 833.33, 0, CURRENT_DATE + 15, 'PENDING'),
    (0, 4, 3, 833.34, 0, CURRENT_DATE + 45, 'PENDING'),
    (0, 4, 4, 833.33, 0, CURRENT_DATE + 75, 'PENDING'),
    (0, 4, 5, 833.33, 0, CURRENT_DATE + 105, 'PENDING'),
    (0, 4, 6, 833.34, 0, CURRENT_DATE + 135, 'PENDING'),
    (0, 4, 7, 833.33, 0, CURRENT_DATE + 165, 'PENDING'),
    (0, 4, 8, 833.33, 0, CURRENT_DATE + 195, 'PENDING'),
    (0, 4, 9, 833.34, 0, CURRENT_DATE + 225, 'PENDING'),
    (0, 4, 10, 833.33, 0, CURRENT_DATE + 255, 'PENDING'),
    (0, 4, 11, 833.33, 0, CURRENT_DATE + 285, 'PENDING'),
    (0, 4, 12, 833.34, 0, CURRENT_DATE + 315, 'PENDING');

-- Lump sum loans get 1 installment
INSERT INTO installment (version, loan_id, installment_number, amount_due, amount_paid, due_date, status)
VALUES
    (0, 1, 1, 1525.00, 0, CURRENT_DATE + 25, 'PENDING'),
    (0, 3, 1, 1025.00, 1025.00, CURRENT_DATE - 15, 'PAID'),
    (0, 5, 1, 500.00, 0, CURRENT_DATE + 20, 'PENDING'),
    (0, 6, 1, 8120.00, 0, CURRENT_DATE + 25, 'PENDING'),
    (0, 7, 1, 2150.00, 0, CURRENT_DATE - 10, 'OVERDUE');

-- Seed Repayments
INSERT INTO repayment (version, loan_id, installment_id, amount, payment_method, transaction_reference, payment_date)
VALUES
    (0, 2, 1, 833.33, 'BANK_TRANSFER', 'TXN-001', CURRENT_DATE - 60),
    (0, 2, 2, 833.33, 'BANK_TRANSFER', 'TXN-002', CURRENT_DATE - 30),
    (0, 8, 1, 500.00, 'CARD', 'TXN-003', CURRENT_DATE - 200),
    (0, 8, 2, 500.00, 'CARD', 'TXN-004', CURRENT_DATE - 170),
    (0, 8, 3, 500.00, 'CARD', 'TXN-005', CURRENT_DATE - 140),
    (0, 8, 4, 500.00, 'CARD', 'TXN-006', CURRENT_DATE - 110),
    (0, 8, 5, 500.00, 'CARD', 'TXN-007', CURRENT_DATE - 80),
    (0, 8, 6, 560.00, 'CARD', 'TXN-008', CURRENT_DATE - 50),
    (0, 3, 26, 1025.00, 'BANK_TRANSFER', 'TXN-009', CURRENT_DATE - 15);

-- Seed Loan Event History for first loan
INSERT INTO loan_event_history (version, loan_id, event_type, description, amount, new_state, occurred_at)
VALUES
    (0, 1, 'LOAN_CREATED', 'Loan created with principal: 1500.00', 1500.00, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    (0, 1, 'LOAN_DISBURSED', 'Loan disbursed, installments generated: 1', 1500.00, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    (0, 2, 'LOAN_CREATED', 'Loan created with principal: 5000.00', 5000.00, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '60 days'),
    (0, 2, 'LOAN_DISBURSED', 'Loan disbursed, installments generated: 6', 5000.00, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '60 days'),
    (0, 2, 'PAYMENT_RECEIVED', 'Payment received: 833.33', 833.33, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '60 days'),
    (0, 2, 'PAYMENT_RECEIVED', 'Payment received: 833.33', 833.33, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '30 days'),
    (0, 3, 'LOAN_CREATED', 'Loan created with principal: 1000.00', 1000.00, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '45 days'),
    (0, 3, 'LOAN_DISBURSED', 'Loan disbursed, installments generated: 1', 1000.00, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '45 days'),
    (0, 3, 'PAYMENT_RECEIVED', 'Payment received: 1025.00', 1025.00, 'CLOSED', CURRENT_TIMESTAMP - INTERVAL '15 days'),
    (0, 3, 'LOAN_CLOSED', 'Loan fully repaid and closed', 1025.00, 'CLOSED', CURRENT_TIMESTAMP - INTERVAL '15 days'),
    (0, 4, 'LOAN_CREATED', 'Loan created with principal: 10000.00', 10000.00, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '15 days'),
    (0, 4, 'LOAN_DISBURSED', 'Loan disbursed, installments generated: 12', 10000.00, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '15 days'),
    (0, 5, 'LOAN_CREATED', 'Loan created with principal: 500.00', 500.00, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '10 days'),
    (0, 5, 'LOAN_CANCELLED', 'Loan cancelled. Reason: Customer request', NULL, 'CANCELLED', CURRENT_TIMESTAMP - INTERVAL '8 days'),
    (0, 7, 'LOAN_CREATED', 'Loan created with principal: 2000.00', 2000.00, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '40 days'),
    (0, 7, 'LOAN_DISBURSED', 'Loan disbursed, installments generated: 1', 2000.00, 'OPEN', CURRENT_TIMESTAMP - INTERVAL '40 days'),
    (0, 7, 'LOAN_OVERDUE', 'Loan marked overdue', 2150.00, 'OVERDUE', CURRENT_TIMESTAMP - INTERVAL '5 days');
