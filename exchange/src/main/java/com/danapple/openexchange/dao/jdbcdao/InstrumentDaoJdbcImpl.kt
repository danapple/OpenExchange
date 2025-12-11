package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.dto.InstrumentStatus
import com.danapple.openexchange.entities.instruments.Instrument
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
open class InstrumentDaoJdbcImpl(@Qualifier("instrumentJdbcClient") private val jdbcClient : JdbcClient) : InstrumentDao {
    private val instrumentCache = ConcurrentHashMap<Long, Instrument>()
    @Volatile private var loaded = false
    override fun getInstrument(instrumentId: Long): Instrument? {
        if (!loaded) {
            logger.info("Need to getAllInstruments for instrumentId {}", instrumentId)
            getAllInstruments();
        }
        return instrumentCache[instrumentId];
    }

    override fun getActiveInstruments(): Collection<Instrument> {
        val instruments = getAllInstruments();
        return instruments.stream().filter( { instrument -> instrument.status == InstrumentStatus.ACTIVE } ).toList();
    }

    @Synchronized
    override fun getAllInstruments(): Collection<Instrument> {
        if (loaded) {
            return instrumentCache.values
        }
        val instruments = HashSet<Instrument>()
        val instrumentRowCallbackHandler = InstrumentRowCallbackHandler(instruments)

        val statement = jdbcClient.sql(INSTRUMENT_QUERY);
        statement.query(instrumentRowCallbackHandler)
        instruments.forEach { instrument -> instrumentCache[instrument.instrumentId] = instrument }
        loaded = true
        return instruments
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(InstrumentDaoJdbcImpl::class.java)
    }
}



const val INSTRUMENT_QUERY = """
 SELECT instrument.instrumentId, status, symbol, assetClass, description, expirationTime, currencyCode, 
 underlyingInstrumentId, valueFactor,
 optionType, strike,
 deliverable.deliverableInstrumentId, deliverable.quantity,
 cash_deliverable.value
 
 FROM instrument
 LEFT JOIN derivative on derivative.instrumentId = instrument.instrumentId
 LEFT JOIN option on option.instrumentId = derivative.instrumentId
 LEFT JOIN equity on equity.instrumentId = derivative.instrumentId
 LEFT JOIN deliverable on deliverable.instrumentId = derivative.instrumentId
 LEFT JOIN cash_deliverable on cash_deliverable.instrumentId = derivative.instrumentId
"""
