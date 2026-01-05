package ch.zankowski.crypto.listing.announcement.binance;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import jakarta.inject.Inject;

@Slf4j
public class BinanceListingAnnouncementJob implements Job {

    private final BinanceListingService binanceListingService;

    @Inject
    BinanceListingAnnouncementJob(final BinanceListingService binanceListingService) {
        this.binanceListingService = binanceListingService;
    }

    @Override
    public void execute(final JobExecutionContext context) {
        try {
            binanceListingService.performAnnouncementListingCheck();
        } catch (final Exception e) {
            log.error("Failed to fetch listing announcements");
        }
    }
}
