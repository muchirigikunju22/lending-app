package com.lending.app.loan.domain.repository;

import com.lending.app.loan.domain.model.Loan;
import com.lending.app.loan.domain.model.LoanState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    Optional<Loan> findByLoanNumber(String loanNumber);

    Optional<Loan> findByIdempotencyKey_Value(String idempotencyKey);

    List<Loan> findByCustomerId(Long customerId);

    List<Loan> findByState(LoanState state);

    List<Loan> findByCustomerIdAndState(Long customerId, LoanState state);

    List<Loan> findByCustomerIdAndStateIn(Long customerId, List<LoanState> states);

    long countByCustomerIdAndStateIn(Long customerId, List<LoanState> states);

    @Query("SELECT l FROM Loan l WHERE l.state = :state AND l.dueDate < :date")
    List<Loan> findByStateAndDueDateBefore(@Param("state") LoanState state, @Param("date") LocalDate date);

    @Query("SELECT l FROM Loan l WHERE l.state IN (:states) AND l.dueDate < :date")
    List<Loan> findByStateInAndDueDateBefore(@Param("states") List<LoanState> states, @Param("date") LocalDate date);
}
