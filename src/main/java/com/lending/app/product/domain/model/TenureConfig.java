package com.lending.app.product.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TenureConfig {

    @Column(name = "tenure_value", nullable = false)
    private Integer value;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenure_type", nullable = false, length = 10)
    private TenureType type;

    private TenureConfig(Integer value, TenureType type) {
        this.value = value;
        this.type = type;
    }

    public static TenureConfig of(Integer value, TenureType type) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Tenure value must be positive");
        }
        if (type == null) {
            throw new IllegalArgumentException("Tenure type cannot be null");
        }
        return new TenureConfig(value, type);
    }
}
