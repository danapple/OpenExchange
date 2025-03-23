package com.danapple.openexchange

import com.danapple.openexchange.dao.jdbcdao.*
import com.danapple.openexchange.engine.EngineFactory
import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.entities.instruments.Instrument
import com.danapple.openexchange.entities.trades.TradeFactory
import com.danapple.openexchange.entities.trades.TradeIdGenerator
import com.danapple.openexchange.entities.trades.TradeLegFactory
import com.danapple.openexchange.entities.trades.TradeLegIdGenerator
import com.danapple.openexchange.orders.OrderFactory
import com.danapple.openexchange.orders.OrderIdGenerator
import io.mockk.mockk
import org.springframework.jdbc.core.simple.JdbcClient
import java.math.BigDecimal
import java.time.Clock

class TestConstants {
    companion object {
        val CLOCK = Clock.systemDefaultZone()
        private val ORDER_ID_GENERATOR = OrderIdGenerator()
        val ORDER_CACHE = OrderCache()
        val ORDER_DAO = OrderDaoJdbcImpl(ArrayList(), ORDER_CACHE)
        val JDBC_CLIENT = mockk<JdbcClient>()
        val CUSTOMER_DAO = CustomerDaoJdbcImpl(JDBC_CLIENT)
        val INSTRUMENT_DAO = InstrumentDaoJdbcImpl(JDBC_CLIENT)

        val ORDER_QUERY_DAO = OrderQueryDaoJdbcImpl(ArrayList(), CUSTOMER_DAO, INSTRUMENT_DAO, ORDER_CACHE)
        val ORDER_FACTORY = OrderFactory(Clock.systemDefaultZone(), ORDER_ID_GENERATOR, INSTRUMENT_DAO, CUSTOMER_DAO)

        private val tradeIdGenerator = TradeIdGenerator()
        val TRADE_FACTORY = TradeFactory(tradeIdGenerator)

        private val tradeLegIdGenerator = TradeLegIdGenerator()
        val TRADE_LEG_FACTORY = TradeLegFactory(tradeLegIdGenerator)

        val INSTRUMENT_ID_1 = 3L
        val INSTRUMENT_1  = Instrument(INSTRUMENT_ID_1)

        val CUSTOMER_ID_1 = 1L
        val CUSTOMER_KEY_1 = "Customer 1"
        val CUSTOMER = Customer(CUSTOMER_ID_1, CUSTOMER_KEY_1)
        val CL_ORD_BUY_1 = "ClOrdBuy1"
        val CL_ORD_BUY_2 = "ClOrdBuy2"

        val CL_ORD_SELL_1 = "ClOrdSell1"
        val CL_ORD_SELL_2 = "ClOrdSell2"

        val ORDER_QUANTITY_1 = 10
        val ORDER_QUANTITY_2 = 20

        val ORDER_CREATETIME_1 = 100101L
        val ORDER_CREATETIME_2 = 200202L

        val ORDER_BUY_1 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_CREATETIME_1, CL_ORD_BUY_1, INSTRUMENT_1, BigDecimal.ONE, ORDER_QUANTITY_1)
        val ORDER_BUY_2 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_CREATETIME_2, CL_ORD_BUY_2, INSTRUMENT_1, BigDecimal.TWO, ORDER_QUANTITY_2)

        val ORDER_SELL_1 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_CREATETIME_1, CL_ORD_SELL_1, INSTRUMENT_1, BigDecimal.ONE, -ORDER_QUANTITY_1)
        val ORDER_SELL_2 = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_CREATETIME_2, CL_ORD_SELL_2, INSTRUMENT_1, BigDecimal.TWO, -ORDER_QUANTITY_2)

        val TRADE_CREATETIME_1 = 300303L

        val ENGINE_FACTORY = EngineFactory(Clock.systemDefaultZone(), TRADE_FACTORY, TRADE_LEG_FACTORY, ORDER_QUERY_DAO, ORDER_DAO, INSTRUMENT_DAO)
    }
}