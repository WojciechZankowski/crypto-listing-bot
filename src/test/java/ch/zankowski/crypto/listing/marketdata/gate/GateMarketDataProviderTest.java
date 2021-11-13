package ch.zankowski.crypto.listing.marketdata.gate;

import ch.zankowski.crypto.listing.exchange.gate.GateExchangeClient;
import ch.zankowski.crypto.listing.marketdata.dto.Ticker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GateMarketDataProviderTest {

    private GateExchangeClient gateExchangeClient;
    private GateMarketDataProvider marketDataProvider;

    @BeforeEach
    void setUp() {
        gateExchangeClient = mock(GateExchangeClient.class);
        marketDataProvider = new GateMarketDataProvider(gateExchangeClient);
    }

    @Test
    void shouldSuccessfullyReturnTickerBasedOnCurrencyPair() {
        when(gateExchangeClient.listTickers()).thenReturn(List.of(
                createTicker("BTC_USDT", BigDecimal.TEN),
                createTicker("ADA_USDT", BigDecimal.valueOf(20)),
                createTicker("ETH_USDT", BigDecimal.valueOf(30))
        ));

        marketDataProvider.refreshMarketData();
        final Ticker ticker = marketDataProvider.getTicker("BTC_USDT");

        assertThat(ticker.getCurrencyPair()).isEqualTo("BTC_USDT");
        assertThat(ticker.getLast()).isEqualTo(BigDecimal.TEN);
        assertThat(ticker.getQuoteVolume()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void shouldSuccessfullyReturnAllTickers() {
        final Ticker bitcoin = createTicker("BTC_USDT", BigDecimal.TEN);
        final Ticker cardano = createTicker("ADA_USDT", BigDecimal.valueOf(20));
        final Ticker ethereum = createTicker("ETH_USDT", BigDecimal.valueOf(30));
        when(gateExchangeClient.listTickers()).thenReturn(List.of(bitcoin, cardano, ethereum));

        marketDataProvider.refreshMarketData();
        final List<Ticker> allTickers = marketDataProvider.getAllTickers();

        assertThat(allTickers).containsExactlyInAnyOrder(bitcoin, cardano, ethereum);
    }

    @Test
    void shouldHaveNoMarketDataAfterInitialization() {
        assertThat(marketDataProvider.getAllTickers()).isEmpty();
    }

    private Ticker createTicker(final String currencyPair, final BigDecimal lastPrice) {
        return Ticker.builder()
                .currencyPair(currencyPair)
                .quoteVolume(BigDecimal.valueOf(100))
                .last(lastPrice)
                .lowestAsk(BigDecimal.ONE)
                .highestBid(BigDecimal.ONE)
                .build();
    }
}
