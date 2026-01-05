package ch.zankowski.crypto.listing;

import ch.zankowski.crypto.listing.dto.CryptoSymbol;
import ch.zankowski.crypto.listing.exchange.gate.GateExchangeService;
import ch.zankowski.crypto.listing.marketdata.gate.GateMarketDataProvider;
import ch.zankowski.crypto.listing.marketdata.dto.Ticker;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Set;

@Path("/listing")
public class ListingResource {

    @Inject
    GateExchangeService exchangeService;

    @Inject
    ListingService listingService;

    @Inject
    GateMarketDataProvider marketDataProvider;

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

    @GET
    @Path("/current-prices")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ticker> allTickers() {
        return marketDataProvider.getAllTickers();
    }

}
