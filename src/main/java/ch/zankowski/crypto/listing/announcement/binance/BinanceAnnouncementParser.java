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
    private static final Pattern CRYPTO_TICKER_WITHOUT_BRACKETS = Pattern.compile("(?:List|list)\\s+([A-Z]+)(?:\\s|&|$)");

    private static final Set<String> EXCLUDED_WORDS = Set.of("futures", "margin", "adds", "will add");

    private static final Predicate<BinanceListingArticle> IS_LISTING_ANNOUNCEMENT = article -> {
        if (!Objects.nonNull(article.getTitle())) {
            return false;
        }
        var lowerCaseTitle = article.getTitle().toLowerCase(Locale.ROOT);
        return lowerCaseTitle.contains("will") && EXCLUDED_WORDS.stream().noneMatch(lowerCaseTitle::contains);
    };

    private BinanceAnnouncementParser() {
        // static util
    }

    static Set<CryptoSymbol> parse(final BinanceListingData listingData) {
        if (listingData == null || listingData.getCatalogs() == null) {
            return Set.of();
        }
        final List<BinanceListingArticle> allArticles = listingData.getCatalogs().stream()
                .flatMap(catalog -> catalog.getArticles() == null ?
                        java.util.stream.Stream.empty() :
                        catalog.getArticles().stream())
                .collect(Collectors.toList());
        return parse(allArticles);
    }

    static Set<CryptoSymbol> parse(final List<BinanceListingArticle> articles) {
        return articles == null ? Set.of() : articles.stream()
                .filter(IS_LISTING_ANNOUNCEMENT)
                .flatMap(article -> {
                    // Try to find ticker in brackets first
                    var tickersInBrackets = CRYPTO_TICKER_PATTERN.matcher(article.getTitle()).results()
                            .map(result -> result.group(1))
                            .filter(Objects::nonNull)
                            .toList();

                    // If ticker found in brackets, use it; otherwise try without brackets
                    if (!tickersInBrackets.isEmpty()) {
                        return tickersInBrackets.stream();
                    } else {
                        return CRYPTO_TICKER_WITHOUT_BRACKETS.matcher(article.getTitle()).results()
                                .map(result -> result.group(1))
                                .filter(Objects::nonNull);
                    }
                })
                .map(coin -> CryptoSymbol.builder()
                        .crypto(coin)
                        .exchange(CryptoExchange.BINANCE)
                        .build())
                .collect(Collectors.toSet());

    }

}
