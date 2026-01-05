package ch.zankowski.crypto.exchange.gate;

import ch.zankowski.crypto.exchange.ExchangeService;
import io.gate.gateapi.ApiException;
import io.gate.gateapi.models.Currency;
import io.gate.gateapi.models.Order;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashSet;
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

    private final GateExchangeClient gateExchangeClient;

    @Inject
    public GateExchangeService(final GateExchangeClient gateExchangeClient) {
        this.gateExchangeClient = gateExchangeClient;
    }

    @Scheduled(every = "1h", identity = "gate-io-supported-currnecies")
    synchronized void fetchSupportedCurrencies() {
        try {
            log.info("Gate IO supported currencies retrieval started");
            final Set<String> refreshedSupportedCurrencies = gateExchangeClient.listCurrencies().stream()
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
    public Order placeOrder(final Order order) {
        try {
            log.info("Order prepared " + order);
            return gateExchangeClient.placeOrder(order);
        } catch (final Exception e) {
            log.error("Failed to create order " + order);
            return null;
        }
    }

}
