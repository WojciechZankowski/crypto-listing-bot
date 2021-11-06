package ch.zankowski.crypto.listing.exchange.gate;

import ch.zankowski.crypto.listing.dto.CryptoSymbol;
import ch.zankowski.crypto.listing.exchange.ExchangeService;
import io.gate.gateapi.ApiClient;
import io.gate.gateapi.ApiException;
import io.gate.gateapi.Configuration;
import io.gate.gateapi.api.SpotApi;
import io.gate.gateapi.models.Currency;
import io.gate.gateapi.models.Order;
import io.gate.gateapi.models.Ticker;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class GateExchangeService implements ExchangeService {

    private static final Predicate<Currency> IS_VALID_CURRENCY = currency ->
            Boolean.FALSE.equals(currency.getDelisted()) &&
                    Boolean.FALSE.equals(currency.getWithdrawDisabled()) &&
                    Boolean.FALSE.equals(currency.getTradeDisabled()) &&
                    Boolean.FALSE.equals(currency.getDepositDisabled());

    private final Set<String> supportedCurrencies = ConcurrentHashMap.newKeySet();

    private final SpotApi apiInstance;

    @Inject
    GateExchangeConfig gateExchangeConfig;

    public GateExchangeService() {
        final ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api.gateio.ws/api/v4");
        apiInstance = new SpotApi(defaultClient);
    }

    @Scheduled(every = "1h", identity = "gate-io-supported-currnecies")
    synchronized void fetchSupportedCurrencies() {
        try {
            log.info("Gate IO supported currencies retrieval started");
            final Set<String> refreshedSupportedCurrencies = apiInstance.listCurrencies().stream()
                    .filter(IS_VALID_CURRENCY)
                    .map(Currency::getCurrency)
                    .collect(Collectors.toSet());

            final Set<String> remove = new HashSet<>(supportedCurrencies);
            remove.removeAll(refreshedSupportedCurrencies);
            log.info("Elements to remove : " + remove);

            supportedCurrencies.removeAll(remove);

            final Set<String> add = new HashSet<>(refreshedSupportedCurrencies);
            add.removeAll(supportedCurrencies);
            log.info("Elements to add : " + add);

            supportedCurrencies.addAll(add);

            log.info("Gate IO supported currencies finished: " + supportedCurrencies);

        } catch (final ApiException e) {
            log.error("Failed to refresh supported currencies");
        }
    }

    @Override
    public synchronized Set<String> getSupportedCurrencies() {
        return supportedCurrencies;
    }

    @Override
    public void placeOrder(final CryptoSymbol cryptoSymbol) {

        try {
            final List<Ticker> tickers = apiInstance.listTickers()
                    .currencyPair(cryptoSymbol.getCrypto() + "_USDT")
                    .execute();

            if (tickers == null || tickers.isEmpty()) {
                return;
            }

            final Order preparedOrder = tickers.stream().findFirst()
                    .map(ticker -> {
                        final Order order = new Order();
                        order.setPrice(ticker.getLast());
                        order.setType(Order.TypeEnum.LIMIT);
                        return order;
                    })
                    .orElse(null);

            log.info("Order prepared " + preparedOrder);

        } catch (final Exception e) {
            log.error("Failed to create order " + cryptoSymbol);
        }
    }

}
