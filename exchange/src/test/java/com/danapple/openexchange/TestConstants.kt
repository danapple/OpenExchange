package com.danapple.openexchange

import com.danapple.openexchange.dao.memoryimplementations.MemoryCustomerDao
import com.danapple.openexchange.dao.memoryimplementations.MemoryInstrumentDao
import com.danapple.openexchange.dao.memoryimplementations.MemoryOrderDao
import com.danapple.openexchange.engine.EngineFactory
import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.entities.instruments.Instrument
import com.danapple.openexchange.entities.trades.TradeFactory
import com.danapple.openexchange.entities.trades.TradeIdGenerator
import com.danapple.openexchange.entities.trades.TradeLegFactory
import com.danapple.openexchange.entities.trades.TradeLegIdGenerator
import com.danapple.openexchange.orders.OrderFactory
import com.danapple.openexchange.orders.OrderIdGenerator
import java.math.BigDecimal
import java.time.Clock

class TestConstants {
    companion object {
        private val ORDER_ID_GENERATOR = OrderIdGenerator()
        val ORDER_DAO = MemoryOrderDao()
        val ORDER_FACTORY = OrderFactory(Clock.systemDefaultZone(), ORDER_ID_GENERATOR, MemoryInstrumentDao(), MemoryCustomerDao())

        private val tradeIdGenerator = TradeIdGenerator()
        val TRADE_FACTORY = TradeFactory(tradeIdGenerator)

        private val tradeLegIdGenerator = TradeLegIdGenerator()
        val TRADE_LEG_FACTORY = TradeLegFactory(tradeLegIdGenerator)

        val INSTRUMENT_ID_1 = 3L
        val INSTRUMENT_1  = Instrument(INSTRUMENT_ID_1)

        val CUSTOMER_ID_1 = "Customer 1"
        val CUSTOMER = Customer(CUSTOMER_ID_1)
        val CL_ORD_BUY_1 = "ClOrdBuy1"
        val CL_ORD_BUY_2 = "ClOrdBuy2"

        val CL_ORD_SELL_1 = "ClOrdSell1"
        val CL_ORD_SELL_2 = "ClOrdSell2"

        val ORDER_QUANTITY_1 = 10
        val ORDER_QUANTITY_2 = 20

        val ORDER_TIMESTAMP_1 = 100101L
        val ORDER_TIMESTAMP_2 = 200202L

        val ORDER_BUY_1 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_1, CL_ORD_BUY_1, INSTRUMENT_1, BigDecimal.ONE, ORDER_QUANTITY_1)
        val ORDER_BUY_2 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_2, CL_ORD_BUY_2, INSTRUMENT_1, BigDecimal.TWO, ORDER_QUANTITY_2)

        val ORDER_SELL_1 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_1, CL_ORD_SELL_1, INSTRUMENT_1, BigDecimal.ONE, -ORDER_QUANTITY_1)
        val ORDER_SELL_2 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_2, CL_ORD_SELL_2, INSTRUMENT_1, BigDecimal.TWO, -ORDER_QUANTITY_2)

        val TRADE_TIMESTAMP_1 = 300303L

        val ENGINE_FACTORY = EngineFactory(Clock.systemDefaultZone(), TRADE_FACTORY, TRADE_LEG_FACTORY, ORDER_DAO)
    }
}