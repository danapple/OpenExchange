package com.danapple.openexchange.dao.memoryimplementations

import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.entities.instruments.Instrument
import com.danapple.openexchange.entities.instruments.Equity
import com.danapple.openexchange.instruments.TradingExchangeDao
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class MemoryInstrumentDao(private val tradingExchangeDao : TradingExchangeDao): InstrumentDao {
    override fun getInstrument(instrumentId: Long) : Instrument {
        return instruments.computeIfAbsent(instrumentId, { Equity(instrumentId, "Equity $instrumentId", tradingExchangeDao.createTradingExchange("Test Exchange 1", TimeZone.getDefault())) } )
    }

    companion object {
        val instruments = ConcurrentHashMap<Long, Instrument>()
    }
}