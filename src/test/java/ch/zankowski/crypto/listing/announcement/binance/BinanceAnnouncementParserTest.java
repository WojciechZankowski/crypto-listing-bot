package ch.zankowski.crypto.listing.announcement.binance;

import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingArticle;
import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingData;
import ch.zankowski.crypto.listing.dto.CryptoExchange;
import ch.zankowski.crypto.listing.dto.CryptoSymbol;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BinanceAnnouncementParserTest {

    @Test
    void shouldSuccessfullyParseAnnouncementOutOfMultipleArticles() {
        final BinanceListingData data = BinanceListingData.builder()
                .articles(List.of(
                        BinanceListingArticle.builder()
                                .id(1L)
                                .code("3cc58db7ef894061b0e449e34b22265a")
                                .title("Binance Futures Will Launch Coin-Margined SAND Perpetual Contracts with Up to" +
                                        " 20X Leverage")
                                .build(),
                        BinanceListingArticle.builder()
                                .id(1L)
                                .code("cfef2fc1127d4a9fbeb8f29f4f223cf8")
                                .title("Binance Will List Moonriver (MOVR)")
                                .build(),
                        BinanceListingArticle.builder()
                                .id(1L)
                                .code("b9fbcf46da3a4d7f8f2d1bf1bd874665")
                                .title("Binance Adds BETA & BNX on Isolated Margin, Stablecoins Annual Interest Rate " +
                                        "Starts at 6.20%!")
                                .build(),
                        BinanceListingArticle.builder()
                                .id(1L)
                                .code("14e4354e11444c7092934e618f5eac64")
                                .title("Introducing the FC Porto Fan Token (PORTO) Token Sale on Binance Launchpad!")
                                .build()
                ))
                .build();

        final Set<CryptoSymbol> symbols = BinanceAnnouncementParser.parse(data);

        assertThat(symbols).containsOnly(cryptoSymbol("MOVR"));
    }

    @Test
    void shouldSuccessfullyParseAnnouncementArticle() {
        final BinanceListingData data = BinanceListingData.builder()
                .articles(List.of(
                        BinanceListingArticle.builder()
                                .id(1L)
                                .code("f9e1123ffc12458795716a0e027ff07a")
                                .title("Binance Will List Rari Governance Token (RGT)")
                                .build()
                ))
                .build();

        final Set<CryptoSymbol> symbols = BinanceAnnouncementParser.parse(data);

        assertThat(symbols).containsOnly(cryptoSymbol("RGT"));
    }

    @Test
    void shouldIgnoreNewlyAddedTradingPairsArticles() {
        final BinanceListingData data = BinanceListingData.builder()
                .articles(List.of(
                        BinanceListingArticle.builder()
                                .id(1L)
                                .code("5c649babec204c27b7d7b734dba937ee")
                                .title("Binance Adds ALGO/RUB, AUD/USDC, LAZIO/BUSD, LUNA/BIDR, MANA/TRY, OXT/BUSD & " +
                                        "SHIB/UAH Trading Pairs")
                                .build()
                ))
                .build();

        final Set<CryptoSymbol> symbols = BinanceAnnouncementParser.parse(data);

        assertThat(symbols).isEmpty();
    }

    @Test
    void shouldIgnoreAddedFuturesArticles() {
        final BinanceListingData data = BinanceListingData.builder()
                .articles(List.of(
                        BinanceListingArticle.builder()
                                .id(1L)
                                .code("8f89686731e04da3b9a98e20a0897413")
                                .title("Binance Futures Will Launch Coin-Margined FTM Perpetual Contracts with Up to " +
                                        "20X Leverage")
                                .build()
                ))
                .build();

        final Set<CryptoSymbol> symbols = BinanceAnnouncementParser.parse(data);

        assertThat(symbols).isEmpty();
    }

    @Test
    void shouldIgnoreMarginArticles() {
        final BinanceListingData data = BinanceListingData.builder()
                .articles(List.of(
                        BinanceListingArticle.builder()
                                .id(1L)
                                .code("8f89686731e04da3b9a98e20a0897413")
                                .title("Binance Futures Will Launch USDT-Margined GALA Perpetual Contracts with Up to" +
                                        " 25X Leverage")
                                .build()
                ))
                .build();

        final Set<CryptoSymbol> symbols = BinanceAnnouncementParser.parse(data);

        assertThat(symbols).isEmpty();
    }

    @Test
    void shouldIgnoreArticlesWithoutTitle() {
        final BinanceListingData data = BinanceListingData.builder()
                .articles(List.of(
                        BinanceListingArticle.builder()
                                .id(1L)
                                .code("8f89686731e04da3b9a98e20a0897413")
                                .build()
                ))
                .build();

        final Set<CryptoSymbol> symbols = BinanceAnnouncementParser.parse(data);

        assertThat(symbols).isEmpty();
    }

    @Test
    void shouldReturnEmptySetOfSymbolsIfDataIsNull() {
        assertThat(BinanceAnnouncementParser.parse((BinanceListingData) null)).isEmpty();
    }

    @Test
    void shouldReturnEmptySetOfSymbolsIfThereIsNoArticles() {
        assertThat(BinanceAnnouncementParser.parse(List.of())).isEmpty();
    }

    private CryptoSymbol cryptoSymbol(final String symbol) {
        return CryptoSymbol.builder()
                .crypto(symbol)
                .exchange(CryptoExchange.BINANCE)
                .build();
    }

}
