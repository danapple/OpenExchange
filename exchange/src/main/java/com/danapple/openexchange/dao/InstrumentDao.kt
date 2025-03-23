package com.danapple.openexchange.dao

import com.danapple.openexchange.entities.instruments.Instrument

interface InstrumentDao {
    fun getInstrument(instrumentId: Long): Instrument?

    fun getInstruments(): Set<Instrument>
}