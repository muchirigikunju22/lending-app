package com.lending.app.loan.api;

import com.lending.app.loan.api.dto.*;
import com.lending.app.loan.domain.model.Repayment;
import com.lending.app.loan.mapper.LoanMapper;
import com.lending.app.loan.service.RepaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans/{loanId}/repayments")
@RequiredArgsConstructor
@Tag(name = "Repayments", description = "Loan repayment APIs")
public class RepaymentController {

    private final RepaymentService repaymentService;
    private final LoanMapper loanMapper;

    @PostMapping
    @Operation(summary = "Make a payment on a loan")
    public ResponseEntity<RepaymentResponse> makePayment(
            @PathVariable @Parameter(example = "2") Long loanId,
            @Valid @RequestBody RepaymentRequest request) {
        Repayment repayment;
        if (request.installmentId() != null) {
            repayment = repaymentService.makePaymentTowardInstallment(
                    loanId, request.installmentId(), request.amount(), request.paymentMethod());
        } else {
            repayment = repaymentService.makePayment(loanId, request.amount(), request.paymentMethod());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(loanMapper.toRepaymentResponse(repayment));
    }

    @GetMapping
    @Operation(summary = "Get payment history for a loan")
    public ResponseEntity<List<RepaymentResponse>> getRepayments(@PathVariable @Parameter(example = "2") Long loanId) {
        List<Repayment> repayments = repaymentService.getRepayments(loanId);
        return ResponseEntity.ok(loanMapper.toRepaymentResponseList(repayments));
    }
}
