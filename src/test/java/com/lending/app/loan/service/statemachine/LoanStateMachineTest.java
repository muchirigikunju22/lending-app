package com.lending.app.loan.service.statemachine;

import com.lending.app.common.exception.BusinessRuleException;
import com.lending.app.loan.domain.model.Loan;
import com.lending.app.loan.domain.model.LoanState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoanStateMachineTest {

    private LoanStateMachine stateMachine;

    @BeforeEach
    void setUp() {
        stateMachine = new LoanStateMachine();
    }

    @Test
    void shouldTransitionFromOpenToOverdue() {
        Loan loan = createLoan(LoanState.OPEN, new BigDecimal("1000.00"));

        stateMachine.transition(loan, LoanState.OVERDUE);

        assertEquals(LoanState.OVERDUE, loan.getState());
    }

    @Test
    void shouldTransitionFromOpenToClosedWhenFullyPaid() {
        Loan loan = createLoan(LoanState.OPEN, BigDecimal.ZERO);

        stateMachine.transition(loan, LoanState.CLOSED);

        assertEquals(LoanState.CLOSED, loan.getState());
    }

    @Test
    void shouldTransitionFromOpenToCancelled() {
        Loan loan = createLoan(LoanState.OPEN, new BigDecimal("1000.00"));
        loan.setTotalRepaid(BigDecimal.ZERO);

        stateMachine.transition(loan, LoanState.CANCELLED);

        assertEquals(LoanState.CANCELLED, loan.getState());
    }

    @Test
    void shouldTransitionFromOverdueToOpen() {
        Loan loan = createLoan(LoanState.OVERDUE, new BigDecimal("500.00"));

        stateMachine.transition(loan, LoanState.OPEN);

        assertEquals(LoanState.OPEN, loan.getState());
    }

    @Test
    void shouldTransitionFromOverdueToClosedWhenFullyPaid() {
        Loan loan = createLoan(LoanState.OVERDUE, BigDecimal.ZERO);

        stateMachine.transition(loan, LoanState.CLOSED);

        assertEquals(LoanState.CLOSED, loan.getState());
    }

    @Test
    void shouldTransitionFromOverdueToWrittenOff() {
        Loan loan = createLoan(LoanState.OVERDUE, new BigDecimal("1000.00"));

        stateMachine.transition(loan, LoanState.WRITTEN_OFF);

        assertEquals(LoanState.WRITTEN_OFF, loan.getState());
    }

    @Test
    void shouldNotTransitionFromOpenToWrittenOff() {
        Loan loan = createLoan(LoanState.OPEN, new BigDecimal("1000.00"));

        assertThrows(BusinessRuleException.class,
                () -> stateMachine.transition(loan, LoanState.WRITTEN_OFF));
    }

    @Test
    void shouldNotTransitionFromClosedToAnyState() {
        Loan loan = createLoan(LoanState.CLOSED, BigDecimal.ZERO);

        assertThrows(BusinessRuleException.class,
                () -> stateMachine.transition(loan, LoanState.OPEN));
    }

    @Test
    void shouldNotCloseLoanWithOutstandingBalance() {
        Loan loan = createLoan(LoanState.OPEN, new BigDecimal("100.00"));

        assertThrows(BusinessRuleException.class,
                () -> stateMachine.transition(loan, LoanState.CLOSED));
    }

    @Test
    void shouldNotCancelLoanWithPayments() {
        Loan loan = createLoan(LoanState.OPEN, new BigDecimal("500.00"));
        loan.setTotalRepaid(new BigDecimal("100.00"));

        assertThrows(BusinessRuleException.class,
                () -> stateMachine.transition(loan, LoanState.CANCELLED));
    }

    @Test
    void shouldIdentifyTerminalStates() {
        assertTrue(stateMachine.isTerminalState(LoanState.CLOSED));
        assertTrue(stateMachine.isTerminalState(LoanState.CANCELLED));
        assertTrue(stateMachine.isTerminalState(LoanState.WRITTEN_OFF));
        assertFalse(stateMachine.isTerminalState(LoanState.OPEN));
        assertFalse(stateMachine.isTerminalState(LoanState.OVERDUE));
    }

    private Loan createLoan(LoanState state, BigDecimal outstandingBalance) {
        Loan loan = new Loan();
        loan.setState(state);
        loan.setOutstandingBalance(outstandingBalance);
        loan.setLoanNumber("TEST-001");
        loan.setTotalRepaid(BigDecimal.ZERO);
        return loan;
    }
}
