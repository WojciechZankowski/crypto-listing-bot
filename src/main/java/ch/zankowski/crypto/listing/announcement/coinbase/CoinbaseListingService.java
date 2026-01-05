package ch.zankowski.crypto.listing.announcement.coinbase;

import ch.zankowski.crypto.listing.announcement.coinbase.client.CoinbaseListingAnnouncementClient;
import ch.zankowski.crypto.listing.dto.CryptoAnnouncement;
import ch.zankowski.crypto.listing.dto.CryptoExchange;
import ch.zankowski.crypto.listing.dto.CryptoSymbol;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.util.Set;
import java.util.function.Consumer;

import static ch.zankowski.crypto.listing.announcement.coinbase.CoinbaseListingAnnouncementParser.extractSymbols;
import static ch.zankowski.crypto.listing.announcement.coinbase.CoinbaseListingAnnouncementParser.isAnnouncementTweet;

@Slf4j
@ApplicationScoped
public class CoinbaseListingService {

    private final CoinbaseListingAnnouncementClient coinbaseListingAnnouncementClient;
    private Event<CryptoAnnouncement> cryptoAnnouncementEvent;

    @Inject
    public CoinbaseListingService(final Event<CryptoAnnouncement> cryptoAnnouncementEvent,
                                  final CoinbaseListingAnnouncementClient coinbaseListingAnnouncementClient) {
        this.cryptoAnnouncementEvent = cryptoAnnouncementEvent;
        this.coinbaseListingAnnouncementClient = coinbaseListingAnnouncementClient;
    }

    private final Consumer<Tweet> announcementTweetConsumer = tweet -> {
        try {
            if (tweet == null) {
                return;
            }
            log.info("New tweet {}", tweet.getText());

            if (isAnnouncementTweet(tweet)) {
                final Set<String> symbols = extractSymbols(tweet);
                log.warn("Coinbase listing announcement for symbols {}", symbols);

                symbols.forEach(symbol -> cryptoAnnouncementEvent.fire(CryptoAnnouncement.builder()
                        .cryptoSymbol(CryptoSymbol.builder()
                                .crypto(symbol)
                                .exchange(CryptoExchange.COINBASE)
                                .build())
                        .build()));
            }
        } catch (final Exception e) {
            log.error("New coinbase pro tweet failed. No exceptions allowed {}", String.valueOf(e));
        }
    };

    void onStart(@Observes StartupEvent ev) {
        subscribeToAnnouncementTweets();
    }

    private void subscribeToAnnouncementTweets() {
        try {
            coinbaseListingAnnouncementClient.subscribeToAnnouncementTweets(announcementTweetConsumer);
        } catch (final Exception e) {
            // Subscription failed, try again
            log.error("Subscription failed to announcement tweets. Trying again.");
            subscribeToAnnouncementTweets();
        }
    }

}
