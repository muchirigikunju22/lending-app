package com.lending.app.customer.domain.repository;

import com.lending.app.customer.domain.model.LoanLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanLimitRepository extends JpaRepository<LoanLimit, Long> {

    Optional<LoanLimit> findByCustomerId(Long customerId);
}
