package com.lending.app.product.service.fee;

import com.lending.app.product.domain.model.FeeConfiguration;

import java.math.BigDecimal;

public interface FeeCalculationStrategy {

    BigDecimal calculate(BigDecimal principal, FeeConfiguration config);
}
