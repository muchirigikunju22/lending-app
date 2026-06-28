package com.lending.app.loan.mapper;

import com.lending.app.loan.api.dto.*;
import com.lending.app.loan.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.fullName", target = "customerName")
    @Mapping(source = "loanProduct.id", target = "productId")
    @Mapping(source = "loanProduct.name", target = "productName")
    @Mapping(source = "idempotencyKey.value", target = "idempotencyKey")
    @Mapping(target = "installments", expression = "java(toInstallmentResponseList(loan.getInstallments()))")
    LoanResponse toResponse(Loan loan);

    List<LoanResponse> toResponseList(List<Loan> loans);

    @Mapping(source = "loan.id", target = "loanId")
    @Mapping(target = "totalInstallments", expression = "java(loan.getInstallments().size())")
    @Mapping(target = "paidInstallments", expression = "java((int) loan.getInstallments().stream().filter(i -> i.getStatus() == InstallmentStatus.PAID).count())")
    @Mapping(target = "overdueInstallments", expression = "java((int) loan.getInstallments().stream().filter(i -> i.getStatus() == InstallmentStatus.OVERDUE).count())")
    LoanSummaryResponse toSummary(Loan loan);

    @Mapping(target = "remainingAmount", expression = "java(installment.getRemainingAmount())")
    InstallmentResponse toInstallmentResponse(Installment installment);

    List<InstallmentResponse> toInstallmentResponseList(List<Installment> installments);

    @Mapping(source = "loan.id", target = "loanId")
    @Mapping(source = "installment.id", target = "installmentId")
    RepaymentResponse toRepaymentResponse(Repayment repayment);

    List<RepaymentResponse> toRepaymentResponseList(List<Repayment> repayments);

    List<LoanTimelineResponse> toTimelineResponseList(List<LoanEventHistory> events);
}
