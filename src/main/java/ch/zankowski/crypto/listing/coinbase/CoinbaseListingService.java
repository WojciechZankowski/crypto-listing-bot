package ch.zankowski.crypto.listing.coinbase;

import ch.zankowski.crypto.listing.coinbase.client.CoinbaseListingAnnouncementClient;
import ch.zankowski.crypto.listing.dto.CryptoAnnouncement;
import ch.zankowski.crypto.listing.dto.CryptoExchange;
import ch.zankowski.crypto.listing.dto.CryptoSymbol;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Set;
import java.util.function.Consumer;

import static ch.zankowski.crypto.listing.coinbase.CoinbaseListingAnnouncementParser.extractSymbols;

@Slf4j
@ApplicationScoped
public class CoinbaseListingService {

    @Inject
    Event<CryptoAnnouncement> cryptoAnnouncementEvent;

    @Inject
    CoinbaseListingAnnouncementClient coinbaseListingAnnouncementClient;

    private final Consumer<Tweet> announcementTweetConsumer = tweet -> {
        try {
            log.info("New tweet " + tweet.getText());

            if (tweet.getText().contains("Inbound transfers")) {
                final Set<String> symbols = extractSymbols(tweet);
                log.warn("======== COINBASE LISTING announced" + symbols);

                symbols.forEach(symbol -> cryptoAnnouncementEvent.fire(CryptoAnnouncement.builder()
                        .cryptoSymbol(CryptoSymbol.builder()
                                .crypto(symbol)
                                .exchange(CryptoExchange.COINBASE)
                                .build())
                        .build()));
            }
        } catch (final Exception e) {
            log.error("No exceptions allowed" + e);
        }
    };

    void onStart(@Observes StartupEvent ev) {
        coinbaseListingAnnouncementClient.subscribeToAnnouncementTweets(announcementTweetConsumer);
    }

}
