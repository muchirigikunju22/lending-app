package com.lending.app.loan.service.schedule;

import com.lending.app.loan.domain.model.Installment;
import com.lending.app.loan.domain.model.Loan;
import com.lending.app.product.domain.model.TenureConfig;
import com.lending.app.product.domain.model.TenureType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class InstallmentScheduleGenerator implements LoanScheduleGenerator {

    @Override
    public List<Installment> generateSchedule(Loan loan) {
        TenureConfig tenureConfig = loan.getLoanProduct().getTenureConfig();
        int numberOfInstallments = tenureConfig.getValue();

        if (tenureConfig.getType() == TenureType.MONTHS) {
            return generateMonthlySchedule(loan, numberOfInstallments);
        } else {
            return generateDailySchedule(loan, numberOfInstallments);
        }
    }

    private List<Installment> generateMonthlySchedule(Loan loan, int months) {
        List<Installment> installments = new ArrayList<>();
        BigDecimal totalAmount = loan.getOutstandingBalance();
        BigDecimal installmentAmount = totalAmount.divide(
                BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);

        LocalDate startDate = loan.getOriginationDate() != null
                ? loan.getOriginationDate()
                : LocalDate.now();

        BigDecimal remainder = totalAmount.subtract(
                installmentAmount.multiply(BigDecimal.valueOf(months)));

        for (int i = 1; i <= months; i++) {
            BigDecimal amount = installmentAmount;
            if (i == months) {
                amount = amount.add(remainder);
            }

            Installment installment = Installment.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .amountDue(amount)
                    .dueDate(startDate.plusMonths(i))
                    .build();

            installments.add(installment);
        }

        return installments;
    }

    private List<Installment> generateDailySchedule(Loan loan, int days) {
        List<Installment> installments = new ArrayList<>();
        BigDecimal totalAmount = loan.getOutstandingBalance();

        LocalDate startDate = loan.getOriginationDate() != null
                ? loan.getOriginationDate()
                : LocalDate.now();

        BigDecimal dailyAmount = totalAmount.divide(
                BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);
        BigDecimal remainder = totalAmount.subtract(
                dailyAmount.multiply(BigDecimal.valueOf(days)));

        for (int i = 1; i <= days; i++) {
            BigDecimal amount = dailyAmount;
            if (i == days) {
                amount = amount.add(remainder);
            }

            Installment installment = Installment.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .amountDue(amount)
                    .dueDate(startDate.plusDays(i))
                    .build();

            installments.add(installment);
        }

        return installments;
    }
}
