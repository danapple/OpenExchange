package com.danapple.openexchange;

import com.danapple.openexchange.Ledger.Ledger;
import com.danapple.openexchange.trades.Trade;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        Ledger ledger = new Ledger();
        List<Trade> trades = ledger.getTrades();
        System.out.printf("Ledger contains %d trades%n", trades.size());
    }
}