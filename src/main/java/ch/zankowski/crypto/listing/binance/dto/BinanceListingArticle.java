package ch.zankowski.crypto.listing.binance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceListingArticle {

    private final Long id;
    private final String code;
    private final String title;
    private final String body;
    private final String type;
    private final Long catalogId;
    private final String catalogName;
    private final String publishDate;

}
