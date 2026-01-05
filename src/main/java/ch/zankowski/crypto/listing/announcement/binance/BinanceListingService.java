package ch.zankowski.crypto.listing.announcement.binance;

import ch.zankowski.crypto.listing.ListingService;
import ch.zankowski.crypto.listing.announcement.binance.client.BinanceListingAnnouncementClient;
import ch.zankowski.crypto.listing.dto.CryptoAnnouncement;
import ch.zankowski.crypto.listing.dto.CryptoSymbol;
import io.quarkus.runtime.StartupEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ApplicationScoped
public class BinanceListingService implements ListingService {

    private final Set<CryptoSymbol> processedCurrencies = ConcurrentHashMap.newKeySet();

    private final BinanceListingAnnouncementClient binanceListingAnnouncementClient;
    private final Scheduler quartz;
    private final Event<CryptoAnnouncement> cryptoAnnouncementEvent;

    @Inject
    public BinanceListingService(
            @RestClient final BinanceListingAnnouncementClient binanceListingAnnouncementClient,
            final Scheduler quartz,
            final Event<CryptoAnnouncement> cryptoAnnouncementEvent) {
        this.quartz = quartz;
        this.binanceListingAnnouncementClient = binanceListingAnnouncementClient;
        this.cryptoAnnouncementEvent = cryptoAnnouncementEvent;
    }

    @SneakyThrows
    void onStart(@Observes StartupEvent ev) {
        try {
            processedCurrencies.addAll(fetchListingAnnouncements());
        } catch (final Exception e) {
            log.error("Failed to fetch initial listing announcements", e);
        }

        final JobDetail job = JobBuilder.newJob(BinanceListingAnnouncementJob.class)
                .withIdentity("Binance Announcement Listing")
                .build();
        final SimpleTrigger listingTrigger = TriggerBuilder.newTrigger()
                .withIdentity("Binance Announcement Listing trigger")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(5)
                        .repeatForever())
                .build();
        quartz.scheduleJob(job, listingTrigger);

        log.info("Binance service initialized, initial currencies: " + processedCurrencies);
    }

    void performAnnouncementListingCheck() {
        log.info("Performing announcement listing check");

        fetchListingAnnouncements().stream()
                .filter(coin -> !processedCurrencies.contains(coin))
                .forEach(coin -> {
                    processedCurrencies.add(coin);
                    cryptoAnnouncementEvent.fire(CryptoAnnouncement.builder()
                        .cryptoSymbol(coin)
                        .build());
                });

        log.info("Announcement listing finished");
    }

    private Set<CryptoSymbol> fetchListingAnnouncements() {
        return BinanceAnnouncementParser.parse(
                binanceListingAnnouncementClient.getListingAnnouncements(
                        1L, 48L, 1L, 3L).getData());
    }

    @Override
    public Set<CryptoSymbol> getProcessedListings() {
        return processedCurrencies;
    }
}
