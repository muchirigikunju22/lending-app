package com.lending.app.customer.domain.model;

import com.lending.app.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "customer")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "national_id", length = 50, unique = true)
    private String nationalId;

    @Column(name = "credit_score", precision = 5, scale = 2)
    private BigDecimal creditScore;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private LoanLimit loanLimit;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private BillingProfile billingProfile;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}
