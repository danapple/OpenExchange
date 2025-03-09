package com.danapple.openexchange.memoryimplementations

import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.instruments.Instrument
import com.danapple.openexchange.instruments.Equity
import com.danapple.openexchange.instruments.TradingExchange
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class MemoryInstrumentDao : InstrumentDao {
    override fun getInstrument(instrumentId: Long) : Instrument {
        return instruments.computeIfAbsent(instrumentId, { Equity(instrumentId, "Equity $instrumentId", tradingExchange) } )
    }

    companion object {
        val tradingExchange = TradingExchange(0, "Trading Exchange", TimeZone.getDefault())

        val instruments = ConcurrentHashMap<Long, Instrument>()
    }
}