package com.lending.app.loan.domain.repository;

import com.lending.app.loan.domain.model.LoanEventHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanEventHistoryRepository extends JpaRepository<LoanEventHistory, Long> {

    List<LoanEventHistory> findByLoanIdOrderByOccurredAtDesc(Long loanId);
}
