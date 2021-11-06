package ch.zankowski.crypto.listing;

import ch.zankowski.crypto.listing.dto.CryptoSymbol;
import ch.zankowski.crypto.listing.exchange.ExchangeService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@Path("/listing")
public class ListingResource {

    @Inject
    ExchangeService exchangeService;

    @Inject
    ListingService listingService;

    @GET
    @Path("/supported-currencies")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> supportedCurrencies() {
        return exchangeService.getSupportedCurrencies();
    }

    @GET
    @Path("/processed-currencies")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<CryptoSymbol> processedCurrencies() {
        return listingService.getProcessedListings();
    }

}
