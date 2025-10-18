package com.danapple.openexchange.marketdata

import LastTrade
import com.danapple.openexchange.book.Book
import com.danapple.openexchange.dto.MarketDepth
import com.danapple.openexchange.entities.trades.Trade
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.atomic.AtomicLong

@Service("marketDataPublisherWebsocket")
class MarketDataPublisherWebsocket(
    @Value("\${openexchange.marketDataPublisher.numberOfLevelsToPublish}") private val numberOfLevelsToPublish: Int,
    private val simpMessagingTemplate: SimpMessagingTemplate
) : MarketDataPublisher {
    private val senderId = UUID.randomUUID().toString()
    private val sequenceNumber = AtomicLong()  // TODO should be per instrument and per type

    override fun publishTopOfBook(timestamp: Long, book: Book) {
        val instrumentId = book.instrument.instrumentId
        val bestBuyPriceLevels = book.getBestBuyPriceLevels(numberOfLevelsToPublish)
        val bestSellPriceLevels = book.getBestSellPriceLevels(numberOfLevelsToPublish)
        val marketDepth = MarketDepth(
            senderId,
            sequenceNumber.getAndIncrement(),
            instrumentId,
            timestamp,
            bestBuyPriceLevels,
            bestSellPriceLevels
        )
        simpMessagingTemplate.convertAndSend("/topics/depth", marketDepth)
        simpMessagingTemplate.convertAndSend("/topics/depth/$instrumentId", marketDepth)
    }

    override fun publishTrades(timestamp: Long, trade: Trade) {
        val instrumentId = trade.tradeLegs.first().orderState.order.instrument.instrumentId
        val lastTrade = LastTrade(
            senderId,
            sequenceNumber.getAndIncrement(),
            instrumentId,
            timestamp,
            trade.price,
            trade.tradeLegs.filter { tradeLeg -> tradeLeg.quantity > 0 }.sumOf { tradeLeg -> tradeLeg.quantity })

        simpMessagingTemplate.convertAndSend("/topics/trades", lastTrade)
        simpMessagingTemplate.convertAndSend("/topics/trades/$instrumentId", lastTrade)

    }
}