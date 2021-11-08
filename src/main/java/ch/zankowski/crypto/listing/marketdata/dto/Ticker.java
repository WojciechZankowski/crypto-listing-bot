package ch.zankowski.crypto.listing.marketdata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Data
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticker {

    private final String currencyPair;
    private final BigDecimal last;
    private final BigDecimal lowestAsk;
    private final BigDecimal highestBid;
    private final BigDecimal changePercentage;
    private final BigDecimal baseVolume;
    private final BigDecimal quoteVolume;
    private final BigDecimal high24h;
    private final BigDecimal low24h;

}
