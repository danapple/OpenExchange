package com.danapple.openexchange.marketdata

import LastTrade
import com.danapple.openexchange.book.Book
import com.danapple.openexchange.dto.MarketDepth
import com.danapple.openexchange.entities.trades.Trade
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.util.*
import java.util.concurrent.atomic.AtomicLong

class MarketDataPublisherWebsocket(
    private val numberOfLevelsToPublish: Int,
    private val simpMessagingTemplate: SimpMessagingTemplate
) : MarketDataPublisher {
    private val senderId = UUID.randomUUID().toString()
    private val sequenceNumber = AtomicLong()

    override fun publishTopOfBook(timestamp: Long, book: Book) {
        val bestBuyPriceLevels = book.getBestBuyPriceLevels(numberOfLevelsToPublish)
        val bestSellPriceLevels = book.getBestSellPriceLevels(numberOfLevelsToPublish)
        val marketDepth = MarketDepth(
            senderId,
            sequenceNumber.getAndIncrement(),
            book.instrument.instrumentId,
            timestamp,
            bestBuyPriceLevels,
            bestSellPriceLevels
        )
        simpMessagingTemplate.convertAndSend("/topics/depth", marketDepth)
    }

    override fun publishTrades(timestamp: Long, trade: Trade) {
        val lastTrade = LastTrade(
            senderId,
            sequenceNumber.getAndIncrement(),
            trade.tradeLegs.first().orderState.order.instrument.instrumentId,
            timestamp,
            trade.price,
            trade.tradeLegs.filter { tradeLeg -> tradeLeg.quantity > 0 }.sumOf { tradeLeg -> tradeLeg.quantity })

        simpMessagingTemplate.convertAndSend("/topics/trades", lastTrade)
    }
}