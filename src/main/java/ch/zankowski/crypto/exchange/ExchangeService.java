package ch.zankowski.crypto.exchange;

import io.gate.gateapi.models.Order;

import java.util.Set;

public interface ExchangeService {

    Set<String> getSupportedCurrencies();

    Order placeOrder(Order order);

}
