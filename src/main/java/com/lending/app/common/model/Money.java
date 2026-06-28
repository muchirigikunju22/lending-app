package com.lending.app.common.model;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Money {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private BigDecimal amount;

    private Money(BigDecimal amount) {
        this.amount = amount.setScale(SCALE, ROUNDING_MODE);
    }

    public static Money of(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative: " + amount);
        }
        return new Money(amount);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public Money add(Money other) {
        if (other == null) return this;
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        if (other == null) return this;
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Subtraction would result in negative amount");
        }
        return new Money(result);
    }

    public Money multiply(BigDecimal multiplier) {
        if (multiplier == null) {
            throw new IllegalArgumentException("Multiplier cannot be null");
        }
        return new Money(this.amount.multiply(multiplier));
    }

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public Money percentage(BigDecimal percent) {
        return new Money(this.amount.multiply(percent).divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE));
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
