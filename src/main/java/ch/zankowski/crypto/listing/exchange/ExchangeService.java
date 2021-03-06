package ch.zankowski.crypto.listing.exchange;

import io.gate.gateapi.models.Order;

import java.util.Set;

public interface ExchangeService {

    Set<String> getSupportedCurrencies();

    Order placeOrder(Order order);

}
