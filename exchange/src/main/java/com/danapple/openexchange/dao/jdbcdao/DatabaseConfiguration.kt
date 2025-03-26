package com.danapple.openexchange.dao.jdbcdao

class DatabaseConfiguration {
    var jdbcUrlTemplate : String? = null
    var shardCount: Int? = null
    var migrationsLocation: String? = null
}