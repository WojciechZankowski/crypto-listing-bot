package ch.zankowski.crypto.listing.marketdata;

import ch.zankowski.crypto.listing.marketdata.dto.Ticker;

import java.util.List;
import java.util.Optional;

public interface MarketDataProvider {

    Optional<Ticker> getRealTimeTicker(final String currencyPair);

    Ticker getTicker(final String currencyPair);

    List<Ticker> getAllTickers();

}
