package com.lending.app.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdempotencyKey {

    @Column(name = "idempotency_key", unique = true, length = 36)
    private String value;

    private IdempotencyKey(String value) {
        this.value = value;
    }

    public static IdempotencyKey generate() {
        return new IdempotencyKey(UUID.randomUUID().toString());
    }

    public static IdempotencyKey of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Idempotency key cannot be null or blank");
        }
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Idempotency key must be a valid UUID: " + value);
        }
        return new IdempotencyKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
