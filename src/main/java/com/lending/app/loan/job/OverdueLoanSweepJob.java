package com.lending.app.loan.job;

import com.lending.app.loan.domain.model.Loan;
import com.lending.app.loan.domain.model.LoanState;
import com.lending.app.loan.domain.repository.LoanRepository;
import com.lending.app.loan.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OverdueLoanSweepJob {

    private final LoanRepository loanRepository;
    private final LoanService loanService;

    @Scheduled(cron = "${lending.jobs.overdue-sweep:0 0 1 * * ?}")
    @Transactional
    public void sweepOverdueLoans() {
        log.info("Starting overdue loan sweep job");

        LocalDate today = LocalDate.now();
        List<Loan> overdueLoans = loanRepository.findByStateAndDueDateBefore(LoanState.OPEN, today);

        log.info("Found {} open loans past due date", overdueLoans.size());

        int processed = 0;
        for (Loan loan : overdueLoans) {
            try {
                loanService.markOverdue(loan.getId());
                processed++;
                log.debug("Marked loan {} as overdue", loan.getLoanNumber());
            } catch (Exception e) {
                log.error("Failed to mark loan {} as overdue: {}", loan.getLoanNumber(), e.getMessage());
            }
        }

        log.info("Overdue loan sweep completed. Processed: {}", processed);
    }
}
