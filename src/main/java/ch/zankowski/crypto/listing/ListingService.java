package ch.zankowski.crypto.listing;

import ch.zankowski.crypto.listing.dto.CryptoSymbol;

import java.util.Set;

public interface ListingService {

    Set<CryptoSymbol> getProcessedListings();

}
