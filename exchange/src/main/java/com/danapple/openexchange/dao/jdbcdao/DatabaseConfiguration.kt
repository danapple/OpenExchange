package com.danapple.openexchange.dao.jdbcdao

class DatabaseConfiguration {
    var jdbcUrlTemplate: String? = null
    var shardCount: Int = 1
    var username: String? = null
    var password: String? = null
    var driverClassName: String? = null
    var maximumPoolSize: Int = 2
    var initializationFailTimeout: Long = -1
    var minimumIdle: Int = 2
}