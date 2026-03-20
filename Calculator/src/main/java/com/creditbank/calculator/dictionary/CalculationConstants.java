package com.creditbank.calculator.dictionary;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CalculationConstants {
    public static final int FINAL_SCALE = 2;
    public static final int CALCULATION_SCALE = 10;
    public static final BigDecimal MONTHS_IN_YEAR_PERCENT = new BigDecimal("1200");
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;


    private CalculationConstants() {
    }
}
