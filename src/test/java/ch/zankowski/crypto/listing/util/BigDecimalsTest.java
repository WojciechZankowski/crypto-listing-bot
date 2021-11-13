package ch.zankowski.crypto.listing.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BigDecimalsTest {

    @Test
    void shouldSuccessfullyCreateBigDecimalFromString() {
        assertThat(BigDecimals.toBigDecimal("100.00")).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(BigDecimals.toBigDecimal(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenThereIsBigDecimalParsingError() {
        assertThat(BigDecimals.toBigDecimal("NOT_A_NUMBER")).isNull();
    }

}
