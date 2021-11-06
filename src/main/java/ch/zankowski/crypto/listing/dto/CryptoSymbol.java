package ch.zankowski.crypto.listing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoSymbol {

    private final String crypto;
    private final CryptoExchange exchange;

}
