package ch.zankowski.crypto.listing.marketdata.gate;

import ch.zankowski.crypto.listing.exchange.gate.GateExchangeClient;
import ch.zankowski.crypto.listing.marketdata.MarketDataProvider;
import ch.zankowski.crypto.listing.marketdata.dto.Ticker;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ApplicationScoped
public class GateMarketDataProvider implements MarketDataProvider {

    private final Map<String, Ticker> marketData = new ConcurrentHashMap<>();

    private final GateExchangeClient gateExchangeClient;

    @Inject
    public GateMarketDataProvider(final GateExchangeClient gateExchangeClient) {
        this.gateExchangeClient = gateExchangeClient;
    }

    @Scheduled(every = "10s", identity = "gate-io-market-data")
    void refreshMarketData() {
        log.info("Market Data refreshment started.");
        gateExchangeClient.listTickers().forEach(ticker -> marketData.put(ticker.getCurrencyPair(), ticker));
        log.info("Market Data refreshment finished.");
    }

    public Ticker getTicker(final String currencyPair) {
        return marketData.get(currencyPair);
    }

    @Override
    public Optional<Ticker> getRealTimeTicker(final String currencyPair) {
        return gateExchangeClient.listTicker(currencyPair);
    }

    public List<Ticker> getAllTickers() {
        return new ArrayList<>(marketData.values());
    }

}
