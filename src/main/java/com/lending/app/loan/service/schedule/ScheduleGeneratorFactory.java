package com.lending.app.loan.service.schedule;

import com.lending.app.product.domain.model.LoanType;
import org.springframework.stereotype.Component;

@Component
public class ScheduleGeneratorFactory {

    private final LumpSumScheduleGenerator lumpSumScheduleGenerator;
    private final InstallmentScheduleGenerator installmentScheduleGenerator;

    public ScheduleGeneratorFactory(LumpSumScheduleGenerator lumpSumScheduleGenerator,
                                     InstallmentScheduleGenerator installmentScheduleGenerator) {
        this.lumpSumScheduleGenerator = lumpSumScheduleGenerator;
        this.installmentScheduleGenerator = installmentScheduleGenerator;
    }

    public LoanScheduleGenerator resolve(LoanType loanType) {
        return switch (loanType) {
            case LUMP_SUM -> lumpSumScheduleGenerator;
            case INSTALLMENT -> installmentScheduleGenerator;
        };
    }
}
