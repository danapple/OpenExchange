package com.danapple.openexchange.marketdata

import com.danapple.openexchange.book.Book
import com.danapple.openexchange.entities.trades.Trade
import org.springframework.stereotype.Service

@Service("marketDataPublisherMain")
class MarketDataPublisherDistributor(private val marketDataPublishers: Set<MarketDataPublisher>) : MarketDataPublisher {
    override fun publishTopOfBook(timestamp: Long, book: Book) {
        marketDataPublishers.forEach { mdp ->
            mdp.publishTopOfBook(timestamp, book)
        }
    }

    override fun publishTrades(timestamp: Long, trade: Trade) {
        marketDataPublishers.forEach { mdp ->
            mdp.publishTrades(timestamp, trade)
        }
    }
}