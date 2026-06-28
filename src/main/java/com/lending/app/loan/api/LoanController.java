package com.lending.app.loan.api;

import com.lending.app.loan.api.dto.*;
import com.lending.app.loan.domain.model.*;
import com.lending.app.loan.mapper.LoanMapper;
import com.lending.app.loan.service.LoanService;
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
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Loan management and lifecycle APIs")
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;

    @PostMapping
    @Operation(summary = "Create a new loan")
    public ResponseEntity<LoanResponse> createLoan(
            @Valid @RequestBody LoanRequest request,
            @RequestHeader(name = "Idempotency-Key", required = false)
            @Parameter(description = "Idempotency key to prevent duplicate loan creation", example = "idemp-demo-001") String idempotencyKey) {
        Loan loan = loanService.createLoan(
                request.customerId(),
                request.productId(),
                request.principalAmount(),
                request.billingCycle(),
                idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanMapper.toResponse(loan));
    }

    @PostMapping("/{id}/disburse")
    @Operation(summary = "Disburse a loan and generate installment schedule")
    public ResponseEntity<LoanResponse> disburseLoan(@PathVariable @Parameter(example = "1") Long id) {
        Loan loan = loanService.disburseLoan(id);
        return ResponseEntity.ok(loanMapper.toResponse(loan));
    }

    @GetMapping
    @Operation(summary = "List all loans")
    public ResponseEntity<List<LoanResponse>> listLoans() {
        List<Loan> loans = loanService.findAll();
        return ResponseEntity.ok(loanMapper.toResponseList(loans));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan details")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable @Parameter(example = "1") Long id) {
        Loan loan = loanService.findById(id);
        return ResponseEntity.ok(loanMapper.toResponse(loan));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a loan")
    public ResponseEntity<LoanResponse> cancelLoan(
            @PathVariable @Parameter(example = "5") Long id,
            @RequestParam(defaultValue = "Customer request") String reason) {
        Loan loan = loanService.cancelLoan(id, reason);
        return ResponseEntity.ok(loanMapper.toResponse(loan));
    }

    @PostMapping("/{id}/write-off")
    @Operation(summary = "Write off an overdue loan")
    public ResponseEntity<LoanResponse> writeOffLoan(@PathVariable @Parameter(example = "4") Long id) {
        Loan loan = loanService.writeOffLoan(id);
        return ResponseEntity.ok(loanMapper.toResponse(loan));
    }

    @GetMapping("/{id}/installments")
    @Operation(summary = "Get loan installment schedule")
    public ResponseEntity<List<InstallmentResponse>> getInstallments(@PathVariable @Parameter(example = "2") Long id) {
        List<Installment> installments = loanService.getInstallments(id);
        return ResponseEntity.ok(loanMapper.toInstallmentResponseList(installments));
    }

    @GetMapping("/{id}/timeline")
    @Operation(summary = "Get loan timeline / audit history")
    public ResponseEntity<List<LoanTimelineResponse>> getLoanTimeline(@PathVariable @Parameter(example = "1") Long id) {
        List<LoanEventHistory> events = loanService.getLoanTimeline(id);
        return ResponseEntity.ok(loanMapper.toTimelineResponseList(events));
    }

    @GetMapping("/{id}/summary")
    @Operation(summary = "Get loan summary")
    public ResponseEntity<LoanSummaryResponse> getLoanSummary(@PathVariable @Parameter(example = "1") Long id) {
        Loan loan = loanService.findById(id);
        return ResponseEntity.ok(loanMapper.toSummary(loan));
    }
}
