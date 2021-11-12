package ch.zankowski.crypto.listing.announcement.binance;

import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingArticle;
import ch.zankowski.crypto.listing.announcement.binance.dto.BinanceListingData;
import ch.zankowski.crypto.listing.dto.CryptoExchange;
import ch.zankowski.crypto.listing.dto.CryptoSymbol;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BinanceAnnouncementParser {

    private static final Pattern CRYPTO_TICKER_PATTERN = Pattern.compile("\\(([^)]+)");

    private static final Set<String> EXCLUDED_WORDS = Set.of("Futures", "Margin", "adds", "Adds");

    private static final Predicate<BinanceListingArticle> IS_LISTING_ANNOUNCEMENT = article -> {
        if (!Objects.nonNull(article.getTitle())) {
            return false;
        }
        return article.getTitle().toLowerCase(Locale.ROOT).contains("will") &&
                EXCLUDED_WORDS.stream().noneMatch(word -> article.getTitle().matches(word));
    };

    private BinanceAnnouncementParser() {
        // static util
    }

    static Set<CryptoSymbol> parse(final BinanceListingData listingData) {
        return listingData == null ? Set.of() : parse(listingData.getArticles());
    }

    static Set<CryptoSymbol> parse(final List<BinanceListingArticle> articles) {
        return articles == null ? Set.of() : articles.stream().filter(IS_LISTING_ANNOUNCEMENT)
                .flatMap(article -> CRYPTO_TICKER_PATTERN.matcher(article.getTitle()).results())
                .map(result -> result.group(1))
                .filter(Objects::nonNull)
                .map(coin -> CryptoSymbol.builder()
                        .crypto(coin)
                        .exchange(CryptoExchange.BINANCE)
                        .build())
                .collect(Collectors.toSet());

    }

}
