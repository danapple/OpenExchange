package com.danapple.openexchange

import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.entities.trades.TradeFactory
import com.danapple.openexchange.entities.trades.TradeLegFactory
import com.danapple.openexchange.orders.OrderFactory
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.time.Clock

abstract class UnitTest : Constants(orderFactory) {

    companion object {
        val tradeFactory = TradeFactory(MemoryIdGenerator())
        val tradeLegFactory = TradeLegFactory(MemoryIdGenerator())

        val orderDao = mockk<OrderDao>()
        init {
            every { orderDao.saveOrder(any()) } just runs
            every { orderDao.updateOrder(any()) } just runs
        }

        private val instrumentDao = mockk<InstrumentDao>()

        val orderFactory = OrderFactory(Clock.systemDefaultZone(), MemoryIdGenerator(), instrumentDao)
    }
}