package com.danapple.openexchange.dao.memoryimplementations

import com.danapple.openexchange.entities.instruments.TradingExchange
import com.danapple.openexchange.instruments.TradingExchangeDao
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

//@Service
class MemoryTradingExchangeDaoImpl : TradingExchangeDao {
    override fun createTradingExchange(exchangeSymbol: String, timezone: TimeZone): TradingExchange {
        return tradingExchanges.computeIfAbsent(exchangeSymbol, {
            val id = nextId.getAndIncrement()
            val tradingExchange = TradingExchange(id, exchangeSymbol, timezone)
            tradingExchangesById[id] = tradingExchange
            tradingExchange
        })
    }

    override fun getTradingExchangeById(exchangeId: Long): TradingExchange? {
        return tradingExchangesById[exchangeId]
    }

    override fun getTradingExchangeBySymbol(symbol: String): TradingExchange? {
        return tradingExchanges[symbol]
    }

    override fun deleteTradingExchange(exchangeId: Long) {
        val tradingExchange = tradingExchangesById.remove(exchangeId)
        if (tradingExchange != null) {
            tradingExchanges.remove(tradingExchange.exchangeSymbol)
        }
    }

    companion object {
        val nextId = AtomicLong()
        val tradingExchanges = ConcurrentHashMap<String, TradingExchange>()
        val tradingExchangesById = ConcurrentHashMap<Long, TradingExchange>()

    }
}