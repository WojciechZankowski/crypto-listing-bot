package ch.zankowski.crypto.listing;

import ch.zankowski.crypto.listing.dto.CryptoAnnouncement;
import ch.zankowski.crypto.listing.exchange.ExchangeService;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Set;

@Slf4j
@ApplicationScoped
public class ListingAnnouncementProcessor {

    @Inject
    ExchangeService exchangeService;

    void onCryptoAnnounced(@Observes CryptoAnnouncement announcement) {
        log.info("Crypto announced " + announcement);

        final Set<String> supportedCurrencies = exchangeService.getSupportedCurrencies();

        log.info("New crypto supported " + supportedCurrencies.contains(announcement.getCryptoSymbol().getCrypto()));

        exchangeService.placeOrder(announcement.getCryptoSymbol());

    }

}
