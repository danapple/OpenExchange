package com.danapple.openexchange.dao

import com.danapple.openexchange.instruments.Instrument

interface InstrumentDao {
    fun getInstrument(instrumentId: Long): Instrument
}