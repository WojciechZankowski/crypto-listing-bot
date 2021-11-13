package ch.zankowski.crypto.listing.announcement.coinbase.client;

import com.github.redouane59.twitter.ITwitterClientV2;
import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import com.github.redouane59.twitter.signature.TwitterCredentials;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

@ApplicationScoped
public class CoinbaseListingAnnouncementClient {

    private final ITwitterClientV2 twitterClient;

    @Inject
    public CoinbaseListingAnnouncementClient(final TwitterClientConfig twitterClientConfig) {
        twitterClient = new TwitterClient(TwitterCredentials.builder()
                .apiKey(twitterClientConfig.apiKey())
                .apiSecretKey(twitterClientConfig.secretApiKey())
                .build());
    }

    public List<Tweet> searchForTweetsWithin7days(final String query) {
        return twitterClient.searchForTweetsWithin7days(query);
    }

    public void subscribeToAnnouncementTweets(final Consumer<Tweet> tweetConsumer) {
        twitterClient.startFilteredStream(tweetConsumer);
    }

}
