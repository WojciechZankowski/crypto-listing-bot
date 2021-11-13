package ch.zankowski.crypto.listing.announcement.coinbase;

import ch.zankowski.crypto.listing.announcement.coinbase.client.CoinbaseListingAnnouncementClient;
import ch.zankowski.crypto.listing.dto.CryptoAnnouncement;
import ch.zankowski.crypto.listing.dto.CryptoExchange;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.enterprise.event.Event;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CoinbaseListingServiceTest {

    private CoinbaseListingService coinbaseListingService;
    private CoinbaseListingAnnouncementClient clientMock;
    private Event<CryptoAnnouncement> announcementEventMock;

    @BeforeEach
    void setUp() {
        clientMock = mock(CoinbaseListingAnnouncementClient.class);
        announcementEventMock = mock(Event.class);
        coinbaseListingService = new CoinbaseListingService(announcementEventMock, clientMock);
    }

    @Test
    void shouldFireAnnouncementEventWhenThereIsNewListingTweet() {
        final ArgumentCaptor<Consumer<Tweet>> consumerArgumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        coinbaseListingService.onStart(null);
        verify(clientMock).subscribeToAnnouncementTweets(consumerArgumentCaptor.capture());

        final ArgumentCaptor<CryptoAnnouncement> announcementArgumentCaptor =
                ArgumentCaptor.forClass(CryptoAnnouncement.class);
        final Tweet tweet = mock(Tweet.class);
        when(tweet.getText()).thenReturn("Inbound transfers for CRO are now available in the regions where trading is" +
                " supported. Traders cannot place orders and no orders will be filled. Trading will begin on or after" +
                " 9AM PT on Wed November 3, if liquidity conditions are met. https://t.co/aKuFAykqyc https://t" +
                ".co/y1ellbkAay\n");

        final Consumer<Tweet> consumer = consumerArgumentCaptor.getValue();
        consumer.accept(tweet);

        verify(announcementEventMock).fire(announcementArgumentCaptor.capture());

        final CryptoAnnouncement cryptoAnnouncement = announcementArgumentCaptor.getValue();
        assertThat(cryptoAnnouncement.getCryptoSymbol().getCrypto()).isEqualTo("CRO");
        assertThat(cryptoAnnouncement.getCryptoSymbol().getExchange()).isEqualTo(CryptoExchange.COINBASE);
    }

    @Test
    void shouldNotFireAnnouncementEventWhenThereIsNoListingTweet() {
        final ArgumentCaptor<Consumer<Tweet>> consumerArgumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        coinbaseListingService.onStart(null);
        verify(clientMock).subscribeToAnnouncementTweets(consumerArgumentCaptor.capture());

        final ArgumentCaptor<CryptoAnnouncement> announcementArgumentCaptor =
                ArgumentCaptor.forClass(CryptoAnnouncement.class);

        final Tweet tweet = mock(Tweet.class);
        when(tweet.getText()).thenReturn("Our TRAC-USDT and TRAC-EUR order books will now enter limit-only mode. " +
                "Limit orders can be placed and cancelled, and matches may occur. Market orders cannot be submitted.");

        final Consumer<Tweet> consumer = consumerArgumentCaptor.getValue();
        consumer.accept(tweet);

        verify(announcementEventMock, never()).fire(announcementArgumentCaptor.capture());
    }

    @Test
    void shouldRetrySubscriptionAfterException() {
        doThrow(new IllegalArgumentException())
                .doNothing()
                .when(clientMock).subscribeToAnnouncementTweets(any(Consumer.class));

        coinbaseListingService.onStart(null);

        verify(clientMock, times(2)).subscribeToAnnouncementTweets(any(Consumer.class));
    }

}
