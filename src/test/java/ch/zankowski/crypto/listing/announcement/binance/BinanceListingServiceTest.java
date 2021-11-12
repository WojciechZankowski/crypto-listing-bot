package ch.zankowski.crypto.listing.announcement.binance;

import ch.zankowski.crypto.listing.announcement.binance.client.BinanceListingAnnouncementClient;
import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingArticle;
import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingData;
import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingResponse;
import ch.zankowski.crypto.listing.dto.CryptoAnnouncement;
import ch.zankowski.crypto.listing.dto.CryptoExchange;
import ch.zankowski.crypto.listing.dto.CryptoSymbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.quartz.Scheduler;

import javax.enterprise.event.Event;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BinanceListingServiceTest {

    private BinanceListingService binanceListingService;
    private BinanceListingAnnouncementClient clientMock;
    private Event<CryptoAnnouncement> eventMock;

    @BeforeEach
    void setUp() {
        eventMock = mock(Event.class);
        clientMock = mock(BinanceListingAnnouncementClient.class);
        binanceListingService = new BinanceListingService(clientMock, mock(Scheduler.class), eventMock);
    }

    @Test
    void shouldNotFireAnnouncementEventWhenCurrencyWasProcessedAtTheStartUp() {
        when(clientMock.getListingAnnouncements(anyLong(), anyLong(), anyLong(), anyLong()))
                .thenReturn(prepareListingResponse(), prepareListingResponse());

        binanceListingService.onStart(null);
        binanceListingService.performAnnouncementListingCheck();

        verify(eventMock, never()).fire(any(CryptoAnnouncement.class));
    }

    @Test
    void shouldFireAnnouncementEventWhenCurrencyWasNotProcessedAtTheStartUp() {
        final ArgumentCaptor<CryptoAnnouncement> cryptoAnnouncementCaptor =
                ArgumentCaptor.forClass(CryptoAnnouncement.class);
        when(clientMock.getListingAnnouncements(anyLong(), anyLong(), anyLong(), anyLong()))
                .thenReturn(prepareListingResponse(List.of()), prepareListingResponse());

        binanceListingService.onStart(null);
        binanceListingService.performAnnouncementListingCheck();

        verify(eventMock).fire(cryptoAnnouncementCaptor.capture());

        final CryptoAnnouncement announcement = cryptoAnnouncementCaptor.getValue();
        assertThat(announcement.getCryptoSymbol().getCrypto()).isEqualTo("MOVR");
        assertThat(announcement.getCryptoSymbol().getExchange()).isEqualTo(CryptoExchange.BINANCE);
    }

    @Test
    void shouldNotFireAnnouncementEventTwiceForTheSameCurrency() {
        when(clientMock.getListingAnnouncements(anyLong(), anyLong(), anyLong(), anyLong()))
                .thenReturn(prepareListingResponse(List.of()), prepareListingResponse(), prepareListingResponse());

        binanceListingService.onStart(null);
        binanceListingService.performAnnouncementListingCheck();
        binanceListingService.performAnnouncementListingCheck();

        verify(eventMock, times(1)).fire(any(CryptoAnnouncement.class));
    }

    @Test
    void shouldSuccessfullyReturnProcessedCurrencies() {
        when(clientMock.getListingAnnouncements(anyLong(), anyLong(), anyLong(), anyLong()))
                .thenReturn(prepareListingResponse());

        binanceListingService.onStart(null);
        final Set<CryptoSymbol> processedListings = binanceListingService.getProcessedListings();

        assertThat(processedListings).extracting(CryptoSymbol::getCrypto).containsExactly("MOVR");
    }

    private BinanceListingResponse prepareListingResponse(final List<BinanceListingArticle> articles) {
        return BinanceListingResponse.builder()
                .data(BinanceListingData.builder()
                        .articles(articles)
                        .build())
                .build();
    }

    private BinanceListingResponse prepareListingResponse() {
        return prepareListingResponse(List.of(
                BinanceListingArticle.builder()
                        .id(1L)
                        .code("3cc58db7ef894061b0e449e34b22265a")
                        .title("Binance Futures Will Launch Coin-Margined SAND Perpetual Contracts " +
                                "with Up to" +
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
                        .title("Binance Adds BETA & BNX on Isolated Margin, Stablecoins Annual " +
                                "Interest Rate " +
                                "Starts at 6.20%!")
                        .build(),
                BinanceListingArticle.builder()
                        .id(1L)
                        .code("14e4354e11444c7092934e618f5eac64")
                        .title("Introducing the FC Porto Fan Token (PORTO) Token Sale on Binance " +
                                "Launchpad!")
                        .build()
        ));
    }
}
