package com.lending.app.loan.service.statemachine;

import com.lending.app.common.exception.BusinessRuleException;
import com.lending.app.loan.domain.model.Loan;
import com.lending.app.loan.domain.model.LoanState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class LoanStateMachine {

    private final Map<LoanState, Set<LoanState>> validTransitions = new EnumMap<>(LoanState.class);

    public LoanStateMachine() {
        validTransitions.put(LoanState.OPEN, Set.of(LoanState.CLOSED, LoanState.CANCELLED, LoanState.OVERDUE));
        validTransitions.put(LoanState.OVERDUE, Set.of(LoanState.OPEN, LoanState.CLOSED, LoanState.WRITTEN_OFF));
        validTransitions.put(LoanState.CLOSED, Set.of());
        validTransitions.put(LoanState.CANCELLED, Set.of());
        validTransitions.put(LoanState.WRITTEN_OFF, Set.of());
    }

    public void transition(Loan loan, LoanState newState) {
        LoanState currentState = loan.getState();

        if (!canTransition(currentState, newState)) {
            throw new BusinessRuleException(
                    String.format("Invalid state transition from %s to %s", currentState, newState));
        }

        validateGuardConditions(loan, currentState, newState);

        log.info("Transitioning loan {} from {} to {}", loan.getLoanNumber(), currentState, newState);
        loan.setState(newState);
    }

    public boolean canTransition(LoanState from, LoanState to) {
        Set<LoanState> allowed = validTransitions.get(from);
        return allowed != null && allowed.contains(to);
    }

    private void validateGuardConditions(Loan loan, LoanState current, LoanState target) {
        switch (target) {
            case CLOSED -> {
                if (loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
                    throw new BusinessRuleException(
                            "Cannot close loan with outstanding balance: " + loan.getOutstandingBalance());
                }
            }
            case CANCELLED -> {
                if (loan.getTotalRepaid().compareTo(BigDecimal.ZERO) > 0) {
                    throw new BusinessRuleException("Cannot cancel loan that has received payments");
                }
            }
            case WRITTEN_OFF -> {
                if (current != LoanState.OVERDUE) {
                    throw new BusinessRuleException("Can only write off overdue loans");
                }
            }
        }
    }

    public boolean isTerminalState(LoanState state) {
        return state == LoanState.CLOSED || state == LoanState.CANCELLED || state == LoanState.WRITTEN_OFF;
    }
}
