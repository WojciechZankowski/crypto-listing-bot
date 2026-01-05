package ch.zankowski.crypto.listing;

import ch.zankowski.crypto.listing.dto.CryptoAnnouncement;
import ch.zankowski.crypto.listing.exchange.gate.GateExchangeService;
import ch.zankowski.crypto.listing.marketdata.gate.GateMarketDataProvider;
import ch.zankowski.crypto.listing.marketdata.dto.Ticker;
import ch.zankowski.crypto.listing.util.BigDecimals;
import io.gate.gateapi.models.Order;
import lombok.extern.slf4j.Slf4j;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@ApplicationScoped
public class ListingAnnouncementProcessor {

    private static final BigDecimal AMOUNT = BigDecimal.valueOf(40);
    private static final BigDecimal LIMIT_MULTIPLIER = BigDecimal.valueOf(1.2);
    private static final MathContext MATH_CONTEXT = new MathContext(2, RoundingMode.HALF_UP);

    private static final ExecutorService THREAD_EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);

    private final GateExchangeService gateExchangeService;
    private final GateMarketDataProvider marketDataProvider;

    @Inject
    public ListingAnnouncementProcessor(final GateExchangeService gateExchangeService,
                                        final GateMarketDataProvider marketDataProvider) {
        this.gateExchangeService = gateExchangeService;
        this.marketDataProvider = marketDataProvider;
    }

    void onCryptoAnnounced(@Observes CryptoAnnouncement announcement) {
        log.info("Crypto announced " + announcement);

        final Set<String> supportedCurrencies = gateExchangeService.getSupportedCurrencies();
        log.info("New crypto supported " + supportedCurrencies.contains(announcement.getCryptoSymbol().getCrypto()));

        final String currencyPair = announcement.getCryptoSymbol().getCrypto() + "_USDT";
        final Order order = createOrder(marketDataProvider.getTicker(currencyPair));

        if (order != null) {
            final Order placedOrder = gateExchangeService.placeOrder(order);

            if (placedOrder != null) {
                THREAD_EXECUTOR_SERVICE.execute(new ListingOrderCancellationThread(gateExchangeService,
                        marketDataProvider, placedOrder));
            }
        }
    }

    private Order createOrder(final Ticker ticker) {
        if (ticker == null || ticker.getLast() == null) {
            return null;
        }

        return ListingOrderCreationHelper.createBuyOrder(
                ticker.getCurrencyPair(),
                calculateLimit(ticker.getLast()),
                calculateOrderAmount(ticker.getLast()));
    }

    private BigDecimal calculateLimit(final BigDecimal price) {
        return price.multiply(LIMIT_MULTIPLIER, MATH_CONTEXT);
    }

    private BigDecimal calculateOrderAmount(final BigDecimal price) {
        return BigDecimals.divide(AMOUNT, price);
    }

}
