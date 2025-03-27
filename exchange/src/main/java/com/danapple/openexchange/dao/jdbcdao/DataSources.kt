package com.danapple.openexchange.dao

import com.danapple.openexchange.dao.jdbcdao.DatabaseConfiguration
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.simple.JdbcClient
import javax.sql.DataSource


@Configuration
open class DataSources {


    @Bean("orderDataSource")
    @ConfigurationProperties("spring.datasource.order")
    open fun orderDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean("orderDatabaseConfiguration")
    @ConfigurationProperties("openexchange.database.order")
    open fun getOrderDatabaseConfiguration() : DatabaseConfiguration {
        return DatabaseConfiguration()
    }

    @Bean("orderJdbcClients")
    open fun getOrderJdbcClients(@Qualifier("orderDataSource") dataSource : DataSource,
                                 @Qualifier("orderDatabaseConfiguration") databaseConfiguration : DatabaseConfiguration,
                                 @Value("\${openexchange.database.order.jdbcUrlTemplate}") jdbcUrlTemplate: String) : List<JdbcClient> {
        val dataSources = ArrayList<DataSource>()
        for (i in 0..databaseConfiguration.shardCount!!) {
            val dataSourceBuilder = DataSourceBuilder.derivedFrom(dataSource)
            dataSourceBuilder.url("${databaseConfiguration.jdbcUrlTemplate}$i")
            val shardDataSource = dataSourceBuilder.build()

            val flywayConfig = org.flywaydb.core.api.configuration.FluentConfiguration()
                .dataSource(shardDataSource)
                .locations(databaseConfiguration.migrationsLocation)


            val flyway = Flyway(flywayConfig)
            if (databaseConfiguration.repair) {
                flyway.repair()
            }
            flyway.migrate()

            dataSources.add(shardDataSource)
        }
        return dataSources.map { JdbcClient.create(it) }
    }

    @Bean("idDataSource")
    @ConfigurationProperties("spring.datasource.id")
    open fun idDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean("idDatabaseConfiguration")
    @ConfigurationProperties("openexchange.database.id")
    open fun getIdDatabaseConfiguration() : DatabaseConfiguration {
        return DatabaseConfiguration()
    }

    @Bean("idJdbcClient")
    @Primary
    open fun getIdJdbcClient(@Qualifier("idDataSource") dataSource : DataSource,
                             @Qualifier("idDatabaseConfiguration") databaseConfiguration : DatabaseConfiguration): JdbcClient {
        val flywayConfig = org.flywaydb.core.api.configuration.FluentConfiguration()
            .dataSource(dataSource)
            .locations(databaseConfiguration.migrationsLocation)
        val flyway = Flyway(flywayConfig)
        if (databaseConfiguration.repair) {
            flyway.repair()
        }
        flyway.migrate()

        return JdbcClient.create(dataSource)
    }

    @Bean("instrumentDataSource")
    @ConfigurationProperties("spring.datasource.instrument")
    open fun instrumentDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean("instrumentDatabaseConfiguration")
    @ConfigurationProperties("openexchange.database.instrument")
    open fun getInstrumentDatabaseConfiguration() : DatabaseConfiguration {
        return DatabaseConfiguration()
    }

    @Bean("instrumentJdbcClient")
    @Primary
    open fun getInstrumentJdbcClient(@Qualifier("instrumentDataSource") dataSource : DataSource,
                                     @Qualifier("instrumentDatabaseConfiguration") databaseConfiguration : DatabaseConfiguration): JdbcClient {
        val flywayConfig = org.flywaydb.core.api.configuration.FluentConfiguration()
            .dataSource(dataSource)
            .locations(databaseConfiguration.migrationsLocation)
        val flyway = Flyway(flywayConfig)
        if (databaseConfiguration.repair) {
            flyway.repair()
        }
        flyway.migrate()
        return JdbcClient.create(dataSource)
    }

    @Bean("customerDataSource")
    @ConfigurationProperties("spring.datasource.customer")
    @Primary
    open fun customerDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean("customerDatabaseConfiguration")
    @ConfigurationProperties("openexchange.database.customer")
    open fun getCustomerDatabaseConfiguration() : DatabaseConfiguration {
        return DatabaseConfiguration()
    }

    @Bean("customerJdbcClient")
    open fun getCustomerJdbcClient(@Qualifier("customerDataSource") dataSource : DataSource,
                                   @Qualifier("customerDatabaseConfiguration") databaseConfiguration : DatabaseConfiguration): JdbcClient {
        val flywayConfig = org.flywaydb.core.api.configuration.FluentConfiguration()
            .dataSource(dataSource)
            .locations(databaseConfiguration.migrationsLocation)
        val flyway = Flyway(flywayConfig)
        if (databaseConfiguration.repair) {
            flyway.repair()
        }
        flyway.migrate()
        return JdbcClient.create(dataSource)
    }
}
