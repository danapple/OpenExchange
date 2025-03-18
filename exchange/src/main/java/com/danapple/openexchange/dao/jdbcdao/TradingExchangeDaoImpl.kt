package com.danapple.openexchange.instruments
// Generated using ChatGPT

import com.danapple.openexchange.entities.instruments.TradingExchange
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.TimeZone

@Repository
open class TradingExchangeDaoImpl(private val jdbcTemplate: NamedParameterJdbcTemplate) : TradingExchangeDao {

    override fun createTradingExchange(exchangeSymbol: String, timezone: TimeZone): TradingExchange {
        val sql = """
            INSERT INTO trading_exchanges (exchange_symbol, timezone) 
            VALUES (:exchangeSymbol, :timezone) RETURNING exchange_id
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("exchangeSymbol", exchangeSymbol)
            .addValue("timezone", timezone.id)

        val exchangeId: Long = jdbcTemplate.queryForObject(sql, params, Long::class.java)!!

        return TradingExchange(exchangeId, exchangeSymbol, timezone)
    }

    override fun getTradingExchangeById(exchangeId: Long): TradingExchange? {
        val sql = "SELECT * FROM trading_exchanges WHERE exchange_id = :exchangeId"
        val params = MapSqlParameterSource("exchangeId", exchangeId)
        return jdbcTemplate.query(sql, params, TradingExchangeRowMapper()).firstOrNull()
    }

    override fun getTradingExchangeBySymbol(symbol: String): TradingExchange? {
        val sql = "SELECT * FROM trading_exchanges WHERE exchange_symbol = :exchangeSymbol"
        val params = MapSqlParameterSource("exchangeSymbol", symbol)
        return jdbcTemplate.query(sql, params, TradingExchangeRowMapper()).firstOrNull()
    }

    override fun deleteTradingExchange(exchangeId: Long) {
        val sql = "DELETE FROM trading_exchanges WHERE exchange_id = :exchangeId"
        val params = MapSqlParameterSource("exchangeId", exchangeId)
        jdbcTemplate.update(sql, params)
    }
}
