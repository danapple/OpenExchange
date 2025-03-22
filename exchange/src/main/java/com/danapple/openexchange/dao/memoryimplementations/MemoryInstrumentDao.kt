package com.danapple.openexchange.dao.memoryimplementations

import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.entities.instruments.Instrument
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class MemoryInstrumentDao() : InstrumentDao {
    override fun getInstrument(instrumentId: Long) : Instrument {
        return instruments.computeIfAbsent(instrumentId, { Instrument(instrumentId) } )
    }

    companion object {
        val instruments = ConcurrentHashMap<Long, Instrument>()
    }
}