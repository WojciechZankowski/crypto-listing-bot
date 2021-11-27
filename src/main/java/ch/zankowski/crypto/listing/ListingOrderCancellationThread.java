package ch.zankowski.crypto.listing;

import ch.zankowski.crypto.listing.exchange.ExchangeService;
import ch.zankowski.crypto.listing.marketdata.MarketDataProvider;
import ch.zankowski.crypto.listing.marketdata.dto.Ticker;
import ch.zankowski.crypto.listing.util.BigDecimals;
import io.gate.gateapi.models.Order;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.function.Consumer;

/**
 * Garbage, re-do if bot will make any money
 */
@Slf4j
public class ListingOrderCancellationThread implements Runnable {

    private static final BigDecimal TAKE_PROFIT_THRESHOLD = BigDecimal.valueOf(0.1);
    private static final BigDecimal STOP_LOSS_THRESHOLD = BigDecimal.valueOf(-0.03);

    private final ExchangeService exchangeService;
    private final MarketDataProvider marketDataProvider;
    private final Order order;

    private boolean keepRetrying = true;
    private int failureCount = 0;

    public ListingOrderCancellationThread(
            final ExchangeService exchangeService,
            final MarketDataProvider marketDataProvider,
            final Order order) {
        this.exchangeService = exchangeService;
        this.marketDataProvider = marketDataProvider;
        this.order = order;
    }

    @Override
    public void run() {
        log.info("Order cancellation thread starting " + order.getCurrencyPair() + "...");

        final Consumer<Ticker> tickerConsumer = ticker -> {

            try {
                final BigDecimal orderPrice = new BigDecimal(order.getPrice());
                final BigDecimal priceDifference = ticker.getLast().subtract(orderPrice);

                final BigDecimal profitAndLoss = BigDecimals.divide(orderPrice, priceDifference);

                log.info("Placing close order, order price: " + orderPrice
                        + ", market price: " + ticker.getLast() + ", profitAndLoss: " + profitAndLoss
                        + ", price difference: " + priceDifference);

                if (profitAndLoss.compareTo(TAKE_PROFIT_THRESHOLD) > 0) {
                    final Order placedOrder =
                            exchangeService.placeOrder(ListingOrderCreationHelper.createSellOrder(
                                    this.order.getCurrencyPair(),
                                    this.order.getPrice(),
                                    this.order.getAmount()));
                    log.warn("Order placed: " + placedOrder);

                    keepRetrying = false;

                } else if (profitAndLoss.compareTo(STOP_LOSS_THRESHOLD) < 0) {
                    final Order placedOrder =
                            exchangeService.placeOrder(ListingOrderCreationHelper.createSellOrder(
                                    this.order.getCurrencyPair(),
                                    orderPrice.add(priceDifference).toPlainString(),
                                    this.order.getAmount()));
                    log.warn("Order placed: " + placedOrder);

                    keepRetrying = false;

                } else {
                    log.info("Order not closed.");
                }

                failureCount = 0;
                // Random lol
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            } catch (final Exception e) {
                failureCount++;
                log.error("Order closure failed. " + e);
            }
        };

        while (keepRetrying && failureCount < 10) {
            try {
                marketDataProvider.getRealTimeTicker(order.getCurrencyPair())
                        .ifPresent(tickerConsumer);
            } catch (final Exception e) {
                failureCount++;
                log.error("Order closure failed. " + e);
            }
        }

        log.info("Order cancellation thread stopping " + order.getCurrencyPair() + "...");
    }


}
