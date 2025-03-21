package com.danapple.openexchange.engine

import com.danapple.openexchange.book.Book
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.entities.instruments.Instrument
import com.danapple.openexchange.entities.trades.TradeFactory
import com.danapple.openexchange.entities.trades.TradeLegFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Clock

@Service
class EngineFactory(@Autowired private val clock : Clock, @Autowired private val tradeFactory: TradeFactory, @Autowired private val tradeLegFactory : TradeLegFactory, @Autowired private val orderDao : OrderDao) {
    private fun createEngine() : Engine {
        val book = Book()
        return Engine(book, clock, tradeFactory, tradeLegFactory, orderDao)
    }

    fun createEngines(instruments: Set<Instrument>): Map<Instrument, Engine> {
        return instruments.associateWith { createEngine() }
    }
}