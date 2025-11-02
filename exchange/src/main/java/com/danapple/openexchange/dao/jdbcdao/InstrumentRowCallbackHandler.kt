package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dto.AssetClass
import com.danapple.openexchange.dto.InstrumentStatus
import com.danapple.openexchange.entities.instruments.Instrument
import org.springframework.jdbc.core.RowCallbackHandler
import java.sql.ResultSet

class InstrumentRowCallbackHandler(private val instruments : MutableSet<Instrument>) : RowCallbackHandler {
    override fun processRow(rs: ResultSet) {
        instruments.add(Instrument(rs.getLong("instrumentId"),
            InstrumentStatus.valueOf(rs.getString("status")),
            rs.getString("symbol"),
            AssetClass.valueOf(rs.getString("assetClass")),
            rs.getString("description"),
            rs.getLong("expirationTime")));
    }
}
