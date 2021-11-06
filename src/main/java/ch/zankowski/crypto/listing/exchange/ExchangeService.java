package ch.zankowski.crypto.listing.exchange;

import ch.zankowski.crypto.listing.dto.CryptoSymbol;

import java.util.Set;

public interface ExchangeService {

    Set<String> getSupportedCurrencies();

    void placeOrder(CryptoSymbol cryptoSymbol);

}
