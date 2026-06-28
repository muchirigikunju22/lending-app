package com.lending.app.common.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoneyFromBigDecimal() {
        Money money = Money.of(new BigDecimal("100.50"));
        assertEquals(new BigDecimal("100.50"), money.getAmount());
    }

    @Test
    void shouldCreateZeroMoney() {
        Money money = Money.zero();
        assertEquals(new BigDecimal("0.00"), money.getAmount());
    }

    @Test
    void shouldAddMoney() {
        Money result = Money.of(new BigDecimal("100.00")).add(Money.of(new BigDecimal("50.00")));
        assertEquals(new BigDecimal("150.00"), result.getAmount());
    }

    @Test
    void shouldSubtractMoney() {
        Money result = Money.of(new BigDecimal("100.00")).subtract(Money.of(new BigDecimal("30.00")));
        assertEquals(new BigDecimal("70.00"), result.getAmount());
    }

    @Test
    void shouldMultiplyMoney() {
        Money result = Money.of(new BigDecimal("100.00")).multiply(new BigDecimal("1.5"));
        assertEquals(new BigDecimal("150.00"), result.getAmount());
    }

    @Test
    void shouldCalculatePercentage() {
        Money result = Money.of(new BigDecimal("1000.00")).percentage(new BigDecimal("15"));
        assertEquals(new BigDecimal("150.00"), result.getAmount());
    }

    @Test
    void shouldCompareGreaterThan() {
        assertTrue(Money.of(new BigDecimal("100.00")).isGreaterThan(Money.of(new BigDecimal("50.00"))));
        assertFalse(Money.of(new BigDecimal("50.00")).isGreaterThan(Money.of(new BigDecimal("100.00"))));
    }

    @Test
    void shouldCheckIsZero() {
        assertTrue(Money.zero().isZero());
        assertFalse(Money.of(BigDecimal.ONE).isZero());
    }

    @Test
    void shouldCheckIsPositive() {
        assertTrue(Money.of(BigDecimal.ONE).isPositive());
        assertFalse(Money.zero().isPositive());
    }

    @Test
    void shouldRejectNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(new BigDecimal("-100.00")));
    }

    @Test
    void shouldRejectSubtractionBelowZero() {
        assertThrows(IllegalArgumentException.class,
                () -> Money.of(new BigDecimal("50.00")).subtract(Money.of(new BigDecimal("100.00"))));
    }

    @Test
    void shouldRejectNullAmount() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(null));
    }

    @Test
    void shouldBeEqualByValue() {
        Money a = Money.of(new BigDecimal("100.00"));
        Money b = Money.of(new BigDecimal("100.00"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
