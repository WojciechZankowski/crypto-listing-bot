package ch.zankowski.crypto.listing.announcement.binance;

import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingArticle;
import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingCatalog;
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
                .catalogs(List.of(
                        BinanceListingCatalog.builder()
                                .catalogId(1L)
                                .catalogName("Announcements")
                                .articles(List.of(
                                        BinanceListingArticle.builder()
                                                .title("Binance Futures Will Launch Coin-Margined SAND Perpetual Contracts with Up to" +
                                                        " 20X Leverage")
                                                .build(),
                                        BinanceListingArticle.builder()
                                                .title("Binance Will List Moonriver (MOVR)")
                                                .build(),
                                        BinanceListingArticle.builder()
                                                .title("Binance Adds BETA & BNX on Isolated Margin, Stablecoins Annual Interest Rate " +
                                                        "Starts at 6.20%!")
                                                .build(),
                                        BinanceListingArticle.builder()
                                                .title("Introducing the FC Porto Fan Token (PORTO) Token Sale on Binance Launchpad!")
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<CryptoSymbol> symbols = BinanceAnnouncementParser.parse(data);

        assertThat(symbols).containsOnly(cryptoSymbol("MOVR"));
    }

    @Test
    void shouldSuccessfullyParseAnnouncementArticle() {
        final BinanceListingData data = BinanceListingData.builder()
                .catalogs(List.of(
                        BinanceListingCatalog.builder()
                                .catalogId(1L)
                                .catalogName("Announcements")
                                .articles(List.of(
                                        BinanceListingArticle.builder()
                                                .title("Binance Will List Rari Governance Token (RGT)")
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<CryptoSymbol> symbols = BinanceAnnouncementParser.parse(data);

        assertThat(symbols).containsOnly(cryptoSymbol("RGT"));
    }

    @Test
    void shouldSuccessfullyParseAnnouncementArticleWithoutTicketInBrackets() {
        final BinanceListingData data = BinanceListingData.builder()
                .catalogs(List.of(
                        BinanceListingCatalog.builder()
                                .catalogId(1L)
                                .catalogName("Announcements")
                                .articles(List.of(
                                        BinanceListingArticle.builder()
                                                .title("Binance Will List KGST & Enable Trading Bots Services on Binance Spot")
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<CryptoSymbol> symbols = BinanceAnnouncementParser.parse(data);

        assertThat(symbols).containsOnly(cryptoSymbol("KGST"));
    }

    @Test
    void shouldIgnoreNewlyAddedTradingPairsArticles() {
        final BinanceListingData data = BinanceListingData.builder()
                .catalogs(List.of(
                        BinanceListingCatalog.builder()
                                .catalogId(1L)
                                .catalogName("Announcements")
                                .articles(List.of(
                                        BinanceListingArticle.builder()
                                                .title("Binance Adds ALGO/RUB, AUD/USDC, LAZIO/BUSD, LUNA/BIDR, MANA/TRY, OXT/BUSD & " +
                                                        "SHIB/UAH Trading Pairs")
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<CryptoSymbol> symbols = BinanceAnnouncementParser.parse(data);

        assertThat(symbols).isEmpty();
    }

    @Test
    void shouldIgnoreAddedFuturesArticles() {
        final BinanceListingData data = BinanceListingData.builder()
                .catalogs(List.of(
                        BinanceListingCatalog.builder()
                                .catalogId(1L)
                                .catalogName("Announcements")
                                .articles(List.of(
                                        BinanceListingArticle.builder()
                                                .title("Binance Futures Will Launch Coin-Margined FTM Perpetual Contracts with Up to " +
                                                        "20X Leverage")
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<CryptoSymbol> symbols = BinanceAnnouncementParser.parse(data);

        assertThat(symbols).isEmpty();
    }

    @Test
    void shouldIgnoreMarginArticles() {
        final BinanceListingData data = BinanceListingData.builder()
                .catalogs(List.of(
                        BinanceListingCatalog.builder()
                                .catalogId(1L)
                                .catalogName("Announcements")
                                .articles(List.of(
                                        BinanceListingArticle.builder()
                                                .title("Binance Futures Will Launch USDT-Margined GALA Perpetual Contracts with Up to" +
                                                        " 25X Leverage")
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<CryptoSymbol> symbols = BinanceAnnouncementParser.parse(data);

        assertThat(symbols).isEmpty();
    }

    @Test
    void shouldIgnoreArticlesWithoutTitle() {
        final BinanceListingData data = BinanceListingData.builder()
                .catalogs(List.of(
                        BinanceListingCatalog.builder()
                                .catalogId(1L)
                                .catalogName("Announcements")
                                .articles(List.of(
                                        BinanceListingArticle.builder()
                                                .build()
                                ))
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
