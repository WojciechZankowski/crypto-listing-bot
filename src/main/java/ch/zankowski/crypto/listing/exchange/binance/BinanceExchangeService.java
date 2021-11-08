package ch.zankowski.crypto.listing.exchange.binance;

import ch.zankowski.crypto.listing.exchange.ExchangeService;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.general.SymbolInfo;
import io.gate.gateapi.models.Order;
import io.quarkus.cache.CacheResult;

import javax.enterprise.context.ApplicationScoped;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class BinanceExchangeService implements ExchangeService {

    private final BinanceApiRestClient binanceApiRestClient;

    public BinanceExchangeService() {
        binanceApiRestClient = BinanceApiClientFactory.newInstance().newRestClient();
    }

    @CacheResult(cacheName = "binance-currencies", lockTimeout = 1800000L)
    @Override
    public Set<String> getSupportedCurrencies() {
        return binanceApiRestClient.getExchangeInfo().getSymbols().stream()
                .map(SymbolInfo::getSymbol)
                .collect(Collectors.toSet());
    }

    @Override
    public void placeOrder(final Order order) {
        throw new UnsupportedOperationException();
    }
}
