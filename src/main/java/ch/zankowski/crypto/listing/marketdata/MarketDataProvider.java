package ch.zankowski.crypto.listing.marketdata;

import ch.zankowski.crypto.listing.marketdata.dto.Ticker;

import java.util.List;

public interface MarketDataProvider {

    Ticker getTicker(final String currencyPair);

    List<Ticker> getAllTickers();

}
