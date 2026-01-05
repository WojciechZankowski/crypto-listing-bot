package ch.zankowski.crypto.listing;

import io.gate.gateapi.models.Order;

import java.math.BigDecimal;

public class ListingOrderCreationHelper {

    public static Order createOrder(
            final String currencyPair,
            final String price,
            final String amount,
            final Order.SideEnum side) {
        final Order order = new Order();
        order.setAccount("spot");
        order.setType(Order.TypeEnum.LIMIT);
        order.setCurrencyPair(currencyPair);
        order.setSide(side);
        order.setPrice(price);
        order.setAmount(amount);
        return order;
    }

    public static Order createBuyOrder(
            final String currencyPair,
            final BigDecimal price,
            final BigDecimal amount) {
        return createOrder(currencyPair, price.toPlainString(), amount.toPlainString(), Order.SideEnum.BUY);
    }

    public static Order createSellOrder(
            final String currencyPair,
            final BigDecimal price,
            final BigDecimal amount) {
        return createOrder(currencyPair, price.toPlainString(), amount.toPlainString(), Order.SideEnum.SELL);
    }

    public static Order createSellOrder(
            final String currencyPair,
            final String price,
            final String amount) {
        return createOrder(currencyPair, price, amount, Order.SideEnum.SELL);
    }

}
