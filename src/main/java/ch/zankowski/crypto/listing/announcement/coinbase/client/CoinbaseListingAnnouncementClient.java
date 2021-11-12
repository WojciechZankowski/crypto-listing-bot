package ch.zankowski.crypto.listing.announcement.coinbase.client;

import com.github.redouane59.twitter.ITwitterClientV2;
import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.List;
import java.util.function.Consumer;

import static ch.zankowski.crypto.listing.announcement.coinbase.CoinbaseListingAnnouncementParser.extractSymbols;

@Slf4j
@ApplicationScoped
public class CoinbaseListingAnnouncementClient {

    private final ITwitterClientV2 twitterClient;

    public CoinbaseListingAnnouncementClient() {
        twitterClient = new TwitterClient(TwitterCredentials.builder()
                .apiKey("D5bqiFqYS5OB80TEOUwfm2HCo")
                .apiSecretKey("r7CwQ9szRCJpixvXT9zKIa0s5lgBZh9smQv94cuIKJEHoJ8k5P")
                .build());
    }

    void onStart(@Observes StartupEvent ev) {
        // Just testing

        final List<Tweet> tweets = twitterClient.searchForTweetsWithin7days("from: CoinbasePro");

        tweets.forEach(tweet -> {
            log.info(tweet.getCreatedAt() + ": " + tweet.getText());
            if (tweet.getText().contains("Inbound transfers")) {
                log.warn("New crypto listed " + extractSymbols(tweet));
            }
        });
    }

    public void subscribeToAnnouncementTweets(final Consumer<Tweet> tweetConsumer) {
        twitterClient.startFilteredStream(tweetConsumer);
    }

}
