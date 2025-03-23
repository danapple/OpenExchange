package com.danapple.openexchange.dao

import org.flywaydb.core.Flyway
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.simple.JdbcClient
import javax.sql.DataSource


@Configuration
open class DataSources {

    @Bean("orderJdbcClients")
    open fun getOrderJdbcClients(): List<JdbcClient> {
        val dataSources = ArrayList<DataSource>()
        for (i in 0..4) {
            val dataSourceBuilder = DataSourceBuilder.create()
            dataSourceBuilder.driverClassName("org.h2.Driver")
            dataSourceBuilder.url("jdbc:h2:file:/tmp/openexchange/order$i")
            dataSourceBuilder.username("SA")
            dataSourceBuilder.password("")
            val dataSource = dataSourceBuilder.build()

            val flywayConfig = org.flywaydb.core.api.configuration.FluentConfiguration()
                .dataSource(dataSource)
                .locations("database/migrations/orders")

            val flyway = Flyway(flywayConfig)
            flyway.migrate()

            dataSources.add(dataSource)
        }
        return dataSources.map { JdbcClient.create(it) }
    }

    @Bean("idJdbcClient")
    open fun getIdJdbcClient(): JdbcClient {
        val dataSourceBuilder = DataSourceBuilder.create()
        dataSourceBuilder.driverClassName("org.h2.Driver")
        dataSourceBuilder.url("jdbc:h2:file:/tmp/openexchange/id")
        dataSourceBuilder.username("SA")
        dataSourceBuilder.password("")

        val dataSource = dataSourceBuilder.build()

        val flywayConfig = org.flywaydb.core.api.configuration.FluentConfiguration()
            .dataSource(dataSource)
            .locations("database/migrations/ids")
        val flyway = Flyway(flywayConfig)
        flyway.migrate()

        return JdbcClient.create(dataSource)
    }

    @Bean("instrumentJdbcClient")
    @Primary
    open fun getInstrumentJdbcClient(): JdbcClient {
        val dataSourceBuilder = DataSourceBuilder.create()
        dataSourceBuilder.driverClassName("org.h2.Driver")
        dataSourceBuilder.url("jdbc:h2:file:/tmp/openexchange/instruments")
        dataSourceBuilder.username("SA")
        dataSourceBuilder.password("")
        val dataSource = dataSourceBuilder.build()

        val flywayConfig = org.flywaydb.core.api.configuration.FluentConfiguration()
            .dataSource(dataSource)
            .locations("database/migrations/instruments")
        val flyway = Flyway(flywayConfig)
        flyway.migrate()
        return JdbcClient.create(dataSource)
    }

    @Bean("customerJdbcClient")
    open fun getCustomerJdbcClient(): JdbcClient {
        val dataSourceBuilder = DataSourceBuilder.create()
        dataSourceBuilder.driverClassName("org.h2.Driver")
        dataSourceBuilder.url("jdbc:h2:file:/tmp/openexchange/customers")
        dataSourceBuilder.username("SA")
        dataSourceBuilder.password("")
        val dataSource = dataSourceBuilder.build()

        val flywayConfig = org.flywaydb.core.api.configuration.FluentConfiguration()
            .dataSource(dataSource)
            .locations("database/migrations/customers")
        val flyway = Flyway(flywayConfig)
        flyway.migrate()
        return JdbcClient.create(dataSource)
    }
}
