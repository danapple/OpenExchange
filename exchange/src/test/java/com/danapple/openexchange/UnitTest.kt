package com.danapple.openexchange

import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.dao.OrderQueryDao
import com.danapple.openexchange.dao.jdbcdao.*
import com.danapple.openexchange.entities.trades.TradeFactory
import com.danapple.openexchange.entities.trades.TradeLegFactory
import com.danapple.openexchange.orders.OrderFactory
import io.mockk.mockk
import org.springframework.jdbc.core.simple.JdbcClient
import java.time.Clock

open class UnitTest :
    ExchangeTest(getOrderDao(), getOrderQueryDao(), getCustomerDao(), getInstrumentDao(), getOrderFactory()) {

    val tradeFactory = TradeFactory(MemoryIdGenerator())
    val tradeLegFactory = TradeLegFactory(MemoryIdGenerator())

    companion object {
        private val jdbcClient = mockk<JdbcClient>()
        private val orderCache = OrderCache()

        fun getOrderDao() : OrderDao {
            return OrderDaoJdbcImpl(listOf(jdbcClient), orderCache)
        }

        fun getOrderQueryDao() : OrderQueryDao {
            return OrderQueryDaoJdbcImpl(listOf(jdbcClient), getCustomerDao(), getInstrumentDao(),  orderCache)
        }

        fun getCustomerDao() : CustomerDao {
            return CustomerDaoJdbcImpl(jdbcClient)
        }

        fun getInstrumentDao() : InstrumentDao {
            return InstrumentDaoJdbcImpl(jdbcClient);
        }

        fun getOrderFactory() : OrderFactory {
            return OrderFactory(Clock.systemDefaultZone(), MemoryIdGenerator(), getInstrumentDao())
        }
    }
}