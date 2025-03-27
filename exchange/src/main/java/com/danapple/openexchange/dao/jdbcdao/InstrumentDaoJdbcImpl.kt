package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.entities.instruments.Instrument
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
open class InstrumentDaoJdbcImpl(@Qualifier("instrumentJdbcClient") private val jdbcClient : JdbcClient) : InstrumentDao {
    val instruments = ConcurrentHashMap<Long, Instrument>()
    override fun getInstrument(instrumentId: Long): Instrument {
        return instruments.computeIfAbsent(instrumentId, { Instrument(instrumentId) })
    }

    override fun getInstruments(): Set<Instrument> {
        return setOf( getInstrument(0), getInstrument(1));
    }
}