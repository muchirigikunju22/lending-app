package com.lending.app.customer.service;

import com.lending.app.common.exception.DuplicateOperationException;
import com.lending.app.common.exception.ResourceNotFoundException;
import com.lending.app.customer.api.dto.*;
import com.lending.app.customer.domain.model.BillingProfile;
import com.lending.app.customer.domain.model.Customer;
import com.lending.app.customer.domain.model.LoanLimit;
import com.lending.app.customer.domain.repository.BillingProfileRepository;
import com.lending.app.customer.domain.repository.CustomerRepository;
import com.lending.app.customer.domain.repository.LoanLimitRepository;
import com.lending.app.customer.mapper.CustomerMapper;
import com.lending.app.loan.domain.model.Loan;
import com.lending.app.loan.domain.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final LoanLimitRepository loanLimitRepository;
    private final BillingProfileRepository billingProfileRepository;
    private final LoanRepository loanRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public Customer registerCustomer(CustomerRequest request) {
        log.info("Registering new customer: {} {}", request.firstName(), request.lastName());

        customerRepository.findByEmail(request.email()).ifPresent(c -> {
            throw new DuplicateOperationException("Customer with email " + request.email() + " already exists");
        });

        Customer customer = customerMapper.toEntity(request);

        LoanLimit defaultLimit = LoanLimit.builder()
                .customer(customer)
                .maxSingleLoan(request.maxSingleLoan() != null ? request.maxSingleLoan() : java.math.BigDecimal.valueOf(10000))
                .maxTotalOutstanding(request.maxTotalOutstanding() != null ? request.maxTotalOutstanding() : java.math.BigDecimal.valueOf(30000))
                .maxActiveLoans(request.maxActiveLoans() != null ? request.maxActiveLoans() : 3)
                .lastReviewed(Instant.now())
                .build();
        customer.setLoanLimit(defaultLimit);

        BillingProfile billingProfile = BillingProfile.builder()
                .customer(customer)
                .billingDay(25)
                .build();
        customer.setBillingProfile(billingProfile);

        return customerRepository.save(customer);
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer updateCustomer(Long id, CustomerRequest request) {
        log.info("Updating customer: {}", id);
        Customer customer = findById(id);
        customerMapper.updateEntityFromRequest(request, customer);
        return customerRepository.save(customer);
    }

    @Transactional
    public LoanLimit setLoanLimits(Long id, LoanLimitRequest request) {
        log.info("Setting loan limits for customer: {}", id);
        Customer customer = findById(id);

        LoanLimit limit = customer.getLoanLimit();
        if (limit == null) {
            limit = LoanLimit.builder().customer(customer).build();
            customer.setLoanLimit(limit);
        }

        limit.setMaxSingleLoan(request.maxSingleLoan());
        limit.setMaxTotalOutstanding(request.maxTotalOutstanding());
        limit.setMaxActiveLoans(request.maxActiveLoans());
        limit.setLastReviewed(Instant.now());

        loanLimitRepository.save(limit);
        return limit;
    }

    public LoanLimit getLoanLimit(Long id) {
        return loanLimitRepository.findByCustomerId(id)
                .orElseThrow(() -> new ResourceNotFoundException("LoanLimit", "customerId=" + id));
    }

    public BillingProfile getBillingProfile(Long id) {
        return billingProfileRepository.findByCustomerId(id)
                .orElseThrow(() -> new ResourceNotFoundException("BillingProfile", "customerId=" + id));
    }

    @Transactional
    public BillingProfile updateBillingProfile(Long id, BillingProfileRequest request) {
        log.info("Updating billing profile for customer: {}", id);
        BillingProfile profile = getBillingProfile(id);
        profile.setBillingDay(request.billingDay());
        return billingProfileRepository.save(profile);
    }

    public List<Loan> getCustomerLoans(Long id) {
        findById(id);
        return loanRepository.findByCustomerId(id);
    }
}
