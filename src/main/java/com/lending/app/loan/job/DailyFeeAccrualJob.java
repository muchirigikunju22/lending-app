package com.lending.app.loan.job;

import com.lending.app.loan.domain.model.*;
import com.lending.app.loan.domain.repository.LoanEventHistoryRepository;
import com.lending.app.loan.domain.repository.LoanRepository;
import com.lending.app.product.domain.model.FeeConfiguration;
import com.lending.app.product.domain.model.FeeType;
import com.lending.app.product.service.fee.FeeCalculationStrategy;
import com.lending.app.product.service.fee.FeeCalculationStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyFeeAccrualJob {

    private final LoanRepository loanRepository;
    private final LoanEventHistoryRepository eventHistoryRepository;
    private final FeeCalculationStrategyFactory strategyFactory;

    @Scheduled(cron = "${lending.jobs.fee-accrual:0 30 1 * * ?}")
    @Transactional
    public void accrueDailyFees() {
        log.info("Starting daily fee accrual job");

        List<Loan> activeLoans = loanRepository.findByStateInAndDueDateBefore(
                List.of(LoanState.OPEN, LoanState.OVERDUE),
                java.time.LocalDate.now().plusDays(1));

        int processed = 0;
        BigDecimal totalFeesAccrued = BigDecimal.ZERO;

        for (Loan loan : activeLoans) {
            try {
                List<FeeConfiguration> dailyFees = loan.getLoanProduct().getFeeConfigurations().stream()
                        .filter(f -> f.getFeeType() == FeeType.DAILY && Boolean.TRUE.equals(f.getActive()))
                        .toList();

                for (FeeConfiguration feeConfig : dailyFees) {
                    FeeCalculationStrategy strategy = strategyFactory.resolve(feeConfig);
                    BigDecimal feeAmount = strategy.calculate(loan.getOutstandingBalance(), feeConfig);

                    if (feeAmount.compareTo(BigDecimal.ZERO) > 0) {
                        loan.accrueFees(feeAmount);
                        totalFeesAccrued = totalFeesAccrued.add(feeAmount);

                        recordFeeAccrual(loan, feeAmount, feeConfig);
                        processed++;
                    }
                }

            } catch (Exception e) {
                log.error("Failed to accrue fees for loan {}: {}", loan.getLoanNumber(), e.getMessage());
            }
        }

        log.info("Daily fee accrual completed. Processed: {} loans, Total fees: {}", processed, totalFeesAccrued);
    }

    private void recordFeeAccrual(Loan loan, BigDecimal feeAmount, FeeConfiguration feeConfig) {
        LoanEventHistory event = LoanEventHistory.builder()
                .loanId(loan.getId())
                .eventType(LoanEventHistory.LoanEventType.FEE_ACCRUED)
                .description(String.format("Daily %s fee accrued: %s", feeConfig.getFeeType(), feeAmount))
                .amount(feeAmount)
                .newState(loan.getState().name())
                .occurredAt(Instant.now())
                .build();
        eventHistoryRepository.save(event);
    }
}
