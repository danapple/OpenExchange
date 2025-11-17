package com.danapple.openexchange.dao.jdbcdao

import org.springframework.jdbc.core.simple.JdbcClient

abstract class ShardedDaoJdbcImpl(val jdbcClients: List<JdbcClient>, private val shardCount: Int) {
    internal fun getJdbcClient(instrumentId: Long): JdbcClient {
        return jdbcClients[(instrumentId % shardCount).toInt()]
    }
}