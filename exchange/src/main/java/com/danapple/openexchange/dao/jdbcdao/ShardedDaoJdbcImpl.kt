package com.danapple.openexchange.dao.jdbcdao

import org.springframework.jdbc.core.simple.JdbcClient

abstract class ShardedDaoJdbcImpl(val jdbcClients : List<JdbcClient>) {
    internal fun getJdbcClient(instrumentId: Long): JdbcClient {
        return jdbcClients[(instrumentId % 100).toInt()]
    }
}