package com.lending.app.product.service.fee;

import com.lending.app.product.domain.model.FeeConfiguration;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedFeeStrategy implements FeeCalculationStrategy {

    @Override
    public BigDecimal calculate(BigDecimal principal, FeeConfiguration config) {
        return config.getAmount();
    }
}
