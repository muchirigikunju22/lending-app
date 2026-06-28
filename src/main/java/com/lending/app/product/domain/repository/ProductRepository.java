package com.lending.app.product.domain.repository;

import com.lending.app.product.domain.model.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<LoanProduct, Long> {

    List<LoanProduct> findByActiveTrue();
}
