package ch.zankowski.crypto.listing.announcement.binance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BinanceListingAnnouncementJobTest {

    private BinanceListingAnnouncementJob announcementJob;
    private BinanceListingService serviceMock;

    @BeforeEach
    void setUp() {
        serviceMock = mock(BinanceListingService.class);
        announcementJob = new BinanceListingAnnouncementJob(serviceMock);
    }

    @Test
    void shouldNotThrowAnyExceptionWhenServiceFails() {
        doThrow(new IllegalArgumentException()).when(serviceMock).performAnnouncementListingCheck();

        assertDoesNotThrow(() -> announcementJob.execute(null));
    }

    @Test
    void shouldPerformAnnouncementListingCheckWhenJobIsTriggered() {
        announcementJob.execute(null);

        verify(serviceMock).performAnnouncementListingCheck();
    } 
}
