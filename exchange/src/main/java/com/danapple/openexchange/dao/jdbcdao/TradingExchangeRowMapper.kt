package com.danapple.openexchange.instruments

import com.danapple.openexchange.entities.instruments.TradingExchange
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.util.TimeZone

class TradingExchangeRowMapper : RowMapper<TradingExchange> {
    override fun mapRow(rs: ResultSet, rowNum: Int): TradingExchange {
        return TradingExchange(
            rs.getLong("exchange_id"),
            rs.getString("exchange_symbol"),
            TimeZone.getTimeZone(rs.getString("timezone"))
        )
    }
}
