package com.lending.app.customer.domain.repository;

import com.lending.app.customer.domain.model.BillingProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillingProfileRepository extends JpaRepository<BillingProfile, Long> {

    Optional<BillingProfile> findByCustomerId(Long customerId);
}
