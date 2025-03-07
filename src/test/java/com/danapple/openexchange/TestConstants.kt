package com.danapple.openexchange

import com.danapple.openexchange.instruments.Equity
import com.danapple.openexchange.instruments.TradingExchange
import com.danapple.openexchange.orders.Order
import com.danapple.openexchange.orders.OrderHolder
import com.danapple.openexchange.trades.Trade
import java.math.BigDecimal
import java.util.*

class TestConstants {
    companion object {
        val TRADING_EXCHANGE_NAME_1 = "Trading Exchange 1"
        val TRADING_EXCHANGE_1 = TradingExchange(TRADING_EXCHANGE_NAME_1, TimeZone.getDefault())

        val EQUITY_NAME_1 = "Equity 1"
        val EQUITY_1  = Equity(EQUITY_NAME_1, TRADING_EXCHANGE_1)

        val CL_ORD_1 = "ClOrd1"

        val ORDER_QUANTITY_1 = 5
        val ORDER_1 = Order.createOrder(CL_ORD_1, EQUITY_1, BigDecimal.ONE, ORDER_QUANTITY_1)
        internal val ORDER_HOLDER_1 = OrderHolder(ORDER_1)

        val EMPTY_TRADE_1 = Trade.createTrade(BigDecimal.ONE, HashSet())
        val EMPTY_TRADE_2 = Trade.createTrade(BigDecimal.TWO, HashSet())
    }
}