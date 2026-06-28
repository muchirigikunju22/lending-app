package com.lending.app.customer.api;

import com.lending.app.customer.api.dto.*;
import com.lending.app.customer.domain.model.BillingProfile;
import com.lending.app.customer.domain.model.Customer;
import com.lending.app.customer.domain.model.LoanLimit;
import com.lending.app.customer.mapper.CustomerMapper;
import com.lending.app.customer.service.CustomerService;
import com.lending.app.loan.api.dto.LoanResponse;
import com.lending.app.loan.domain.model.Loan;
import com.lending.app.loan.mapper.LoanMapper;
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
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management APIs")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final LoanMapper loanMapper;

    @PostMapping
    @Operation(summary = "Register a new customer")
    public ResponseEntity<CustomerResponse> registerCustomer(@Valid @RequestBody CustomerRequest request) {
        Customer customer = customerService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerMapper.toResponse(customer));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer profile")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable @Parameter(example = "1") Long id) {
        Customer customer = customerService.findById(id);
        return ResponseEntity.ok(customerMapper.toResponse(customer));
    }

    @GetMapping
    @Operation(summary = "List all customers")
    public ResponseEntity<List<CustomerResponse>> listCustomers() {
        List<Customer> customers = customerService.findAll();
        return ResponseEntity.ok(customerMapper.toResponseList(customers));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable @Parameter(example = "1") Long id,
                                                            @Valid @RequestBody CustomerRequest request) {
        Customer customer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(customerMapper.toResponse(customer));
    }

    @PutMapping("/{id}/loan-limits")
    @Operation(summary = "Set loan limits for customer")
    public ResponseEntity<LoanLimitResponse> setLoanLimits(@PathVariable @Parameter(example = "1") Long id,
                                                            @Valid @RequestBody LoanLimitRequest request) {
        LoanLimit limit = customerService.setLoanLimits(id, request);
        return ResponseEntity.ok(customerMapper.toLoanLimitResponse(limit));
    }

    @GetMapping("/{id}/loan-limit")
    @Operation(summary = "Get customer loan limit")
    public ResponseEntity<LoanLimitResponse> getLoanLimit(@PathVariable @Parameter(example = "1") Long id) {
        LoanLimit limit = customerService.getLoanLimit(id);
        return ResponseEntity.ok(customerMapper.toLoanLimitResponse(limit));
    }

    @PutMapping("/{id}/billing-profile")
    @Operation(summary = "Update billing profile")
    public ResponseEntity<BillingProfileResponse> updateBillingProfile(@PathVariable @Parameter(example = "1") Long id,
                                                                        @Valid @RequestBody BillingProfileRequest request) {
        BillingProfile profile = customerService.updateBillingProfile(id, request);
        return ResponseEntity.ok(customerMapper.toBillingProfileResponse(profile));
    }

    @GetMapping("/{id}/loans")
    @Operation(summary = "Get customer's loans")
    public ResponseEntity<List<LoanResponse>> getCustomerLoans(@PathVariable @Parameter(example = "1") Long id) {
        List<Loan> loans = customerService.getCustomerLoans(id);
        return ResponseEntity.ok(loanMapper.toResponseList(loans));
    }
}
