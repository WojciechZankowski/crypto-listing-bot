package ch.zankowski.crypto.listing;

import ch.zankowski.crypto.listing.dto.CryptoAnnouncement;
import ch.zankowski.crypto.listing.exchange.gate.GateExchangeService;
import ch.zankowski.crypto.listing.marketdata.MarketDataProvider;
import ch.zankowski.crypto.listing.marketdata.dto.Ticker;
import io.gate.gateapi.models.Order;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Set;

@Slf4j
@ApplicationScoped
public class ListingAnnouncementProcessor {

    private static final BigDecimal AMOUNT = BigDecimal.valueOf(100);
    private static final BigDecimal LIMIT_MULTIPLIER = BigDecimal.valueOf(1.3);
    private static final MathContext MATH_CONTEXT = new MathContext(2, RoundingMode.HALF_UP);

    @Inject
    GateExchangeService gateExchangeService;

    @Inject
    MarketDataProvider marketDataProvider;

    void onCryptoAnnounced(@Observes CryptoAnnouncement announcement) {
        log.info("Crypto announced " + announcement);

        final Set<String> supportedCurrencies = gateExchangeService.getSupportedCurrencies();
        log.info("New crypto supported " + supportedCurrencies.contains(announcement.getCryptoSymbol().getCrypto()));

        final String currencyPair = announcement.getCryptoSymbol().getCrypto() + "_USDT";

        final Order order = createOrder(marketDataProvider.getTicker(currencyPair));

        if (order != null) {
            gateExchangeService.placeOrder(order);
        }
    }

    private Order createOrder(final Ticker ticker) {
        if (ticker == null || ticker.getLast() == null) {
            return null;
        }

        final Order order = new Order();
        order.setCurrencyPair(ticker.getCurrencyPair());
        order.setPrice(calculateLimit(ticker.getLast()).toPlainString());
        order.setAmount(AMOUNT.toPlainString());
        order.setType(Order.TypeEnum.LIMIT);
        return order;
    }

    private BigDecimal calculateLimit(final BigDecimal price) {
        return price.multiply(LIMIT_MULTIPLIER, MATH_CONTEXT);
    }

}
