package com.danapple.openexchange.dao

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.*
import javax.sql.DataSource

@Configuration
open class DataSources {

        @Bean
        open fun namedParameterJdbcTemplate(instrumentDataSource: DataSource) : NamedParameterJdbcTemplate {
                return NamedParameterJdbcTemplate(instrumentDataSource)
        }

        @Bean("orderDataSources")
        open fun getOrderDataSources(): List<DataSource> {
                val dataSources = ArrayList<DataSource>()
                for (i in 0..100) {
                        val dataSourceBuilder = DataSourceBuilder.create()
                        dataSourceBuilder.driverClassName("org.h2.Driver")
                        dataSourceBuilder.url("jdbc:h2:file:/tmp/openexchange/order$i")
                        dataSourceBuilder.username("SA")
                        dataSourceBuilder.password("")
                        dataSources.add(dataSourceBuilder.build())
                    }
                return dataSources
            }
    
        @Bean("idDataSource")
        open fun getOrderIdDataSource(): DataSource {
                val dataSourceBuilder = DataSourceBuilder.create()
                dataSourceBuilder.driverClassName("org.h2.Driver")
                dataSourceBuilder.url("jdbc:h2:file:/tmp/openexchange/id")
                dataSourceBuilder.username("SA")
                dataSourceBuilder.password("")
                return dataSourceBuilder.build()
            }

        @Bean("instrumentDataSource")
        open fun getInstrumentDataSource(): DataSource {
                val dataSourceBuilder = DataSourceBuilder.create()
                dataSourceBuilder.driverClassName("org.h2.Driver")
                dataSourceBuilder.url("jdbc:h2:file:/tmp/openexchange/instruments")
                dataSourceBuilder.username("SA")
                dataSourceBuilder.password("")
                return dataSourceBuilder.build()
        }
    }
