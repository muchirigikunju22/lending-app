package com.lending.app.product.service.fee;

import com.lending.app.product.domain.model.FeeConfiguration;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DailyAccrualStrategy implements FeeCalculationStrategy {

    @Override
    public BigDecimal calculate(BigDecimal principal, FeeConfiguration config) {
        return principal
                .multiply(config.getAmount())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
