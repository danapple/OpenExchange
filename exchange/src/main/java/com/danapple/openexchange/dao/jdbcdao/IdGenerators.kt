package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.DaoIdGenerator
import com.danapple.openexchange.dao.IdDao
import com.danapple.openexchange.dao.IdGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class IdGenerators(private val idDao: IdDao) {
    @Bean("orderIdGenerator")
    open fun orderIdGenerator() : IdGenerator{
        return DaoIdGenerator(IdDao.IdType.ORDER, idDao, 1000)
    }

    @Bean("tradeIdGenerator")
    open fun tradeIdGenerator() : IdGenerator{
        return DaoIdGenerator(IdDao.IdType.TRADE, idDao, 1000)
    }

    @Bean("tradeLegIdGenerator")
    open fun tradeLegIdGenerator() : IdGenerator{
        return DaoIdGenerator(IdDao.IdType.TRADE_LEG, idDao, 1000)
    }
}