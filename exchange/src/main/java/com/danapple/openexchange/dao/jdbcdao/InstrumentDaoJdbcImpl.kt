package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.entities.instruments.Instrument
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
open class InstrumentDaoJdbcImpl(@Qualifier("instrumentJdbcClient") private val jdbcClient : JdbcClient) : InstrumentDao {
    private val instrumentCache = ConcurrentHashMap<Long, Instrument>()
    private var loaded = false
    override fun getInstrument(instrumentId: Long): Instrument? {
        if (!loaded) {
            getActiveInstruments();
        }
        return instrumentCache[instrumentId];
    }

    override fun getActiveInstruments(): Set<Instrument> {
        val instruments = HashSet<Instrument>()
        val instrumentRowCallbackHandler = InstrumentRowCallbackHandler(instruments)

        val statement = jdbcClient.sql(
            """SELECT instrumentId, status, symbol, assetClass, description, expirationTime
                FROM instrument
                WHERE status = 'ACTIVE'
                """);
        statement.query(instrumentRowCallbackHandler)
        instruments.forEach { instrument -> instrumentCache[instrument.instrumentId] = instrument }
        loaded = true
        return instruments
    }

    override fun getAllInstruments(): Set<Instrument> {
        val instruments = HashSet<Instrument>()
        val instrumentRowCallbackHandler = InstrumentRowCallbackHandler(instruments)

        val statement = jdbcClient.sql(
            """SELECT instrumentId, status, symbol, assetClass, description, expirationTime
                FROM instrument
                """);
        statement.query(instrumentRowCallbackHandler)
        instruments.forEach { instrument -> instrumentCache[instrument.instrumentId] = instrument }
        return instruments
    }
}