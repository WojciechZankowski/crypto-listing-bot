package ch.zankowski.crypto.listing.coinbase;

import com.github.redouane59.twitter.dto.tweet.Tweet;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CoinbaseListingAnnouncementParser {

    private static final Pattern FIRST_SENTENCE_PATTERN = Pattern.compile("^.*?[.!?](?:\\s|$)(?!.*\\))");
    private static final Pattern CRYPTO_TICKER_PATTERN = Pattern.compile("[A-Z]{2,10}");

    private CoinbaseListingAnnouncementParser() {
        // static util
    }

    public static Set<String> extractSymbols(final Tweet tweet) {
        return FIRST_SENTENCE_PATTERN.matcher(tweet.getText()).results()
                .map(result -> result.group(0))
                .filter(Objects::nonNull)
                .flatMap(firstSentence -> CRYPTO_TICKER_PATTERN.matcher(firstSentence).results())
                .map(result -> result.group(0))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

}
