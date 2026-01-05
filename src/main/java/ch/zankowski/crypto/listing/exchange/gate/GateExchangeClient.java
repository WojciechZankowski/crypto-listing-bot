package ch.zankowski.crypto.listing.exchange.gate;

import io.gate.gateapi.ApiClient;
import io.gate.gateapi.ApiException;
import io.gate.gateapi.api.SpotApi;
import io.gate.gateapi.models.Currency;
import io.gate.gateapi.models.Order;
import io.gate.gateapi.models.Ticker;
import lombok.extern.slf4j.Slf4j;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.zankowski.crypto.listing.util.BigDecimals.toBigDecimal;

@Slf4j
@ApplicationScoped
public class GateExchangeClient {

    private final SpotApi apiInstance;

    @Inject
    public GateExchangeClient(final GateExchangeConfig gateExchangeConfig) {
        final ApiClient defaultClient = new ApiClient(gateExchangeConfig.key(), gateExchangeConfig.secret());
        defaultClient.setBasePath("https://api.gateio.ws/api/v4");
        apiInstance = new SpotApi(defaultClient);
    }

    public List<Currency> listCurrencies() throws ApiException {
        return apiInstance.listCurrencies();
    }

    public Optional<ch.zankowski.crypto.listing.marketdata.dto.Ticker> listTicker(final String currencyPair) {
        try {
            return apiInstance.listTickers().currencyPair(currencyPair).execute().stream()
                    .map(this::map)
                    .findFirst();
        } catch (final ApiException e) {
            log.error("Failed to retrieve tickers");
            return Optional.empty();
        }
    }

    public List<ch.zankowski.crypto.listing.marketdata.dto.Ticker> listTickers() {
        try {
            return apiInstance.listTickers().execute().stream()
                    .map(this::map)
                    .collect(Collectors.toList());
        } catch (final ApiException e) {
            log.error("Failed to retrieve tickers");
            return List.of();
        }
    }

    private ch.zankowski.crypto.listing.marketdata.dto.Ticker map(final Ticker ticker) {
        return ch.zankowski.crypto.listing.marketdata.dto.Ticker.builder()
                .currencyPair(ticker.getCurrencyPair())
                .changePercentage(toBigDecimal(ticker.getChangePercentage()))
                .high24h(toBigDecimal(ticker.getHigh24h()))
                .low24h(toBigDecimal(ticker.getLow24h()))
                .highestBid(toBigDecimal(ticker.getHighestBid()))
                .lowestAsk(toBigDecimal(ticker.getLowestAsk()))
                .baseVolume(toBigDecimal(ticker.getBaseVolume()))
                .quoteVolume(toBigDecimal(ticker.getQuoteVolume()))
                .last(toBigDecimal(ticker.getLast()))
                .build();
    }

    public Order placeOrder(final Order order) throws ApiException {
        return apiInstance.createOrder(order, null);
    }

}
