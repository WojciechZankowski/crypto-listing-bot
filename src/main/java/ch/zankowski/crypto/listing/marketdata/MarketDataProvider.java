package ch.zankowski.crypto.listing.marketdata;

import ch.zankowski.crypto.listing.exchange.gate.GateExchangeService;
import ch.zankowski.crypto.listing.marketdata.dto.Ticker;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ApplicationScoped
public class MarketDataProvider {

    @Inject
    GateExchangeService gateExchangeService;

    private final Map<String, Ticker> marketData = new ConcurrentHashMap<>();

    @Scheduled(every = "10s", identity = "gate-io-market-data")
    void refreshMarketData() {
        log.info("Market Data refreshment started.");

        gateExchangeService.getAllTickers().forEach(ticker -> marketData.put(ticker.getCurrencyPair(), ticker));

        log.info("Market Data refreshment finished.");
    }

    public Ticker getTicker(final String currencyPair) {
        return marketData.get(currencyPair);
    }

    public List<Ticker> getAllTickers() {
        return new ArrayList<>(marketData.values());
    }

}
