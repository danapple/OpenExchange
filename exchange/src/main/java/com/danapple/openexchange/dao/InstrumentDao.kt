package com.danapple.openexchange.dao

import com.danapple.openexchange.entities.instruments.Instrument

interface InstrumentDao {
    fun getInstrument(instrumentId: Long): Instrument?

    fun getActiveInstruments(): Set<Instrument>
    fun getAllInstruments(): Set<Instrument>
}