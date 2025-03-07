package com.danapple.openexchange;

import com.danapple.openexchange.ledger.Ledger;
import com.danapple.openexchange.orders.Order;
import com.danapple.openexchange.trades.Trade;
import java.time.Clock;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        Trade.TradeFactory.setClock(Clock.systemDefaultZone());
        Order.OrderFactory.setClock(Clock.systemDefaultZone());

        Ledger ledger = new Ledger();
        List<Trade> trades = ledger.getTrades();
        System.out.printf("Ledger contains %d trades%n", trades.size());
    }
}