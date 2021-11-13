package ch.zankowski.crypto.listing.util;

import java.math.BigDecimal;

public class BigDecimals {

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

}
