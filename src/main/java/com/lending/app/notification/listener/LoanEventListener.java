package com.lending.app.notification.listener;

import com.lending.app.common.event.*;
import com.lending.app.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleLoanCreated(LoanCreatedEvent event) {
        log.info("Handling loan created event for loan: {}", event.getLoanId());

        Map<String, String> variables = new HashMap<>();
        variables.put("loanId", event.getLoanId().toString());
        variables.put("amount", event.getPrincipalAmount().toString());

        notificationService.processEventForAllChannels(
                event.getCustomerId(), event.getLoanId(), "LOAN_CREATED", variables);
    }

    @Async
    @EventListener
    public void handleLoanDisbursed(LoanDisbursedEvent event) {
        log.info("Handling loan disbursed event for loan: {}", event.getLoanId());

        Map<String, String> variables = new HashMap<>();
        variables.put("loanId", event.getLoanId().toString());
        variables.put("amount", event.getDisbursedAmount().toString());
        variables.put("dueDate", event.getDueDate().toString());

        notificationService.processEventForAllChannels(
                event.getCustomerId(), event.getLoanId(), "LOAN_DISBURSED", variables);
    }

    @Async
    @EventListener
    public void handlePaymentReceived(PaymentReceivedEvent event) {
        log.info("Handling payment received event for loan: {}", event.getLoanId());

        Map<String, String> variables = new HashMap<>();
        variables.put("loanId", event.getLoanId().toString());
        variables.put("amount", event.getAmount().toString());
        variables.put("remainingBalance", event.getRemainingBalance().toString());

        notificationService.processEventForAllChannels(
                event.getCustomerId(), event.getLoanId(), "PAYMENT_RECEIVED", variables);
    }

    @Async
    @EventListener
    public void handleLoanOverdue(LoanOverdueEvent event) {
        log.info("Handling loan overdue event for loan: {}", event.getLoanId());

        Map<String, String> variables = new HashMap<>();
        variables.put("loanId", event.getLoanId().toString());
        variables.put("outstandingBalance", event.getOutstandingBalance().toString());
        variables.put("dueDate", event.getDueDate().toString());
        variables.put("daysOverdue", event.getDaysOverdue().toString());

        notificationService.processEventForAllChannels(
                event.getCustomerId(), event.getLoanId(), "LOAN_OVERDUE", variables);
    }

    @Async
    @EventListener
    public void handleLoanClosed(LoanClosedEvent event) {
        log.info("Handling loan closed event for loan: {}", event.getLoanId());

        Map<String, String> variables = new HashMap<>();
        variables.put("loanId", event.getLoanId().toString());
        variables.put("totalPaid", event.getTotalPaid().toString());

        notificationService.processEventForAllChannels(
                event.getCustomerId(), event.getLoanId(), "LOAN_CLOSED", variables);
    }

    @Async
    @EventListener
    public void handleLoanCancelled(LoanCancelledEvent event) {
        log.info("Handling loan cancelled event for loan: {}", event.getLoanId());

        Map<String, String> variables = new HashMap<>();
        variables.put("loanId", event.getLoanId().toString());
        variables.put("reason", event.getReason());

        notificationService.processEventForAllChannels(
                event.getCustomerId(), event.getLoanId(), "LOAN_CANCELLED", variables);
    }
}
