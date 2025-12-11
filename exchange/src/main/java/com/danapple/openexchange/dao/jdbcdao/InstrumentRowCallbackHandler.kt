package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dto.AssetClass
import com.danapple.openexchange.dto.InstrumentStatus
import com.danapple.openexchange.dto.OptionType
import com.danapple.openexchange.entities.instruments.Equity
import com.danapple.openexchange.entities.instruments.Instrument
import com.danapple.openexchange.entities.instruments.Option
import org.springframework.jdbc.core.RowCallbackHandler
import java.sql.ResultSet

class InstrumentRowCallbackHandler(private val instruments: MutableSet<Instrument>) : RowCallbackHandler {
    override fun processRow(rs: ResultSet) {
        val instrumentId = rs.getLong("instrumentId")
        val status = InstrumentStatus.valueOf(rs.getString("status"))
        val symbol = rs.getString("symbol")
        val assetClass = AssetClass.valueOf(rs.getString("assetClass"))
        val description = rs.getString("description")
        val expirationTime = rs.getLong("expirationTime")
        val currencyCode = rs.getString("currencyCode")
        when (assetClass) {
            AssetClass.EQUITY -> {
                instruments.add(
                    Equity(
                        instrumentId = instrumentId,
                        status = status,
                        symbol = symbol,
                        assetClass = assetClass,
                        description = description,
                        expirationTime = expirationTime,
                        currencyCode = currencyCode,
                    )
                )
            }

            AssetClass.OPTION -> {
                val optionType = OptionType.valueOf(rs.getString("optionType"))
                val strike = rs.getFloat("strike")
                val valueFactor = rs.getFloat("valueFactor")
                val deliverableInstrumentId = rs.getLong("deliverableInstrumentId")
                val quantity = rs.getLong("quantity")
                val value = rs.getFloat("value")

                val underlyingInstrumentId = rs.getLong("underlyingInstrumentId")
                instruments.add(
                    Option(
                        instrumentId = instrumentId,
                        status = status,
                        symbol = symbol,
                        assetClass = assetClass,
                        description = description,
                        expirationTime = expirationTime,
                        currencyCode = currencyCode,
                        underlyingInstrumentId = underlyingInstrumentId,
                        valueFactor = valueFactor,
                        optionType = optionType,
                        strike = strike,
                        deliverables = emptySet(),
                        cashDeliverables = emptySet()
                    )
                )
            }

            AssetClass.COMMODITY -> {
                TODO()
            }

            AssetClass.FUTURE -> {
                TODO()
            }
        }
    }
}
