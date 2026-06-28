package com.lending.app.loan.api.dto;

import com.lending.app.loan.domain.model.LoanState;

import java.math.BigDecimal;

public record LoanSummaryResponse(
        Long loanId,
        String loanNumber,
        LoanState state,
        BigDecimal principalAmount,
        BigDecimal outstandingBalance,
        BigDecimal totalRepaid,
        Integer totalInstallments,
        Integer paidInstallments,
        Integer overdueInstallments
) {
}
