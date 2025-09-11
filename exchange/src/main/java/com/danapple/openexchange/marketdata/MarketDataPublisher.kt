package com.danapple.openexchange.marketdata

import com.danapple.openexchange.book.Book
import com.danapple.openexchange.entities.trades.Trade

interface MarketDataPublisher {
    fun publishTopOfBook(timestamp: Long, book: Book)
    fun publishTrades(timestamp: Long, trade: Trade)
}