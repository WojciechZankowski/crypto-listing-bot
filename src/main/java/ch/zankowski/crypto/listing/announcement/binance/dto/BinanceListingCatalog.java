package ch.zankowski.crypto.listing.announcement.binance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceListingCatalog {

    private final Long catalogId;
    private final String catalogName;
    private final List<BinanceListingArticle> articles;

}

