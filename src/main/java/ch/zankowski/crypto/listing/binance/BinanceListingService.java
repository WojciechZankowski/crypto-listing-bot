package ch.zankowski.crypto.listing.binance;

import ch.zankowski.crypto.listing.ListingService;
import ch.zankowski.crypto.listing.binance.client.BinanceListingAnnouncementClient;
import ch.zankowski.crypto.listing.binance.dto.BinanceListingArticle;
import ch.zankowski.crypto.listing.dto.CryptoAnnouncement;
import ch.zankowski.crypto.listing.dto.CryptoExchange;
import ch.zankowski.crypto.listing.dto.CryptoSymbol;
import io.quarkus.runtime.StartupEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@ApplicationScoped
public class BinanceListingService implements ListingService {

    private static final Pattern CRYPTO_TICKER_PATTERN = Pattern.compile("\\(([^)]+)");

    private static final Set<String> EXCLUDED_WORDS = Set.of("Futures", "'Margin", "adds", "Adds");

    private static final Predicate<BinanceListingArticle> IS_LISTING_ANNOUNCEMENT = article -> {
        if (!Objects.nonNull(article.getTitle())) {
            return false;
        }
        return article.getTitle().toLowerCase(Locale.ROOT).contains("will") &&
                EXCLUDED_WORDS.stream().noneMatch(word -> article.getTitle().matches(word));
    };

    private final Set<CryptoSymbol> processedCurrencies = ConcurrentHashMap.newKeySet();

    @Inject
    Scheduler quartz;

    @Inject
    @RestClient
    BinanceListingAnnouncementClient binanceListingAnnouncementClient;

    @Inject
    Event<CryptoAnnouncement> cryptoAnnouncementEvent;

    @SneakyThrows
    void onStart(@Observes StartupEvent ev) {
        final JobDetail job = JobBuilder.newJob(BinanceListingAnnouncementJob.class)
                .withIdentity("Binance Announcement Listing")
                .build();
        final SimpleTrigger listingTrigger = TriggerBuilder.newTrigger()
                .withIdentity("Binance Announcement Listing trigger")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(2)
                        .repeatForever())
                .build();
        quartz.scheduleJob(job, listingTrigger);

        processedCurrencies.addAll(fetchListingAnnouncements()
                .collect(Collectors.toSet()));

        log.info("Binance service initialized, initial currencies: " + processedCurrencies);
    }

    void performAnnouncementListingCheck() {
        log.info("Performing announcement listing check");

        fetchListingAnnouncements()
                .filter(coin -> !processedCurrencies.contains(coin))
                .forEach(coin -> cryptoAnnouncementEvent.fire(CryptoAnnouncement.builder()
                        .cryptoSymbol(coin)
                        .build()));

        log.info("Announcement listing finished");
    }

    Stream<CryptoSymbol> fetchListingAnnouncements() {
        return binanceListingAnnouncementClient.getListingAnnouncements(48L, 1L, 5L, System.currentTimeMillis())
                .getData().getArticles().stream()
                .filter(IS_LISTING_ANNOUNCEMENT)
                .flatMap(article -> CRYPTO_TICKER_PATTERN.matcher(article.getTitle()).results())
                .map(result -> result.group(1))
                .filter(Objects::nonNull)
                .map(coin -> CryptoSymbol.builder()
                        .crypto(coin)
                        .exchange(CryptoExchange.BINANCE)
                        .build());
    }

    public static class BinanceListingAnnouncementJob implements Job {

        @Inject
        BinanceListingService binanceListingService;

        @Override
        public void execute(final JobExecutionContext context) {
            try {
                binanceListingService.performAnnouncementListingCheck();
            } catch (final Exception e) {
                log.error("Failed to fetch listing announcements");
            }
        }
    }

    @Override
    public Set<CryptoSymbol> getProcessedListings() {
        return processedCurrencies;
    }
}
