package com.danapple.openexchange.instruments
// Generated using ChatGPT

import com.danapple.openexchange.entities.instruments.TradingExchange
import java.util.TimeZone

interface TradingExchangeDao {
    fun createTradingExchange(exchangeSymbol: String, timezone: TimeZone): TradingExchange
    fun getTradingExchangeById(exchangeId: Long): TradingExchange?
    fun getTradingExchangeBySymbol(symbol: String): TradingExchange?
    fun deleteTradingExchange(exchangeId: Long)
}
