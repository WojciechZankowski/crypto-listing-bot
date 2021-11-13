package ch.zankowski.crypto.listing.announcement.coinbase;

import ch.zankowski.crypto.listing.announcement.coinbase.client.CoinbaseListingAnnouncementClient;
import ch.zankowski.crypto.listing.dto.CryptoAnnouncement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.event.Event;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    void test() {

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
