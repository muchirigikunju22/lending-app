package com.lending.app.loan.service.schedule;

import com.lending.app.loan.domain.model.Installment;
import com.lending.app.loan.domain.model.Loan;

import java.util.List;

public interface LoanScheduleGenerator {

    List<Installment> generateSchedule(Loan loan);
}
