package com.danapple.openexchange

import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.dao.OrderQueryDao
import com.danapple.openexchange.entities.trades.TradeFactory
import com.danapple.openexchange.entities.trades.TradeLegFactory
import com.danapple.openexchange.orders.OrderFactory
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.time.Clock

open class UnitTest :
    ExchangeTest(getOrderDao(), getOrderQueryDao(), getCustomerDao(), getInstrumentDao(), getOrderFactory()) {

    val tradeFactory = TradeFactory(MemoryIdGenerator())
    val tradeLegFactory = TradeLegFactory(MemoryIdGenerator())

    companion object {
//        private val jdbcClient = mockk<JdbcClient>()
//        private val orderCache = OrderCache()

        fun getOrderDao(): OrderDao {
            val orderDao = mockk<OrderDao>()
            every { orderDao.saveOrder(any()) } just runs
            every { orderDao.updateOrder(any()) } just runs
            return orderDao
//            return OrderDaoJdbcImpl(listOf(jdbcClient), orderCache)
        }

        fun getOrderQueryDao(): OrderQueryDao {
            return mockk<OrderQueryDao>()
            //  return OrderQueryDaoJdbcImpl(listOf(jdbcClient), getCustomerDao(), getInstrumentDao(),  orderCache)
        }

        fun getCustomerDao(): CustomerDao {
            return mockk<CustomerDao>()
            //   return CustomerDaoJdbcImpl(jdbcClient)
        }

        fun getInstrumentDao(): InstrumentDao {
            return mockk<InstrumentDao>()
            //  return InstrumentDaoJdbcImpl(jdbcClient);
        }

        fun getOrderFactory(): OrderFactory {
            return OrderFactory(Clock.systemDefaultZone(), MemoryIdGenerator(), getInstrumentDao())
        }

    }
}