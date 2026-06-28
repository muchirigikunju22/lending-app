package com.lending.app.loan.domain.repository;

import com.lending.app.loan.domain.model.Installment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {

    List<Installment> findByLoanId(Long loanId);
}
