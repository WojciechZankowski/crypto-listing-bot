package ch.zankowski.crypto.listing.announcement.coinbase.client;

import ch.zankowski.crypto.listing.announcement.coinbase.CoinbaseListingAnnouncementParser;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CoinbaseListingAnnouncementParserTest {

    @Test
    void shouldSuccessfullyExtractSingleSymbol() {
        final Tweet tweet = mock(Tweet.class);
        when(tweet.getText()).thenReturn("Inbound transfers for CRO are now available in the regions where trading is" +
                " supported. Traders cannot place orders and no orders will be filled. Trading will begin on or after" +
                " 9AM PT on Wed November 3, if liquidity conditions are met. https://t.co/aKuFAykqyc https://t" +
                ".co/y1ellbkAay\n");

        assertThat(CoinbaseListingAnnouncementParser.extractSymbols(tweet)).containsExactly("CRO");

    }

    @Test
    void shouldSuccessfullyExtractMultipleSymbols() {
        final Tweet tweet = mock(Tweet.class);
        when(tweet.getText()).thenReturn("Inbound transfers for BADGER and RARI  are now available in the regions " +
                "where trading is supported. Traders cannot place orders and no orders will be filled. Trading will " +
                "begin on or after 9AM PT on Thursday October 14, if liquidity conditions are met.");

        assertThat(CoinbaseListingAnnouncementParser.extractSymbols(tweet)).containsExactlyInAnyOrder("BADGER", "RARI");
    }

    @Test
    void shouldSuccessfullyExtractMultipleSymbolsWithAndSymbol() {
        final Tweet tweet = mock(Tweet.class);
        when(tweet.getText()).thenReturn("Inbound transfers for KRL, LCX, SUKU & TRAC are now available in the " +
                "regions where trading is supported. Traders cannot place orders and no orders will be filled. " +
                "Trading will begin on or after 9AM PT on Thurs October 28, if liquidity conditions are met. " +
                "https://blog.coinbase.com/kryll-krl-lcx-");

        assertThat(CoinbaseListingAnnouncementParser.extractSymbols(tweet))
                .containsExactlyInAnyOrder("KRL", "LCX", "SUKU", "TRAC");
    }

    @Test
    void shouldNotIdentifyNewPairTweetAsAnAnnouncement() {
        final Tweet tweet = mock(Tweet.class);
        when(tweet.getText()).thenReturn("Our TRAC-USDT and TRAC-EUR order books will now enter limit-only mode. " +
                "Limit orders can be placed and cancelled, and matches may occur. Market orders cannot be submitted.");

        assertThat(CoinbaseListingAnnouncementParser.isAnnouncementTweet(tweet)).isFalse();
    }

    @Test
    void shouldIdentifyTweetAsAnAnnouncement() {
        final Tweet tweet = mock(Tweet.class);
        when(tweet.getText()).thenReturn("Inbound transfers for KRL, LCX, SUKU & TRAC are now available in the " +
                "regions where trading is supported. Traders cannot place orders and no orders will be filled. " +
                "Trading will begin on or after 9AM PT on Thurs October 28, if liquidity conditions are met. " +
                "https://blog.coinbase.com/kryll-krl-lcx-");

        assertThat(CoinbaseListingAnnouncementParser.isAnnouncementTweet(tweet)).isTrue();
    }

}
