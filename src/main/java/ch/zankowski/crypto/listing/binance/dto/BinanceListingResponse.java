package ch.zankowski.crypto.listing.binance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceListingResponse {

    private final String code;
    private final String message;
    private final String messageDetail;
    private final BinanceListingData data;

}
