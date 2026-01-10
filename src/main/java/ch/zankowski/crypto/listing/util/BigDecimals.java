package ch.zankowski.crypto.listing.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigDecimals {

    private static final MathContext DIVISION_CONTEXT = new MathContext(4, RoundingMode.HALF_UP);

    private BigDecimals() {
        // static util
    }


    public static BigDecimal toBigDecimal(final String value) {
        try {
            return value == null ? null : new BigDecimal(value);
        } catch (final Exception e) {
            return null;
        }
    }

    public static BigDecimal divide(final BigDecimal dividend, final BigDecimal divisor) {
        return dividend.divide(divisor, DIVISION_CONTEXT);
    }

    public static boolean isZero(final BigDecimal value) {
        return value != null && BigDecimal.ZERO.compareTo(value) == 0;
    }

}
