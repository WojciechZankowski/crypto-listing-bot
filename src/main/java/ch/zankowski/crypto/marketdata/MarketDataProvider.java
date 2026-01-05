package ch.zankowski.crypto.marketdata;

import ch.zankowski.crypto.marketdata.dto.Ticker;

import java.util.List;
import java.util.Optional;

public interface MarketDataProvider {

    Optional<Ticker> getRealTimeTicker(final String currencyPair);

    Ticker getTicker(final String currencyPair);

    List<Ticker> getAllTickers();

}
