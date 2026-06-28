package com.lending.app.product.service.fee;

import com.lending.app.product.domain.model.FeeConfiguration;
import com.lending.app.product.domain.model.FeeType;
import org.springframework.stereotype.Component;

@Component
public class FeeCalculationStrategyFactory {

    private final FixedFeeStrategy fixedFeeStrategy;
    private final PercentageFeeStrategy percentageFeeStrategy;
    private final DailyAccrualStrategy dailyAccrualStrategy;

    public FeeCalculationStrategyFactory(FixedFeeStrategy fixedFeeStrategy,
                                          PercentageFeeStrategy percentageFeeStrategy,
                                          DailyAccrualStrategy dailyAccrualStrategy) {
        this.fixedFeeStrategy = fixedFeeStrategy;
        this.percentageFeeStrategy = percentageFeeStrategy;
        this.dailyAccrualStrategy = dailyAccrualStrategy;
    }

    public FeeCalculationStrategy resolve(FeeConfiguration config) {
        if (config.getFeeType() == FeeType.DAILY) {
            return dailyAccrualStrategy;
        }
        return switch (config.getCalculationMethod()) {
            case FIXED -> fixedFeeStrategy;
            case PERCENTAGE -> percentageFeeStrategy;
        };
    }
}
