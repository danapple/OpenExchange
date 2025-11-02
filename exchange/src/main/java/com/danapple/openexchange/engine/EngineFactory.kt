package com.danapple.openexchange.engine

import com.danapple.openexchange.book.Book
import com.danapple.openexchange.customerupdates.CustomerUpdateSender
import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.dao.OrderQueryDao
import com.danapple.openexchange.dao.TradeDao
import com.danapple.openexchange.entities.instruments.Instrument
import com.danapple.openexchange.entities.trades.TradeFactory
import com.danapple.openexchange.entities.trades.TradeLegFactory
import com.danapple.openexchange.marketdata.MarketDataPublisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
open class EngineFactory(
    private val clock: Clock, private val tradeFactory: TradeFactory,
    private val tradeLegFactory: TradeLegFactory, private val orderQueryDao: OrderQueryDao,
    private val orderDao: OrderDao, private val instrumentDao: InstrumentDao,
    private val tradeDao: TradeDao,
    @Qualifier("marketDataPublisherMain") private val marketDataPublisher: MarketDataPublisher,
    private val customerUpdateSender: CustomerUpdateSender
) {

    @Bean
    open fun createEngines(instruments: Set<Instrument>): Map<Instrument, Engine> {
        val ordersByInstrument = orderQueryDao.getOpenOrders().groupBy { orderState ->
            logger.info("Loaded order $orderState")
            orderState.order.instrument
        }

        val instrumentsM = instrumentDao.getActiveInstruments()
        val engines = instrumentsM.associateWith { instrument ->
            val book = Book(instrument)
            ordersByInstrument.getOrDefault(instrument, emptyList()).forEach { orderState ->
                book.addOrder(orderState)
            }
            val engine = Engine(
                book, clock, tradeFactory, tradeLegFactory, orderDao, tradeDao,
                marketDataPublisher,
                customerUpdateSender
            )
            engine.publishInitialTopOfBook()
            engine
        }
        return engines
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(EngineFactory::class.java)
    }
}