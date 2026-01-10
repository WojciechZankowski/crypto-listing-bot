package ch.zankowski.crypto.listing;

import ch.zankowski.crypto.exchange.gate.GateExchangeService;
import ch.zankowski.crypto.listing.dto.CryptoAnnouncement;
import ch.zankowski.crypto.listing.util.BigDecimals;
import ch.zankowski.crypto.marketdata.dto.Ticker;
import ch.zankowski.crypto.marketdata.gate.GateMarketDataProvider;
import io.gate.gateapi.models.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ch.zankowski.crypto.listing.util.BigDecimals.isZero;

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
        try {
            log.info("Crypto announced: {}", announcement.getCryptoSymbol());

            final boolean isSupported = gateExchangeService.getSupportedCurrencies().contains(announcement.getCryptoSymbol().getCrypto());
            if (!isSupported) {
                log.warn("New crypto {} is not supported on Gate.io exchange", announcement.getCryptoSymbol().getCrypto());
                return;
            }

            final String currencyPair = announcement.getCryptoSymbol().getCrypto() + "_USDT";
            final Order draftOrder = createOrder(marketDataProvider.getTicker(currencyPair));

            if (draftOrder != null) {
                final Order placedOrder = gateExchangeService.placeOrder(draftOrder);

                if (placedOrder != null) {
                    THREAD_EXECUTOR_SERVICE.execute(new ListingOrderCancellationThread(gateExchangeService,
                            marketDataProvider, placedOrder));
                }

                log.info("Placed order: {}", placedOrder);
            }
        } catch (final Exception e) {
            log.error("Processing listing announcement failed for {}, error: {}", announcement.getCryptoSymbol(), String.valueOf(e));
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
