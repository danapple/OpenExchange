package com.danapple.openexchange.dao.jdbcdao

class DatabaseConfiguration {
    var jdbcUrlTemplate: String? = null
    var shardCount: Int = 1
    var migrationsLocation: String? = null
    var repair: Boolean = false
}