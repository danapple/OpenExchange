package com.danapple.openexchange

import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.engine.EngineFactory
import com.danapple.openexchange.dao.memoryimplementations.MemoryCustomerDao
import com.danapple.openexchange.dao.memoryimplementations.MemoryInstrumentDao
import com.danapple.openexchange.dao.memoryimplementations.MemoryTradingExchangeDaoImpl
import com.danapple.openexchange.entities.instruments.Equity
import com.danapple.openexchange.entities.instruments.TradingExchange
import com.danapple.openexchange.entities.trades.TradeFactory
import com.danapple.openexchange.entities.trades.TradeIdGenerator
import com.danapple.openexchange.entities.trades.TradeLegFactory
import com.danapple.openexchange.entities.trades.TradeLegIdGenerator
import com.danapple.openexchange.orders.OrderFactory
import com.danapple.openexchange.orders.OrderIdGenerator
import java.math.BigDecimal
import java.time.Clock
import java.util.*

class TestConstants {
    companion object {
        private val ORDER_ID_GENERATOR = OrderIdGenerator()
        val TRADING_EXCHANGE_DAO = MemoryTradingExchangeDaoImpl()
        val ORDER_FACTORY = OrderFactory(Clock.systemDefaultZone(), ORDER_ID_GENERATOR, MemoryInstrumentDao(TRADING_EXCHANGE_DAO), MemoryCustomerDao())

        private val tradeIdGenerator = TradeIdGenerator()
        val TRADE_FACTORY = TradeFactory(tradeIdGenerator)

        private val tradeLegIdGenerator = TradeLegIdGenerator()
        val TRADE_LEG_FACTORY = TradeLegFactory(tradeLegIdGenerator)

        val TRADING_EXCHANGE_NAME_1 = "Trading Exchange 1"
        val TRADING_EXCHANGE_ID_1 = 2L
        val TRADING_EXCHANGE_1 = TradingExchange(TRADING_EXCHANGE_ID_1, TRADING_EXCHANGE_NAME_1, TimeZone.getDefault())

        val EQUITY_NAME_1 = "Equity 1"
        val EQUITY_ID_1 = 3L
        val EQUITY_1  = Equity(EQUITY_ID_1, EQUITY_NAME_1, TRADING_EXCHANGE_1)

        val CUSTOMER_ID_1 = "Customer 1"
        val CUSTOMER = Customer(CUSTOMER_ID_1)
        val CL_ORD_1 = "ClOrd1"

        val ORDER_QUANTITY_1 = 5
        val ORDER_TIMESTAMP_1 = 300303L
        val ORDER_1 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_1, CL_ORD_1, EQUITY_1, BigDecimal.ONE, ORDER_QUANTITY_1)

        val TRADE_TIMESTAMP_1 = 300303L

        val ENGINE_FACTORY = EngineFactory(Clock.systemDefaultZone(), TRADE_FACTORY, TRADE_LEG_FACTORY)
    }
}