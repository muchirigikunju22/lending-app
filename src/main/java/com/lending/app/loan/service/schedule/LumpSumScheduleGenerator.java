package com.lending.app.loan.service.schedule;

import com.lending.app.loan.domain.model.Installment;
import com.lending.app.loan.domain.model.Loan;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LumpSumScheduleGenerator implements LoanScheduleGenerator {

    @Override
    public List<Installment> generateSchedule(Loan loan) {
        Installment installment = Installment.builder()
                .loan(loan)
                .installmentNumber(1)
                .amountDue(loan.getOutstandingBalance())
                .dueDate(loan.getDueDate())
                .build();

        return List.of(installment);
    }
}
